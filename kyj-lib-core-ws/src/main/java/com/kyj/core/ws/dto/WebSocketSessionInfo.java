package com.kyj.core.ws.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 2025-09-24
 * @author 김용준
 * 웹소켓 세션 정보 DTO
 */
@Getter
@Builder
public class WebSocketSessionInfo {

    /**
     * 세션 ID
     */
    private final String sessionId;

    /**
     * 사용자 ID
     */
    private final String userId;

    /**
     * 사용자명
     */
    private final String username;

    /**
     * 원격 주소
     */
    private final String remoteAddress;

    /**
     * 사용자 에이전트
     */
    private final String userAgent;

    /**
     * 연결 시간
     */
    private final LocalDateTime connectTime;

    /**
     * 마지막 활동 시간
     */
    private final LocalDateTime lastActivity;

    /**
     * 세션 속성
     */
    private final Map<String, Object> attributes;

    /**
     * 세션 상태
     */
    private final SessionStatus status;

    /**
     * 인증 여부
     */
    private final boolean authenticated;

    /**
     * 권한 정보
     */
    private final String[] roles;

    /**
     * 세션 상태 열거형
     */
    public enum SessionStatus {
        CONNECTING,     // 연결 중
        CONNECTED,      // 연결됨
        AUTHENTICATED,  // 인증됨
        DISCONNECTING,  // 연결 해제 중
        DISCONNECTED    // 연결 해제됨
    }

    /**
     * 연결 지속 시간 (밀리초)
     */
    public long getConnectionDuration() {
        if (connectTime == null) {
            return 0;
        }
        return java.time.Duration.between(connectTime, LocalDateTime.now()).toMillis();
    }

    /**
     * 마지막 활동 이후 경과 시간 (밀리초)
     */
    public long getIdleTime() {
        if (lastActivity == null) {
            return 0;
        }
        return java.time.Duration.between(lastActivity, LocalDateTime.now()).toMillis();
    }

    /**
     * 세션이 활성 상태인지 확인
     */
    public boolean isActive() {
        return status == SessionStatus.CONNECTED || status == SessionStatus.AUTHENTICATED;
    }

    /**
     * 세션이 인증된 상태인지 확인
     */
    public boolean isAuthenticated() {
        return authenticated && status == SessionStatus.AUTHENTICATED;
    }

    /**
     * 특정 권한을 가지고 있는지 확인
     */
    public boolean hasRole(String role) {
        if (roles == null || role == null) {
            return false;
        }
        for (String userRole : roles) {
            if (role.equals(userRole)) {
                return true;
            }
        }
        return false;
    }
}