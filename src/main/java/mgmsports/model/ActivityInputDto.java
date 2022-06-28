package mgmsports.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.Date;

/**
 * Create activity request body
 *
 * @author Chuc Ba Hieu
 */
@Data
public class ActivityInputDto {

    @NotEmpty(message = "Please provide account id")
    @Size(max = 255, message = "Account id must less than or equal 255")
    private String accountId;

    @NotEmpty(message = "Please provide activity title")
    @Size(max = 255, message = "Title must less than or equal 255")
    private String title;

    @Size(max = 255, message = "Location must less than or equal 255")
    private String location;

    @PositiveOrZero(message = "Distance must greater than or equal to 0")
    private Double distance;

    @PositiveOrZero(message = "Distance must greater than or equal to 0")
    private Double duration;

    @NotNull(message = "Please provide activity date")
    private String activityDate;

    @NotNull(message = "Please provide activity type")
    private ActivityType activityType;

    @Size(max = 255, message = "Activity id must less than or equal 255")
    private String activityId;

    @Size(max = 255, message = "Workout type must less than or equal 255")
    private String workoutType;

    public ActivityInputDto() {
    }
}
