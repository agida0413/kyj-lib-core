package com.kyj.core.security.client.util;

import com.kyj.core.security.client.dto.SecurityUserInfo;

/**
 * 2025-09-21
 * @author 김용준
 * 정적 메서드로 현재 요청의 사용자 정보에 접근할 수 있는 유틸리티 클래스
 * ThreadLocal을 사용하여 요청별로 사용자 정보를 저장
 */
public class SecurityHolder {

    private static final ThreadLocal<SecurityUserInfo> userInfoHolder = new ThreadLocal<>();

    /**
     * 현재 스레드에 사용자 정보 저장
     * @param userInfo 사용자 정보
     */
    public static void setUserInfo(SecurityUserInfo userInfo) {
        userInfoHolder.set(userInfo);
    }

    /**
     * 현재 스레드의 사용자 정보 조회
     * @return 사용자 정보 (없으면 인증되지 않은 상태)
     */
    public static SecurityUserInfo getUserInfo() {
        SecurityUserInfo userInfo = userInfoHolder.get();
        return userInfo != null ? userInfo : SecurityUserInfo.unauthenticated();
    }

    /**
     * 현재 사용자가 인증되었는지 확인
     * @return 인증 여부
     */
    public static boolean isAuthenticated() {
        return getUserInfo().isAuthenticated();
    }

    /**
     * 현재 사용자명 조회
     * @return 사용자명 (인증되지 않은 경우 null)
     */
    public static String getUsername() {
        SecurityUserInfo userInfo = getUserInfo();
        return userInfo.isAuthenticated() ? userInfo.getUsername() : null;
    }

    /**
     * 현재 사용자 ID 조회
     * @return 사용자 ID (인증되지 않은 경우 null)
     */
    public static String getUserId() {
        SecurityUserInfo userInfo = getUserInfo();
        return userInfo.isAuthenticated() ? userInfo.getUserId() : null;
    }

    /**
     * 현재 사용자 이메일 조회
     * @return 이메일 (인증되지 않은 경우 null)
     */
    public static String getEmail() {
        SecurityUserInfo userInfo = getUserInfo();
        return userInfo.isAuthenticated() ? userInfo.getEmail() : null;
    }

    /**
     * 현재 사용자 권한 조회
     * @return 권한 정보 (인증되지 않은 경우 null)
     */
    public static String getRoles() {
        SecurityUserInfo userInfo = getUserInfo();
        return userInfo.isAuthenticated() ? userInfo.getRoles() : null;
    }

    /**
     * 현재 사용자가 특정 권한을 가지고 있는지 확인
     * @param role 확인할 권한
     * @return 권한 보유 여부
     */
    public static boolean hasRole(String role) {
        String roles = getRoles();
        return roles != null && roles.contains(role);
    }

    /**
     * 인증이 필요한 경우 예외 발생
     * @throws com.kyj.core.security.client.exception.AuthenticationException 인증되지 않은 경우
     */
    public static void requireAuthenticated() {
        if (!isAuthenticated()) {
            throw new com.kyj.core.security.client.exception.AuthenticationException();
        }
    }

    /**
     * 특정 권한이 필요한 경우 예외 발생
     * @param role 필요한 권한
     * @throws com.kyj.core.security.client.exception.AuthenticationException 인증되지 않은 경우
     * @throws com.kyj.core.security.client.exception.AuthorizationException 권한이 없는 경우
     */
    public static void requireRole(String role) {
        requireAuthenticated();
        if (!hasRole(role)) {
            throw new com.kyj.core.security.client.exception.AuthorizationException(
                com.kyj.core.security.client.exception.SecurityErrCode.SEC_ROLE_002,
                "필요한 권한이 없습니다: " + role
            );
        }
    }

    /**
     * ThreadLocal 정리 (필터에서 요청 완료 후 호출)
     */
    public static void clear() {
        userInfoHolder.remove();
    }
}