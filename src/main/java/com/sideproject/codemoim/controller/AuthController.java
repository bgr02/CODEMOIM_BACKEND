package com.sideproject.codemoim.controller;

import com.sideproject.codemoim.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    //private final CookieCsrfTokenRepository cookieCsrfTokenRepository;

    /*@GetMapping("/csrf-token")
    public void createCsrfToken(HttpServletRequest request) {
        CsrfToken csrf = cookieCsrfTokenRepository.loadToken(request);

        if (csrf == null) {
            csrf = cookieCsrfTokenRepository.generateToken(request);
        }
    }*/

    @PostMapping("/access-token")
    public void reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {
        authService.reissueAccessToken(request, response);
    }

    @GetMapping("/clear-cookie")
    public void clearCookie(HttpServletRequest request, HttpServletResponse response) {
        authService.clearCookie(request, response);
    }

    @GetMapping("/validate-auth")
    public boolean validateAuth(HttpServletRequest request, HttpServletResponse response) {
        return authService.validateAuth(request);
    }

}
