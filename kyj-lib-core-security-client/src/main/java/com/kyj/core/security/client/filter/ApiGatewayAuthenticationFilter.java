package com.kyj.core.security.client.filter;

import com.kyj.core.security.client.dto.SecurityUserInfo;
import com.kyj.core.security.client.exception.JwtException;
import com.kyj.core.security.client.service.SecurityClientService;
import com.kyj.core.security.client.util.SecurityHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 2025-09-21
 * @author 김용준
 * AWS API Gateway에서 전달된 사용자 정보를 처리하는 필터
 * API Gateway에서 JWT 검증을 완료하고 헤더로 사용자 정보를 전달하는 경우 사용
 *
 * 설정: security.client.api-gateway.enabled=true 로 활성화
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Order(1) // JWT 필터보다 먼저 실행
@ConditionalOnProperty(value = "security.client.api-gateway.enabled", havingValue = "true")
public class ApiGatewayAuthenticationFilter extends OncePerRequestFilter {

    private final SecurityClientService securityClientService;

    // AWS API Gateway에서 전달하는 헤더명들
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USERNAME_HEADER = "X-Username";
    private static final String EMAIL_HEADER = "X-Email";
    private static final String ROLES_HEADER = "X-Roles";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // API Gateway 헤더에서 사용자 정보 추출
            String userId = request.getHeader(USER_ID_HEADER);
            String username = request.getHeader(USERNAME_HEADER);
            String email = request.getHeader(EMAIL_HEADER);
            String roles = request.getHeader(ROLES_HEADER);

            SecurityUserInfo userInfo;

            // API Gateway 헤더가 있는 경우 헤더 우선 처리
            if (StringUtils.hasText(userId) && StringUtils.hasText(username)) {
                log.debug("API Gateway 헤더 발견: userId={}, username={}", userId, username);
                userInfo = securityClientService.getUserInfoFromApiGatewayHeaders(userId, username, email, roles);
            } else {
                // API Gateway 헤더가 없으면 Authorization 헤더 확인
                String authorizationHeader = request.getHeader("Authorization");
                userInfo = securityClientService.getUserInfoFromHeader(authorizationHeader);
            }

            // SecurityHolder에 사용자 정보 저장 (요청 스코프)
            SecurityHolder.setUserInfo(userInfo);

            if (userInfo.isAuthenticated()) {
                log.debug("인증된 사용자: {} (ID: {})", userInfo.getUsername(), userInfo.getUserId());
            } else {
                log.debug("인증되지 않은 요청");
            }

        } catch (JwtException e) {
            log.warn("인증 처리 예외: [{}] {}", e.getErrorCode().getCode(), e.getDisplayMessage());
            SecurityHolder.setUserInfo(SecurityUserInfo.unauthenticated());
        } catch (Exception e) {
            log.error("API Gateway 인증 처리 중 예상치 못한 오류 발생: {}", e.getMessage());
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
     * 특정 경로는 API Gateway 필터를 건너뛰도록 설정할 수 있음
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