package mgmsports.config;

import lombok.extern.slf4j.Slf4j;
import mgmsports.common.property.SecurityProperties;
import mgmsports.common.property.SecurityProperties.Cors;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Cors config
 *
 * @author Chuc Ba Hieu
 */
@Slf4j
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private SecurityProperties properties;

    public CorsConfig(SecurityProperties properties) {
        this.properties = properties;
        log.info("Created");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        Cors cors = properties.getCors();
        String[] extendedAllowedHeaders = ArrayUtils.addAll(cors.getAllowedHeaders(),
                properties.getJwt().getTokenRequestHeaderName());
        String[] extendedExposedHeaders = ArrayUtils.addAll(cors.getExposedHeaders(),
                properties.getJwt().getTokenResponseHeaderName());

        registry.addMapping("/**")
                .allowedOrigins(cors.getAllowedOrigins())
                .allowedMethods(cors.getAllowedMethods())
                .allowedHeaders(extendedAllowedHeaders)
                .exposedHeaders(extendedExposedHeaders)
                .allowCredentials(true);

    }

}
