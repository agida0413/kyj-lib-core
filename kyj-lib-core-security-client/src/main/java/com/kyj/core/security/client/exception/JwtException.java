package com.kyj.core.security.client.exception;

/**
 * 2025-09-21
 * @author 김용준
 * JWT 관련 예외 클래스
 */
public class JwtException extends SecurityClientException {

    public JwtException() {
        super(SecurityErrCode.SEC002);
    }

    public JwtException(String customMessage) {
        super(SecurityErrCode.SEC002, customMessage);
    }

    public JwtException(SecurityErrCode errorCode) {
        super(errorCode);
    }

    public JwtException(SecurityErrCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public JwtException(SecurityErrCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}