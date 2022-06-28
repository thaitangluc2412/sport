package mgmsports.service;

import mgmsports.common.exception.EntityNotFoundException;
import mgmsports.common.exception.InvalidFileException;
import mgmsports.model.ActivityDto;
import mgmsports.model.ActivityInputDto;
import mgmsports.model.ActivityType;
import mgmsports.model.UserStatisticDto;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.List;

/**
 * Activity interface
 *
 * @author qngo
 */
public interface ActivityService {

    List<ActivityDto> getActivitiesByUserOrderByDate(String userID) throws EntityNotFoundException;

    void updateActivityStatus(String activityId) throws EntityNotFoundException;

    void createActivity(ActivityInputDto activityInputDto, MultipartFile activityImage) throws EntityNotFoundException, InvalidFileException, ParseException;

    void updateActivity(ActivityInputDto activityInputUpdateDto, MultipartFile activityImage) throws EntityNotFoundException, InvalidFileException, ParseException;

    ActivityType[] getActivityType();

    UserStatisticDto getUserStatistic(String accountId) throws EntityNotFoundException;

    List<String> getSuggestionByAccount(String accountId) throws EntityNotFoundException;
}
