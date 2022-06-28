package mgmsports.controller;

import mgmsports.common.exception.EntityNotFoundException;
import mgmsports.model.IndividualUserStatisticDto;
import mgmsports.model.ProfileAccountDto;
import mgmsports.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

/**
 * Activity Controller
 *
 * @author mctran
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;

    /**
     * get list of profileAccountDto for searching user according to username
     *
     * @param username username passed to find any user_name containing this param
     * @return list of accounts containing param username in account_username
     */
    @GetMapping({"/{username}"})
    public ResponseEntity<Object> getAccountsByUsername(@PathVariable("username") String username) {
        Optional<List<ProfileAccountDto>> profileAccountDtos = Optional.ofNullable(accountService.getAllUsersByUserName(username));
        if (profileAccountDtos.isPresent()) {
            return new ResponseEntity<>(profileAccountDtos.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("No account exits", HttpStatus.OK);
    }

    /**
     * Get user statistic
     *
     * @param accountId account id
     * @param timeInterval time interval
     * @param timeZoneOffset time zone offset
     * @return user statistic
     * @throws EntityNotFoundException throws Exception if account not found in database
     */
    @GetMapping("/statistic/{id}")
    public ResponseEntity<Object> getUserStatisticByAccountId(@PathVariable("id") String accountId,
                                                              @RequestParam("timeinterval") String timeInterval,
                                                              @RequestParam("timezoneoffset") String timeZoneOffset) throws EntityNotFoundException {
        Optional<IndividualUserStatisticDto> profileAccountDtos = Optional.ofNullable(accountService.getUserStatistic(accountId, timeInterval, timeZoneOffset));
        if (profileAccountDtos.isPresent()) {
            return new ResponseEntity<>(profileAccountDtos.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("No account exits", HttpStatus.OK);
    }
}