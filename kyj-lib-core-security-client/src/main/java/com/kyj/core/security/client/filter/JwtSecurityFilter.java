package com.kyj.core.security.client.filter;

import com.kyj.core.api.CmErrCode;
import com.kyj.core.security.auth.exception.SecurityErrorCode;
import com.kyj.core.security.auth.jwt.AuthJWTUtil;
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
 * 일반 JWT 모드 보안 필터
 * Authorization 헤더에서 JWT 토큰을 추출하여 검증하고 사용자 정보를 처리
 */
@Slf4j
@RequiredArgsConstructor
public class JwtSecurityFilter extends OncePerRequestFilter {

    private final SecurityProperties properties;
    private final AuthJWTUtil authJWTUtil;
    private final TokenBlacklistService blacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
        try {
            SecurityUser user = extractUserFromToken(request);

            // 토큰 만료시 GONE 응답으로 클라이언트에 재발급 유도
            if (user.isExpired()) {
                SecurityResponseUtil.writeErrorResponse(response, HttpStatus.GONE, SecurityErrorCode.SEC011);
                return;
            }

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
     * JWT 토큰에서 사용자 정보 추출
     */
    private SecurityUser extractUserFromToken(HttpServletRequest request) {
        String token = extractTokenFromHeader(request);

        if (!StringUtils.hasText(token)) {
            log.debug("JWT 토큰이 없음");
            return SecurityUser.anonymous();
        }

        try {
            // 토큰 블랙리스트 체크 (활성화된 경우)
            if (properties.isEnableBlacklist() && blacklistService != null) {
                if (blacklistService.isTokenBlacklisted(token)) {
                    log.warn("블랙리스트 토큰 접근 차단");
                    return SecurityUser.anonymous();
                }
            }

            // JWT 토큰 파싱 - security-auth 모듈의 AuthJWTUtil 사용
            if (authJWTUtil.isExpired(token)) {
                log.debug("만료된 JWT 토큰");
                return SecurityUser.expired(); // 토큰 만료 상태 표시
            }

            if (!authJWTUtil.validate(token)) {
                log.debug("유효하지 않은 JWT 토큰");
                return SecurityUser.anonymous();
            }

            String userId = authJWTUtil.getUserId(token);
            String username = authJWTUtil.getUsername(token);
            String email = authJWTUtil.getEmail(token);
            String roles = authJWTUtil.getRoles(token);

            // 필수 정보 확인
            if (!StringUtils.hasText(userId) || !StringUtils.hasText(username)) {
                log.debug("JWT 토큰에 필수 정보 없음: userId={}, username={}",
                     userId != null ? "***" : null, username != null ? "***" : null);
                return SecurityUser.anonymous();
            }

            // 사용자 블랙리스트 체크 (활성화된 경우)
            if (properties.isEnableBlacklist() && blacklistService != null) {
                if (blacklistService.isUserBlacklisted(userId)) {
                    log.warn("블랙리스트 사용자 접근 차단: {}", "***");
                    return SecurityUser.anonymous();
                }
            }

            log.debug("JWT 사용자 인증 성공: userId={}, username={}", "***", "***");
            return SecurityUser.authenticated(userId, username, email, roles);

        } catch (Exception e) {
            log.debug("JWT 토큰 파싱 실패: {}", e.getMessage());
            return SecurityUser.anonymous();
        }
    }

    /**
     * Authorization 헤더에서 JWT 토큰 추출
     */
    private String extractTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

}