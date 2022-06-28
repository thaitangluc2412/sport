package mgmsports.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import mgmsports.common.MgmSportsUtils;
import mgmsports.common.property.SecurityProperties;
import mgmsports.model.AccountDto;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Authentication success handler for sending the response
 * to the client after successful authentication.
 *
 * @author Chuc Ba hieu
 */
@Slf4j
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private ObjectMapper objectMapper;
    private JwtService jwtService;
    private long defaultExpirationMillis;

    public AuthenticationSuccessHandler(ObjectMapper objectMapper, JwtService jwtService, SecurityProperties properties) {

        this.objectMapper = objectMapper;
        this.jwtService = jwtService;
        this.defaultExpirationMillis = properties.getJwt().getExpirationTime();

        log.info("Created");
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        // Instead of handle(request, response, authentication),
        // the statements below are introduced
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String expirationMillisStr = request.getParameter("expirationMillis");
        long expirationMillis = expirationMillisStr == null ?
                defaultExpirationMillis : Long.valueOf(expirationMillisStr);

        // get the current-user
        AccountDto currentUser = MgmSportsUtils.currentUser();

        jwtService.addAuthHeader(response, currentUser.getUsername(), expirationMillis);

        // write current-user data to the response
        response.getOutputStream().print(
                objectMapper.writeValueAsString(currentUser));

        // as done in the base class
        clearAuthenticationAttributes(request);

        log.debug("Authentication succeeded for user: " + currentUser);
    }
}
