package mgmsports.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * Activity Data transfer
 *
 * @author qngo
 */
@Data
public class ActivityDto {

    private String activityId;

    private String accountId;

    private String title;

    private String location;

    private Double distance;

    private String workoutType;

    private Double duration;

    private String activityDate;

    private String imageLink;

    private Date createdDate;

    private Date lastModifiedDate;

    private ActivityType activityType;
}