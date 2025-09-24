package com.kyj.core.ws.service;

import com.kyj.core.ws.config.WebSocketProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.util.Set;

/**
 * 2025-09-24
 * @author 김용준
 * 웹소켓 메시지 처리 서비스 구현체
 */
@Slf4j
@RequiredArgsConstructor
public class WebSocketMessageServiceImpl implements WebSocketMessageService {

    private final WebSocketSessionService sessionService;
    private final WebSocketProperties webSocketProperties;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Override
    public void sendToUser(String userId, String destination, Object message) {
        if (!validateMessage(message)) {
            log.warn("유효하지 않은 메시지: {}", message);
            return;
        }

        try {
            // Spring WebSocket의 사용자별 메시지 전송 기능 사용
            messagingTemplate.convertAndSendToUser(userId, destination, message);
            log.debug("사용자 메시지 전송: userId={}, destination={}", userId, destination);
        } catch (Exception e) {
            log.error("사용자 메시지 전송 실패: userId={}, error={}", userId, e.getMessage(), e);
        }
    }

    @Override
    public void sendToSession(String sessionId, String destination, Object message) {
        if (!validateMessage(message)) {
            log.warn("유효하지 않은 메시지: {}", message);
            return;
        }

        if (!sessionService.isSessionValid(sessionId)) {
            log.warn("유효하지 않은 세션: {}", sessionId);
            return;
        }

        try {
            // 세션별 개별 전송은 사용자 ID를 통해 처리
            String userId = (String) sessionService.getSessionAttribute(sessionId, "userId");
            if (userId != null) {
                sendToUser(userId, destination, message);
            } else {
                log.warn("세션에 사용자 ID가 없음: {}", sessionId);
            }
        } catch (Exception e) {
            log.error("세션 메시지 전송 실패: sessionId={}, error={}", sessionId, e.getMessage(), e);
        }
    }

    @Override
    public void broadcast(String destination, Object message) {
        if (!webSocketProperties.getMessageProcessing().isEnableBroadcast()) {
            log.debug("브로드캐스트가 비활성화됨");
            return;
        }

        if (!validateMessage(message)) {
            log.warn("유효하지 않은 메시지: {}", message);
            return;
        }

        try {
            messagingTemplate.convertAndSend(destination, message);
            log.debug("브로드캐스트 메시지 전송: destination={}, 활성 세션 수={}",
                     destination, sessionService.getActiveSessionCount());
        } catch (Exception e) {
            log.error("브로드캐스트 메시지 전송 실패: destination={}, error={}", destination, e.getMessage(), e);
        }
    }

    @Override
    public void sendToTopic(String topic, Object message) {
        String destination = "/topic/" + topic;
        broadcast(destination, message);
    }

    @Override
    public void sendToQueue(String queue, Object message) {
        String destination = "/queue/" + queue;
        broadcast(destination, message);
    }

    @Override
    public void processMessage(String sessionId, Object message) {
        log.debug("메시지 처리 시작: sessionId={}, message={}", sessionId, message);

        try {
            // 메시지 전처리
            Object processedMessage = preprocessMessage(message);

            // 메시지 변환 (필요한 경우)
            Object transformedMessage = transformMessage(processedMessage, "default");

            // 메시지 후처리
            postprocessMessage(sessionId, transformedMessage);

            log.debug("메시지 처리 완료: sessionId={}", sessionId);
        } catch (Exception e) {
            log.error("메시지 처리 실패: sessionId={}, error={}", sessionId, e.getMessage(), e);
            handleMessageError(sessionId, message, e);
        }
    }

    @Override
    public Object transformMessage(Object message, String targetType) {
        if (message == null) {
            return null;
        }

        try {
            // 기본 메시지 변환 로직
            switch (targetType.toLowerCase()) {
                case "json":
                    return convertToJson(message);
                case "string":
                    return message.toString();
                case "default":
                default:
                    return message;
            }
        } catch (Exception e) {
            log.warn("메시지 변환 실패: targetType={}, error={}", targetType, e.getMessage());
            return message; // 변환 실패 시 원본 반환
        }
    }

    @Override
    public boolean validateMessage(Object message) {
        if (message == null) {
            return false;
        }

        // 메시지 크기 제한 체크
        String messageStr = message.toString();
        long maxSize = webSocketProperties.getMessageProcessing().getMaxMessageSize();
        if (maxSize > 0 && messageStr.length() > maxSize) {
            log.warn("메시지 크기가 제한을 초과: {} bytes (최대: {} bytes)",
                    messageStr.length(), maxSize);
            return false;
        }

        // 추가 검증 로직 (필요시 확장)
        return validateMessageContent(message);
    }

    /**
     * 메시지 전처리
     */
    protected Object preprocessMessage(Object message) {
        // 기본 구현은 메시지를 그대로 반환
        // 하위 클래스에서 오버라이드하여 전처리 로직 구현 가능
        return message;
    }

    /**
     * 메시지 후처리
     */
    protected void postprocessMessage(String sessionId, Object message) {
        // 기본 구현은 비어있음
        // 하위 클래스에서 오버라이드하여 후처리 로직 구현 가능
    }

    /**
     * 메시지 내용 검증
     */
    protected boolean validateMessageContent(Object message) {
        // 기본 구현은 항상 통과
        // 하위 클래스에서 오버라이드하여 구체적인 검증 로직 구현 가능
        return true;
    }

    /**
     * JSON 변환
     */
    protected Object convertToJson(Object message) {
        // 기본 구현은 toString() 사용
        // 실제 구현에서는 JSON 라이브러리를 사용하여 변환
        return message.toString();
    }

    /**
     * 메시지 처리 오류 핸들링
     */
    protected void handleMessageError(String sessionId, Object message, Exception error) {
        log.error("메시지 오류 처리: sessionId={}, message={}, error={}",
                 sessionId, message, error.getMessage());

        // 에러 메시지를 클라이언트에 전송 (선택적)
        try {
            sendToSession(sessionId, "/queue/errors",
                         "메시지 처리 중 오류가 발생했습니다: " + error.getMessage());
        } catch (Exception e) {
            log.error("에러 메시지 전송 실패: {}", e.getMessage());
        }
    }
}