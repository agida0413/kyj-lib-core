package com.kyj.core.security.client.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 2025-09-21
 * @author 김용준
 * MSA 서비스에서 AWS API Gateway로부터 전달받은 JWT 토큰을 파싱하는 유틸리티
 * API Gateway에서 이미 검증된 토큰을 처리하므로 추가 검증은 선택적으로 수행
 */
@Slf4j
@Component
public class ClientJWTUtil {

    private final SecretKey secretKey;

    public ClientJWTUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    /**
     * JWT 토큰에서 모든 클레임을 추출
     * @param token JWT 토큰
     * @return Claims 객체
     */
    public Claims getAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("JWT 토큰 파싱 실패: {}", e.getMessage());
            throw new RuntimeException("유효하지 않은 JWT 토큰입니다.", e);
        }
    }

    /**
     * JWT 토큰 유효성 검증 (선택적 사용)
     * @param token JWT 토큰
     * @return 유효성 여부
     */
    public boolean isValid(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException |
                 UnsupportedJwtException | IllegalArgumentException e) {
            log.warn("JWT 토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    /**
     * JWT 토큰 만료 여부 확인
     * @param token JWT 토큰
     * @return 만료 여부
     */
    public boolean isExpired(String token) {
        try {
            Date expiration = getAllClaims(token).getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            log.error("JWT 토큰 만료 여부 확인 실패: {}", e.getMessage());
            return true;
        }
    }

    /**
     * 사용자명 추출
     * @param token JWT 토큰
     * @return 사용자명
     */
    public String getUsername(String token) {
        return getAllClaims(token).get("username", String.class);
    }

    /**
     * 사용자 ID 추출
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public String getUserId(String token) {
        return getAllClaims(token).get("userId", String.class);
    }

    /**
     * 이메일 추출
     * @param token JWT 토큰
     * @return 이메일
     */
    public String getEmail(String token) {
        return getAllClaims(token).get("email", String.class);
    }

    /**
     * 역할(권한) 추출
     * @param token JWT 토큰
     * @return 역할 정보
     */
    public String getRoles(String token) {
        return getAllClaims(token).get("roles", String.class);
    }

    /**
     * 토큰 카테고리 추출 (access/refresh)
     * @param token JWT 토큰
     * @return 토큰 카테고리
     */
    public String getCategory(String token) {
        return getAllClaims(token).get("category", String.class);
    }

    /**
     * 커스텀 클레임 추출
     * @param token JWT 토큰
     * @param claimName 클레임 이름
     * @param claimType 클레임 타입
     * @return 클레임 값
     */
    public <T> T getCustomClaim(String token, String claimName, Class<T> claimType) {
        return getAllClaims(token).get(claimName, claimType);
    }
}