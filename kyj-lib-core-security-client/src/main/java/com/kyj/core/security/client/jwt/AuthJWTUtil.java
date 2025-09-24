package com.kyj.core.security.client.jwt;

import com.kyj.core.security.client.exception.SecurityErrorCode;
import com.kyj.core.security.client.util.SecurityResponseUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * 2025-09-21
 * @author 김용준
 * OAuth2 인증/인가 서버에서 사용하는 JWT 유틸리티
 */
@Slf4j
@Component
public class AuthJWTUtil {

    private final SecretKey secretKey;

    public AuthJWTUtil(@Value("${kyj.security.auth.token.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    /**
     * JWT 토큰 만료 여부 확인
     */
    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    /**
     * JWT 토큰 유효성 검증 (응답 처리 포함)
     */
    public boolean validate(String token, HttpServletResponse response) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            SecurityResponseUtil.writeErrorResponse(response, HttpStatus.UNAUTHORIZED, SecurityErrorCode.SEC002);
            return false;
        } catch (ExpiredJwtException e) {
            SecurityResponseUtil.writeErrorResponse(response, HttpStatus.GONE, SecurityErrorCode.SEC003);
            return false;
        } catch (Exception e) {
            SecurityResponseUtil.writeErrorResponse(response, HttpStatus.UNAUTHORIZED, SecurityErrorCode.SEC005);
            return false;
        }
        return true;
    }

    /**
     * JWT 토큰 유효성 검증 (간단 버전)
     */
    public boolean validate(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 사용자명 추출
     */
    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    /**
     * 토큰 카테고리 추출
     */
    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    /**
     * 사용자 ID 추출
     */
    public String getUserId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userId", String.class);
    }

    /**
     * 역할 정보 추출
     */
    public String getRoles(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("roles", String.class);
    }

    /**
     * 이메일 추출
     */
    public String getEmail(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("email", String.class);
    }

    /**
     * 모든 클레임 추출
     */
    public Claims getAllClaims(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }

    /**
     * JWT 토큰 생성
     * @param category 토큰 카테고리 (access/refresh)
     * @param username 사용자명
     * @param userId 사용자 ID
     * @param email 이메일
     * @param roles 역할 정보
     * @param expiredMs 만료 시간(밀리초)
     */
    public String createJwt(String category, String username, String userId, String email, String roles, Long expiredMs) {
        return Jwts.builder()
                .claim("category", category)
                .claim("username", username)
                .claim("userId", userId)
                .claim("email", email)
                .claim("roles", roles)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    /**
     * 커스텀 클레임으로 JWT 토큰 생성
     */
    public String createJwtWithCustomClaims(Map<String, Object> claims, Long expiredMs) {
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

}