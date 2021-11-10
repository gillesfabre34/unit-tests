package com.airbus.retex.configuration;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.provider.error.DefaultOAuth2ExceptionRenderer;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.OAuth2ExceptionRenderer;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.airbus.retex.config.RetexConfig;
import com.airbus.retex.config.RetexSsoClientProperties;
import com.airbus.retex.configuration.filter.ApiKeyAuthenticationProvider;
import com.airbus.retex.configuration.filter.ClientApiKeyFilter;
import com.airbus.retex.utils.CustumOidcUserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class RetexSecurityConfiguration extends WebSecurityConfigurerAdapter {

    public static final String THING_WORX_API_KEY = "api-key";

    @Autowired
    private RetexConfig retexConfig;

    @Autowired
    private CustumOidcUserService custumOidcUserService;

    @Autowired(required = false)
    private ConfigurationOverride configurationOverride;

    @Autowired
    private ApiKeyAuthenticationProvider apiKeyAuthenticationProvider;

    private static final String[] AUTHORIZED_LIST = {
            "/api/swagger-ui.html", "/swagger-resources/**",
            "/swagger-ui.html", "/v2/api-docs", "/webjars/**"
    };

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        // setAllowCredentials(true) is important, otherwise:
        // The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
        configuration.setAllowCredentials(true);
        // setAllowedHeaders is important! Without it, OPTIONS preflight request
        // will fail with 403 Invalid CORS request
        List<String> headers = Arrays.asList("Authorization", "Cache-Control", "Content-Type");
        configuration.setAllowedHeaders(headers);
        configuration.setExposedHeaders(headers);
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public FilterRegistrationBean corsFilter() {
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(corsConfigurationSource()));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    //FIXME
    /*@Bean
    public AccessDecisionManager accessDecisionManager() {
        WebExpressionVoter webExprVoter = new WebExpressionVoter();
        webExprVoter.setExpressionHandler(new OAuth2WebSecurityExpressionHandler());
        List<AccessDecisionVoter<? extends Object>> decisionVoters
                = List.of(
                webExprVoter,
                new RoleVoter(),
                new AuthenticatedVoter(),
                new FeatureRightVoter());
        return new UnanimousBased(decisionVoters);
    }*/

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        boolean overriddenSsoAutomaticRedirect = retexConfig.getSso().isAutomaticRedirect();

        if (configurationOverride != null) {
            configurationOverride.configureBefore(http);
            overriddenSsoAutomaticRedirect = configurationOverride.ssoAutomaticRedirect(overriddenSsoAutomaticRedirect);
        }

        if (!overriddenSsoAutomaticRedirect) {
            http.exceptionHandling()
                // Prevent the default redirect to the login page when not authenticated.
                //   It should be set before other exceptionHandling.defaultAuthenticationEntryPointFor() registrations.
                .defaultAuthenticationEntryPointFor(new OAuth2AuthenticationEntryPoint(), AnyRequestMatcher.INSTANCE)
            ;
        }

        // @formatter:off
        http.cors().configurationSource(corsConfigurationSource());
        http.authorizeRequests().and().authenticationProvider(apiKeyAuthenticationProvider)
                .addFilterBefore(new ClientApiKeyFilter(THING_WORX_API_KEY), BasicAuthenticationFilter.class);
        http.authorizeRequests()
                .antMatchers(AUTHORIZED_LIST).permitAll()
                .antMatchers(HttpMethod.GET, "/api/media/**").authenticated()
                .antMatchers("/api/**").authenticated()
                .and()
            .oauth2Login()
                .userInfoEndpoint()
                    .oidcUserService(this.oidcUserService())
                    .and()
                .and()
            .sessionManagement()
                .sessionAuthenticationStrategy(new SessionAuthStrategy(retexConfig.getSessionTimeout()))
                .and()
            .csrf().disable()
        ;
        // @formatter:on

        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());

        if (!overriddenSsoAutomaticRedirect) {
            http.oauth2Login()
                // Deactivate redirect after the authentication success
                .successHandler(new OnSuccess(retexConfig.getSso().getSuccessRedirectUri()))
                // Deactivate redirect after the authentication failure
                .failureHandler(new OnFailure())
            ;
        }
    }

    private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        final OidcUserService delegate = new OidcUserService();

        return userRequest -> {
            // Delegate to the default implementation for loading a user
            OidcUser oidcUser = delegate.loadUser(userRequest);
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            RetexSsoClientProperties clientProperties = retexConfig.getSso().getClient().get(registrationId);
            if (clientProperties == null) {
                OAuth2Error oauth2Error = new OAuth2Error("No clientProperties found for this registrationId: " + registrationId);
                throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
            }
            return custumOidcUserService.create(clientProperties, oidcUser);
        };
    }


    private static class SessionAuthStrategy implements SessionAuthenticationStrategy {

        private int sessionTimeout; // in seconds

        public SessionAuthStrategy(Duration sessionTimeout) {
            this.sessionTimeout = Math.toIntExact(sessionTimeout.toSeconds());
        }

        @Override
        public void onAuthentication(Authentication authentication, HttpServletRequest request, HttpServletResponse response) throws SessionAuthenticationException {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.setMaxInactiveInterval(sessionTimeout);
            }
        }
    }

    private static class OnSuccess implements AuthenticationSuccessHandler {

        private String redirectUri = null;

        public OnSuccess(String redirectUri) {
            if (redirectUri != null && !redirectUri.isEmpty()) {
                this.redirectUri = redirectUri;
            }
        }

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
                throws IOException, ServletException {
            // Do not add anything there because this handler is optional

            // Create Success response
            if (redirectUri == null) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.flushBuffer();
            } else {
                // This not use response.sendRedirect(redirectUri) because it adds the host part in the URL
                response.setStatus(HttpServletResponse.SC_FOUND);
                response.setHeader("Location", redirectUri);
                response.flushBuffer();
            }
        }
    }

    private static class OnFailure implements AuthenticationFailureHandler {
        @Override
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
                throws IOException, ServletException {
            log.error("Authentication failure: " + request.getRequestURI(), exception);
            // Do not add anything there because this handler is optional

            // Create the error response
            //   code from org.springframework.security.oauth2.provider.error.AbstractOAuth2SecurityExceptionHandler.doHandle()
            OAuth2ExceptionRenderer exceptionRenderer = new DefaultOAuth2ExceptionRenderer();
            WebResponseExceptionTranslator exceptionTranslator = new DefaultWebResponseExceptionTranslator();
            try {
                ResponseEntity<OAuth2Exception> result = exceptionTranslator.translate(exception);
                exceptionRenderer.handleHttpEntityResponse(result, new ServletWebRequest(request, response));
                response.flushBuffer();
            } catch (ServletException | IOException | RuntimeException e) {
                throw e;
            } catch (Exception e) {
                // Wrap other Exceptions. These are not expected to happen
                throw new RuntimeException(e);
            }
        }
    }
}

