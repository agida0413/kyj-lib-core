package com.kyj.core.security.client.exception;

import lombok.Getter;

/**
 * 2025-09-21
 * @author 김용준
 * 시큐리티 클라이언트 기본 예외 클래스
 */
@Getter
public class SecurityClientException extends RuntimeException {

    private final SecurityErrCode errorCode;
    private final String customMessage;

    /**
     * 에러코드만으로 예외 생성
     * @param errorCode 에러코드
     */
    public SecurityClientException(SecurityErrCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.customMessage = null;
    }

    /**
     * 에러코드와 커스텀 메시지로 예외 생성
     * @param errorCode 에러코드
     * @param customMessage 커스텀 메시지
     */
    public SecurityClientException(SecurityErrCode errorCode, String customMessage) {
        super(customMessage != null ? customMessage : errorCode.getMessage());
        this.errorCode = errorCode;
        this.customMessage = customMessage;
    }

    /**
     * 에러코드와 원인 예외로 예외 생성
     * @param errorCode 에러코드
     * @param cause 원인 예외
     */
    public SecurityClientException(SecurityErrCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.customMessage = null;
    }

    /**
     * 에러코드, 커스텀 메시지, 원인 예외로 예외 생성
     * @param errorCode 에러코드
     * @param customMessage 커스텀 메시지
     * @param cause 원인 예외
     */
    public SecurityClientException(SecurityErrCode errorCode, String customMessage, Throwable cause) {
        super(customMessage != null ? customMessage : errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.customMessage = customMessage;
    }

    /**
     * 표시할 메시지 반환 (커스텀 메시지 우선)
     * @return 표시할 메시지
     */
    public String getDisplayMessage() {
        return customMessage != null ? customMessage : errorCode.getMessage();
    }
}