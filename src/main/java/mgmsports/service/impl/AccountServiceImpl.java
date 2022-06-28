package mgmsports.service.impl;

import lombok.extern.slf4j.Slf4j;
import mgmsports.common.exception.EntityNotFoundException;
import mgmsports.common.mapper.AccountMapper;
import mgmsports.dao.entity.Account;
import mgmsports.dao.entity.Activity;
import mgmsports.dao.entity.Profile;
import mgmsports.dao.repository.AccountRepository;
import mgmsports.dao.repository.ActivityRepository;
import mgmsports.dao.repository.ProfileRepository;
import mgmsports.model.IndividualUserStatisticDto;
import mgmsports.model.ProfileAccountDto;
import mgmsports.model.TimeInterval;
import mgmsports.security.MgmSportsPrincipal;
import mgmsports.service.AccountService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static mgmsports.common.util.TimeIntervalUtil.getClientCurrentLocalDateFromDate;
import static mgmsports.common.util.TimeUtil.dateToString;
import static mgmsports.common.util.TimeUtil.getClientCurrentLocalTime;
import static mgmsports.common.util.TimeUtil.serverLocalTimeToClientLocalTime;

/**
 * Implement interface Account Service
 *
 * @author mctran
 */

@Slf4j
@Service
@Qualifier("AccountService")
public class AccountServiceImpl implements AccountService {

    private AccountMapper accountMapper;
    private AccountRepository accountRepository;
    private ProfileRepository profileRepository;
    private ActivityRepository activityRepository;

    public AccountServiceImpl(AccountMapper accountMapper,
                              AccountRepository accountRepository,
                              ProfileRepository profileRepository,
                              ActivityRepository activityRepository) {
        this.accountMapper = accountMapper;
        this.accountRepository = accountRepository;
        this.profileRepository = profileRepository;
        this.activityRepository = activityRepository;
    }

    /**
     * Get list of profileAccountDto
     *
     * @param username user_name
     * @return ist of profileAccountDto
     *
     */
    @Override
    public List<ProfileAccountDto> getAllUsersByUserName(String username) {
        List<Account> accounts = accountRepository.findAllByUserNameContainingIgnoreCase(username.toLowerCase());
        List<Profile> profiles = new ArrayList<>();
        accounts.forEach(account -> profiles.add(profileRepository.findProfileByAccount_AccountId(account.getAccountId())));
        return IntStream.range(0, accounts.size())
                .mapToObj(i -> accountMapper.profileAndAcountToProfileAccountDto(accounts.get(i), profiles.get(i)))
                .collect(Collectors.toList());
    }

    /**
     * Find account by social id
     *
     * @param socialId social id
     * @return optional account object
     */
    @Override
    public Optional<Account> findUserBySocialId(String socialId) {
        return accountRepository.findAccountBySocialId(socialId);
    }

    /**
     * Find account by username
     *
     * @param userName username
     * @return  optional account object
     */
    @Override
    public Optional<Account> findUserByUserName(String userName) {
        return accountRepository.findAccountByUserName(userName);
    }

    @Override
    public Optional<Account> findUserByUserId(String userId) {
        return accountRepository.findAccountByAccountId(userId);
    }

    @Override
    public ProfileAccountDto findProfileUserByUserId(String userId) {
        Account account = accountRepository.getAccountByAccountId(userId);
        Profile profile = profileRepository.findProfileByAccount_AccountId(userId);
        return accountMapper.profileAndAcountToProfileAccountDto(account, profile);
    }

    /**
     * Save account to database
     *
     * @param account account data
     */
    @Override
    public void saveUser(Account account) {
        accountRepository.save(account);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user having username: " + username);

        // delegates to findUserByUsername
        Account user = findUserByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        log.debug("Loaded user having username: " + username);

        return new MgmSportsPrincipal(user.toAccountDto());
    }

    /**
     * * Get User statistic
     *
     * @param accountId account id
     * @param timeInterval time interval
     * @param timeZoneOffset time zone offset
     * @return user statistic
     * @throws EntityNotFoundException throws Exception if account not found in database
     */
    @Override
    public IndividualUserStatisticDto getUserStatistic(String accountId, String timeInterval, String timeZoneOffset) throws EntityNotFoundException {
        int activeDayTotal;
        double activityTimeTotal = 0.0;
        double runningDistanceTotal = 0.0;
        double cyclingDistanceTotal = 0.0;
        double cyclingTimeTotal = 0.0;
        double runningTimeTotal = 0.0;
        double meditationTimeTotal = 0.0;
        double climbingTimeTotal = 0.0;
        double skatingTimeTotal = 0.0;
        double swimmingTimeTotal = 0.0;
        double yogaTimeTotal = 0.0;
        double hikingTimeTotal = 0.0;
        double gymTimeTotal = 0.0;
        Date registerDate;
        int runningRating;
        int runningRank;
        int totalActiveRunningMember;

        int clientTimeZoneOffset = Integer.parseInt(timeZoneOffset);
        TimeInterval timeIntervalValue = TimeInterval.values()[Integer.parseInt(timeInterval)];
        Date endDate = getClientCurrentLocalTime(clientTimeZoneOffset);
        Date beginDate = getClientCurrentLocalDateFromDate(timeIntervalValue, clientTimeZoneOffset);

        Account account = accountRepository.getAccountByAccountId(accountId);
        IndividualUserStatisticDto individualUserStatisticDto = new IndividualUserStatisticDto();
        if (account == null) {
            throw new EntityNotFoundException(Account.class, "accountId");
        } else {
            activeDayTotal = activityRepository.getNumberOfDaysEngageActivity(accountId, beginDate, endDate);
            registerDate = serverLocalTimeToClientLocalTime(account.getCreatedDate(), clientTimeZoneOffset);
            try {
                runningRating = activityRepository.getUserRunningRating(accountId, beginDate, endDate);
                individualUserStatisticDto.setRunningDistanceRating(runningRating);
            } catch (Exception e) {
                individualUserStatisticDto.setRunningDistanceRating(0);
            }
            try {
                runningRank = activityRepository.getUserRunningRank(accountId, beginDate, endDate);
                totalActiveRunningMember = accountRepository.getNumberOfActiveRunningMembers(beginDate, endDate);
                individualUserStatisticDto.setRunningRank(String.valueOf(runningRank) + "/" + String.valueOf(totalActiveRunningMember));
            } catch (Exception e) {
                individualUserStatisticDto.setRunningRank("-");
            }
            for (Activity activity : activityRepository.findActivitiesByAccountAndActivityDateBetweenAndIsActive(account, beginDate, endDate, true)) {
                activityTimeTotal += activity.getDuration();
                switch (activity.getActivityType()) {
                    case Running:
                        runningDistanceTotal += activity.getDistance();
                        runningTimeTotal += activity.getDuration();
                        break;
                    case Cycling:
                        cyclingDistanceTotal += activity.getDistance();
                        cyclingTimeTotal += activity.getDuration();
                        break;
                    case Climbing:
                        climbingTimeTotal += activity.getDuration();
                        break;
                    case Skating:
                        skatingTimeTotal += activity.getDuration();
                        break;
                    case Swimming:
                        swimmingTimeTotal += activity.getDuration();
                        break;
                    case Gym:
                        gymTimeTotal += activity.getDuration();
                        break;
                    case Hiking:
                        hikingTimeTotal += activity.getDuration();
                        break;
                    case Yoga:
                        yogaTimeTotal += activity.getDuration();
                        break;
                    case Meditation:
                        meditationTimeTotal += activity.getDuration();
                        break;
                }
            }
        }
        individualUserStatisticDto.setActiveDayTotal(activeDayTotal);
        individualUserStatisticDto.setBeginDate(dateToString(beginDate));
        individualUserStatisticDto.setEndDate(dateToString(endDate));
        individualUserStatisticDto.setRegisterDate(dateToString(registerDate));
        individualUserStatisticDto.setRunningDistanceTotal(runningDistanceTotal);
        individualUserStatisticDto.setCyclingDistanceTotal(cyclingDistanceTotal);
        individualUserStatisticDto.setClimbingTimeTotal(climbingTimeTotal);
        individualUserStatisticDto.setHikingTimeTotal(hikingTimeTotal);
        individualUserStatisticDto.setSkatingTimeTotal(skatingTimeTotal);
        individualUserStatisticDto.setSwimmingTimeTotal(swimmingTimeTotal);
        individualUserStatisticDto.setYogaTimeTotal(yogaTimeTotal);
        individualUserStatisticDto.setMeditationTimeTotal(meditationTimeTotal);
        individualUserStatisticDto.setGymTimeTotal(gymTimeTotal);
        individualUserStatisticDto.setActivityTimeTotal(activityTimeTotal);
        individualUserStatisticDto.setRunningTimeTotal(runningTimeTotal);
        individualUserStatisticDto.setCyclingTimeTotal(cyclingTimeTotal);

        return individualUserStatisticDto;
    }

}