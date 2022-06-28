package mgmsports.service;

import mgmsports.common.exception.EntityNotFoundException;
import mgmsports.model.ProfileAccountDto;
import mgmsports.model.TeamDto;
import mgmsports.model.TeamHeaderDto;
import mgmsports.model.TeamIdStringsDto;
import mgmsports.model.TeamMemberDto;
import mgmsports.model.TeamStatisticDto;
import mgmsports.model.TimeInterval;

import java.util.List;

/**
 * Activity interface
 *
 * @author dntvo
 */
public interface TeamService {

    List<TeamDto> getOwnedTeams (String userId) throws EntityNotFoundException;

    List<TeamDto> getJoinedTeam(String userId) throws EntityNotFoundException;

    void updateTeamStatus(String teamId) throws EntityNotFoundException;

    void createTeam(TeamDto teamDto) throws EntityNotFoundException;

    TeamHeaderDto getTeamHeaderDtoSer (String teamId);

    List<TeamMemberDto> getTeamMembersDtoSer (String teamId);

    boolean addMemberToTeam(String teamId, String accountId) throws EntityNotFoundException;

    boolean removeMemberFromTeam(String teamId, String accountId) throws EntityNotFoundException;

    List<ProfileAccountDto> getAccountsInTeamByFullName(String teamId, String fullName) throws EntityNotFoundException;

    List<ProfileAccountDto> getAccountsNotInTeamByFullName(String teamId, String userName) throws EntityNotFoundException;

    TeamStatisticDto getTeamStatistic(String teamId, String timeInterval, String timeZoneOffset) throws EntityNotFoundException;

    List<TeamStatisticDto> get2TeamStatisticsToCompare(String homeTeamId, String awayTeamId, String timeInterval, String timeZoneOffset) throws EntityNotFoundException;

    List<TeamHeaderDto> getOtherTeams(String teamId, String name) throws EntityNotFoundException;

    TimeInterval[] getTimeInterval();

    List<TeamIdStringsDto> getOtherTeamId(String teamId) throws EntityNotFoundException;

    boolean isUserExistedInTeam(String teamId, String accountId);
}