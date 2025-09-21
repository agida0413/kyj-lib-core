package com.kyj.core.security.client.exception;

/**
 * 2025-09-21
 * @author 김용준
 * 권한 관련 예외 클래스
 */
public class AuthorizationException extends SecurityClientException {

    public AuthorizationException() {
        super(SecurityErrCode.SEC_ROLE_001);
    }

    public AuthorizationException(String customMessage) {
        super(SecurityErrCode.SEC_ROLE_001, customMessage);
    }

    public AuthorizationException(SecurityErrCode errorCode) {
        super(errorCode);
    }

    public AuthorizationException(SecurityErrCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public AuthorizationException(SecurityErrCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}