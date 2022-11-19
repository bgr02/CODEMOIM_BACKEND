package com.sideproject.codemoim.handler;

import com.sideproject.codemoim.property.CustomProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class CustomCookieDeleteLogoutHandler implements LogoutHandler {

    private final CustomProperties customProperties;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String[] deleteCookies = {"access_token", "refresh_token", "login_status"};

        for (String deleteCookie : deleteCookies) {
            Cookie[] cookies = request.getCookies();

            if (cookies != null && cookies.length > 0) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(deleteCookie)) {
                        cookie.setDomain(customProperties.getCookieConfig().getRootDomain());
                        cookie.setValue(null);
                        cookie.setPath("/");
                        cookie.setMaxAge(0);
                        response.addCookie(cookie);
                    }
                }
            }
        }
    }

}
