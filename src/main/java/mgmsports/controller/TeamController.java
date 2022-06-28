package mgmsports.controller;

import mgmsports.common.exception.EntityNotFoundException;
import mgmsports.model.TeamAccountDto;
import mgmsports.model.TeamDto;
import mgmsports.model.TeamHeaderDto;
import mgmsports.model.TeamMemberDto;
import mgmsports.model.ProfileAccountDto;
import mgmsports.model.TeamStatisticDto;
import mgmsports.model.TimeInterval;
import mgmsports.model.TeamIdStringsDto;
import mgmsports.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;


import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Team Controller
 *
 * @author dntvo
 */
@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    /**
     * Get list of teams created by a user
     *
     * @param id account ID of a user
     * @return list of team
     */
    @GetMapping("/created")
    public ResponseEntity<Object> getOwnedTeams(@Valid @RequestParam("accountId") String id) throws EntityNotFoundException {
        Optional<List<TeamDto>> teamsOptional = Optional.ofNullable(teamService.getOwnedTeams(id));
        if (teamsOptional.isPresent()) {
            return new ResponseEntity<>(teamsOptional.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("No account exists", HttpStatus.OK);
    }

    /**
     * get list of teams joined by a user
     *
     * @param id account ID of a user
     * @return list of team
     */

    @GetMapping("/joined")
    public ResponseEntity<Object> getJoinedTeams(@Valid @RequestParam("accountId") String id) throws EntityNotFoundException {
        Optional<List<TeamDto>> allTeamsOptional = Optional.ofNullable(teamService.getJoinedTeam(id));
        if (allTeamsOptional.isPresent()) {
            return new ResponseEntity<>(allTeamsOptional.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("No account exists", HttpStatus.OK);
    }

    /**
     * Do soft-delete a team
     *
     * @param id team Id
     */
    @PutMapping("/softdelete")
    public ResponseEntity<Object> softDeleteTeam(@Valid @RequestParam("teamId") String id) throws EntityNotFoundException {
        teamService.updateTeamStatus(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Create a team with teamDto
     *
     * @param teamDto team Dto
     */
    @PostMapping
    public ResponseEntity<Object> createTeam(@Valid @RequestBody TeamDto teamDto) throws EntityNotFoundException {
        teamService.createTeam(teamDto);
        return ResponseEntity.ok().build();
    }

    /**
     * Get Team general information
     *
     * @param teamId team ID
     */
    @GetMapping("/teamHeader/{teamId}")
    public ResponseEntity<Object> getTeamHeader(@Valid @PathVariable("teamId") String teamId) {
        Optional<TeamHeaderDto> teamHeaderDto = Optional.ofNullable(teamService.getTeamHeaderDtoSer(teamId));
        if (teamHeaderDto.isPresent()) {
            return new ResponseEntity<>(teamHeaderDto.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("No account exists", HttpStatus.OK);
    }

    /**
     * Get all team members
     *
     * @param teamId team ID
     */
    @GetMapping("/teamMember/{teamId}")
    public ResponseEntity<Object> getTeamsMembers(@Valid @PathVariable("teamId") String teamId) throws EntityNotFoundException {
        Optional<List<TeamMemberDto>> teamMemberDtos = Optional.ofNullable(teamService.getTeamMembersDtoSer(teamId));
        if (teamMemberDtos.isPresent()) {
            return new ResponseEntity<>(teamMemberDtos.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("No account exists", HttpStatus.OK);
    }

    /**
     * Add a member to a team
     *
     * @param teamAccountDto contains teamId, accountId
     * @throws EntityNotFoundException throws Exception if account or team not found in database
     */
    @PostMapping("/add")
    public ResponseEntity<Object> addMember(@Valid @RequestBody TeamAccountDto teamAccountDto) throws EntityNotFoundException {
        if (teamService.addMemberToTeam(teamAccountDto.getTeamId(), teamAccountDto.getAccountId())) {
            return ResponseEntity.ok().build();
        } else {
            return new ResponseEntity<>("User already in team", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Remove a member from a team
     *
     * @param teamAccountDto contains teamId, accountId
     * @throws EntityNotFoundException throws Exception if account or team not found in database
     */
    @PutMapping("/remove")
    public ResponseEntity<Object> removeMember(@Valid @RequestBody TeamAccountDto teamAccountDto) throws EntityNotFoundException {
        if (teamService.removeMemberFromTeam(teamAccountDto.getTeamId(), teamAccountDto.getAccountId())) {
            return ResponseEntity.ok().build();
        } else {
            return new ResponseEntity<>("Host cannot be removed", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get list account (has fullName like "name") in a team
     *
     * @param teamId id of team
     * @param name fullName
     * @return list account
     * @throws EntityNotFoundException throws Exception if account or team not found in database
     */
    @GetMapping("/member/{id}")
    public ResponseEntity<Object> getAccountsInTeamByTeamId(@PathVariable("id") String teamId, @Valid @RequestParam("fullname") String name) throws EntityNotFoundException {
        Optional<List<ProfileAccountDto>> allAccountsOptional = Optional.ofNullable(teamService.getAccountsInTeamByFullName(teamId, name));
        if (allAccountsOptional.isPresent()) {
            return new ResponseEntity<>(allAccountsOptional.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("No account exists", HttpStatus.OK);
    }

    /**
     * Get list account (has fullName like "name") not in a team
     *
     * @param teamId id of team
     * @param name fullName
     * @return list account
     * @throws EntityNotFoundException throws Exception if account or team not found in database
     */
    @GetMapping("/notmember/{id}")
    public ResponseEntity<Object> getAccountsNotInTeamByTeamIdAndUserName(@PathVariable("id") String teamId, @Valid @RequestParam("fullname") String name) throws EntityNotFoundException {
        Optional<List<ProfileAccountDto>> allAccountsOptional = Optional.ofNullable(teamService.getAccountsNotInTeamByFullName(teamId, name));
        if (allAccountsOptional.isPresent()) {
            return new ResponseEntity<>(allAccountsOptional.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("No account exists", HttpStatus.OK);
    }

    /**
     * Get team statistic by teamId
     *
     * @param id teamId
     * @param timeInterval timeInterval
     * @param timeZoneOffset timeZoneOffset of Client
     * @return teamStatistic
     * @throws EntityNotFoundException throws Exception if team not found in database
     */
    @GetMapping("/statistic/{id}")
    public ResponseEntity<Object> getTeamStatistic(@PathVariable("id") String id,
                                                   @RequestParam("timeinterval") String timeInterval,
                                                   @RequestParam("timezoneoffset") String timeZoneOffset) throws EntityNotFoundException {
        Optional<TeamStatisticDto> teamStatisticDto = Optional.ofNullable(teamService.getTeamStatistic(id, timeInterval, timeZoneOffset));
        if (teamStatisticDto.isPresent()) {
            return new ResponseEntity<>(teamStatisticDto.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("No statistic exists", HttpStatus.OK);
    }

    /**
     * Get 2 team statistics to compare
     *
     * @param id homeTeamId
     * @param competitorId awayTeamId
     * @param timeInterval timeInterval
     * @param timeZoneOffset timeZoneOffset
     * @return 2 teamStatistics
     * @throws EntityNotFoundException throws Exception if team not found in database
     */
    @GetMapping("/compare/{id}")
    public ResponseEntity<Object> getTeamStatisticToCompare(@PathVariable("id") String id,
                                                            @RequestParam("competitor") String competitorId,
                                                            @RequestParam("timeinterval") String timeInterval,
                                                            @RequestParam("timezoneoffset") String timeZoneOffset) throws EntityNotFoundException {
        Optional<List<TeamStatisticDto>> teamStatisticDtos = Optional.ofNullable(teamService.get2TeamStatisticsToCompare(id, competitorId, timeInterval, timeZoneOffset));
        if (teamStatisticDtos.isPresent()) {
            return new ResponseEntity<>(teamStatisticDtos.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("No statistics exists", HttpStatus.OK);
    }

    /**
     * Get other teams
     *
     * @param id team id
     * @param name other team's name
     * @return other teams info
     * @throws EntityNotFoundException throws Exception if team not found in database
     */
    @GetMapping("/{id}/otherteams")
    public ResponseEntity<Object> getOtherTeams(@PathVariable("id") String id, @RequestParam("name") String name) throws EntityNotFoundException {
        Optional<List<TeamHeaderDto>> otherTeams = Optional.ofNullable(teamService.getOtherTeams(id, name));
        if (otherTeams.isPresent()) {
            return new ResponseEntity<>(otherTeams.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("No other teams exists", HttpStatus.OK);
    }
    /**
     * Get other teams id
     *
     * @param id team id
     * @return other teams's id
     * @throws EntityNotFoundException throws Exception if team not found in database
     */
    @GetMapping("/{id}/otherteamsid")
    public ResponseEntity<Object> getOtherTeamsId(@PathVariable("id") String id) throws EntityNotFoundException {
        Optional<List<TeamIdStringsDto>> otherTeams = Optional.ofNullable(teamService.getOtherTeamId(id));
        if (otherTeams.isPresent()) {
            return new ResponseEntity<>(otherTeams.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("No other teams exists", HttpStatus.OK);
    }

    /**
     * Get time intervals
     *
     * @return time intervals
     */
    @GetMapping("/timeinterval")
    public ResponseEntity<Object> getTimeInterval() {
        Optional<TimeInterval[]> timeIntervals = Optional.ofNullable(teamService.getTimeInterval());
        if (timeIntervals.isPresent()) {
            return new ResponseEntity<>(timeIntervals.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("No time intervals exists", HttpStatus.OK);
    }

    @GetMapping("/isUserExistedInTeam/{teamId}")
    public ResponseEntity<Boolean> isUserExistedInTeam(@PathVariable("teamId") String teamId, @Valid @RequestParam("accountId") String accountId) {
        boolean isUserExistedInTeam = teamService.isUserExistedInTeam(teamId, accountId);
        if (isUserExistedInTeam) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        return new ResponseEntity<>(false, HttpStatus.OK);
    }
}