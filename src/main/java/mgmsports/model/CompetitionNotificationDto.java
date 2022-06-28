package mgmsports.model;

import lombok.Data;

@Data
public class CompetitionNotificationDto {
    private Long competitionNotificationId;
    private String accountId;
    private String fullName;
    private ActivityType activityType;
    private String imageLink;
    private String competitionId;
}
