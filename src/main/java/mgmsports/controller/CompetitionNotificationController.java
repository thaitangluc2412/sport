package mgmsports.controller;

import mgmsports.dao.entity.CompetitionNotification;
import mgmsports.model.CompetitionNotificationDto;
import mgmsports.service.CompetitionNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Competition notification controller
 *
 * @author Chuc Ba Hieu
 */
@RestController
@RequestMapping("/api/competitionnotification")
public class CompetitionNotificationController {

    private CompetitionNotificationService competitionNotificationService;

    @Autowired
    public CompetitionNotificationController(CompetitionNotificationService competitionNotificationService) {
        this.competitionNotificationService = competitionNotificationService;
    }

    /**
     * Get all notification user wasn't seen it
     *
     * @param userId account id
     * @return list notification
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getAllNotificationByUserId(@PathVariable("userId") String userId) {
        List<CompetitionNotification> competitionNotifications = competitionNotificationService.getAllNotifiByUserId(userId);
        List<CompetitionNotificationDto> data = competitionNotifications.stream().map(CompetitionNotification::toCompetitionNotificationDto).collect(Collectors.toList());
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    /**
     * Update notification when user seen it
     *
     * @param userId account id
     * @return Status OK
     */
    @PutMapping("/update/{userId}")
    public ResponseEntity<Object> updateSeen(@PathVariable("userId") String userId) {
        competitionNotificationService.updateNotificationByAccountId(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Update notification by notification id
     *
     * @param notificationId notification id
     * @return status OK
     */
    @PutMapping("/{notificationId}")
    public ResponseEntity<Object> updateNotification(@PathVariable("notificationId") Long notificationId) {
        competitionNotificationService.updateNotification(notificationId);
        return ResponseEntity.ok().build();
    }
}
