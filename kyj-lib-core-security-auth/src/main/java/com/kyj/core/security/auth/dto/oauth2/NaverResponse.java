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
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response == null) {
            return null;
        }
        Object id = response.get("id");
        return id != null ? id.toString() : null;
    }

    @Override
    public String getEmail() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response == null) {
            return null;
        }
        Object email = response.get("email");
        return email != null ? email.toString() : null;
    }

    @Override
    public String getName() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response == null) {
            return null;
        }
        Object name = response.get("name");
        return name != null ? name.toString() : null;
    }

    @Override
    public String getNickname() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response == null) {
            return null;
        }
        Object nickname = response.get("nickname");
        return nickname != null ? nickname.toString() : null;
    }

    @Override
    public String getProfile() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response == null) {
            return null;
        }
        Object profileObj = response.get("profile_image");
        return profileObj != null ? profileObj.toString() : null;
    }
}