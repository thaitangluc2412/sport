package mgmsports.service;

import mgmsports.common.exception.EntityNotFoundException;
import mgmsports.dao.entity.Account;
import mgmsports.model.IndividualUserStatisticDto;
import mgmsports.model.ProfileAccountDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

/**
 * Account interface
 *
 * @author mctran
 */
public interface AccountService extends UserDetailsService {
    List<ProfileAccountDto> getAllUsersByUserName(String username);
    Optional<Account> findUserBySocialId(String socialId);
    Optional<Account> findUserByUserName(String userName);
    Optional<Account> findUserByUserId(String userId);
    ProfileAccountDto findProfileUserByUserId(String userId);
    void saveUser(Account account);
    IndividualUserStatisticDto getUserStatistic(String accountId,
                                                String timeInterval,
                                                String timeZoneOffset) throws EntityNotFoundException;
}