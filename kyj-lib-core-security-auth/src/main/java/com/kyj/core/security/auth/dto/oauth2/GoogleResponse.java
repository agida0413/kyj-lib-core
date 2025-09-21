package com.kyj.core.security.auth.dto.oauth2;

import java.util.Map;

/**
 * 2025-09-21
 * @author 김용준
 * Google OAuth2 사용자 정보 응답 구현체
 */
public class GoogleResponse implements OAuth2Response {

    private final Map<String, Object> attributes;

    public GoogleResponse(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderId() {
        return attributes.get("sub").toString();
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }

    @Override
    public String getNickname() {
        return attributes.get("name").toString();
    }
}