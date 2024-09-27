package com.goormdari.global.config.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Slf4j
@Component
public class JWTUtil {

    private final SecretKey secretKey;
    private final long jwtExpirationInMs;

    public JWTUtil(@Value("${app.auth.token-secret}") String secretKey,
                   @Value("${app.auth.access-token-expiration-msec}") long jwtExpirationInMs) {

        this.secretKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    /**
     JWT 토큰 생성

     @Param username 사용자 이름
     @return 생성된 JWT 토큰
     */
    public String generateToken(String username, String role) {

        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, claims -> claims.get("username", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).build().parseSignedClaims(token).getBody();
    }

    /**
     * JWT 토큰의 유효성 검증
     *
     * @param token JWT 토큰
     * @return 토큰 유효성
     */
    public Boolean validateToken(String token) {
        final String username = extractUsername(token);
        return (!isTokenExpired(token));
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * JWT 토큰에서 사용자 이름 추출
     *
     * @param token JWT 토큰
     * @return 사용자 이름
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username").toString();
    }

    /**
     * JWT 토큰에서 권한 추출
     *
     * @param token JWT 토큰
     * @return 사용자 권한
     */
    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role").toString();
    }

    /**
     * JWT 토큰이 만료되었는지 확인
     *
     * @param token JWT 토큰
     * @return 키 만료 여부
     */
    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }
}
