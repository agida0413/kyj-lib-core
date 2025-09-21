package com.kyj.core.security.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 2025-09-21
 * @author 김용준
 * JWT 토큰에서 추출한 사용자 정보를 담는 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityUserInfo {

    /**
     * 사용자명
     */
    private String username;

    /**
     * 사용자 ID
     */
    private String userId;

    /**
     * 이메일
     */
    private String email;

    /**
     * 역할/권한 정보
     */
    private String roles;

    /**
     * 토큰 카테고리 (access/refresh)
     */
    private String category;

    /**
     * 사용자 인증 여부
     */
    private boolean authenticated;

    /**
     * 인증되지 않은 사용자 정보 생성
     */
    public static SecurityUserInfo unauthenticated() {
        return SecurityUserInfo.builder()
                .authenticated(false)
                .build();
    }

    /**
     * 인증된 사용자 정보 생성
     */
    public static SecurityUserInfo authenticated(String username, String userId, String email, String roles, String category) {
        return SecurityUserInfo.builder()
                .username(username)
                .userId(userId)
                .email(email)
                .roles(roles)
                .category(category)
                .authenticated(true)
                .build();
    }
}