package mgmsports.security;

import lombok.extern.slf4j.Slf4j;
import mgmsports.common.MgmSportsUtils;
import mgmsports.common.property.SecurityProperties;
import mgmsports.model.AccountDto;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Authentication success handler for redirecting the
 * OAuth2 signed in user to a URL with a short lived auth token
 *
 * @author Chuc Ba Hieu
 */
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private SecurityProperties properties;
    private JwtService jwtService;

    public OAuth2AuthenticationSuccessHandler(SecurityProperties properties, JwtService jwtService) {
        this.properties = properties;
        this.jwtService = jwtService;
        log.info("Created");
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        AccountDto currentUser = MgmSportsUtils.currentUser();

        String shortLivedAuthToken = jwtService.createToken(
                JwtService.AUTH_AUDIENCE,
                currentUser.getId(),
                (long) properties.getJwt().getShortLivedTime());

        String targetUrl = MgmSportsUtils.fetchCookie(request,
                HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_COOKIE_PARAM_NAME)
                .map(Cookie::getValue)
                .orElse(properties.getOauth2AuthenticationSuccessUrl());

        HttpCookieOAuth2AuthorizationRequestRepository.deleteCookies(request, response);
        return targetUrl + shortLivedAuthToken;
    }
}