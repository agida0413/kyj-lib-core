package com.kyj.core.security.client.service;

/**
 * 토큰 블랙리스트 서비스 인터페이스
 * 사용자가 커스텀 구현 가능하도록 인터페이스로 분리
 */
public interface TokenBlacklistService {



    /**
     * 토큰이 블랙리스트에 있는지 확인
     * @param token JWT 토큰
     * @return 블랙리스트 여부
     */
    boolean isTokenBlacklisted(String token);

    /**
     * 사용자가 블랙리스트에 있는지 확인
     * @param userId 사용자 ID
     * @return 블랙리스트 여부
     */
    boolean isUserBlacklisted(String userId);


}