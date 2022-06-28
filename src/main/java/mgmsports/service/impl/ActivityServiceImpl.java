package mgmsports.service.impl;

import mgmsports.common.exception.EntityNotFoundException;
import mgmsports.common.exception.InvalidFileException;
import mgmsports.common.mapper.ActivityMapper;
import mgmsports.dao.entity.Account;
import mgmsports.dao.entity.Activity;
import mgmsports.dao.entity.CompetitionNotification;
import mgmsports.dao.entity.LocationSuggestion;
import mgmsports.dao.repository.AccountRepository;
import mgmsports.dao.repository.ActivityRepository;
import mgmsports.dao.repository.CompetitionNotificationRepository;
import mgmsports.dao.repository.SuggestionLocationRepository;
import mgmsports.model.ActivityDto;
import mgmsports.model.ActivityInputDto;
import mgmsports.model.ActivityType;
import mgmsports.model.UserStatisticDto;
import mgmsports.service.ActivityService;
import mgmsports.service.CompetitionNotificationService;
import mgmsports.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

import static mgmsports.common.util.TimeUtil.stringToDate;

/**
 * Implement interface Activity Service
 *
 * @author qngo
 */
@Service
public class ActivityServiceImpl implements ActivityService {

    private ActivityRepository activityRepository;
    private AccountRepository accountRepository;
    private CompetitionNotificationRepository competitionNotificationRepository;
    private ActivityMapper activityMapper;
    private FileStorageService activityImageService;
    private SuggestionLocationRepository suggestionLocationRepository;
    private CompetitionNotificationService competitionNotificationService;

    @Autowired
    public ActivityServiceImpl(ActivityRepository activityRepository,
                               AccountRepository accountRepository,
                               CompetitionNotificationRepository competitionNotificationRepository,
                               ActivityMapper activityMapper,
                               @Qualifier("activityImageService") FileStorageService activityImageService,
                               SuggestionLocationRepository suggestionLocationRepository,
                               CompetitionNotificationService competitionNotificationService) {
        this.activityRepository = activityRepository;
        this.accountRepository = accountRepository;
        this.competitionNotificationRepository = competitionNotificationRepository;
        this.activityMapper = activityMapper;
        this.activityImageService = activityImageService;
        this.suggestionLocationRepository = suggestionLocationRepository;
        this.competitionNotificationService = competitionNotificationService;
    }

    /**
     * Get list of activity dto
     *
     * @param accountId account id
     * @return list of activity dto
     */
    @Override
    public List<ActivityDto> getActivitiesByUserOrderByDate(String accountId) throws EntityNotFoundException {
        if (!accountRepository.existsById(accountId)) {
            throw new EntityNotFoundException(Account.class, "accountId", accountId);
        }
        return activityRepository.findActivitiesByAccountId(accountId)
                .stream()
                .map(activityMapper::activityToActivityDto)
                .collect(Collectors.toList());
    }

    /**
     * Change updateActivityStatus field in active table
     *
     * @param activityId activity id
     */
    @Override
    @Transactional
    public void updateActivityStatus(String activityId) throws EntityNotFoundException {
        if (!activityRepository.existsById(activityId)) {
            throw new EntityNotFoundException(Activity.class, "activityId", activityId);
        }
        activityRepository.updateActivityStatus(activityId);
    }

    /**
     * Save enter suggestion
     *
     * @param accountId account id
     * @param location  location of activity
     */
    private void saveSuggestion(String accountId, String location) {
        if (suggestionLocationRepository.checkLocationExists(accountId, location)) {
            LocationSuggestion locationSuggestion = suggestionLocationRepository.findLocationSuggestionByAccount_AccountIdAndSuggestion(accountId, location);
            suggestionLocationRepository.save(locationSuggestion);
        } else {
            LocationSuggestion locationSuggestion = new LocationSuggestion();
            locationSuggestion.setSuggestion(location);
            Account account = accountRepository.getAccountByAccountId(accountId);
            locationSuggestion.setAccount(account);
            suggestionLocationRepository.save(locationSuggestion);
        }
    }

    /**
     * Get array suggestion by account
     *
     * @param accountId account id
     * @return array of suggestion
     */
    @Override
    public List<String> getSuggestionByAccount(String accountId) throws EntityNotFoundException {
        if (!accountRepository.existsById(accountId)) {
            throw new EntityNotFoundException(Account.class, "accountId", accountId);
        }
        List<LocationSuggestion> locationSuggestions = suggestionLocationRepository.findAllByAccount_AccountIdOrderByLastEnterDateDesc(accountId);
        return locationSuggestions.stream().map(LocationSuggestion::getSuggestion).collect(Collectors.toList());
    }

    /**
     * Create an activity
     *
     * @param activityInputDto activity data
     * @throws EntityNotFoundException EntityNotFoundException
     * @throws InvalidFileException InvalidFileException
     * @throws ParseException ParseException
     */
    @Override
    public void createActivity(ActivityInputDto activityInputDto, MultipartFile activityImage) throws EntityNotFoundException, InvalidFileException, ParseException {
        Account account = accountRepository.getAccountByAccountId(activityInputDto.getAccountId());
        if (account == null) {
            throw new EntityNotFoundException(Account.class, "AccountId", activityInputDto.getAccountId());
        }
        Activity activity = customActivityMapper(activityInputDto, account, activityImage);
        activityRepository.save(activity);

        List<CompetitionNotification> competitionNotificationHosts = competitionNotificationRepository.getCompetitionNotificationByHost_AccountIdAndActivity_ActivityId(account.getAccountId(), activity.getActivityId());
        if (!competitionNotificationHosts.isEmpty()) {
            competitionNotificationHosts.forEach(e -> competitionNotificationService.notify("/topic/" + e.getCompetitionId(), e));
        }
        saveSuggestion(activity.getAccount().getAccountId(), activity.getLocation());
    }

    /**
     * Update an activity
     *
     * @param activityInputDto activity data
     * @throws EntityNotFoundException EntityNotFoundException
     * @throws InvalidFileException InvalidFileException
     * @throws ParseException ParseException
     */
    @Override
    public void updateActivity(ActivityInputDto activityInputDto, MultipartFile activityImage) throws EntityNotFoundException, InvalidFileException, ParseException {
        Activity activity = activityRepository.findActivityByActivityId(activityInputDto.getActivityId());
        if (activity == null) {
            throw new EntityNotFoundException(Activity.class, "ActivityId", activityInputDto.getActivityId());
        }
        String fileUri;
        activity.setTitle( activityInputDto.getTitle() );
        activity.setLocation( activityInputDto.getLocation() );
        activity.setDuration( activityInputDto.getDuration() );
        activity.setWorkoutType( activityInputDto.getWorkoutType() );
        activity.setDistance( activityInputDto.getDistance() );
        if ( activityInputDto.getActivityDate() != null ) {
            activity.setActivityDate( stringToDate( activityInputDto.getActivityDate() ) );
        }
        activity.setActivityType( activityInputDto.getActivityType() );
        if (activityImage != null) {
            fileUri = handleImageFile(activity, activityImage);
            activity.setImageLink(fileUri);
        }
        activityRepository.save(activity);
        List<CompetitionNotification> competitionNotificationHosts = competitionNotificationRepository.getCompetitionNotificationByActivity_ActivityIdAndSeenIsFalse(activity.getActivityId());
        if (!competitionNotificationHosts.isEmpty()) {
            competitionNotificationHosts.forEach(e -> competitionNotificationService.notify("/topic/" + e.getCompetitionId(), e));
        }
        saveSuggestion(activity.getAccount().getAccountId(), activity.getLocation());
    }

    /**
     * Get activity type from enum
     *
     * @return list of activity type
     */
    @Override
    public ActivityType[] getActivityType() {
        return ActivityType.class.getEnumConstants();
    }

    @Override
    public UserStatisticDto getUserStatistic(String accountId) {
        UserStatisticDto userStatisticDto = new UserStatisticDto();
        List<Activity> activityList = activityRepository.findActivitiesByAccountIdLast7Day(accountId);
        Double runningDis = 0.0, cyclingDis = 0.0, gymTime = 0.0, hikingTime = 0.0, meditationTime = 0.0,
                yogaTime = 0.0, skatingTime = 0.0, climbingTime = 0.0, swimmingTime = 0.0;
        for (Activity activity : activityList) {
            switch (activity.getActivityType()) {
                case Running:
                    runningDis += activity.getDistance();
                    break;
                case Cycling:
                    cyclingDis += activity.getDistance();
                    break;
                case Yoga:
                    yogaTime += activity.getDuration();
                    break;
                case Hiking:
                    hikingTime += activity.getDuration();
                    break;
                case Skating:
                    skatingTime += activity.getDuration();
                    break;
                case Climbing:
                    climbingTime += activity.getDuration();
                    break;
                case Swimming:
                    swimmingTime += activity.getDuration();
                    break;
                case Meditation:
                    meditationTime += activity.getDuration();
                    break;
                default:
                    gymTime += activity.getDuration();
            }
        }
        userStatisticDto.setCyclingDisTotal(cyclingDis);
        userStatisticDto.setGymTimeTotal(gymTime);
        userStatisticDto.setRunningDisTotal(runningDis);
        userStatisticDto.setClimbingTimeTotal(climbingTime);
        userStatisticDto.setYogaTimeTotal(yogaTime);
        userStatisticDto.setHikingTimeTotal(hikingTime);
        userStatisticDto.setMeditationTimeTotal(meditationTime);
        userStatisticDto.setSwimmingTimeTotal(swimmingTime);
        userStatisticDto.setSkatingTimeTotal(skatingTime);

        return userStatisticDto;
    }

    /**
     * Handle activity image
     *
     * @param activity activity
     * @param activityImage activity image file
     * @return uri
     * @throws InvalidFileException InvalidFileException
     */
    private String handleImageFile(Activity activity, MultipartFile activityImage) throws InvalidFileException {
        if (activityImage == null || activityImage.getOriginalFilename() == null) {
            throw new InvalidFileException("File doesn't exist");
        }
        if (!activityImageService.isValidImageExtension(activityImage.getOriginalFilename().toLowerCase())) {
            throw new InvalidFileException("Must be a image file extension!");
        }
        String newName = activity.getAccount().getAccountId() + activity.getActivityId();
        String fileName = activityImageService.storeFile(activityImage, newName);
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/image/")
                .path(fileName)
                .toUriString();
    }


    private Activity customActivityMapper(ActivityInputDto activityInputDto, Account account, MultipartFile activityImage) throws ParseException, InvalidFileException {
        Activity activity = new Activity();
        activity.setActive(true);
        activity.setAccount(account);
        activity.setActivityDate(stringToDate(activityInputDto.getActivityDate()));
        activity.setDistance(activityInputDto.getDistance());
        activity.setDuration(activityInputDto.getDuration());
        activity.setTitle(activityInputDto.getTitle());
        activity.setLocation(activityInputDto.getLocation());
        activity.setWorkoutType(activityInputDto.getWorkoutType());
        activity.setActivityType(activityInputDto.getActivityType());
        String fileUri = "";
        if (activityImage != null) {
            fileUri = handleImageFile(activity, activityImage);
        }
        activity.setImageLink(fileUri);

        return activity;
    }
}