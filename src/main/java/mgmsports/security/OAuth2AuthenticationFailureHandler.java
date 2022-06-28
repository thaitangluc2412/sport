package mgmsports.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * OAuth2 Authentication failure handler for removing oauth2 related cookies
 *
 * @author Chuc Ba Hieu
 */
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        HttpCookieOAuth2AuthorizationRequestRepository.deleteCookies(request, response);
        super.onAuthenticationFailure(request, response, exception);
    }
}
