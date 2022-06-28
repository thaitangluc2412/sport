package mgmsports.dao.repository;

import mgmsports.dao.entity.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Competition Repository
 *
 * @author dntvo
 */
@Repository
public interface CompetitionRepository extends JpaRepository <Competition, String> {

    /**
     * Get all competitions by an account Id
     *
     * @param account_id account id
     * @return list of competitions
     */
    @Query(nativeQuery = true, value = "select * from Competition c where c.host_id = :accountId or c.invitee_id =:accountId order by created_date desc")
    List<Competition> getCompetitionByAccountId(@Param("accountId") String account_id);

    /**
     * Delete a competition with competition Id
     *
     * @param competition_Id competition id
     */
    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from Competition where competition_id = :id")
    void removeCompetition(@Param("id") String competition_Id);

    /**
     * Check whether a competition between two account id exists or not
     *
     * @param hostId host account id
     * @param inviteeId invitee account id
     * @return true/false value
     */
    @Query(nativeQuery = true,value = "select exists (select c from Competition c where ((c.host_id =:host and c.invitee_id =:invitee) or (c.invitee_id =:host and c.host_id =:invitee)))")
    boolean checkCompetitionExist(@Param("host") String hostId, @Param("invitee") String inviteeId);
}
