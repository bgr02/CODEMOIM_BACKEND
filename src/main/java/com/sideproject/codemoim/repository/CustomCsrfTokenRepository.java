package com.sideproject.codemoim.repository;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

public final class CustomCsrfTokenRepository implements CsrfTokenRepository {

    static final String DEFAULT_CSRF_COOKIE_NAME = "XSRF-TOKEN";

    static final String DEFAULT_CSRF_PARAMETER_NAME = "_csrf";

    static final String DEFAULT_CSRF_HEADER_NAME = "X-XSRF-TOKEN";

    private String parameterName = DEFAULT_CSRF_PARAMETER_NAME;

    private String headerName = DEFAULT_CSRF_HEADER_NAME;

    private String cookieName = DEFAULT_CSRF_COOKIE_NAME;

    private boolean cookieHttpOnly = true;

    private String cookiePath;

    private String cookieDomain;

    private Boolean secure;

    public CustomCsrfTokenRepository() {
    }

    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        return new DefaultCsrfToken(this.headerName, this.parameterName, createNewToken());
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        String tokenValue = (token != null) ? token.getToken() : "";

        ResponseCookie cookie;

        if (StringUtils.hasLength(this.cookieDomain)) {
            cookie = ResponseCookie.from(this.cookieName, tokenValue)
                    .secure((this.secure != null) ? this.secure : request.isSecure())
                    .path(StringUtils.hasLength(this.cookiePath) ? this.cookiePath : this.getRequestContext(request))
                    .maxAge((token != null) ? -1 : 0)
                    .httpOnly(this.cookieHttpOnly)
                    .sameSite("Strict")
                    .domain(this.cookieDomain)
                    .build();
        } else {
            cookie = ResponseCookie.from(this.cookieName, tokenValue)
                    .secure((this.secure != null) ? this.secure : request.isSecure())
                    .path(StringUtils.hasLength(this.cookiePath) ? this.cookiePath : this.getRequestContext(request))
                    .maxAge((token != null) ? -1 : 0)
                    .httpOnly(this.cookieHttpOnly)
                    .sameSite("Strict")
                    .build();
        }

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, this.cookieName);
        if (cookie == null) {
            return null;
        }
        String token = cookie.getValue();
        if (!StringUtils.hasLength(token)) {
            return null;
        }
        return new DefaultCsrfToken(this.headerName, this.parameterName, token);
    }

    /**
     * Sets the name of the HTTP request parameter that should be used to provide a token.
     * @param parameterName the name of the HTTP request parameter that should be used to
     * provide a token
     */
    public void setParameterName(String parameterName) {
        Assert.notNull(parameterName, "parameterName is not null");
        this.parameterName = parameterName;
    }

    /**
     * Sets the name of the HTTP header that should be used to provide the token.
     * @param headerName the name of the HTTP header that should be used to provide the
     * token
     */
    public void setHeaderName(String headerName) {
        Assert.notNull(headerName, "headerName is not null");
        this.headerName = headerName;
    }

    /**
     * Sets the name of the cookie that the expected CSRF token is saved to and read from.
     * @param cookieName the name of the cookie that the expected CSRF token is saved to
     * and read from
     */
    public void setCookieName(String cookieName) {
        Assert.notNull(cookieName, "cookieName is not null");
        this.cookieName = cookieName;
    }

    /**
     * Sets the HttpOnly attribute on the cookie containing the CSRF token. Defaults to
     * <code>true</code>.
     * @param cookieHttpOnly <code>true</code> sets the HttpOnly attribute,
     * <code>false</code> does not set it
     */
    public void setCookieHttpOnly(boolean cookieHttpOnly) {
        this.cookieHttpOnly = cookieHttpOnly;
    }

    private String getRequestContext(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        return (contextPath.length() > 0) ? contextPath : "/";
    }

    /**
     * Factory method to conveniently create an instance that has
     * {@link #setCookieHttpOnly(boolean)} set to false.
     * @return an instance of CookieCsrfTokenRepository with
     * {@link #setCookieHttpOnly(boolean)} set to false
     */
    public static CustomCsrfTokenRepository withHttpOnlyFalse() {
        CustomCsrfTokenRepository result = new CustomCsrfTokenRepository();
        result.setCookieHttpOnly(false);
        return result;
    }

    private String createNewToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Set the path that the Cookie will be created with. This will override the default
     * functionality which uses the request context as the path.
     * @param path the path to use
     */
    public void setCookiePath(String path) {
        this.cookiePath = path;
    }

    /**
     * Get the path that the CSRF cookie will be set to.
     * @return the path to be used.
     */
    public String getCookiePath() {
        return this.cookiePath;
    }

    /**
     * Sets the domain of the cookie that the expected CSRF token is saved to and read
     * from.
     * @param cookieDomain the domain of the cookie that the expected CSRF token is saved
     * to and read from
     * @since 5.2
     */
    public void setCookieDomain(String cookieDomain) {
        this.cookieDomain = cookieDomain;
    }

    /**
     * Sets secure flag of the cookie that the expected CSRF token is saved to and read
     * from. By default secure flag depends on {@link ServletRequest#isSecure()}
     * @param secure the secure flag of the cookie that the expected CSRF token is saved
     * to and read from
     * @since 5.4
     */
    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

}