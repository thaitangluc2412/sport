package mgmsports.service.impl;

import mgmsports.common.exception.EntityNotFoundException;
import mgmsports.common.mapper.ProfileMapper;
import mgmsports.common.mapper.TeamHeaderMapper;
import mgmsports.common.mapper.TeamMapper;
import mgmsports.common.mapper.TeamMemberMapper;
import mgmsports.common.util.TimeIntervalUtil;
import mgmsports.dao.entity.Account;
import mgmsports.dao.entity.Activity;
import mgmsports.dao.entity.Profile;
import mgmsports.dao.entity.Team;
import mgmsports.dao.repository.AccountRepository;
import mgmsports.dao.repository.ActivityRepository;
import mgmsports.dao.repository.ProfileRepository;
import mgmsports.dao.repository.TeamRepository;
import mgmsports.model.ActivityType;
import mgmsports.model.ProfileAccountDto;
import mgmsports.model.TeamDto;
import mgmsports.model.TeamHeaderDto;
import mgmsports.model.TeamIdStringsDto;
import mgmsports.model.TeamMemberDto;
import mgmsports.model.TeamStatisticDto;
import mgmsports.model.TimeInterval;
import mgmsports.service.ProfileService;
import mgmsports.service.TeamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static mgmsports.common.util.TimeUtil.dateToString;
import static mgmsports.common.util.TimeUtil.getClientCurrentLocalTime;

/**
 * Implement interface Activity Service
 *
 * @author dntvo
 */
@Service
public class TeamServiceImpl implements TeamService {

    private TeamRepository teamRepository;
    private TeamMapper teamMapper;
    private AccountRepository accountRepository;
    private ProfileRepository profileRepository;
    private ProfileService profileService;
    private TeamHeaderMapper teamHeaderMapper;
    private ProfileMapper profileMapper;
    private TeamMemberMapper teamMemberMapper;
    private ActivityRepository activityRepository;

    public TeamServiceImpl(
            TeamRepository teamRepository,
            TeamMemberMapper teamMemberMapper,
            ProfileMapper profileMapper,
            TeamMapper teamMapper,
            AccountRepository accountRepository,
            ProfileService profileService,
            TeamHeaderMapper teamHeaderMapper,
            ProfileRepository profileRepository,
            ActivityRepository activityRepository
    ) {
        this.teamRepository = teamRepository;
        this.teamMapper = teamMapper;
        this.accountRepository = accountRepository;
        this.profileRepository = profileRepository;
        this.teamHeaderMapper = teamHeaderMapper;
        this.profileMapper = profileMapper;
        this.profileService = profileService;
        this.teamMemberMapper = teamMemberMapper;
        this.activityRepository = activityRepository;
    }

    /**
     * Get list of team dto which is created by a user
     *
     * @param accountId account id of an account
     * @return list of team dto
     */
    @Override
    public List<TeamDto> getOwnedTeams(String accountId) throws EntityNotFoundException {
        Account account = accountRepository.getAccountByAccountId(accountId);
        if (account == null)
            throw new EntityNotFoundException(Account.class, "accountId", accountId);
        return teamRepository.findOwnedTeams(accountId)
                .stream().filter(i -> i.isActive())
                .map(teamMapper::teamToTeamDto)
                .collect(Collectors.toList());
    }

    /**
     * Get list of team dto which is joined by a user
     *
     * @param accountId account id of an account
     * @return list of team dto
     */
    @Override
    public List<TeamDto> getJoinedTeam(String accountId) throws EntityNotFoundException {
        Account account = accountRepository.getAccountByAccountId(accountId);
        if (account == null)
            throw new EntityNotFoundException(Account.class, "accountId", accountId);
        return teamRepository.findJoinedTeams(accountId)
                .stream().filter(i -> i.isActive()).filter(i -> !i.getHostId().equals(accountId))
                .map(teamMapper::teamToTeamDto)
                .collect(Collectors.toList());
    }

    /**
     * Update status of a team
     *
     * @param teamId team Id of a team to delete
     * @return true if update successfully/false if not
     */
    @Override
    @Transactional
    public void updateTeamStatus(String teamId) throws EntityNotFoundException {
        if (teamRepository.existsById(teamId))
            teamRepository.updateTeamStatus(teamId);
        else
            throw new EntityNotFoundException(Team.class, "teamId", teamId);
    }

    /**
     * Create a team
     *
     * @param TeamDto team Dto to map to Team
     */
    @Override
    public void createTeam(TeamDto TeamDto) throws EntityNotFoundException {
        Account account = accountRepository.getAccountByAccountId(TeamDto.getHostId());
        if (account == null) {
            throw new EntityNotFoundException(Account.class, "HostId", TeamDto.getHostId());
        } else {
            Team team = new Team();
            team.getAccounts().add(account);
            TeamMapper.INSTANCE.updateTeamFromTeamDto(TeamDto, team);
            team.setActive(true);
            account.getTeams().add(team);
            teamRepository.save(team);
        }
    }

    /**
     * Add a member into a team after making sure that team and account are valid and account is still not a member of the team
     *
     * @param teamId    Id of team
     * @param accountId Id of account
     * @return true if succeed, false if not
     * @throws EntityNotFoundException throws Exception if account or team not found in database
     */
    @Override
    public boolean addMemberToTeam(String teamId, String accountId) throws EntityNotFoundException {
        Account account = accountRepository.getAccountByAccountId(accountId);
        Team team;
        if (account == null) {
            throw new EntityNotFoundException(Account.class, "AccountId", accountId);
        } else {
            team = teamRepository.findTeamByTeamId(teamId);
            if (team == null) {
                throw new EntityNotFoundException(Team.class, "TeamId", teamId);
            } else {
                for (Account account1 : team.getAccounts()) {
                    if (accountId.equals(account1.getAccountId())) {
                        return false;
                    }
                }
                team.getAccounts().add(account);
                teamRepository.save(team);
                return true;
            }
        }
    }

    /**
     * Remove a member from a team
     *
     * @param teamId    Id of team
     * @param accountId Id of account
     * @return true if succeed, false if not
     * @throws EntityNotFoundException throws Exception if account or team not found in database
     */
    @Override
    public boolean removeMemberFromTeam(String teamId, String accountId) throws EntityNotFoundException {
        Account account = accountRepository.getAccountByAccountId(accountId);
        Team team;
        if (account == null) {
            throw new EntityNotFoundException(Account.class, "AccountID", accountId);
        } else {
            team = teamRepository.findTeamByTeamId(teamId);
            if (team == null) {
                throw new EntityNotFoundException(Team.class, "TeamId", teamId);
            } else {
                if (!accountId.equals(team.getHostId())) {
                    team.getAccounts().remove(account);
                    teamRepository.save(team);
                    return true;
                }
                return false;
            }
        }
    }

    /**
     * Get list account (has fullName like "fullName") in a team
     *
     * @param teamId teamId
     * @param fullName fullName of profile
     * @return list account
     * @throws EntityNotFoundException throws Exception if team not found in database
     */
    @Override
    public List<ProfileAccountDto> getAccountsInTeamByFullName(String teamId, String fullName) throws EntityNotFoundException {
        Team team = teamRepository.findTeamByTeamId(teamId);
        List<ProfileAccountDto> list = new ArrayList<>();
        if (team == null) {
            throw new EntityNotFoundException(Team.class, "TeamId", teamId);
        } else {
            return mapper(profileRepository.findProfilesByAccount_TeamsAndFullNameContainsIgnoreCase(team, fullName));
        }
    }

    /**
     * Get list account (has fullName like "fullName") in not a team
     *
     * @param teamId teamId
     * @param fullName fullName of profile
     * @return list account
     * @throws EntityNotFoundException throws Exception if team not found in database
     */
    @Override
    public List<ProfileAccountDto> getAccountsNotInTeamByFullName(String teamId, String fullName) throws EntityNotFoundException {
        Team team = teamRepository.findTeamByTeamId(teamId);
        List<ProfileAccountDto> list = new ArrayList<>();
        if (team == null) {
            throw new EntityNotFoundException(Team.class, "TeamId", teamId);
        } else {
            return mapper(profileRepository.findProfilesByAccount_TeamsNotContainsAndFullNameContainsIgnoreCase(team, fullName));
        }
    }

    /**
     * Get team statistic
     *
     * @param teamId teamId
     * @param timeInterval timeInterval
     * @param timeZoneOffset timeZoneOffset of Client
     * @return teamStatisticDto
     * @throws EntityNotFoundException throws Exception if team not found in database
     */
    @Override
    public TeamStatisticDto getTeamStatistic(String teamId, String timeInterval, String timeZoneOffset) throws EntityNotFoundException {
        int totalMember = 0;
        List<Account> activeAccounts;
        double totalDurationTime = 0.0;
        double totalRunningDistance = 0.0;
        double totalCyclingDistance = 0.0;
        double totalGymTime = 0.0;
        double totalMeditationTime = 0.0;
        double totalClimbingTime = 0.0;
        double totalSkatingTime = 0.0;
        double totalSwimmingTime = 0.0;
        double totalYogaTime = 0.0;
        double totalHikingTime = 0.0;
        Date dateBegin;
        Date today = getClientCurrentLocalTime(Integer.parseInt(timeZoneOffset));

        Team team = teamRepository.findTeamByTeamId(teamId);

        if (team == null) {
            throw new EntityNotFoundException(Team.class, "TeamId", teamId);
        } else {
            TimeInterval timeIntervalValue = TimeInterval.values()[Integer.parseInt(timeInterval)];
            dateBegin = TimeIntervalUtil.getClientCurrentLocalDateFromDate(timeIntervalValue, Integer.parseInt(timeZoneOffset));
            totalMember = accountRepository.findAccountsByTeamsContains(team).size();

            for (Activity activity : activityRepository.findActivitiesByAccount_TeamsAndActivityDateBetweenAndIsActive(team, dateBegin, today, true)) {
                totalDurationTime += activity.getDuration();
                if (activity.getActivityType().equals(ActivityType.Running)) {
                    totalRunningDistance += activity.getDistance();
                }
                if (activity.getActivityType().equals(ActivityType.Cycling)) {
                    totalCyclingDistance += activity.getDistance();
                }
                if (activity.getActivityType().equals(ActivityType.Climbing)) {
                    totalClimbingTime += activity.getDuration();
                }
                if (activity.getActivityType().equals(ActivityType.Hiking)) {
                    totalHikingTime += activity.getDuration();
                }
                if (activity.getActivityType().equals(ActivityType.Skating)) {
                    totalSkatingTime += activity.getDuration();
                }
                if (activity.getActivityType().equals(ActivityType.Swimming)) {
                    totalSwimmingTime += activity.getDuration();
                }
                if (activity.getActivityType().equals(ActivityType.Yoga)) {
                    totalYogaTime += activity.getDuration();
                }
                if (activity.getActivityType().equals(ActivityType.Meditation)) {
                    totalMeditationTime += activity.getDuration();
                }
                if (activity.getActivityType().equals(ActivityType.Gym)) {
                    totalGymTime += activity.getDuration();
                }
            }
            activeAccounts = accountRepository.findDistinctAccountsByActivities_ActivityDateAfterAndTeamsContainsAndActivities_IsActive(dateBegin, team, true);
            TeamStatisticDto teamStatisticDto = new TeamStatisticDto();
            teamStatisticDto.setTeamName(team.getName());
            teamStatisticDto.setMemberTotal(String.valueOf(totalMember));
            teamStatisticDto.setActiveMemberTotal(String.valueOf(activeAccounts.size()));
            teamStatisticDto.setActivityTimeTotal(String.valueOf(totalDurationTime));
            teamStatisticDto.setRunningDistanceTotal(String.valueOf(totalRunningDistance));
            teamStatisticDto.setCyclingDistanceTotal(String.valueOf(totalCyclingDistance));
            teamStatisticDto.setMeditationTimeTotal(String.valueOf(totalMeditationTime));
            teamStatisticDto.setClimbingTimeTotal(String.valueOf(totalClimbingTime));
            teamStatisticDto.setSkatingTimeTotal(String.valueOf(totalSkatingTime));
            teamStatisticDto.setSwimmingTimeTotal(String.valueOf(totalSwimmingTime));
            teamStatisticDto.setYogaTimeTotal(String.valueOf(totalYogaTime));
            teamStatisticDto.setGymTimeTotal(String.valueOf(totalGymTime));
            teamStatisticDto.setHikingTimeTotal(String.valueOf(totalHikingTime));
            teamStatisticDto.setBeginDate(dateToString(dateBegin));
            teamStatisticDto.setEndDate(dateToString(today));
            System.out.println(totalDurationTime);
            return teamStatisticDto;
        }
    }

    /**
     * Get 2 team statistics to compare
     *
     * @param homeTeamId homeTeamId
     * @param awayTeamId awayTeamId
     * @param timeInterval timeInterval
     * @param timeZoneOffset timeZoneOffset of Client
     * @return 2 teamStatistics
     * @throws EntityNotFoundException throws Exception if team not found in database
     */
    @Override
    public List<TeamStatisticDto> get2TeamStatisticsToCompare(String homeTeamId, String awayTeamId, String timeInterval, String timeZoneOffset) throws EntityNotFoundException {
        List<TeamStatisticDto> list = new ArrayList<>();

        list.add(getTeamStatistic(homeTeamId, timeInterval, timeZoneOffset));
        list.add(getTeamStatistic(awayTeamId, timeInterval, timeZoneOffset));

        return list;
    }

    /**
     * Get other teams
     *
     * @param teamId team id
     * @param name name of other teams
     * @return other teams
     * @throws EntityNotFoundException throws Exception if team not found in database
     */
    @Override
    public List<TeamHeaderDto> getOtherTeams(String teamId, String name) throws EntityNotFoundException {
        Team team = teamRepository.findTeamByTeamId(teamId);
        List<TeamHeaderDto> teamHeaderDtos = new ArrayList<>();
        if (team == null) {
            throw new EntityNotFoundException(Team.class, "TeamId", teamId);
        }
        for (Team otherTeam : teamRepository.findTeamsByTeamIdNotLikeAndNameContainsIgnoreCaseOrderByName(teamId, name)) {
            TeamHeaderDto teamHeaderDto = new TeamHeaderDto();
            teamHeaderDto.setName(otherTeam.getName());
            teamHeaderDto.setHostId(otherTeam.getHostId());
            Profile profile = profileRepository.findProfileByAccount_AccountId(otherTeam.getHostId());
            teamHeaderDto.setFullName(profile.getFullName());
            teamHeaderDto.setImageLink(profile.getImageLink());

            teamHeaderDtos.add(teamHeaderDto);

        }
        return teamHeaderDtos;
    }

    /**
     * Get list team id
     *
     * @param teamId team id
     * @return list team id
     * @throws EntityNotFoundException throws Exception if team not found in database
     */
    @Override
    public List<TeamIdStringsDto> getOtherTeamId(String teamId) throws EntityNotFoundException {
        Team team = teamRepository.findTeamByTeamId(teamId);
        List<TeamIdStringsDto> list = new ArrayList<>();
        if (team == null) {
            throw new EntityNotFoundException(Team.class, "TeamId", teamId);
        }
        for (Team otherTeam : teamRepository.findTeamsByTeamIdNotLikeAndNameContainsIgnoreCaseOrderByName(teamId, "")) {
            list.add(new TeamIdStringsDto(otherTeam.getTeamId()));
        }
        return list;
    }

    /**
     * Get all time intervals
     *
     * @return Array of time interval
     */
    @Override
    public TimeInterval[] getTimeInterval() {
        return TimeInterval.class.getEnumConstants();
    }

    /**
     * Get team general information
     *
     * @param teamId team ID
     * @return general information of a team
     */
    @Override
    public TeamHeaderDto getTeamHeaderDtoSer(String teamId) {
        Team team = teamRepository.findTeamByTeamId(teamId);
        String accountId = team.getHostId();
        Profile hostProfile = profileMapper.profileDtoToProfile(profileService.getOneProfileByAccountId(accountId));
        return teamHeaderMapper.teamAndProfileDtoToTeamHeaderDto(team, hostProfile);
    }

    /**
     * Get all accounts of team
     *
     * @param teamId team ID
     * @return all members of a team
     */
    @Override
    public List<TeamMemberDto> getTeamMembersDtoSer(String teamId) {
        Team team = teamRepository.findTeamByTeamId(teamId);
        List<Account> accounts = accountRepository.findAccountsByTeamsContains(team);
        List<Profile> profiles = new ArrayList<>();
        accounts.forEach(account -> profiles.add(profileMapper.profileDtoToProfile(profileService.getOneProfileByAccountId(account.getAccountId()))));

        return IntStream.range(0, accounts.size())
                .mapToObj(i -> teamMemberMapper.accountAndProfileToTeamMemberDto(accounts.get(i), profiles.get(i)))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isUserExistedInTeam(String teamId, String accountId) {
        return (teamRepository.isUserExistedInTeam(teamId, accountId));
    }

    private List<ProfileAccountDto> mapper(List<Profile> listSrc) {
        List<ProfileAccountDto> listDes = new ArrayList<>();
        for (Profile profile : listSrc) {
            ProfileAccountDto profileAccountDto = new ProfileAccountDto();
            profileAccountDto.setAccountId(profile.getAccount().getAccountId());
            profileAccountDto.setEmail(profile.getAccount().getEmail());
            profileAccountDto.setFullName(profile.getFullName());
            profileAccountDto.setImageLink(profile.getImageLink());
            listDes.add(profileAccountDto);
        }
        return listDes;
    }
}