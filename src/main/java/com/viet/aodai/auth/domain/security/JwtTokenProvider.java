package com.viet.aodai.auth.domain.security;


import com.viet.aodai.auth.domain.enumeration.TokenType;
import com.viet.aodai.core.common.exception.AuthException;
import com.viet.aodai.user.domain.entity.User;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Clock;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final JwtProperties jwtProperties;
    private Key key;
    private Clock clock;

    @Value("${spring.jwt.signerKey}")
    private String signerKey;

    @PostConstruct
    public void init(){
        byte[] keyBytes = Decoders.BASE64.decode(signerKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        log.info("signerKey: {}", signerKey);
     //   this.clock = Clock.systemUTC();
    }

    public String generateAccessToken(User user){
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getAccessTokenExpiration());

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole().name());
        claims.put("type", TokenType.ACCESS.name());
        claims.put("userId", user.getUserId());

        return Jwts.builder()
                .setSubject(user.getUserId().toString())
                .setClaims(claims)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

    }

    public String generateRefreshToken(User user, String deviceFingerprint) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getAccessTokenExpiration());

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole().name());
        claims.put("type", TokenType.REFRESH.name());
        claims.put("userId", user.getUserId());

        return Jwts.builder()
                .setSubject(user.getUserId().toString())
                .setClaims(claims)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

    }
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            log.error("JWT token is expired: {}", ex.getMessage());
            throw new AuthException("Token is expired");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
            throw new AuthException("Invalid token format");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
            throw new AuthException("Unsupported token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
            throw new AuthException("Token claims are empty");
        } catch (JwtException ex) {
            log.error("JWT validation failed: {}", ex.getMessage());
            throw new AuthException("Token validation failed");
        }
    }

    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new AuthException("Unable to parse JWT claims");
        }
    }

    public String getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    public Date getExpirationDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

}
