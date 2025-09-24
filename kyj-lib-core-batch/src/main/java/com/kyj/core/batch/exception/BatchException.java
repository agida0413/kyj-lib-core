package com.kyj.core.batch.exception;

/**
 * 2025-09-24
 * @author 김용준
 * 배치 모듈 공통 예외 클래스
 */
public class BatchException extends RuntimeException {

    public BatchException(String message) {
        super(message);
    }

    public BatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public BatchException(Throwable cause) {
        super(cause);
    }
}