package com.sideproject.codemoim.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sideproject.codemoim.filter.JwtAuthenticationFilter;
import com.sideproject.codemoim.filter.LoginAuthenticationFilter;
import com.sideproject.codemoim.handler.*;
import com.sideproject.codemoim.property.CustomProperties;
import com.sideproject.codemoim.provider.JwtAuthenticationProvider;
import com.sideproject.codemoim.provider.LoginAuthenticationProvider;
import com.sideproject.codemoim.repository.CustomCsrfTokenRepository;
import com.sideproject.codemoim.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.sideproject.codemoim.service.CustomOAuth2UserService;
import com.sideproject.codemoim.util.CustomRequestMatcher;
import com.sideproject.codemoim.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsUtils;

import java.util.Arrays;
import java.util.List;

@Slf4j
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    public static final String DEFAULT_FILTER_PROCESSES_URL = "/auth/login";
    public static final String DEFAULT_API_URL = "/**";

    private final UnauthorizedAuthenticationEntryPointHandler unauthorizedAuthenticationEntryPointHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final Oauth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler;
    private final Oauth2AuthenticationFailureHandler oauth2AuthenticationFailureHandler;
    private final LoginAuthenticationProvider loginAuthenticationProvider;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final LoginAuthenticationSuccessHandler loginAuthenticationSuccessHandler;
    private final CommonAuthenticationFailureHandler commonAuthenticationFailureHandler;
    private final CustomCookieDeleteLogoutHandler customCookieDeleteLogoutHandler;
    //private final CustomLogoutSuccessHandler customLogoutSuccessHandler;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final CustomProperties customProperties;

    /*@Bean
    public AuthenticationManager getAuthenticationManager() throws Exception {
        return super.authenticationManagerBean();
    }*/

    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository(jwtUtil);
    }

    @Bean
    public CustomCsrfTokenRepository customCsrfTokenRepository() {
        CustomCsrfTokenRepository customCsrfTokenRepository = CustomCsrfTokenRepository.withHttpOnlyFalse();
        customCsrfTokenRepository.setCookieDomain(customProperties.getCookieConfig().getRootDomain());
        customCsrfTokenRepository.setSecure(customProperties.getCookieConfig().getSecure());

        return customCsrfTokenRepository;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(loginAuthenticationProvider);
        auth.authenticationProvider(jwtAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf()
                //.disable()
                .csrfTokenRepository(customCsrfTokenRepository())
                //.ignoringAntMatchers("/auth/csrf-token")
                .and()
                .formLogin()
                .disable()
                .httpBasic()
                .disable()
                .exceptionHandling()
                .authenticationEntryPoint(unauthorizedAuthenticationEntryPointHandler)
                .and()
                .authorizeRequests()
                //.antMatchers("/",
                //        "/error",
                //        "/favicon.ico",
                //        "/**/*.png",
                //        "/**/*.gif",
                //        "/**/*.svg",
                //        "/**/*.jpg",
                //        "/**/*.html",
                //        "/**/*.css",
                //        "/**/*.js")
                //.permitAll()
                .antMatchers("/api/external/health-check",
                        "/auth/access-token",
                        "/auth/validate-auth",
                        "/api/user/username-duplicate-check",
                        "/api/user/signup",
                        "/api/email/duplicate-check",
                        "/api/email/key-expire/**",
                        "/api/email/send-find-password-email",
                        "/api/email/verify-key",
                        "/api/user/key-expired/**",
                        "/api/user/find-password",
                        "/api/board/tree-list",
                        "/api/board/sub-list",
                        "/api/tag/list",
                        "/api/tag/all-list",
                        "/api/post/modify-view-count",
                        "/api/post/list",
                        "/api/post/info",
                        "/api/post/vote",
                        "/search/**",
                        "/api/profile/rank",
                        "/api/tag/follower-rank",
                        "/api/tag/post-rank",
                        "/api/tag/detail",
                        "/api/post/tag-post",
                        "/api/post/dashboard-fixed-list",
                        "/api/post/dashboard-non-fixed-list",
                        "/api/profile/info",
                        "/api/profile/following-info",
                        "/api/profile/follower-info",
                        "/api/profile/tag-info",
                        "/api/post/post-list-by-profile",
                        "/api/post/comment-post-list-by-profile",
                        "/api/post/scrap-list-by-profile")
                .permitAll()
                //.anyRequest()
                //.authenticated()
                .and()
                .oauth2Login()
                .authorizationEndpoint()
                .authorizationRequestRepository(cookieAuthorizationRequestRepository())
                .and()
                .redirectionEndpoint()
                .baseUri("/oauth2/callback/*")
                .and()
                .userInfoEndpoint()
                .userService(customOAuth2UserService)
                .and()
                .successHandler(oauth2AuthenticationSuccessHandler)
                .failureHandler(oauth2AuthenticationFailureHandler)
                .and()
                .logout()
                .permitAll()
                .addLogoutHandler(customCookieDeleteLogoutHandler)
                .logoutSuccessHandler(new CustomLogoutSuccessHandler(objectMapper, customCsrfTokenRepository()));

        List<String> passUrlList = Arrays.asList(
                "/api/external/health-check",
                "/auth/access-token",
                "/auth/validate-auth",
                "/api/user/username-duplicate-check",
                "/api/user/signup",
                "/api/email/duplicate-check",
                "/api/email/key-expire/**",
                "/api/email/send-find-password-email",
                "/api/email/verify-key",
                "/api/user/key-expired/**",
                "/api/user/find-password",
                "/api/board/tree-list",
                "/api/board/sub-list",
                "/api/tag/list",
                "/api/tag/all-list",
                "/api/post/modify-view-count",
                "/api/post/list",
                "/api/post/info",
                "/api/post/vote",
                "/search/**",
                "/api/profile/rank",
                "/api/tag/follower-rank",
                "/api/tag/post-rank",
                "/api/tag/detail",
                "/api/post/tag-post",
                "/api/post/dashboard-fixed-list",
                "/api/post/dashboard-non-fixed-list",
                "/api/profile/info",
                "/api/profile/following-info",
                "/api/profile/follower-info",
                "/api/profile/tag-info",
                "/api/post/post-list-by-profile",
                "/api/post/comment-post-list-by-profile",
                "/api/post/scrap-list-by-profile"
        );

        http.addFilterBefore(createLoginAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(createJwtAuthenticationFilter(passUrlList, DEFAULT_API_URL), UsernamePasswordAuthenticationFilter.class);
    }

    private LoginAuthenticationFilter createLoginAuthenticationFilter() throws Exception {
        LoginAuthenticationFilter loginAuthenticationFilter =
                new LoginAuthenticationFilter(DEFAULT_FILTER_PROCESSES_URL, jwtUtil, loginAuthenticationSuccessHandler, commonAuthenticationFailureHandler);
        loginAuthenticationFilter.setAuthenticationManager(this.authenticationManager());

        return loginAuthenticationFilter;
    }

    private JwtAuthenticationFilter createJwtAuthenticationFilter(List<String> pathUrlList, String checkUrl) throws Exception {
        RequestMatcher requestMatcher = new CustomRequestMatcher(pathUrlList, checkUrl);

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(requestMatcher, commonAuthenticationFailureHandler);
        jwtAuthenticationFilter.setAuthenticationManager(this.authenticationManager());

        return jwtAuthenticationFilter;
    }

}