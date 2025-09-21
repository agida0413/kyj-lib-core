package com.kyj.core.security.client.util;

import com.kyj.core.security.client.dto.SecurityUserInfo;
import com.kyj.core.security.client.service.SecurityClientService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 2025-09-21
 * @author 김용준
 * 시큐리티 컨텍스트 관련 유틸리티
 * 현재 요청의 사용자 정보를 쉽게 가져올 수 있는 편의 메서드 제공
 */
@Component
@RequiredArgsConstructor
public class SecurityContextUtil {

    private final SecurityClientService securityClientService;

    /**
     * 현재 HTTP 요청에서 사용자 정보를 가져옴
     * @return 사용자 정보
     */
    public SecurityUserInfo getCurrentUserInfo() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return SecurityUserInfo.unauthenticated();
        }

        String authorizationHeader = request.getHeader("Authorization");
        return securityClientService.getUserInfoFromHeader(authorizationHeader);
    }

    /**
     * 현재 사용자명을 가져옴
     * @return 사용자명 (인증되지 않은 경우 null)
     */
    public String getCurrentUsername() {
        SecurityUserInfo userInfo = getCurrentUserInfo();
        return userInfo.isAuthenticated() ? userInfo.getUsername() : null;
    }

    /**
     * 현재 사용자의 ID를 가져옴
     * @return 사용자 ID (인증되지 않은 경우 null)
     */
    public String getCurrentUserId() {
        SecurityUserInfo userInfo = getCurrentUserInfo();
        return userInfo.isAuthenticated() ? userInfo.getUserId() : null;
    }

    /**
     * 현재 사용자의 이메일을 가져옴
     * @return 이메일 (인증되지 않은 경우 null)
     */
    public String getCurrentUserEmail() {
        SecurityUserInfo userInfo = getCurrentUserInfo();
        return userInfo.isAuthenticated() ? userInfo.getEmail() : null;
    }

    /**
     * 현재 사용자가 특정 권한을 가지고 있는지 확인
     * @param role 확인할 권한
     * @return 권한 보유 여부
     */
    public boolean hasRole(String role) {
        SecurityUserInfo userInfo = getCurrentUserInfo();
        return securityClientService.hasRole(userInfo, role);
    }

    /**
     * 현재 사용자가 인증되었는지 확인
     * @return 인증 여부
     */
    public boolean isAuthenticated() {
        return getCurrentUserInfo().isAuthenticated();
    }

    /**
     * 현재 HTTP 요청을 가져옴
     * @return HttpServletRequest (요청 컨텍스트가 없는 경우 null)
     */
    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}