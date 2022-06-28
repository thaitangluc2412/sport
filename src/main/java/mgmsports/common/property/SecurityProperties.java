package mgmsports.common.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Bind all the file security properties
 *
 * @author Chuc Ba Hieu
 */
@ConfigurationProperties(prefix = "security")
@Getter
@Setter
public class SecurityProperties {

    /**
     * The default URL to redirect to after
     * a user logs in using OAuth2/OpenIDConnect
     */
    private String oauth2AuthenticationSuccessUrl;

    private Jwt jwt;

    private Cors cors;

    @Getter
    @Setter
    public static class Jwt {
        private long expirationTime = 86400000;
        private int shortLivedTime = 120000;
        private String secret = "926D96C90030DD58429D2751AC1BDBBC";
        private String tokenPrefix = "Bearer";
        private String tokenRequestHeaderName = "Authorization";
        private String tokenResponseHeaderName = "MgmSports-Authorization";
    }

    /**
     * CORS configuration related properties
     */
    @Getter
    @Setter
    public static class Cors {

        /**
         * Comma separated whitelisted URLs for CORS.
         * Should contain the applicationURL at the minimum.
         * Not providing this property would disable CORS configuration.
         */
        private String[] allowedOrigins;

        /**
         * Methods to be allowed, e.g. GET,POST,...
         */
        private String[] allowedMethods = {"GET", "HEAD", "POST", "PUT", "DELETE", "TRACE", "OPTIONS", "PATCH"};

        /**
         * Request headers to be allowed, e.g. content-type,accept,origin,x-requested-with,...
         */
        private String[] allowedHeaders = {
                "Accept",
                "Accept-Encoding",
                "Accept-Language",
                "Cache-Control",
                "Connection",
                "Content-Length",
                "Content-Type",
                "Cookie",
                "Host",
                "Origin",
                "Pragma",
                "Referer",
                "User-Agent",
                "x-requested-with",
        };

        /**
         * Response headers that you want to expose to the client JavaScript programmer,
         *
         * <br>
         * See <a href="http://stackoverflow.com/questions/25673089/why-is-access-control-expose-headers-needed#answer-25673446">
         * here</a> to know why this could be needed.
         */
        private String[] exposedHeaders = {
                "Cache-Control",
                "Connection",
                "Content-Type",
                "Date",
                "Expires",
                "Pragma",
                "Server",
                "Set-Cookie",
                "Transfer-Encoding",
                "X-Content-Type-Options",
                "X-XSS-Protection",
                "X-Frame-Options",
                "X-Application-Context",
        };

    }

}
