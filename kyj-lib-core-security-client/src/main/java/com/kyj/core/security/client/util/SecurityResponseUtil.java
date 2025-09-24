package com.kyj.core.security.client.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyj.core.api.ApiResponse;
import com.kyj.core.security.client.exception.SecurityErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

/**
 * 2025-09-21
 * @author 김용준
 * 보안 관련 HTTP 응답 유틸리티 (ApiResponse 사용)
 */
@Slf4j
public final class SecurityResponseUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private SecurityResponseUtil() {
        // 유틸리티 클래스 - 인스턴스 생성 방지
    }

    /**
     * 성공 응답 작성 (ApiResponse 사용)
     */
    public static void writeSuccessResponse(HttpServletResponse response) throws IOException {
        writeSuccessResponse(response, null);
    }

    /**
     * 성공 응답 작성 (데이터 포함)
     */
    public static void writeSuccessResponse(HttpServletResponse response, Object data) throws IOException {
        ApiResponse<Object> responseBody = ApiResponse.success().data(data).build();

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }

    /**
     * 에러 응답 작성 (ApiResponse 사용)
     */
    public static void writeErrorResponse(HttpServletResponse response, HttpStatus httpStatus, SecurityErrorCode errorCode) {
        writeErrorResponse(response, httpStatus, errorCode, null);
    }

    /**
     * 에러 응답 작성 (데이터 포함)
     */
    public static void writeErrorResponse(HttpServletResponse response, HttpStatus httpStatus, SecurityErrorCode errorCode, Object data) {
        try {
            ApiResponse<Object> responseBody = ApiResponse.error()
                    .msg(errorCode.getMessage())
                    .status(httpStatus.value())
                    .code(errorCode.getCode())
                    .data(data)
                    .build();

            response.setStatus(httpStatus.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(responseBody));
        } catch (IOException e) {
            log.error("에러 응답 작성 실패: {}", e.getMessage());
        }
    }


    /**
     * 에러 응답 작성 (커스텀 메시지)
     */
    public static void writeErrorResponse(HttpServletResponse response, HttpStatus httpStatus, SecurityErrorCode errorCode, String customMessage) {
        try {
            ApiResponse<Object> responseBody = ApiResponse.error()
                    .msg(customMessage != null ? customMessage : errorCode.getMessage())
                    .status(httpStatus.value())
                    .code(errorCode.getCode())
                    .build();

            response.setStatus(httpStatus.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(responseBody));
        } catch (IOException e) {
            log.error("에러 응답 작성 실패: {}", e.getMessage());
        }
    }

}