package com.kyj.core.security.auth.handler;

import com.kyj.core.security.client.exception.SecurityErrorCode;
import com.kyj.core.security.client.util.SecurityResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 2025-09-27
 * @author 김용준
 * 인증 실패 시 JSON 응답을 반환하는 커스텀 EntryPoint
 * 기본 로그인 페이지 대신 401 에러 응답 반환
 */
@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                        AuthenticationException authException) throws IOException, ServletException {

        log.debug("인증되지 않은 접근 시도: {} {}", request.getMethod(), request.getRequestURI());

        // JSON 에러 응답 반환 (기본 로그인 페이지 대신)
        SecurityResponseUtil.writeErrorResponse(response, HttpStatus.UNAUTHORIZED, SecurityErrorCode.SEC010);
    }
}