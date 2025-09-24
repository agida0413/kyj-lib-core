package com.kyj.core.ws.service;

import com.kyj.core.ws.config.WebSocketProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 2025-09-24
 * @author 김용준
 * 웹소켓 세션 관리 서비스 구현체
 */
@Slf4j
@RequiredArgsConstructor
public class WebSocketSessionServiceImpl implements WebSocketSessionService {

    private final WebSocketProperties webSocketProperties;

    // 세션 저장소
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    // 사용자별 세션 매핑
    private final Map<String, Set<String>> userSessions = new ConcurrentHashMap<>();

    // 세션별 속성 저장소
    private final Map<String, Map<String, Object>> sessionAttributes = new ConcurrentHashMap<>();

    // 세션 생성 시간
    private final Map<String, Long> sessionCreationTimes = new ConcurrentHashMap<>();

    @Override
    public void addSession(String sessionId, WebSocketSession session) {
        sessions.put(sessionId, session);
        sessionCreationTimes.put(sessionId, System.currentTimeMillis());
        log.debug("세션 추가: {}", sessionId);

        // 최대 세션 수 체크
        checkMaxSessions();
    }

    @Override
    public void removeSession(String sessionId) {
        WebSocketSession removed = sessions.remove(sessionId);
        sessionAttributes.remove(sessionId);
        sessionCreationTimes.remove(sessionId);

        // 사용자 세션에서도 제거
        userSessions.values().forEach(sessionSet -> sessionSet.remove(sessionId));

        if (removed != null) {
            log.debug("세션 제거: {}", sessionId);
        }
    }

    @Override
    public WebSocketSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    @Override
    public Set<String> getAllSessionIds() {
        return new HashSet<>(sessions.keySet());
    }

    @Override
    public int getActiveSessionCount() {
        return sessions.size();
    }

    @Override
    public void addUserSession(String userId, String sessionId) {
        userSessions.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
        log.debug("사용자 세션 매핑 추가: {} -> {}", userId, sessionId);
    }

    @Override
    public void removeUserSession(String userId, String sessionId) {
        Set<String> sessionSet = userSessions.get(userId);
        if (sessionSet != null) {
            sessionSet.remove(sessionId);
            if (sessionSet.isEmpty()) {
                userSessions.remove(userId);
            }
        }
        log.debug("사용자 세션 매핑 제거: {} -> {}", userId, sessionId);
    }

    @Override
    public Set<String> getUserSessions(String userId) {
        return new HashSet<>(userSessions.getOrDefault(userId, Set.of()));
    }

    @Override
    public void setSessionAttribute(String sessionId, String key, Object value) {
        sessionAttributes.computeIfAbsent(sessionId, k -> new ConcurrentHashMap<>()).put(key, value);
    }

    @Override
    public Object getSessionAttribute(String sessionId, String key) {
        Map<String, Object> attributes = sessionAttributes.get(sessionId);
        return attributes != null ? attributes.get(key) : null;
    }

    @Override
    public Map<String, Object> getSessionAttributes(String sessionId) {
        return new HashMap<>(sessionAttributes.getOrDefault(sessionId, Map.of()));
    }

    @Override
    public boolean isSessionValid(String sessionId) {
        WebSocketSession session = sessions.get(sessionId);
        if (session == null) {
            return false;
        }

        // 세션이 열려있는지 확인
        if (!session.isOpen()) {
            removeSession(sessionId);
            return false;
        }

        // 타임아웃 체크
        if (webSocketProperties.getSessionManagement().getSessionTimeout() > 0) {
            Long creationTime = sessionCreationTimes.get(sessionId);
            if (creationTime != null) {
                long elapsed = System.currentTimeMillis() - creationTime;
                long timeout = webSocketProperties.getSessionManagement().getSessionTimeout() * 1000;
                if (elapsed > timeout) {
                    log.debug("세션 타임아웃: {}", sessionId);
                    removeSession(sessionId);
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void cleanupExpiredSessions() {
        log.debug("만료된 세션 정리 시작");
        int initialSize = sessions.size();

        // 유효하지 않은 세션들 수집
        Set<String> invalidSessions = new HashSet<>();
        for (String sessionId : sessions.keySet()) {
            if (!isSessionValid(sessionId)) {
                invalidSessions.add(sessionId);
            }
        }

        // 유효하지 않은 세션들 제거
        invalidSessions.forEach(this::removeSession);

        int removedCount = initialSize - sessions.size();
        if (removedCount > 0) {
            log.info("만료된 세션 {} 개 정리 완료", removedCount);
        }
    }

    /**
     * 최대 세션 수 체크
     */
    private void checkMaxSessions() {
        int maxSessions = webSocketProperties.getSessionManagement().getMaxSessions();
        if (maxSessions > 0 && sessions.size() > maxSessions) {
            log.warn("최대 세션 수 초과: 현재 {}, 최대 {}", sessions.size(), maxSessions);

            // 가장 오래된 세션 제거
            removeOldestSession();
        }
    }

    /**
     * 가장 오래된 세션 제거
     */
    private void removeOldestSession() {
        String oldestSessionId = sessionCreationTimes.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (oldestSessionId != null) {
            log.debug("가장 오래된 세션 제거: {}", oldestSessionId);

            // 세션을 닫고 제거
            WebSocketSession session = sessions.get(oldestSessionId);
            if (session != null && session.isOpen()) {
                try {
                    session.close();
                } catch (Exception e) {
                    log.warn("세션 닫기 실패: {}", e.getMessage());
                }
            }
            removeSession(oldestSessionId);
        }
    }
}