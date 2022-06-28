package mgmsports.dao.repository;

import mgmsports.dao.entity.Account;
import mgmsports.dao.entity.Activity;
import mgmsports.dao.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Activity Repository
 *
 * @author qngo
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, String> {

    /**
     * Get activity By Accountid order by Date
     *
     * @param account_id account id
     * @return list of activity
     */
    @Query("select a from Activity a where a.account.accountId = :id and a.isActive = true order by a.activityDate desc")
    List<Activity> findActivitiesByAccountId(@Param("id") String account_id);

    /**
     * This function will change updateActivityStatus field in activity table to true or false
     *
     * @param activityId activity id
     */
    @Modifying
    @Query("update Activity a set a.isActive = false where a.activityId = ?1")
    void updateActivityStatus(String activityId);

    Activity findActivityByActivityId(String act);

    /**
     * This function gets list of activities in last seven days
     *
     * @param account_id passed to get list of activities of this account
     * @return list of activities
     */
    @Query(nativeQuery = true, value = "select * from activity where account_id = ?1 and is_active = true and activity_date > current_date - interval '7' day order by activity_date desc")
    List<Activity> findActivitiesByAccountIdLast7Day(@Param("id") String account_id);

    /**
     * This function gets list of activities after :date
     *
     * @param team team
     * @param dateBegin date begin
     * @param dateEnd date end
     * @param isActive is active activity
     * @return list of activities
     */
    List<Activity> findActivitiesByAccount_TeamsAndActivityDateBetweenAndIsActive(Team team,
                                                                                  Date dateBegin,
                                                                                  Date dateEnd,
                                                                                  boolean isActive);

    /**
     * Get number of days that user engage in activity in a certain time interval
     *
     * @param accountId account id
     * @param beginDate beginning date
     * @param endDate ending date
     * @return number of days that user engage in activity
     */
    @Query(nativeQuery = true, value = "select count(DISTINCT pg_catalog.date(a.activity_date)) " +
            "from activity a join account acc on a.account_id = acc.account_id " +
            "where acc.account_id like :id " +
            "and a.activity_date >= :beginDate " +
            "and a.activity_date <= :endDate " +
            "AND a.is_active = true")
    int getNumberOfDaysEngageActivity(@Param("id") String accountId,
                                      @Param("beginDate") Date beginDate,
                                      @Param("endDate") Date endDate);

    /**
     * Get user running rating (based on other user's running activities (max = 5)) in a certain time interval
     *
     * @param accountId account id
     * @param beginDate beginning date
     * @param endDate ending date
     * @return user running rating
     */
    @Query(nativeQuery = true, value = "SELECT 100 * sum(ac.distance) / (SELECT SUM(a.distance) " +
            "FROM activity a WHERE a.activity_date >= :beginDate AND a.activity_date <= :endDate " +
            "AND a.activity_type = 0 " +
            "AND a.is_active = true " +
            "GROUP BY a.account_id " +
            "ORDER BY sum(a.distance) DESC LIMIT 1) " +
            "FROM activity ac WHERE ac.account_id LIKE :id " +
            "AND ac.is_active = true " +
            "AND ac.activity_type = 0 " +
            "AND ac.activity_date >= :beginDate AND ac.activity_date <= :endDate " +
            "GROUP BY ac.account_id")
    int getUserRunningRating(@Param("id") String accountId,
                             @Param("beginDate") Date beginDate,
                             @Param("endDate") Date endDate);

    /**
     * Find all activities of user in a certain time interval
     *
     * @param account account of user
     * @param dateBegin beginning date
     * @param dateEnd ending date
     * @param isActive is active activity
     * @return list activities
     */
    List<Activity> findActivitiesByAccountAndActivityDateBetweenAndIsActive(Account account, Date dateBegin, Date dateEnd, boolean isActive);

    @Query(nativeQuery = true, value = "SELECT r.row_number FROM " +
            "(SELECT ROW_NUMBER () OVER ( ORDER BY SUM(a.distance) DESC) AS row_number, SUM(a.distance), a.account_id AS id " +
            "FROM activity a WHERE a.activity_type = 0 " +
            "AND a.is_active = true " +
            "AND a.activity_date >= :beginDate AND a.activity_date <= :endDate " +
            "GROUP BY a.account_id ) r " +
            "WHERE r.id LIKE :id")
    int getUserRunningRank(@Param("id") String accountId,
                           @Param("beginDate") Date beginDate,
                           @Param("endDate") Date endDate);

}

