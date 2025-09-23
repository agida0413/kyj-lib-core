package com.kyj.core.security.client.filter;

import com.kyj.core.api.CmErrCode;
import com.kyj.core.security.auth.exception.SecurityErrorCode;
import com.kyj.core.security.auth.util.SecurityResponseUtil;
import com.kyj.core.security.client.config.SecurityProperties;
import com.kyj.core.security.client.dto.SecurityUser;
import com.kyj.core.security.client.service.TokenBlacklistService;
import com.kyj.core.security.client.util.EndpointMatcher;
import com.kyj.core.security.client.util.SecurityContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * AWS API Gateway 모드 보안 필터
 * API Gateway에서 인증을 처리하고 헤더로 사용자 정보를 전달받아 처리
 */
@Slf4j
@RequiredArgsConstructor
public class ApiGatewaySecurityFilter extends OncePerRequestFilter {

    private final SecurityProperties properties;
    private final TokenBlacklistService blacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
        try {
            SecurityUser user = extractUserFromHeaders(request);

            // 인증 실패시 공통 응답으로 처리 (퍼블릭 엔드포인트는 shouldNotFilter에서 처리)
            if (!user.isAuthenticated()) {
                SecurityResponseUtil.writeErrorResponse(response, HttpStatus.UNAUTHORIZED, SecurityErrorCode.SEC010);
                return;
            }

            SecurityContext.setUser(user);
            filterChain.doFilter(request, response);
        } finally {
            SecurityContext.clear();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 설정된 정적 퍼블릭 엔드포인트만 필터에서 제외 (애노테이션 기반은 인터셉터에서 처리)
        return EndpointMatcher.isStaticPublicEndpoint(request, properties.getStaticPublicEndpoints());
    }

    /**
     * API Gateway 헤더에서 사용자 정보 추출
     */
    private SecurityUser extractUserFromHeaders(HttpServletRequest request) {
        SecurityProperties.ApiGateway config = properties.getApiGateway();

        String userId = request.getHeader(config.getUserIdHeader());
        String username = request.getHeader(config.getUsernameHeader());
        String email = request.getHeader(config.getEmailHeader());
        String roles = request.getHeader(config.getRolesHeader());

        // 필수 정보가 없으면 익명 사용자
        if (!StringUtils.hasText(userId) || !StringUtils.hasText(username)) {
            log.debug("API Gateway 헤더에 필수 정보 없음: userId={}, username={}",
                     userId != null ? "***" : null, username != null ? "***" : null);
            return SecurityUser.anonymous();
        }

        // 사용자 블랙리스트 체크 (활성화된 경우)
        if (properties.isEnableBlacklist() && blacklistService != null) {
            if (blacklistService.isUserBlacklisted(userId)) {
                log.warn("블랙리스트 사용자 접근 차단: {}", userId != null ? "***" : null);
                return SecurityUser.anonymous();
            }

            // 토큰 블랙리스트 체크 (토큰 헤더가 있는 경우)
            String token = request.getHeader(config.getTokenHeader());
            if (StringUtils.hasText(token) && blacklistService.isTokenBlacklisted(token)) {
                log.warn("블랙리스트 토큰 접근 차단: {}", userId != null ? "***" : null);
                return SecurityUser.anonymous();
            }
        }

        log.debug("API Gateway 사용자 인증 성공: userId={}, username={}", "***", "***");
        return SecurityUser.authenticated(userId, username, email, roles);
    }

}