package com.kyj.core.security.client.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyj.core.api.ResApiDTO;
import com.kyj.core.api.ResApiErrDTO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

/**
 * 2025-09-21
 * @author 김용준
 * 시큐리티 관련 응답을 Core API 형식으로 통일하는 유틸리티
 * 기존 SecurityResponse와 동일한 역할을 하지만 Core의 ResApiErrDTO 형식 사용
 */
@Slf4j
public final class SecurityResponseUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 성공 응답 작성 (ResApiDTO 형식)
     * @param response HTTP 응답
     * @param data 응답 데이터
     */
    public static <T> void writeSuccessResponse(HttpServletResponse response, T data) {
        try {
            ResApiDTO<T> responseBody = new ResApiDTO<>(data);

            response.setStatus(HttpStatus.OK.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(responseBody));
        } catch (IOException e) {
            log.error("성공 응답 작성 실패: {}", e.getMessage());
        }
    }

    /**
     * 에러 응답 작성 (ResApiErrDTO 형식)
     * @param response HTTP 응답
     * @param httpStatus HTTP 상태코드
     * @param errorCode 시큐리티 에러코드
     */
    public static void writeErrorResponse(HttpServletResponse response, HttpStatus httpStatus, SecurityErrCode errorCode) {
        writeErrorResponse(response, httpStatus, errorCode, null);
    }

    /**
     * 커스텀 메시지와 함께 에러 응답 작성
     * @param response HTTP 응답
     * @param httpStatus HTTP 상태코드
     * @param errorCode 시큐리티 에러코드
     * @param customMessage 커스텀 메시지 (null이면 기본 메시지 사용)
     */
    public static void writeErrorResponse(HttpServletResponse response, HttpStatus httpStatus,
                                        SecurityErrCode errorCode, String customMessage) {
        try {
            response.setStatus(httpStatus.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            String message = customMessage != null ? customMessage : errorCode.getMessage();

            // Core API 형식 에러 응답 (ResApiErrDTO와 동일한 구조)
            ResApiErrDTO<Void> errorResponse = new ResApiErrDTO<>(
                message,
                httpStatus.value(),
                (Void) null,
                errorCode.getCode()
            );

            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));

        } catch (IOException e) {
            log.error("에러 응답 작성 실패: {}", e.getMessage());
            // 최후의 수단으로 간단한 에러 응답
            try {
                response.getWriter().write("{\"error\":\"Internal Server Error\"}");
            } catch (IOException ex) {
                log.error("최종 에러 응답 작성도 실패: {}", ex.getMessage());
            }
        }
    }

    /**
     * SecurityClientException으로 에러 응답 작성
     * @param response HTTP 응답
     * @param httpStatus HTTP 상태코드
     * @param exception SecurityClientException
     */
    public static void writeErrorResponse(HttpServletResponse response, HttpStatus httpStatus,
                                        SecurityClientException exception) {
        writeErrorResponse(response, httpStatus, exception.getErrorCode(), exception.getDisplayMessage());
    }

    /**
     * 간단한 에러 메시지로 응답 작성
     * @param response HTTP 응답
     * @param httpStatus HTTP 상태코드
     * @param message 에러 메시지
     * @param errorCode 에러코드 문자열
     */
    public static void writeSimpleErrorResponse(HttpServletResponse response, HttpStatus httpStatus,
                                              String message, String errorCode) {
        try {
            response.setStatus(httpStatus.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            ResApiErrDTO<Void> errorResponse = new ResApiErrDTO<>(
                message,
                httpStatus.value(),
                (Void) null,
                errorCode
            );

            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));

        } catch (IOException e) {
            log.error("간단한 에러 응답 작성 실패: {}", e.getMessage());
        }
    }

    /**
     * 인증 실패 응답 (401)
     * @param response HTTP 응답
     */
    public static void writeUnauthorizedResponse(HttpServletResponse response) {
        writeErrorResponse(response, HttpStatus.UNAUTHORIZED, SecurityErrCode.SEC_AUTH_001);
    }

    /**
     * 권한 없음 응답 (403)
     * @param response HTTP 응답
     */
    public static void writeForbiddenResponse(HttpServletResponse response) {
        writeErrorResponse(response, HttpStatus.FORBIDDEN, SecurityErrCode.SEC_ROLE_001);
    }

    /**
     * JWT 관련 에러 응답
     * @param response HTTP 응답
     * @param jwtErrorCode JWT 에러코드
     */
    public static void writeJwtErrorResponse(HttpServletResponse response, SecurityErrCode jwtErrorCode) {
        HttpStatus status = jwtErrorCode == SecurityErrCode.SEC_JWT_002 ?
                           HttpStatus.GONE : HttpStatus.UNAUTHORIZED;
        writeErrorResponse(response, status, jwtErrorCode);
    }
}