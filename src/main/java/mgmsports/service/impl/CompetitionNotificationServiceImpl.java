package mgmsports.service.impl;

import lombok.extern.slf4j.Slf4j;
import mgmsports.dao.entity.Account;
import mgmsports.dao.entity.CompetitionNotification;
import mgmsports.dao.repository.CompetitionNotificationRepository;
import mgmsports.model.CompetitionNotificationDto;
import mgmsports.service.CompetitionNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Competition service
 *
 * @author Chuc Ba Hieu
 */
@Slf4j
@Service
public class CompetitionNotificationServiceImpl implements CompetitionNotificationService {

    private CompetitionNotificationRepository competitionNotificationRepository;

    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public CompetitionNotificationServiceImpl(CompetitionNotificationRepository competitionNotificationRepository, SimpMessagingTemplate messagingTemplate) {
        this.competitionNotificationRepository = competitionNotificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Get all notification user wasn't seen by account id
     *
     * @param userId account id
     * @return list competition notification
     */
    @Override
    public List<CompetitionNotification> getAllNotifiByUserId(String userId) {
        return competitionNotificationRepository.getAllByInvitee_AccountIdAndSeenIsFalse(userId);
    }

    /**
     * Update competition notification by notification id
     *
     * @param notificationId notification id
     */
    @Transactional
    @Override
    public void updateNotification(Long notificationId) {
        if (!competitionNotificationRepository.existsById(notificationId)) {
            log.info("Nothing to update!");
            return;
        }
        competitionNotificationRepository.updateNotification(notificationId);
    }

    /**
     * Update competition notification by account id
     *
     * @param accountId account id
     */
    @Transactional
    @Override
    public void updateNotificationByAccountId(String accountId) {
        competitionNotificationRepository.updateNotificationByUserId(accountId);
    }

    /**
     * Push notification to client when had new notification
     *
     * @param direction notification channel
     * @param competitionNotification notification
     */
    @Override
    public void notify(String direction, CompetitionNotification competitionNotification) {
        messagingTemplate.convertAndSend(direction, competitionNotification.toCompetitionNotificationDto());
    }

    /**
     * Push chanel and notification to client
     *
     * @param direction notification channel
     * @param competitionNotifications notification
     * @param account account
     */
    @Override
    public void notify(String direction, List<CompetitionNotification> competitionNotifications, Account account) {
        messagingTemplate.convertAndSend("/notification/" + account.getAccountId(), direction);
        if (competitionNotifications == null || competitionNotifications.isEmpty())
            return;
        List<CompetitionNotificationDto> data = competitionNotifications.stream().map(CompetitionNotification::toCompetitionNotificationDto).collect(Collectors.toList());
        messagingTemplate.setSendTimeout(1000);
        messagingTemplate.convertAndSend(direction, data);
        messagingTemplate.setSendTimeout(-1L);
    }
}
