package mgmsports.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.KeyLengthException;
import mgmsports.common.property.SecurityProperties;
import mgmsports.security.JwtAuthenticationProvider;
import mgmsports.security.JwtService;
import mgmsports.security.MgmOAuth2UserService;
import mgmsports.security.MgmOidcUserService;
import mgmsports.security.AuthenticationSuccessHandler;
import mgmsports.security.OAuth2AuthenticationFailureHandler;
import mgmsports.security.OAuth2AuthenticationSuccessHandler;
import mgmsports.service.AccountService;
import mgmsports.service.ProfileService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * Bean provider
 *
 * @author Chuc Ba Hieu
 */
@Configuration
public class BeanConfig {

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    @ConditionalOnMissingBean(JwtService.class)
    public JwtService jwtService(SecurityProperties properties) throws KeyLengthException {
        return new JwtService(properties.getJwt().getSecret());
    }

    @Bean
    @ConditionalOnMissingBean(AuthenticationSuccessHandler.class)
    public AuthenticationSuccessHandler authenticationSuccessHandler(ObjectMapper objectMapper, JwtService jwtService, SecurityProperties properties) {
        return new AuthenticationSuccessHandler(objectMapper, jwtService, properties);
    }

    @Bean
    @ConditionalOnMissingBean(OAuth2AuthenticationSuccessHandler.class)
    public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler(SecurityProperties properties, JwtService jwtService) {
        return new OAuth2AuthenticationSuccessHandler(properties, jwtService);
    }

    @Bean
    @ConditionalOnMissingBean(OAuth2AuthenticationFailureHandler.class)
    public OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler() {
        return new OAuth2AuthenticationFailureHandler();
    }

    @Bean
    @ConditionalOnMissingBean(MgmOAuth2UserService.class)
    public MgmOAuth2UserService mgmOAuth2UserService(AccountService accountService, ProfileService profileService) {
        return new MgmOAuth2UserService(accountService, profileService);
    }

    @Bean
    @ConditionalOnMissingBean(MgmOidcUserService.class)
    public MgmOidcUserService mgmOidcUserService(MgmOAuth2UserService mgmOAuth2UserService) {
        return new MgmOidcUserService(mgmOAuth2UserService);
    }

    @Bean
    @ConditionalOnMissingBean(JwtAuthenticationProvider.class)
    public JwtAuthenticationProvider jwtAuthenticationProvider(JwtService jwtService, AccountService accountService) {
        return new JwtAuthenticationProvider(jwtService, accountService);
    }

    @Bean
    public ClassLoaderTemplateResolver templateResolver() {

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

        templateResolver.setPrefix("templates/example/");
        templateResolver.setCacheable(false);
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setCharacterEncoding("UTF-8");

        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());

        return templateEngine;
    }

}
