package mgmsports.dao.entity;

import lombok.Data;
import mgmsports.model.CompetitionNotificationDto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

@Entity
@Data
@Table(name = "competition_notification")
public class CompetitionNotification {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long competitionNotificationId;

    @ManyToOne
    @JoinColumn(name = "host_id")
    private Account host;

    @ManyToOne
    @JoinColumn(name = "invitee_id")
    private Account invitee;

    @Column(name = "competition_id")
    private String competitionId;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;

    @Column(name = "seen")
    private boolean seen;

    public CompetitionNotificationDto toCompetitionNotificationDto() {
        CompetitionNotificationDto notificationDto = new CompetitionNotificationDto();
        notificationDto.setAccountId(host.getAccountId());
        notificationDto.setCompetitionNotificationId(this.competitionNotificationId);
        notificationDto.setFullName(host.getProfile().getFullName());
        notificationDto.setActivityType(activity.getActivityType());
        notificationDto.setImageLink(host.getProfile().getImageLink());
        notificationDto.setCompetitionId(competitionId);
        return notificationDto;
    }

}
