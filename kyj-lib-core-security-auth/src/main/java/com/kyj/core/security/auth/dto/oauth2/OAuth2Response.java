package com.kyj.core.security.auth.dto.oauth2;

/**
 * 2025-09-21
 * @author 김용준
 * OAuth2 제공자별 응답 정보를 추상화한 인터페이스
 */
public interface OAuth2Response {

    /**
     * 제공자 이름 반환 (google, naver 등)
     */
    String getProvider();

    /**
     * 제공자에서의 사용자 ID 반환
     */
    String getProviderId();

    /**
     * 이메일 반환
     */
    String getEmail();

    /**
     * 사용자명 반환
     */
    String getName();

    /**
     * 닉네임 반환
     */
    String getNickname();

    /**
     *
     * 프로필이미지 반환
     */
    String getProfile();
}