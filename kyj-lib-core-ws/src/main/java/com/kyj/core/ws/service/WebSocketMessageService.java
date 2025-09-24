package com.kyj.core.ws.service;

/**
 * 2025-09-24
 * @author 김용준
 * 웹소켓 메시지 처리 서비스 인터페이스
 */
public interface WebSocketMessageService {

    /**
     * 특정 사용자에게 메시지 전송
     */
    void sendToUser(String userId, String destination, Object message);

    /**
     * 특정 세션에 메시지 전송
     */
    void sendToSession(String sessionId, String destination, Object message);

    /**
     * 모든 사용자에게 브로드캐스트
     */
    void broadcast(String destination, Object message);

    /**
     * 특정 토픽 구독자들에게 메시지 전송
     */
    void sendToTopic(String topic, Object message);

    /**
     * 특정 큐에 메시지 전송
     */
    void sendToQueue(String queue, Object message);

    /**
     * 메시지 처리 (외부에서 받은 메시지 처리)
     */
    void processMessage(String sessionId, Object message);

    /**
     * 메시지 변환 (타입별 메시지 변환)
     */
    Object transformMessage(Object message, String targetType);

    /**
     * 메시지 유효성 검증
     */
    boolean validateMessage(Object message);
}