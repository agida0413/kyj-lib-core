package com.kyj.core.ws.util;

import com.kyj.core.ws.dto.WebSocketMessage;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 2025-09-24
 * @author 김용준
 * 웹소켓 유틸리티 클래스
 */
@Slf4j
public final class WebSocketUtil {

    private WebSocketUtil() {
        // 유틸리티 클래스 - 인스턴스 생성 방지
    }

    /**
     * 메시지 ID 생성
     */
    public static String generateMessageId() {
        return UUID.randomUUID().toString();
    }

    /**
     * 기본 웹소켓 메시지 생성
     */
    public static WebSocketMessage createMessage(String messageType, Object content) {
        return WebSocketMessage.builder()
                .messageId(generateMessageId())
                .messageType(messageType)
                .content(content)
                .timestamp(LocalDateTime.now())
                .status(WebSocketMessage.MessageStatus.CREATED)
                .priority(0)
                .build();
    }

    /**
     * 사용자간 메시지 생성
     */
    public static WebSocketMessage createUserMessage(String sender, String recipient, Object content) {
        return WebSocketMessage.builder()
                .messageId(generateMessageId())
                .messageType("user_message")
                .sender(sender)
                .recipient(recipient)
                .content(content)
                .timestamp(LocalDateTime.now())
                .status(WebSocketMessage.MessageStatus.CREATED)
                .priority(1)
                .build();
    }

    /**
     * 브로드캐스트 메시지 생성
     */
    public static WebSocketMessage createBroadcastMessage(String sender, Object content) {
        return WebSocketMessage.builder()
                .messageId(generateMessageId())
                .messageType("broadcast")
                .sender(sender)
                .content(content)
                .timestamp(LocalDateTime.now())
                .status(WebSocketMessage.MessageStatus.CREATED)
                .priority(2)
                .build();
    }

    /**
     * 알림 메시지 생성
     */
    public static WebSocketMessage createNotificationMessage(String recipient, Object content) {
        return WebSocketMessage.builder()
                .messageId(generateMessageId())
                .messageType("notification")
                .recipient(recipient)
                .content(content)
                .timestamp(LocalDateTime.now())
                .status(WebSocketMessage.MessageStatus.CREATED)
                .priority(3)
                .build();
    }

    /**
     * 에러 메시지 생성
     */
    public static WebSocketMessage createErrorMessage(String recipient, String errorMessage) {
        return WebSocketMessage.builder()
                .messageId(generateMessageId())
                .messageType("error")
                .recipient(recipient)
                .content("오류가 발생했습니다")
                .errorMessage(errorMessage)
                .timestamp(LocalDateTime.now())
                .status(WebSocketMessage.MessageStatus.FAILED)
                .priority(10)
                .build();
    }

    /**
     * 시스템 메시지 생성
     */
    public static WebSocketMessage createSystemMessage(Object content) {
        return WebSocketMessage.builder()
                .messageId(generateMessageId())
                .messageType("system")
                .sender("SYSTEM")
                .content(content)
                .timestamp(LocalDateTime.now())
                .status(WebSocketMessage.MessageStatus.CREATED)
                .priority(5)
                .build();
    }

    /**
     * 메시지 유효성 검증
     */
    public static boolean isValidMessage(WebSocketMessage message) {
        if (message == null) {
            return false;
        }

        if (message.getMessageId() == null || message.getMessageId().isEmpty()) {
            log.warn("메시지 ID가 없음");
            return false;
        }

        if (message.getMessageType() == null || message.getMessageType().isEmpty()) {
            log.warn("메시지 타입이 없음");
            return false;
        }

        if (message.isExpired()) {
            log.warn("만료된 메시지: {}", message.getMessageId());
            return false;
        }

        return true;
    }

    /**
     * 메시지 크기 확인 (bytes)
     */
    public static long getMessageSize(Object message) {
        if (message == null) {
            return 0;
        }
        return message.toString().getBytes().length;
    }

    /**
     * 메시지 내용 정리 (보안상 민감한 정보 제거)
     */
    public static WebSocketMessage sanitizeMessage(WebSocketMessage message) {
        if (message == null) {
            return null;
        }

        // 민감한 정보가 포함된 메시지 내용 정리
        Object sanitizedContent = sanitizeContent(message.getContent());

        return WebSocketMessage.builder()
                .messageId(message.getMessageId())
                .messageType(message.getMessageType())
                .sender(message.getSender())
                .recipient(message.getRecipient())
                .content(sanitizedContent)
                .data(message.getData())
                .timestamp(message.getTimestamp())
                .expiresAt(message.getExpiresAt())
                .priority(message.getPriority())
                .status(message.getStatus())
                .errorMessage(message.getErrorMessage())
                .build();
    }

    /**
     * 메시지 내용 정리
     */
    private static Object sanitizeContent(Object content) {
        if (content == null) {
            return null;
        }

        String contentStr = content.toString();

        // 민감한 정보 패턴 제거 (예: 비밀번호, 토큰 등)
        contentStr = contentStr.replaceAll("(?i)password[\"']?\\s*[:=]\\s*[\"'][^\"']*[\"']", "password: \"***\"");
        contentStr = contentStr.replaceAll("(?i)token[\"']?\\s*[:=]\\s*[\"'][^\"']*[\"']", "token: \"***\"");
        contentStr = contentStr.replaceAll("(?i)secret[\"']?\\s*[:=]\\s*[\"'][^\"']*[\"']", "secret: \"***\"");

        return contentStr;
    }

    /**
     * 연결 지속 시간을 사람이 읽기 쉬운 형태로 변환
     */
    public static String formatDuration(long durationMillis) {
        if (durationMillis < 1000) {
            return durationMillis + "ms";
        }

        long seconds = durationMillis / 1000;
        if (seconds < 60) {
            return seconds + "s";
        }

        long minutes = seconds / 60;
        if (minutes < 60) {
            return minutes + "m " + (seconds % 60) + "s";
        }

        long hours = minutes / 60;
        return hours + "h " + (minutes % 60) + "m " + (seconds % 60) + "s";
    }
}