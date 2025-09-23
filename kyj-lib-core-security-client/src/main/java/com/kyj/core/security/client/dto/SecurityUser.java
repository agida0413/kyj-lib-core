package com.kyj.core.security.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
     * 권한 구분자
     */
    public static final String ROLE_DELIMITER = ",";

    /**
     * 인증 여부
     */
    private boolean authenticated;

    /**
     * 토큰 만료 여부
     */
    private boolean expired;

    /**
     * 익명(비인증) 사용자 생성
     */
    public static SecurityUser anonymous() {
        return SecurityUser.builder()
                .authenticated(false)
                .expired(false)
                .build();
    }

    /**
     * 토큰 만료된 사용자 생성
     */
    public static SecurityUser expired() {
        return SecurityUser.builder()
                .authenticated(false)
                .expired(true)
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
                .expired(false)
                .build();
    }

    /**
     * 특정 권한 보유 여부 확인
     */
    public boolean hasRole(String role) {
        if (!authenticated || !StringUtils.hasText(roles) || !StringUtils.hasText(role)) {
            return false;
        }

        // 정확한 매칭을 위해 쉼표로 분리하여 체크
        return Arrays.stream(roles.split(ROLE_DELIMITER))
                .map(String::trim)
                .anyMatch(r -> r.equals(role.trim()));
    }



    /**
     * 여러 권한 중 하나라도 보유 여부 확인
     */
    public boolean hasAnyRole(String... roles) {
        if (!authenticated || roles == null || roles.length == 0) {
            return false;
        }

        for (String role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 관리자 권한 확인
     */
    public boolean isAdmin() {
        return hasRole("ROLE_ADMIN");
    }


}