package com.sideproject.codemoim.util;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

public class CustomRequestMatcher implements RequestMatcher {

    private final OrRequestMatcher pathRequestMatcher;
    private final RequestMatcher checkPathMatcher;

    public CustomRequestMatcher(List<String> pathUrlList, String checkUrl) {
        pathRequestMatcher = new OrRequestMatcher(pathUrlList.stream().map(AntPathRequestMatcher::new).collect(Collectors.toList()));

        checkPathMatcher = new AntPathRequestMatcher(checkUrl);
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        if (pathRequestMatcher.matches(request)) {
            return false;
        }

        return checkPathMatcher.matches(request);
    }

}
