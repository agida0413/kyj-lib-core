package com.kyj.core.security.auth.exception;

/**
 * 2025-09-21
 * @author 김용준
 * 보안 관련 예외 클래스
 */
public class SecurityException extends RuntimeException {

    private final SecurityErrorCode errorCode;

    public SecurityException(SecurityErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public SecurityException(SecurityErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public SecurityException(SecurityErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public SecurityErrorCode getErrorCode() {
        return errorCode;
    }
}