package mgmsports.service.impl;

import mgmsports.common.exception.EntityNotFoundException;
import mgmsports.common.mapper.CompetitionMapper;
import mgmsports.dao.entity.Account;
import mgmsports.dao.entity.Competition;
import mgmsports.dao.entity.CompetitionNotification;
import mgmsports.dao.repository.AccountRepository;
import mgmsports.dao.repository.CompetitionNotificationRepository;
import mgmsports.dao.repository.CompetitionRepository;
import mgmsports.model.CompetitionDto;
import mgmsports.service.CompetitionNotificationService;
import mgmsports.service.ActivityService;
import mgmsports.service.CompetitionService;
import mgmsports.service.ProfileService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implement interface Competition Service
 *
 * @author dntvo
 */
@Service
public class CompetitionServiceImpl implements CompetitionService {

    private CompetitionRepository competitionRepository;
    private AccountRepository accountRepository;
    private CompetitionMapper competitionMapper;
    private CompetitionNotificationRepository competitionNotificationRepository;
    private CompetitionNotificationService competitionNotificationService;
    private ActivityService activityService;
    private ProfileService profileService;

    public CompetitionServiceImpl(CompetitionRepository competitionRepository,
                                  AccountRepository accountRepository,
                                  CompetitionMapper competitionMapper,
                                  CompetitionNotificationRepository competitionNotificationRepository,
                                  CompetitionNotificationService competitionNotificationService,
                                  ActivityService activityService,
                                  ProfileService profileService) {
        this.competitionRepository = competitionRepository;
        this.accountRepository = accountRepository;
        this.competitionMapper = competitionMapper;
        this.competitionNotificationRepository = competitionNotificationRepository;
        this.competitionNotificationService = competitionNotificationService;
        this.activityService = activityService;
        this.profileService = profileService;
    }

    /**
     * Create a new competition
     *
     * @param competitionDto competition Dto
     */
    @Override
    public void createCompetition(CompetitionDto competitionDto) throws EntityExistsException {
        if (competitionDto.getHostId().equals(competitionDto.getInviteeId())) {
            throw new EntityExistsException("Cannot create a competition with one account");
        } else {
            Account hostAccount = accountRepository.getAccountByAccountId(competitionDto.getHostId());
            Account inviteeAccount = accountRepository.getAccountByAccountId(competitionDto.getInviteeId());
            if (competitionRepository.checkCompetitionExist(competitionDto.getHostId(), competitionDto.getInviteeId())) {
                throw new EntityExistsException("Competition has already existed");
            } else {
                Competition competition = new Competition();
                competition.setHost(hostAccount);
                competition.setInvitee(inviteeAccount);
                CompetitionMapper.INSTANCE.competitionDtoToCompetition(competitionDto, competition);
                hostAccount.getListHosts().add(competition);
                inviteeAccount.getListInvitees().add(competition);
                competitionRepository.save(competition);

                competitionNotificationService.notify("/topic/" + competition.getCompetitionId() + hostAccount.getAccountId(), null, hostAccount); // topic of invitee and host will subscribe this
                competitionNotificationService.notify("/topic/" + competition.getCompetitionId() + inviteeAccount.getAccountId(), null, inviteeAccount); // topic of host and invitee will subscribe this
            }
        }
    }

    /**
     * Delete an existing competition
     *
     * @param competitionId competition id
     */
    @Override
    public void removeCompetition(String competitionId) throws EntityNotFoundException {
        if (competitionRepository.existsById(competitionId)) {
            competitionRepository.removeCompetition(competitionId);
        } else
            throw new EntityNotFoundException(Competition.class, "competitionId", competitionId);
    }

    /**
     * Get list of competition dto
     *
     * @param accountId account id of an account
     * @return list of competition dto
     */
    @Override
    public List<CompetitionDto> getCompetition(String accountId) throws EntityNotFoundException {
        Account account = accountRepository.getAccountByAccountId(accountId);
        if (account == null) {
            throw new EntityNotFoundException(Account.class, "accountId", accountId);
        }
        List<CompetitionDto> listCompetitions = competitionRepository.getCompetitionByAccountId(accountId).stream()
                .map(competitionMapper::competitionToCompetitionDto).collect(Collectors.toList());
        listCompetitions.forEach(competitionDto -> {
            try {
                competitionDto.getHost().setStatisticDto(activityService.getUserStatistic(competitionDto.getHostId()));
                competitionDto.getHost().setProfileDto(profileService.getProfileByAccountId(competitionDto.getHostId()));
                competitionDto.getInvitee().setStatisticDto(activityService.getUserStatistic(competitionDto.getInviteeId()));
                competitionDto.getInvitee().setProfileDto(profileService.getProfileByAccountId(competitionDto.getInviteeId()));
            } catch (EntityNotFoundException e) {
                e.printStackTrace();
            }
        });
        return listCompetitions;
    }
}