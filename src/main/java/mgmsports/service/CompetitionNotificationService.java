package mgmsports.service;

import mgmsports.dao.entity.Account;
import mgmsports.dao.entity.CompetitionNotification;

import java.util.List;

public interface CompetitionNotificationService {
    List<CompetitionNotification> getAllNotifiByUserId(String userId);
    void updateNotification(Long notificationId);
    void updateNotificationByAccountId(String accountId);
    void notify(String direction, CompetitionNotification competitionNotification);
    void notify(String direction, List<CompetitionNotification> competitionNotifications, Account account);
}
