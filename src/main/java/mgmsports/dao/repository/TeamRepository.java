package mgmsports.dao.repository;

import mgmsports.dao.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Account entity
 *
 * @author dntvo
 */
@Repository
public interface TeamRepository extends JpaRepository<Team, String> {
    /**
     * Get teams joined by a user
     *
     * @param account_id account Id of an account
     * @return team data
     */
    @Query("select a.teams from Account a where a.accountId = :id")
    List<Team> findJoinedTeams(@Param("id") String account_id);

    /**
     * Update team status to database
     *
     * @param team_id team Id of a team
     */
    @Modifying
    @Query("update Team set active = false where teamId = :id")
    void updateTeamStatus(@Param("id") String team_id);

    /**
     * Get team from database by team Id
     *
     * @param teamId teamId
     * @return team data
     */
    Team findTeamByTeamId(String teamId);

    /**
     * Get all teams created by a user
     *
     * @param account_id account Id of a user
     */
    @Query("select t from Team t where t.hostId = :id order by t.createdDate desc")
    List<Team> findOwnedTeams(@Param("id") String account_id);

    /**
     * Get all teams except for team with teamId
     *
     * @param teamId team id
     * @param name name of other teams
     * @return teams except for team with teamId
     */
    List<Team> findTeamsByTeamIdNotLikeAndNameContainsIgnoreCaseOrderByName(String teamId, String name);

    @Query(nativeQuery = true, value = "SELECT EXISTS(SELECT * FROM TEAM_ACCOUNT WHERE TEAM_ID =:teamId AND ACCOUNT_ID = :accountId)")
    boolean isUserExistedInTeam(@Param("teamId") String teamId, @Param("accountId") String accountId);
}