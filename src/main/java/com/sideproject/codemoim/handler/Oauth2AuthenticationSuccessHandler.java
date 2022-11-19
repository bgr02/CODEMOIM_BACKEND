package com.sideproject.codemoim.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sideproject.codemoim.domain.Token;
import com.sideproject.codemoim.domain.User;
import com.sideproject.codemoim.dto.CustomOauth2User;
import com.sideproject.codemoim.exception.BadRequestException;
import com.sideproject.codemoim.exception.ReissueAccessTokenErrorException;
import com.sideproject.codemoim.property.CustomProperties;
import com.sideproject.codemoim.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.sideproject.codemoim.repository.UserRepository;
import com.sideproject.codemoim.service.TokenService;
import com.sideproject.codemoim.service.UserService;
import com.sideproject.codemoim.util.CookieUtils;
import com.sideproject.codemoim.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import static com.sideproject.codemoim.repository.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class Oauth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final ObjectMapper objectMapper;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final CustomProperties customProperties;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (response.isCommitted()) {
            logger.debug("This request has already been processed.");

            return;
        }

        CustomOauth2User principal = (CustomOauth2User) authentication.getPrincipal();

        String userId = principal.getName();
        String provider = principal.getProvider();

        Optional<User> optionalUser = userRepository.searchUserByIdAndStatus(Long.parseLong(userId));

        User user = optionalUser.orElseThrow(() -> {
            throw new ReissueAccessTokenErrorException("Invalid User.");
        });

        tokenService.deleteTokenByUserAndExpiredDate(user);

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String accessToken = jwtUtil.createAccessToken(userId, provider, authorities);
        String refreshToken = jwtUtil.createRefreshToken(userId, provider, authorities);

        Token token = Token.builder()
                .user(user)
                .refreshToken(refreshToken)
                .expiredDate(LocalDateTime.now().plusSeconds(customProperties.getToken().getRefreshTokenExpiredTime() / 1000))
                .provider(provider)
                .build();

        tokenService.createToken(token);

        String accessTokenCookie = CookieUtils.createTokenCookie("access_token", customProperties.getCookieConfig().getRootDomain(), accessToken, customProperties.getToken().getTokenExpiredTime(), customProperties.getCookieConfig().getHttpOnly(), customProperties.getCookieConfig().getSecure());
        String refreshTokenCookie = CookieUtils.createTokenCookie("refresh_token", customProperties.getCookieConfig().getRootDomain(), refreshToken, customProperties.getToken().getRefreshTokenExpiredTime(), customProperties.getCookieConfig().getHttpOnly(), customProperties.getCookieConfig().getSecure());

        String loginStatusCookie = ResponseCookie.from("login_status", "loggingIn")
                .domain(customProperties.getCookieConfig().getRootDomain())
                .path("/")
                .maxAge(-1)
                .secure(customProperties.getCookieConfig().getHttpOnly())
                .sameSite("Lax")
                .build()
                .toString();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie);
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie);
        response.addHeader(HttpHeaders.SET_COOKIE, loginStatusCookie);

        String targetUrl = determineTargetUrl(request, response, authentication);

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new BadRequestException("Unsupported Redirect Uri request.");
        }

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

//        return UriComponentsBuilder.fromUriString(targetUrl)
//                .queryParam("expired_time", customProperties.getToken().getTokenExpiredTime())
//                .build().toUriString();

        return targetUrl;
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        return customProperties.getOauth2().getAuthorizedRedirectUris()
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    URI authorizedURI = URI.create(authorizedRedirectUri);

                    if (authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort()) {
                        return true;
                    }

                    return false;
                });
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

}
