package com.kyj.core.security.auth.handler;

import com.kyj.core.security.client.exception.SecurityErrorCode;
import com.kyj.core.security.client.util.SecurityResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 2025-09-27
 * @author 김용준
 * 인가 실패 시 JSON 응답을 반환하는 커스텀 AccessDeniedHandler
 * 기본 403 페이지 대신 JSON 에러 응답 반환
 */
@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                      AccessDeniedException accessDeniedException) throws IOException, ServletException {

        log.debug("권한이 없는 접근 시도: {} {}", request.getMethod(), request.getRequestURI());

        // JSON 에러 응답 반환 (기본 403 페이지 대신)
        SecurityResponseUtil.writeErrorResponse(response, HttpStatus.FORBIDDEN, SecurityErrorCode.SEC011);
    }
}