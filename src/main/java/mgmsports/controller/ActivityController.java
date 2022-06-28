package mgmsports.controller;

import lombok.extern.slf4j.Slf4j;
import mgmsports.common.exception.EntityNotFoundException;
import mgmsports.common.exception.InvalidFileException;
import mgmsports.model.ActivityDto;
import mgmsports.model.ActivityInputDto;
import mgmsports.model.ActivityType;
import mgmsports.model.UserStatisticDto;
import mgmsports.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;


/**
 * Activity Controller
 *
 * @author qngo
 */
@Slf4j
@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private ActivityService activityService;

    @Autowired
    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    /**
     * get list of activity by account id order by activity date
     *
     * @param id account id
     * @return list of activity
     */
    @GetMapping
    public ResponseEntity<Object> getActivities(@RequestParam("accountId") String id) throws EntityNotFoundException {
        Optional<List<ActivityDto>> activitiesOptional = Optional.ofNullable(activityService.getActivitiesByUserOrderByDate(id));
        if (activitiesOptional.isPresent()) {
            return new ResponseEntity<>(activitiesOptional.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("No activity exits", HttpStatus.OK);
    }

    /**
     * This is a soft delete, hide the activity
     *
     * @param id activity id
     * @return response
     */
    @PutMapping("/softdelete")
    public ResponseEntity<Object> softDeleteActivity(@RequestParam("activityId") String id) throws EntityNotFoundException {
        activityService.updateActivityStatus(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Receive activity object
     *
     * @param activityInputDto activity data
     * @return response
     */
    @PostMapping
    public ResponseEntity<Object> createActivity(@RequestPart("activity") @Valid ActivityInputDto activityInputDto, @RequestPart(value = "activityImage", required = false) MultipartFile activityImage) throws EntityNotFoundException, InvalidFileException, ParseException {
        activityService.createActivity(activityInputDto, activityImage);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Update an activity object
     *
     * @param activityInputDto activityInputUpdateDto
     * @param activityImage activityImage
     * @return response
     * @throws EntityNotFoundException EntityNotFoundException
     * @throws InvalidFileException InvalidFileException
     * @throws ParseException ParseException
     */
    @PutMapping
    public ResponseEntity<Object> updateActivity(@RequestPart("activity") @Valid ActivityInputDto activityInputDto, @RequestPart(value = "activityImage", required = false) MultipartFile activityImage) throws EntityNotFoundException, InvalidFileException, ParseException {
        activityService.updateActivity(activityInputDto, activityImage);
        return ResponseEntity.ok().build();
    }

    /**
     * Get activity type
     *
     * @return list of activity type
     */
    @GetMapping("/activitytypes")
    public ResponseEntity<Object> getActivityType() {
        Optional<ActivityType[]> optionalActivityTypes = Optional.ofNullable(activityService.getActivityType());
        if (optionalActivityTypes.isPresent()) {
            return new ResponseEntity<>(optionalActivityTypes.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("No activity type exits", HttpStatus.OK);
    }

    /**
     * Get statistic of user have activities last seven days
     *
     * @param accountId passed to get user statistic of this account
     * @return user statistic
     */
    @GetMapping("/statistic")
    public ResponseEntity<Object> getUserStatistic(@RequestParam("accountId") String accountId) throws EntityNotFoundException {
        Optional<UserStatisticDto> userStatisticDtoOptional = Optional.ofNullable(activityService.getUserStatistic(accountId));
        if (userStatisticDtoOptional.isPresent()) {
            return new ResponseEntity<>(userStatisticDtoOptional.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("No activity exits", HttpStatus.OK);
    }

    @GetMapping("/location/suggestion")
    public ResponseEntity<Object> getLocationSuggestion(@RequestParam("accountId") String accountId) throws EntityNotFoundException {
        Optional<List<String>> locationSuggestionDtoOptional = Optional.ofNullable(activityService.getSuggestionByAccount(accountId));
        return locationSuggestionDtoOptional.<ResponseEntity<Object>>map(strings -> new ResponseEntity<>(strings, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>("No activity exits", HttpStatus.OK));
    }
}
