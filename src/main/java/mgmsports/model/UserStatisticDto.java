package mgmsports.model;

import lombok.Data;

/**
 * user statistic dto
 *
 * @author qngo
 */
@Data
public class UserStatisticDto {
    private Double runningDisTotal;
    private Double cyclingDisTotal;
    private Double gymTimeTotal;
    private Double meditationTimeTotal;
    private Double climbingTimeTotal;
    private Double skatingTimeTotal;
    private Double swimmingTimeTotal;
    private Double yogaTimeTotal;
    private Double hikingTimeTotal;
}
