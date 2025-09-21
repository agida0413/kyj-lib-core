package com.kyj.core.security.auth.dto.oauth2;

import java.util.Map;

/**
 * 2025-09-21
 * @author 김용준
 * Kakao OAuth2 사용자 정보 응답 구현체
 */
public class KakaoResponse implements OAuth2Response {

    private final Map<String, Object> attributes;

    public KakaoResponse(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getEmail() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount == null) {
            return null;
        }
        return kakaoAccount.get("email") != null ? kakaoAccount.get("email").toString() : null;
    }

    @Override
    public String getName() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount == null) {
            return null;
        }
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        if (profile == null) {
            return null;
        }
        return profile.get("nickname") != null ? profile.get("nickname").toString() : null;
    }

    @Override
    public String getNickname() {
        return getName(); // 카카오에서는 nickname이 name과 같음
    }
}