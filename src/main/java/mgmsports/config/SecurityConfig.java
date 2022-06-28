package mgmsports.config;

import mgmsports.common.property.SecurityProperties;
import mgmsports.security.*;
import mgmsports.service.AccountService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration class.
 *
 * @author Chuc Ba Hieu
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String GOOD_USER = "GOOD_USER";

    private AccountService accountService;
    private PasswordEncoder passwordEncoder;
    private JwtAuthenticationProvider jwtAuthenticationProvider;
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    private AuthenticationFailureHandler authenticationFailureHandler;
    private SecurityProperties properties;
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private MgmOidcUserService oidcUserService;
    private MgmOAuth2UserService oAuth2UserService;

    public SecurityConfig(AccountService accountService,
                          PasswordEncoder passwordEncoder,
                          JwtAuthenticationProvider jwtAuthenticationProvider,
                          AuthenticationSuccessHandler authenticationSuccessHandler,
                          AuthenticationFailureHandler authenticationFailureHandler,
                          SecurityProperties properties,
                          OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
                          OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler,
                          MgmOidcUserService oidcUserService,
                          MgmOAuth2UserService oAuth2UserService) {
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.properties = properties;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
        this.oAuth2AuthenticationFailureHandler = oAuth2AuthenticationFailureHandler;
        this.oidcUserService = oidcUserService;
        this.oAuth2UserService = oAuth2UserService;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.
                sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

                .formLogin()
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler).and()

                .logout().disable()

                .exceptionHandling()
                .authenticationEntryPoint(new Http403ForbiddenEntryPoint()).and()

                .addFilterBefore(
                        new MgmTokenAuthenticationFilter(super.authenticationManager(), properties),
                        UsernamePasswordAuthenticationFilter.class).cors().and().csrf().disable()

                //Social login config
                .oauth2Login()
                .authorizationEndpoint()
                .authorizationRequestRepository(
                        new HttpCookieOAuth2AuthorizationRequestRepository(properties)).and()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler)
                .userInfoEndpoint()
                .oidcUserService(oidcUserService)
                .userService(oAuth2UserService).and().and()

                .authorizeRequests()
                .antMatchers("/api/**").hasRole(GOOD_USER)
                .anyRequest().permitAll();

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(accountService)
                .passwordEncoder(passwordEncoder).and()
                .authenticationProvider(jwtAuthenticationProvider);
    }
}
