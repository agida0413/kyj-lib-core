package com.kyj.core.security.client.filter;

import com.kyj.core.security.client.dto.SecurityUserInfo;
import com.kyj.core.security.client.exception.JwtException;
import com.kyj.core.security.client.exception.SecurityErrCode;
import com.kyj.core.security.client.exception.SecurityResponseUtil;
import com.kyj.core.security.client.service.SecurityClientService;
import com.kyj.core.security.client.util.SecurityHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 2025-09-21
 * @author 김용준
 * JWT 토큰을 자동으로 처리하여 사용자 정보를 SecurityHolder에 저장하는 필터
 * 이 필터가 등록되면 모든 요청에서 자동으로 JWT 토큰을 검증하고 블랙리스트를 확인함
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final SecurityClientService securityClientService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // Authorization 헤더에서 토큰 추출 및 검증
            String authorizationHeader = request.getHeader("Authorization");
            SecurityUserInfo userInfo = securityClientService.getUserInfoFromHeader(authorizationHeader);

            // SecurityHolder에 사용자 정보 저장 (요청 스코프)
            SecurityHolder.setUserInfo(userInfo);

            if (userInfo.isAuthenticated()) {
                log.debug("인증된 사용자: {} (ID: {})", userInfo.getUsername(), userInfo.getUserId());
            } else {
                log.debug("인증되지 않은 요청");
            }

        } catch (JwtException e) {
            log.warn("JWT 처리 예외: [{}] {}", e.getErrorCode().getCode(), e.getDisplayMessage());
            SecurityHolder.setUserInfo(SecurityUserInfo.unauthenticated());
        } catch (Exception e) {
            log.error("JWT 토큰 처리 중 예상치 못한 오류 발생: {}", e.getMessage());
            SecurityHolder.setUserInfo(SecurityUserInfo.unauthenticated());
        }

        try {
            // 다음 필터로 요청 전달
            filterChain.doFilter(request, response);
        } finally {
            // 요청 완료 후 SecurityHolder 정리
            SecurityHolder.clear();
        }
    }

    /**
     * 특정 경로는 JWT 필터를 건너뛰도록 설정할 수 있음
     * 필요에 따라 오버라이드하여 사용
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // health check, actuator 등은 필터 건너뛰기
        return path.startsWith("/actuator") ||
               path.startsWith("/health") ||
               path.startsWith("/favicon.ico");
    }
}