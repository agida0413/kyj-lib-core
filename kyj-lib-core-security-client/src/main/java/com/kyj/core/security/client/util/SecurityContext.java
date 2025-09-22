package com.kyj.core.security.client.util;

import com.kyj.core.api.CmErrCode;
import com.kyj.core.exception.custom.KyjAccessException;
import com.kyj.core.exception.custom.KyjAuthException;
import com.kyj.core.security.client.dto.SecurityUser;

/**
 * 보안 컨텍스트 유틸리티
 * 현재 요청의 사용자 정보에 대한 편의 메서드 제공
 */
public class SecurityContext {

    private static final ThreadLocal<SecurityUser> userHolder = new ThreadLocal<>();

    /**
     * 현재 사용자 정보 설정 (필터에서만 사용)
     */
    public static void setUser(SecurityUser user) {
        userHolder.set(user);
    }

    /**
     * 현재 사용자 정보 조회
     */
    public static SecurityUser getUser() {
        SecurityUser user = userHolder.get();
        return user != null ? user : SecurityUser.anonymous();
    }

    /**
     * 현재 사용자 ID 조회
     */
    public static String getUserId() {
        SecurityUser user = getUser();
        return user.isAuthenticated() ? user.getUserId() : null;
    }

    /**
     * 현재 사용자명 조회
     */
    public static String getUsername() {
        SecurityUser user = getUser();
        return user.isAuthenticated() ? user.getUsername() : null;
    }

    /**
     * 현재 사용자 이메일 조회
     */
    public static String getEmail() {
        SecurityUser user = getUser();
        return user.isAuthenticated() ? user.getEmail() : null;
    }

    /**
     * 현재 사용자 권한 조회
     */
    public static String getRoles() {
        SecurityUser user = getUser();
        return user.isAuthenticated() ? user.getRoles() : null;
    }

    /**
     * 현재 사용자 인증 여부 확인
     */
    public static boolean isAuthenticated() {
        return getUser().isAuthenticated();
    }

    /**
     * 현재 사용자가 특정 권한을 가지고 있는지 확인
     */
    public static boolean hasRole(String role) {
        return getUser().hasRole(role);
    }

    /**
     * 현재 사용자가 관리자인지 확인
     */
    public static boolean isAdmin() {
        return getUser().isAdmin();
    }

    /**
     * 인증 필수 검증 (인증되지 않으면 예외 발생)
     */
    public static void requireAuthenticated() {
        if (!isAuthenticated()) {
            throw new KyjAuthException(CmErrCode.SEC010);
        }
    }

    /**
     * 특정 권한 필수 검증
     */
    public static void requireRole(String role) {
        requireAuthenticated();
        if (!hasRole(role)) {
            throw new KyjAccessException(CmErrCode.SEC011);
        }
    }

    /**
     * 관리자 권한 필수 검증
     */
    public static void requireAdmin() {
        requireAuthenticated();
        if (!isAdmin()) {
            throw new KyjAccessException(CmErrCode.SEC011);
        }
    }

    /**
     * 컨텍스트 정리 (필터에서만 사용)
     */
    public static void clear() {
        userHolder.remove();
    }
}