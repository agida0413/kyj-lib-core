package com.kyj.core.ws.exception;

/**
 * 2025-09-24
 * @author 김용준
 * 웹소켓 모듈 공통 예외 클래스
 */
public class WebSocketException extends RuntimeException {

    public WebSocketException(String message) {
        super(message);
    }

    public WebSocketException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebSocketException(Throwable cause) {
        super(cause);
    }
}