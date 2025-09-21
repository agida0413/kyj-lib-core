package com.kyj.core.security.auth.dto.oauth2;

import java.util.Map;

/**
 * 2025-09-21
 * @author 김용준
 * Naver OAuth2 사용자 정보 응답 구현체
 */
public class NaverResponse implements OAuth2Response {

    private final Map<String, Object> attributes;

    public NaverResponse(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        return ((Map<String, Object>) attributes.get("response")).get("id").toString();
    }

    @Override
    public String getEmail() {
        return ((Map<String, Object>) attributes.get("response")).get("email").toString();
    }

    @Override
    public String getName() {
        return ((Map<String, Object>) attributes.get("response")).get("name").toString();
    }

    @Override
    public String getNickname() {
        return ((Map<String, Object>) attributes.get("response")).get("nickname").toString();
    }
}