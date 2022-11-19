package com.sideproject.codemoim.util;

import com.sideproject.codemoim.exception.JwtExpiredTokenException;
import com.sideproject.codemoim.property.CustomProperties;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final CustomProperties customProperties;

    public String createAccessToken(String userId, String provider, Collection<? extends GrantedAuthority> authorities) {
        long accessTokenExpiredTime = customProperties.getToken().getTokenExpiredTime();

        return createToken(userId, provider, authorities, accessTokenExpiredTime);
    }

    public String createRefreshToken(String userId, String provider, Collection<? extends GrantedAuthority> authorities) {
        long refreshTokenExpiredTime = customProperties.getToken().getRefreshTokenExpiredTime();

        return createToken(userId, provider, authorities, refreshTokenExpiredTime);
    }

    public String createToken(String userId, String provider, Collection<? extends GrantedAuthority> authorities, long tokenExpiredTime) {
        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("provider", provider);
        claims.put("roles", authorities.stream().map(role -> role.toString()).collect(Collectors.toList()));

        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + tokenExpiredTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer("codemoim")
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS512, customProperties.getToken().getSecretKey())
                .compact();
    }

    public Jws<Claims> parserToken(String token) throws BadCredentialsException, JwtExpiredTokenException {
        Jws<Claims> claimsJws = null;

        try {
            claimsJws = Jwts.parser().setSigningKey(customProperties.getToken().getSecretKey()).parseClaimsJws(token);
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | SignatureException e) {
            throw new BadCredentialsException("Invalid JWT: ", e);
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredTokenException("Token Expired: ", e);
        }

        return claimsJws;
    }
}
