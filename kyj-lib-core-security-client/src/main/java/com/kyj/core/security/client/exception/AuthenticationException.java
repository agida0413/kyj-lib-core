package com.kyj.core.security.client.exception;

/**
 * 2025-09-21
 * @author 김용준
 * 인증 관련 예외 클래스
 */
public class AuthenticationException extends SecurityClientException {

    public AuthenticationException() {
        super(SecurityErrCode.SEC_AUTH_001);
    }

    public AuthenticationException(String customMessage) {
        super(SecurityErrCode.SEC_AUTH_001, customMessage);
    }

    public AuthenticationException(SecurityErrCode errorCode) {
        super(errorCode);
    }

    public AuthenticationException(SecurityErrCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public AuthenticationException(SecurityErrCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}