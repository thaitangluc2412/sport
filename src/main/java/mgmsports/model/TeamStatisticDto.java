package mgmsports.model;

import lombok.Data;

@Data
public class TeamStatisticDto {

    private String teamName;
    private String memberTotal;
    private String activeMemberTotal;
    private String activityTimeTotal;
    private String runningDistanceTotal;
    private String cyclingDistanceTotal;
    private String meditationTimeTotal;
    private String climbingTimeTotal;
    private String skatingTimeTotal;
    private String swimmingTimeTotal;
    private String yogaTimeTotal;
    private String hikingTimeTotal;
    private String gymTimeTotal;
    private String beginDate;
    private String endDate;
}
