package mgmsports.security;

import mgmsports.common.MgmSportsUtils;
import mgmsports.common.property.SecurityProperties;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.util.Assert;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;

/**
 * Cookie based repository for storing Authorization requests
 *
 * @author Chuc Ba Hieu
 */
public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private static final String AUTHORIZATION_REQUEST_COOKIE_NAME = "mgm_oauth2_authorization_request";
    public static final String REDIRECT_URI_COOKIE_PARAM_NAME = "mgm_redirect_uri";

    private int cookieExpirySecs;

    public HttpCookieOAuth2AuthorizationRequestRepository(SecurityProperties properties) {
        cookieExpirySecs = properties.getJwt().getShortLivedTime() / 1000;
    }

    /**
     * Load authorization request from cookie
     */
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {

        Assert.notNull(request, "request cannot be null");

        return MgmSportsUtils.fetchCookie(request, AUTHORIZATION_REQUEST_COOKIE_NAME)
                .map(this::deserialize)
                .orElse(null);
    }

    /**
     * Save authorization request in cookie
     */
    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request,
                                         HttpServletResponse response) {

        Assert.notNull(request, "request cannot be null");
        Assert.notNull(response, "response cannot be null");

        if (authorizationRequest == null) {

            deleteCookies(request, response);
            return;
        }

        Cookie cookie = new Cookie(AUTHORIZATION_REQUEST_COOKIE_NAME, serialize(authorizationRequest));
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(cookieExpirySecs);
        response.addCookie(cookie);

        String mgmRedirectUri = request.getParameter(REDIRECT_URI_COOKIE_PARAM_NAME);
        if (StringUtils.isNotBlank(mgmRedirectUri)) {

            cookie = new Cookie(REDIRECT_URI_COOKIE_PARAM_NAME, mgmRedirectUri);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(cookieExpirySecs);
            response.addCookie(cookie);
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {

        return loadAuthorizationRequest(request);
    }

    /**
     * Utility for deleting related cookies
     */
    public static void deleteCookies(HttpServletRequest request, HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0)
            for (Cookie cooky : cookies)
                if (cooky.getName().equals(AUTHORIZATION_REQUEST_COOKIE_NAME) ||
                        cooky.getName().equals(REDIRECT_URI_COOKIE_PARAM_NAME)) {

                    cooky.setValue("");
                    cooky.setPath("/");
                    cooky.setMaxAge(0);
                    response.addCookie(cooky);
                }
    }

    private String serialize(OAuth2AuthorizationRequest authorizationRequest) {
        return Base64.getUrlEncoder().encodeToString(
                SerializationUtils.serialize(authorizationRequest));
    }

    private OAuth2AuthorizationRequest deserialize(Cookie cookie) {
        return SerializationUtils.deserialize(
                Base64.getUrlDecoder().decode(cookie.getValue()));
    }
}
