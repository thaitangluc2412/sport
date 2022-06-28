package mgmsports.dao.repository;

import mgmsports.dao.entity.LocationSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Suggestion Location Repository
 *
 * @author qngo
 */
public interface SuggestionLocationRepository extends JpaRepository<LocationSuggestion, String> {

    @Query(nativeQuery = true, value = "select exists (select s from location_suggestion as s where (s.account_id = :accountId) and (s.suggestion = :location))")
    boolean checkLocationExists(@Param("accountId") String accountId, @Param("location") String location);

    LocationSuggestion findLocationSuggestionByAccount_AccountIdAndSuggestion(String accountId, String location);

    @Query(nativeQuery = true, value = "select * from location_suggestion where account_id = ?1 and last_enter_date > current_date - interval '30' day order by last_enter_date desc")
    List<LocationSuggestion> findAllByAccount_AccountIdOrderByLastEnterDateDesc(@Param("id") String accountId);

}
