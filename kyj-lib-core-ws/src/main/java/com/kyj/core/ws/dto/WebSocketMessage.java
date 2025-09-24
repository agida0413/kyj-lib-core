package com.kyj.core.ws.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 2025-09-24
 * @author 김용준
 * 웹소켓 메시지 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {

    /**
     * 메시지 ID
     */
    private String messageId;

    /**
     * 메시지 타입
     */
    private String messageType;

    /**
     * 송신자
     */
    private String sender;

    /**
     * 수신자
     */
    private String recipient;

    /**
     * 메시지 내용
     */
    private Object content;

    /**
     * 메시지 데이터
     */
    private Map<String, Object> data;

    /**
     * 생성 시간
     */
    private LocalDateTime timestamp;

    /**
     * 만료 시간
     */
    private LocalDateTime expiresAt;

    /**
     * 우선순위
     */
    private Integer priority;

    /**
     * 메시지 상태
     */
    private MessageStatus status;

    /**
     * 에러 메시지
     */
    private String errorMessage;

    /**
     * 메시지 상태 열거형
     */
    public enum MessageStatus {
        CREATED,    // 생성됨
        SENT,       // 전송됨
        DELIVERED,  // 전달됨
        READ,       // 읽음
        FAILED,     // 실패
        EXPIRED     // 만료됨
    }

    /**
     * 현재 시간으로 타임스탬프 설정
     */
    public WebSocketMessage withCurrentTimestamp() {
        return WebSocketMessage.builder()
                .messageId(this.messageId)
                .messageType(this.messageType)
                .sender(this.sender)
                .recipient(this.recipient)
                .content(this.content)
                .data(this.data)
                .timestamp(LocalDateTime.now())
                .expiresAt(this.expiresAt)
                .priority(this.priority)
                .status(this.status)
                .errorMessage(this.errorMessage)
                .build();
    }

    /**
     * 메시지가 만료되었는지 확인
     */
    public boolean isExpired() {
        if (expiresAt == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(expiresAt);
    }
}