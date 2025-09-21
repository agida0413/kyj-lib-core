package com.kyj.core.security.client.exception;

import com.kyj.core.api.ResApiErrDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 2025-09-21
 * @author 김용준
 * 시큐리티 클라이언트 관련 예외를 처리하는 글로벌 예외 핸들러
 * Core API 형식으로 통일된 응답을 제공
 */
@Slf4j
@RestControllerAdvice
public class SecurityExceptionHandler {

    /**
     * 시큐리티 클라이언트 기본 예외 처리
     * @param ex SecurityClientException
     * @return 에러 응답
     */
    @ExceptionHandler(SecurityClientException.class)
    public ResponseEntity<ResApiErrDTO<Void>> handleSecurityClientException(SecurityClientException ex) {
        log.warn("시큐리티 클라이언트 예외 발생: [{}] {}", ex.getErrorCode().getCode(), ex.getDisplayMessage());

        HttpStatus status = determineHttpStatus(ex.getErrorCode());

        ResApiErrDTO<Void> errorResponse = new ResApiErrDTO<>(
            ex.getDisplayMessage(),
            status.value(),
            (Void) null,
            ex.getErrorCode().getCode()
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * 인증 예외 처리
     * @param ex AuthenticationException
     * @return 에러 응답
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResApiErrDTO<Void>> handleAuthenticationException(AuthenticationException ex) {
        log.warn("인증 예외 발생: [{}] {}", ex.getErrorCode().getCode(), ex.getDisplayMessage());

        ResApiErrDTO<Void> errorResponse = new ResApiErrDTO<>(
            ex.getDisplayMessage(),
            HttpStatus.UNAUTHORIZED.value(),
            (Void) null,
            ex.getErrorCode().getCode()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * 권한 예외 처리
     * @param ex AuthorizationException
     * @return 에러 응답
     */
    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ResApiErrDTO<Void>> handleAuthorizationException(AuthorizationException ex) {
        log.warn("권한 예외 발생: [{}] {}", ex.getErrorCode().getCode(), ex.getDisplayMessage());

        ResApiErrDTO<Void> errorResponse = new ResApiErrDTO<>(
            ex.getDisplayMessage(),
            HttpStatus.FORBIDDEN.value(),
            (Void) null,
            ex.getErrorCode().getCode()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    /**
     * JWT 예외 처리
     * @param ex JwtException
     * @return 에러 응답
     */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ResApiErrDTO<Void>> handleJwtException(JwtException ex) {
        log.warn("JWT 예외 발생: [{}] {}", ex.getErrorCode().getCode(), ex.getDisplayMessage());

        // JWT 만료는 410 GONE, 나머지는 401 UNAUTHORIZED
        HttpStatus status = ex.getErrorCode() == SecurityErrCode.SEC_JWT_002 ?
                           HttpStatus.GONE : HttpStatus.UNAUTHORIZED;

        ResApiErrDTO<Void> errorResponse = new ResApiErrDTO<>(
            ex.getDisplayMessage(),
            status.value(),
            (Void) null,
            ex.getErrorCode().getCode()
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * 일반적인 SecurityException 처리 (SecurityHolder에서 발생)
     * @param ex SecurityException
     * @return 에러 응답
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ResApiErrDTO<Void>> handleSecurityException(SecurityException ex) {
        log.warn("보안 예외 발생: {}", ex.getMessage());

        // 메시지 내용으로 에러 타입 판단
        String message = ex.getMessage();
        SecurityErrCode errorCode;
        HttpStatus status;

        if (message.contains("인증이 필요")) {
            errorCode = SecurityErrCode.SEC_AUTH_001;
            status = HttpStatus.UNAUTHORIZED;
        } else if (message.contains("권한")) {
            errorCode = SecurityErrCode.SEC_ROLE_001;
            status = HttpStatus.FORBIDDEN;
        } else {
            errorCode = SecurityErrCode.SEC_SYS_001;
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        ResApiErrDTO<Void> errorResponse = new ResApiErrDTO<>(
            message,
            status.value(),
            (Void) null,
            errorCode.getCode()
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * 에러코드에 따른 HTTP 상태코드 결정
     * @param errorCode 시큐리티 에러코드
     * @return HTTP 상태코드
     */
    private HttpStatus determineHttpStatus(SecurityErrCode errorCode) {
        if (errorCode.getCode().startsWith("SEC_JWT_")) {
            return errorCode == SecurityErrCode.SEC_JWT_002 ? HttpStatus.GONE : HttpStatus.UNAUTHORIZED;
        } else if (errorCode.getCode().startsWith("SEC_AUTH_")) {
            return HttpStatus.UNAUTHORIZED;
        } else if (errorCode.getCode().startsWith("SEC_ROLE_")) {
            return HttpStatus.FORBIDDEN;
        } else if (errorCode.getCode().startsWith("SEC_BLACK_")) {
            return HttpStatus.FORBIDDEN;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}