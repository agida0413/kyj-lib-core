package com.kyj.core.ws.service;

import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Set;

/**
 * 2025-09-24
 * @author 김용준
 * 웹소켓 세션 관리 서비스 인터페이스
 */
public interface WebSocketSessionService {

    /**
     * 세션 추가
     */
    void addSession(String sessionId, WebSocketSession session);

    /**
     * 세션 제거
     */
    void removeSession(String sessionId);

    /**
     * 세션 조회
     */
    WebSocketSession getSession(String sessionId);

    /**
     * 모든 세션 ID 조회
     */
    Set<String> getAllSessionIds();

    /**
     * 활성 세션 수 조회
     */
    int getActiveSessionCount();

    /**
     * 사용자별 세션 추가
     */
    void addUserSession(String userId, String sessionId);

    /**
     * 사용자별 세션 제거
     */
    void removeUserSession(String userId, String sessionId);

    /**
     * 사용자의 모든 세션 조회
     */
    Set<String> getUserSessions(String userId);

    /**
     * 세션 속성 설정
     */
    void setSessionAttribute(String sessionId, String key, Object value);

    /**
     * 세션 속성 조회
     */
    Object getSessionAttribute(String sessionId, String key);

    /**
     * 세션의 모든 속성 조회
     */
    Map<String, Object> getSessionAttributes(String sessionId);

    /**
     * 세션 유효성 검사
     */
    boolean isSessionValid(String sessionId);

    /**
     * 세션 정리 (만료된 세션 제거)
     */
    void cleanupExpiredSessions();
}