--------Trigger for competition notification--------

---After deleted 1 row in competition table
CREATE OR REPLACE FUNCTION deleteCompetitionNotificationWhenCompetitionIsDeleted()
  RETURNS TRIGGER AS
$$
BEGIN
  DELETE FROM competition_notification WHERE host_id = old.host_id AND invitee_id = old.invitee_id;
  DELETE FROM competition_notification WHERE host_id = old.invitee_id AND invitee_id = old.host_id;
  RETURN old;
END;
$$
LANGUAGE 'plpgsql';
DROP TRIGGER IF EXISTS after_delete_competition ON competition;
CREATE TRIGGER after_delete_competition AFTER DELETE ON "competition" FOR EACH ROW EXECUTE PROCEDURE  deleteCompetitionNotificationWhenCompetitionIsDeleted();

----User exists in competition table inserted new activity
CREATE OR REPLACE FUNCTION insertIntoCompetitionNotificationWhenUserExistsInCompetitionInsertNewActivity()
  RETURNS TRIGGER AS
$$
DECLARE
  a competition%ROWTYPE;
  b competition%ROWTYPE;
BEGIN
  FOR a IN
    SELECT * FROM competition WHERE host_id = new.account_id
  LOOP
    INSERT INTO competition_notification(activity_id, host_id, invitee_id, seen, competition_id) VALUES (new.activity_id, new.account_id, a.invitee_id, FALSE, concat(a.competition_id, a.invitee_id));
  END LOOP;
  FOR b IN
    SELECT * From competition WHERE invitee_id = new.account_id
  LOOP
    INSERT INTO competition_notification(activity_id, host_id, invitee_id, seen, competition_id) VALUES (new.activity_id, new.account_id, b.host_id, FALSE , concat(b.competition_id, b.host_id));
  END LOOP;
  RETURN new;
END;
$$
LANGUAGE 'plpgsql';
DROP TRIGGER IF EXISTS after_insert_activity ON activity;
CREATE TRIGGER after_insert_activity AFTER INSERT ON "activity" FOR EACH ROW EXECUTE PROCEDURE insertIntoCompetitionNotificationWhenUserExistsInCompetitionInsertNewActivity();

----User exists in competition table updated activity
CREATE OR REPLACE FUNCTION updateCompetitionNotificationWhenUserExistsInCompetitionUpdateActivity()
  RETURNS TRIGGER AS
$$
BEGIN
  UPDATE competition_notification SET seen = FALSE WHERE activity_id = new.activity_id;
  RETURN new;
END;
$$
LANGUAGE 'plpgsql';
DROP TRIGGER IF EXISTS after_update_activity ON activity;
CREATE TRIGGER after_update_activity AFTER UPDATE ON "activity" FOR EACH ROW
  WHEN (old.distance IS DISTINCT FROM new.distance OR old.duration IS DISTINCT FROM new.duration)
  EXECUTE PROCEDURE updateCompetitionNotificationWhenUserExistsInCompetitionUpdateActivity();
select * from information_schema.triggers