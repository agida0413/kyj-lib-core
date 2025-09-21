package com.kyj.core.security.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 2025-09-21
 * @author 김용준
 * 인증 서버에서 토큰 관리를 위한 서비스 인터페이스
 */
public interface AuthTokenService {

    /**
     * 리프레시 토큰을 Redis에 저장
     * @param key 저장 키 (일반적으로 사용자 ID)
     * @param token 리프레시 토큰
     */
    void addRefreshToken(String key, String token);

    /**
     * 리프레시 토큰을 Redis에서 삭제
     * @param key 저장 키
     * @param token 리프레시 토큰
     */
    void deleteRefreshToken(String key, String token);

    /**
     * 리프레시 토큰 존재 여부 확인
     * @param key 저장 키
     * @param token 리프레시 토큰
     * @return 존재 여부
     */
    boolean isRefreshTokenExists(String key, String token);

    /**
     * 토큰을 블랙리스트에 추가
     * @param token 블랙리스트에 추가할 토큰
     */
    void addToBlacklist(String token);

    /**
     * 토큰이 블랙리스트에 있는지 확인
     * @param token 확인할 토큰
     * @return 블랙리스트 포함 여부
     */
    boolean isTokenBlacklisted(String token);

    /**
     * 액세스 토큰과 리프레시 토큰을 재발급
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @return 재발급 성공 여부
     */
    boolean reissueTokens(HttpServletRequest request, HttpServletResponse response);

    /**
     * 로그아웃 처리 (토큰 무효화)
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @return 로그아웃 성공 여부
     */
    boolean logout(HttpServletRequest request, HttpServletResponse response);

    /**
     * 사용자의 모든 리프레시 토큰 삭제
     * @param userId 사용자 ID
     */
    void deleteAllRefreshTokensByUser(String userId);
}