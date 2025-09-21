package com.kyj.core.security.client.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 2025-09-21
 * @author 김용준
 * 토큰 블랙리스트 관리 서비스
 * Redis를 사용하여 로그아웃된 토큰이나 무효화된 토큰을 관리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String BLACKLIST_PREFIX = "blacklist:token:";

    /**
     * 토큰을 블랙리스트에 추가
     * @param token JWT 토큰
     * @param expirationTimeInSeconds 만료 시간 (초)
     */
    public void addToBlacklist(String token, long expirationTimeInSeconds) {
        try {
            String key = BLACKLIST_PREFIX + token;
            redisTemplate.opsForValue().set(key, "blacklisted", expirationTimeInSeconds, TimeUnit.SECONDS);
            log.debug("토큰이 블랙리스트에 추가되었습니다: {}", token.substring(0, Math.min(10, token.length())) + "...");
        } catch (Exception e) {
            log.error("토큰 블랙리스트 추가 실패: {}", e.getMessage());
        }
    }

    /**
     * 토큰이 블랙리스트에 있는지 확인
     * @param token JWT 토큰
     * @return 블랙리스트 여부
     */
    public boolean isBlacklisted(String token) {
        try {
            String key = BLACKLIST_PREFIX + token;
            Boolean exists = redisTemplate.hasKey(key);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.error("토큰 블랙리스트 확인 실패: {}", e.getMessage());
            // Redis 연결 실패 시 안전을 위해 false 반환 (비즈니스 로직에 따라 조정 가능)
            return false;
        }
    }

    /**
     * 사용자의 모든 토큰을 블랙리스트에 추가 (전체 로그아웃)
     * @param userId 사용자 ID
     * @param expirationTimeInSeconds 만료 시간 (초)
     */
    public void addUserToBlacklist(String userId, long expirationTimeInSeconds) {
        try {
            String key = BLACKLIST_PREFIX + "user:" + userId;
            redisTemplate.opsForValue().set(key, "blacklisted", expirationTimeInSeconds, TimeUnit.SECONDS);
            log.debug("사용자의 모든 토큰이 블랙리스트에 추가되었습니다: {}", userId);
        } catch (Exception e) {
            log.error("사용자 블랙리스트 추가 실패: {}", e.getMessage());
        }
    }

    /**
     * 사용자가 블랙리스트에 있는지 확인
     * @param userId 사용자 ID
     * @return 블랙리스트 여부
     */
    public boolean isUserBlacklisted(String userId) {
        try {
            String key = BLACKLIST_PREFIX + "user:" + userId;
            Boolean exists = redisTemplate.hasKey(key);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.error("사용자 블랙리스트 확인 실패: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 블랙리스트에서 토큰 제거 (필요한 경우)
     * @param token JWT 토큰
     */
    public void removeFromBlacklist(String token) {
        try {
            String key = BLACKLIST_PREFIX + token;
            redisTemplate.delete(key);
            log.debug("토큰이 블랙리스트에서 제거되었습니다: {}", token.substring(0, Math.min(10, token.length())) + "...");
        } catch (Exception e) {
            log.error("토큰 블랙리스트 제거 실패: {}", e.getMessage());
        }
    }
}