package mgmsports.dao.repository;

import mgmsports.dao.entity.CompetitionNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompetitionNotificationRepository extends JpaRepository<CompetitionNotification, Long> {

    List<CompetitionNotification> getAllByInvitee_AccountIdAndSeenIsFalse(String userId);

    List<CompetitionNotification> getAllByHost_AccountIdAndInvitee_AccountId(String hostId, String inviteeId);

    List<CompetitionNotification> getCompetitionNotificationByHost_AccountIdAndActivity_ActivityId(String accountId, String activityId);

    List<CompetitionNotification> getCompetitionNotificationByActivity_ActivityIdAndSeenIsFalse(String activityId);

    @Modifying
    @Query("update CompetitionNotification cn set cn.seen = true where cn.competitionNotificationId = ?1")
    void updateNotification(Long notificationId);

    @Modifying
    @Query("update CompetitionNotification cn set cn.seen = true where cn.invitee.accountId = ?1")
    void updateNotificationByUserId(String userId);
}
