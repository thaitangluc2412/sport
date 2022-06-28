package mgmsports.common;

import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import mgmsports.dao.entity.Account;
import mgmsports.model.AccountDto;
import mgmsports.security.JwtService;
import mgmsports.security.MgmSportsPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Useful helper methods
 *
 * @author Chuc Ba Hieu
 */
@Component
@Slf4j
public class MgmSportsUtils {

    /**
     * Fetches a cookie from the request
     */
    public static Optional<Cookie> fetchCookie(HttpServletRequest request, String name) {

        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0)
            for (Cookie cocky : cookies)
                if (cocky.getName().equals(name))
                    return Optional.of(cocky);

        return Optional.empty();

    }

    /**
     * Gets the current-user
     */
    public static AccountDto currentUser() {

        // get the authentication object
        Authentication auth = SecurityContextHolder
                .getContext().getAuthentication();

        // get the user from the authentication object
        return currentUser(auth);

    }

    /**
     * Extracts the current-user from authentication object
     *
     * @param auth auth
     * @return Account data object
     */
    public static AccountDto currentUser(Authentication auth) {

        if (auth != null) {
            Object principal = auth.getPrincipal();
            if (principal instanceof MgmSportsPrincipal) {
                return ((MgmSportsPrincipal) principal).currentUser();
            }
        }
        return null;

    }

    /**
     * Throws AccessDeniedException is not authorized
     *
     * @param authorized authorized
     * @param messageKey error message
     */
    public static void ensureAuthority(boolean authorized, String messageKey) {

        if (!authorized)
            throw new AccessDeniedException(messageKey);

    }

    /**
     * Throws BadCredentialsException if
     * user's credentials were updated after the JWT was issued
     */
    public static void ensureCredentialsUpToDate(JWTClaimsSet claims, Account user) {

        long issueTime = (long) claims.getClaim(JwtService.MGM_IAT);

        ensureCredentials(issueTime >= user.getCredentialsUpdated(),
                "Token has become obsolete");

    }

    /**
     * Throws BadCredentialsException if not valid
     *
     * @param valid valid
     * @param messageKey error message
     */
    public static void ensureCredentials(boolean valid, String messageKey) {

        if (!valid)
            throw new BadCredentialsException(messageKey);
    }

}
