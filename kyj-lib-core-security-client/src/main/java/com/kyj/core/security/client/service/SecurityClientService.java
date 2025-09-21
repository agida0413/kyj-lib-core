package com.kyj.core.security.client.service;

import com.kyj.core.security.client.dto.SecurityUserInfo;
import com.kyj.core.security.client.exception.JwtException;
import com.kyj.core.security.client.exception.SecurityErrCode;
import com.kyj.core.security.client.jwt.ClientJWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 2025-09-21
 * @author 김용준
 * 보안 클라이언트의 핵심 서비스
 * JWT 토큰 파싱, 블랙리스트 확인, AWS API Gateway 헤더 처리 등을 담당
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityClientService {

    private final ClientJWTUtil clientJWTUtil;
    private final TokenBlacklistService tokenBlacklistService;

    /**
     * Authorization 헤더에서 사용자 정보 추출
     * @param authorizationHeader Authorization 헤더 값
     * @return 사용자 정보
     */
    public SecurityUserInfo getUserInfoFromHeader(String authorizationHeader) {
        // Authorization 헤더 검증
        if (!StringUtils.hasText(authorizationHeader)) {
            log.debug("Authorization 헤더가 없습니다.");
            return SecurityUserInfo.unauthenticated();
        }

        // Bearer 토큰 형식 확인
        if (!authorizationHeader.startsWith("Bearer ")) {
            log.debug("Bearer 토큰 형식이 아닙니다: {}", authorizationHeader.substring(0, Math.min(20, authorizationHeader.length())));
            return SecurityUserInfo.unauthenticated();
        }

        String token = authorizationHeader.substring(7);
        return getUserInfoFromToken(token);
    }

    /**
     * JWT 토큰에서 사용자 정보 추출
     * @param token JWT 토큰
     * @return 사용자 정보
     */
    public SecurityUserInfo getUserInfoFromToken(String token) {
        try {
            // 토큰 형식 검증
            if (!StringUtils.hasText(token)) {
                throw new JwtException(SecurityErrCode.SEC001, "토큰이 없습니다.");
            }

            // 블랙리스트 확인
            if (tokenBlacklistService.isBlacklisted(token)) {
                throw new JwtException(SecurityErrCode.SEC005, "블랙리스트에 등록된 토큰입니다.");
            }

            // JWT 토큰 파싱
            String username = clientJWTUtil.getUsername(token);
            String userId = clientJWTUtil.getUserId(token);
            String email = clientJWTUtil.getEmail(token);
            String roles = clientJWTUtil.getRoles(token);
            String category = clientJWTUtil.getCategory(token);

            // 필수 정보 검증
            if (!StringUtils.hasText(username) || !StringUtils.hasText(userId)) {
                throw new JwtException(SecurityErrCode.SEC013, "토큰에서 사용자 정보를 찾을 수 없습니다.");
            }

            // 사용자별 블랙리스트 확인
            if (tokenBlacklistService.isUserBlacklisted(userId)) {
                throw new JwtException(SecurityErrCode.SEC041, "블랙리스트에 등록된 사용자입니다.");
            }

            // 토큰 만료 확인 (선택적)
            if (clientJWTUtil.isExpired(token)) {
                throw new JwtException(SecurityErrCode.SEC003, "만료된 토큰입니다.");
            }

            log.debug("JWT 토큰에서 사용자 정보 추출 성공: username={}, userId={}", username, userId);

            return SecurityUserInfo.authenticated(username, userId, email, roles, category);

        } catch (JwtException e) {
            // 이미 JwtException인 경우 그대로 전파
            throw e;
        } catch (Exception e) {
            log.error("JWT 토큰 파싱 중 예상치 못한 오류: {}", e.getMessage(), e);
            throw new JwtException(SecurityErrCode.SEC004, "토큰 검증에 실패했습니다.", e);
        }
    }

    /**
     * AWS API Gateway에서 전달된 사용자 정보 처리
     * API Gateway에서 JWT 검증을 완료하고 헤더로 사용자 정보를 전달하는 경우
     * @param userIdHeader X-User-Id 헤더
     * @param usernameHeader X-Username 헤더
     * @param emailHeader X-Email 헤더
     * @param rolesHeader X-Roles 헤더
     * @return 사용자 정보
     */
    public SecurityUserInfo getUserInfoFromApiGatewayHeaders(String userIdHeader, String usernameHeader,
                                                           String emailHeader, String rolesHeader) {
        try {
            // 필수 헤더 검증
            if (!StringUtils.hasText(userIdHeader) || !StringUtils.hasText(usernameHeader)) {
                log.debug("API Gateway 필수 헤더가 누락되었습니다: userId={}, username={}", userIdHeader, usernameHeader);
                return SecurityUserInfo.unauthenticated();
            }

            // 사용자별 블랙리스트 확인
            if (tokenBlacklistService.isUserBlacklisted(userIdHeader)) {
                throw new JwtException(SecurityErrCode.SEC041, "블랙리스트에 등록된 사용자입니다.");
            }

            log.debug("API Gateway 헤더에서 사용자 정보 추출 성공: username={}, userId={}", usernameHeader, userIdHeader);

            return SecurityUserInfo.authenticated(usernameHeader, userIdHeader, emailHeader, rolesHeader, "access");

        } catch (JwtException e) {
            throw e;
        } catch (Exception e) {
            log.error("API Gateway 헤더 처리 중 예상치 못한 오류: {}", e.getMessage(), e);
            throw new JwtException(SecurityErrCode.SEC032, "API Gateway 인증 정보 파싱 실패입니다.", e);
        }
    }

    /**
     * 사용자 권한 확인
     * @param userInfo 사용자 정보
     * @param requiredRole 필요한 권한
     * @return 권한 보유 여부
     */
    public boolean hasRole(SecurityUserInfo userInfo, String requiredRole) {
        if (!userInfo.isAuthenticated()) {
            return false;
        }

        String roles = userInfo.getRoles();
        return StringUtils.hasText(roles) && roles.contains(requiredRole);
    }

    /**
     * 관리자 권한 확인
     * @param userInfo 사용자 정보
     * @return 관리자 여부
     */
    public boolean isAdmin(SecurityUserInfo userInfo) {
        return hasRole(userInfo, "ROLE_ADMIN");
    }
}