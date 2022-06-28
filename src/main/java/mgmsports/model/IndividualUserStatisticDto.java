package mgmsports.model;

import lombok.Data;

@Data
public class IndividualUserStatisticDto {
    private int activeDayTotal;
    private double activityTimeTotal;
    private double runningDistanceTotal;
    private double runningTimeTotal;
    private double cyclingDistanceTotal;
    private double cyclingTimeTotal;
    private double meditationTimeTotal;
    private double climbingTimeTotal;
    private double skatingTimeTotal;
    private double swimmingTimeTotal;
    private double yogaTimeTotal;
    private double hikingTimeTotal;
    private double gymTimeTotal;
    private String beginDate;
    private String endDate;
    private String registerDate;
    private int runningDistanceRating;
    private String runningRank;
}
