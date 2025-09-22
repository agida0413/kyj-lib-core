package com.kyj.core.security.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 보안 사용자 정보 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityUser {

    /**
     * 사용자 ID
     */
    private String userId;

    /**
     * 사용자명
     */
    private String username;

    /**
     * 이메일
     */
    private String email;

    /**
     * 권한 정보 (쉼표 구분)
     */
    private String roles;

    /**
     * 인증 여부
     */
    private boolean authenticated;

    /**
     * 익명(비인증) 사용자 생성
     */
    public static SecurityUser anonymous() {
        return SecurityUser.builder()
                .authenticated(false)
                .build();
    }

    /**
     * 인증된 사용자 생성
     */
    public static SecurityUser authenticated(String userId, String username, String email, String roles) {
        return SecurityUser.builder()
                .userId(userId)
                .username(username)
                .email(email)
                .roles(roles)
                .authenticated(true)
                .build();
    }

    /**
     * 특정 권한 보유 여부 확인
     */
    public boolean hasRole(String role) {
        return authenticated && roles != null && roles.contains(role);
    }

    /**
     * 관리자 권한 확인
     */
    public boolean isAdmin() {
        return hasRole("ADMIN") || hasRole("ROLE_ADMIN");
    }
}