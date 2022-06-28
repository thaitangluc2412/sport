package mgmsports.dao.repository;

import mgmsports.dao.entity.Profile;
import mgmsports.dao.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Profile Repository
 *
 * @author mctran
 */

public interface ProfileRepository extends JpaRepository<Profile, String> {

    /**
     * Get user profile from database by accountId
     *
     * @param accountId account_id
     * @return user profile
     */
    Profile findProfileByAccount_AccountId(String accountId);

    /**
     * Get profiles from database
     *
     * @param name full_name
     * @return list of profile
     */
    @Query(nativeQuery = true, value = "select * from Profile a where LOWER(unaccent_replace(a.full_name)) like %?1%")
    List<Profile> findAllByFullNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Get list of profiles contain name without this account
     *
     * @param fullName  fullname
     * @param accountId account_id
     * @return list of profile
     */
    @Query(nativeQuery = true, value = "select * from Profile p where " +
            "p.account_id not in (select account_id from account where account_id = :id) " +
            "and p.account_id not in (select invitee_id from competition c where c.host_id = :id) " +
            "and p.account_id not in (select host_id from competition c where c.invitee_id = :id) " +
            "and LOWER(unaccent_replace(full_name)) like %:name% ")
    List<Profile> findProfilesByAccount_AccountIdNotContainsAndFullNameContainsIgnoreCase(@Param("name") String fullName, @Param("id") String accountId);

    /**
     * Get list Profile (by account_teams and fullName) not in team
     *
     * @param team     team
     * @param fullName fullname
     * @return list Profile
     */
    List<Profile> findProfilesByAccount_TeamsNotContainsAndFullNameContainsIgnoreCase(Team team, String fullName);

    /**
     * Get list Profile (by account_teams and fullName) in team
     *
     * @param team     team
     * @param fullName fullname
     * @return list Profile
     */
    List<Profile> findProfilesByAccount_TeamsAndFullNameContainsIgnoreCase(Team team, String fullName);
}