package mgmsports.dao.repository;

import mgmsports.dao.entity.Account;
import mgmsports.dao.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {

    /**
     * Get account from database
     *
     * @param accountId account id
     * @return account data
     */
    Account getAccountByAccountId(String accountId);

    /**
     * Get account from database
     *
     * @param name user_name
     * @return list of accounts
     */
    @Query(nativeQuery = true, value = "select * from Account a where LOWER(unaccent_replace(a.user_name)) like %?1%")
    List<Account> findAllByUserNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Get accountId by fbUserId
     *
     * @param fbUserId fbUserId of account
     * @return accountId
     */
    @Query("select a.accountId from Account a where a.socialId = ?1")
    String getAccountIdBySocialId(@Param("fbUserId") String fbUserId);

    /**
     * Check if exist an account containing fbUserId
     *
     * @param fbUserId of account
     * @return true if exists, false if not
     */
    boolean existsAccountBySocialId(String fbUserId);

    /**
     * Get list account (has userName like "userName") not in team
     *
     * @param team team entity
     * @param userName userName of account
     * @return list account not in team
     */
    List<Account> findAccountsByTeamsNotContainsAndUserNameContaining(Team team, String userName);

    /**
     * Get list account (has userName like "userName") in team
     *
     * @param team team entity
     * @param userName userName of account
     * @return list account not in team
     */
    List<Account> findAccountsByTeamsContainsAndUserNameContaining(Team team, String userName);

    /**
     * Get list account in team
     *
     * @param team team entity
     * @return list account in team
     */
    List<Account> findAccountsByTeamsContains(Team team);

    /**
     * Find account by social id
     *
     * @param socialId social id
     * @return account
     */
    Optional<Account> findAccountBySocialId(String socialId);

    /**
     * Find account by username
     *
     * @param userName social id
     * @return account
     */
    Optional<Account> findAccountByUserName(String userName);

    /**
     * Find account by account Id
     * @param userId account id
     * @return account
     */
    Optional<Account> findAccountByAccountId(String userId);

    /**
     * Get list account active in team after date
     *
     * @param date date
     * @param team team entity
     * @param isActive is active activity
     * @return list account in team
     */
    List<Account> findDistinctAccountsByActivities_ActivityDateAfterAndTeamsContainsAndActivities_IsActive(Date date,
                                                                                                           Team team,
                                                                                                           boolean isActive);

    /**
     * Get number of active running members with time interval
     *
     * @param beginDate beginning date
     * @param endDate ending date
     * @return number of active running members
     */
    @Query(nativeQuery = true, value = "SELECT COUNT (DISTINCT a.account_id) " +
            "FROM activity a WHERE a.activity_type = 0 " +
            "AND a.activity_date >= :beginDate " +
            "AND a.activity_date <= :endDate " +
            "AND a.is_active = true")
    int getNumberOfActiveRunningMembers(@Param("beginDate") Date beginDate,
                                        @Param("endDate") Date endDate);
}
