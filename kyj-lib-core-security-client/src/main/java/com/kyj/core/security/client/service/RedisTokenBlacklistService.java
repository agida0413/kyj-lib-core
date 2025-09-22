package com.kyj.core.security.client.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * Redis 기반 블랙리스트 서비스 기본 구현체
 * TokenBlacklistService 인터페이스 구현
 */
@Slf4j
@RequiredArgsConstructor
public class RedisTokenBlacklistService implements TokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String TOKEN_BLACKLIST_PREFIX = "security:blacklist:token:";
    private static final String USER_BLACKLIST_PREFIX = "security:blacklist:user:";



    @Override
    public boolean isTokenBlacklisted(String token) {
        try {
            String key = TOKEN_BLACKLIST_PREFIX + token;
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("토큰 블랙리스트 확인 실패: {}", e.getMessage());
            return false; // 에러 시 통과 처리 (보안상 false 반환)
        }
    }

    @Override
    public boolean isUserBlacklisted(String userId) {
        try {
            String key = USER_BLACKLIST_PREFIX + userId;
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("사용자 블랙리스트 확인 실패: {}", e.getMessage());
            return false; // 에러 시 통과 처리 (보안상 false 반환)
        }
    }


}