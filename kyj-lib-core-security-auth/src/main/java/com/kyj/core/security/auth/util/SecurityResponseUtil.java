package com.kyj.core.security.auth.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyj.core.api.ResApiDTO;
import com.kyj.core.api.ResApiErrDTO;
import com.kyj.core.security.auth.exception.SecurityErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

/**
 * 2025-09-21
 * @author 김용준
 * 보안 관련 HTTP 응답 유틸리티 (kyj-lib-core API 응답 포맷 사용)
 */
@Slf4j
public final class SecurityResponseUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private SecurityResponseUtil() {
        // 유틸리티 클래스 - 인스턴스 생성 방지
    }

    /**
     * 성공 응답 작성 (kyj-lib-core ResApiDTO 사용)
     */
    public static void writeSuccessResponse(HttpServletResponse response) throws IOException {
        writeSuccessResponse(response, null);
    }

    /**
     * 성공 응답 작성 (데이터 포함)
     */
    public static void writeSuccessResponse(HttpServletResponse response, Object data) throws IOException {
        ResApiDTO<Object> responseBody = new ResApiDTO<>(data);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }

    /**
     * 에러 응답 작성 (kyj-lib-core ResApiErrDTO 사용)
     */
    public static void writeErrorResponse(HttpServletResponse response, HttpStatus httpStatus, SecurityErrorCode errorCode) {
        writeErrorResponse(response, httpStatus, errorCode, null);
    }

    /**
     * 에러 응답 작성 (데이터 포함)
     */
    public static void writeErrorResponse(HttpServletResponse response, HttpStatus httpStatus, SecurityErrorCode errorCode, Object data) {
        try {
            ResApiErrDTO<Object> responseBody = new ResApiErrDTO<>(errorCode.getMessage(), httpStatus.value(), data, errorCode.getCode());

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
            ResApiErrDTO<Object> responseBody = new ResApiErrDTO<>(
                    customMessage != null ? customMessage : errorCode.getMessage(),
                    httpStatus.value(),
                    null,
                    errorCode.getCode()
            );

            response.setStatus(httpStatus.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(responseBody));
        } catch (IOException e) {
            log.error("에러 응답 작성 실패: {}", e.getMessage());
        }
    }

}