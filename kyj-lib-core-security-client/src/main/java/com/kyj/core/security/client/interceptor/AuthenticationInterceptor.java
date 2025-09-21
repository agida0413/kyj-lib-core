package com.kyj.core.security.client.interceptor;

import com.kyj.core.security.client.annotation.PublicEndpoint;
import com.kyj.core.security.client.annotation.RequireAuth;
import com.kyj.core.security.client.annotation.RequireRole;
import com.kyj.core.security.client.dto.SecurityUserInfo;
import com.kyj.core.security.client.exception.AuthenticationException;
import com.kyj.core.security.client.exception.AuthorizationException;
import com.kyj.core.security.client.exception.SecurityErrCode;
import com.kyj.core.security.client.util.SecurityHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 2025-09-21
 * @author 김용준
 * 기본 인증 필요 정책을 적용하는 인터셉터
 * - 기본: 모든 엔드포인트는 인증 필요
 * - @PublicEndpoint: 인증 불필요 (퍼블릭)
 * - @RequireAuth: 인증 필요 + 특정 권한
 * - @RequireRole: 인증 필요 + 특정 권한
 */
@Slf4j
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // Handler가 메서드가 아닌 경우 (예: 정적 리소스) 통과
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        Class<?> clazz = handlerMethod.getBeanType();

        SecurityUserInfo userInfo = SecurityHolder.getUserInfo();

        // 1. @PublicEndpoint 확인 - 퍼블릭 엔드포인트는 인증 불필요
        PublicEndpoint publicEndpoint = getPublicEndpointAnnotation(method, clazz);
        if (publicEndpoint != null) {
            log.debug("퍼블릭 엔드포인트 접근: {}", request.getRequestURI());
            return true;
        }

        // 2. 기본 정책: 인증 필요
        if (!userInfo.isAuthenticated()) {
            log.warn("인증되지 않은 사용자가 보호된 리소스에 접근 시도: {}", request.getRequestURI());
            throw new AuthenticationException(SecurityErrCode.SEC010, "인증이 필요한 요청입니다.");
        }

        // 3. @RequireAuth 처리
        RequireAuth requireAuth = getRequireAuthAnnotation(method, clazz);
        if (requireAuth != null && requireAuth.roles().length > 0) {
            checkRolePermission(userInfo, requireAuth.roles(), requireAuth.requireAll(), request.getRequestURI());
        }

        // 4. @RequireRole 처리
        RequireRole requireRole = getRequireRoleAnnotation(method, clazz);
        if (requireRole != null) {
            checkRolePermission(userInfo, requireRole.value(), requireRole.requireAll(), request.getRequestURI());
        }

        return true;
    }

    /**
     * PublicEndpoint 어노테이션 추출 (메서드 우선, 클래스는 보조)
     */
    private PublicEndpoint getPublicEndpointAnnotation(Method method, Class<?> clazz) {
        PublicEndpoint methodAnnotation = method.getAnnotation(PublicEndpoint.class);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }
        return clazz.getAnnotation(PublicEndpoint.class);
    }

    /**
     * RequireAuth 어노테이션 추출 (메서드 우선, 클래스는 보조)
     */
    private RequireAuth getRequireAuthAnnotation(Method method, Class<?> clazz) {
        RequireAuth methodAnnotation = method.getAnnotation(RequireAuth.class);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }
        return clazz.getAnnotation(RequireAuth.class);
    }

    /**
     * RequireRole 어노테이션 추출 (메서드 우선, 클래스는 보조)
     */
    private RequireRole getRequireRoleAnnotation(Method method, Class<?> clazz) {
        RequireRole methodAnnotation = method.getAnnotation(RequireRole.class);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }
        return clazz.getAnnotation(RequireRole.class);
    }

    /**
     * 권한 확인 로직
     */
    private void checkRolePermission(SecurityUserInfo userInfo, String[] requiredRoles, boolean requireAll, String requestUri) {
        if (requiredRoles.length == 0) {
            return;
        }

        String userRoles = userInfo.getRoles();
        if (!StringUtils.hasText(userRoles)) {
            log.warn("권한 정보가 없는 사용자의 접근: {} -> {}", userInfo.getUsername(), requestUri);
            throw new AuthorizationException(SecurityErrCode.SEC021, "권한 정보가 없습니다.");
        }

        boolean hasPermission = false;
        int matchedRoles = 0;

        for (String requiredRole : requiredRoles) {
            if (userRoles.contains(requiredRole)) {
                matchedRoles++;
                hasPermission = true;
                if (!requireAll) {
                    // 하나라도 있으면 됨
                    break;
                }
            }
        }

        // 모든 권한이 필요한 경우
        if (requireAll && matchedRoles != requiredRoles.length) {
            hasPermission = false;
        }

        if (!hasPermission) {
            log.warn("권한 부족한 사용자 접근: {} (가진 권한: {}, 필요한 권한: {}, 모두 필요: {}) -> {}",
                    userInfo.getUsername(), userRoles, Arrays.toString(requiredRoles), requireAll, requestUri);

            throw new AuthorizationException(SecurityErrCode.SEC021,
                    String.format("필요한 권한이 부족합니다. 필요: %s", Arrays.toString(requiredRoles)));
        }

        log.debug("권한 확인 성공: {} -> {}", userInfo.getUsername(), requestUri);
    }
}