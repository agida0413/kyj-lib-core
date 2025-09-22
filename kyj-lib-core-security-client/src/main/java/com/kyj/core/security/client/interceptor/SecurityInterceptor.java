package com.kyj.core.security.client.interceptor;

import com.kyj.core.security.client.annotation.PublicEndpoint;
import com.kyj.core.security.client.annotation.RequireRole;
import com.kyj.core.security.client.dto.SecurityUser;
import com.kyj.core.security.client.util.SecurityContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

/**
 * 보안 인터셉터
 * 디폴트 인증 필수, @PublicEndpoint만 예외
 * @RequireRole로 권한 체크
 */
@Slf4j
public class SecurityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        // HandlerMethod가 아닌 경우 통과
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        Class<?> clazz = handlerMethod.getBeanType();

        // 1. @PublicEndpoint 확인 - 퍼블릭 엔드포인트는 인증 불필요
        if (isPublicEndpoint(method, clazz)) {
            log.debug("퍼블릭 엔드포인트 접근: {}", request.getRequestURI());
            return true;
        }

        // 2. 디폴트 정책: 인증 필수
        SecurityUser user = SecurityContext.getUser();
        if (!user.isAuthenticated()) {
            log.warn("인증되지 않은 사용자 접근 시도: {}", request.getRequestURI());
            SecurityContext.requireAuthenticated(); // core 모듈의 예외 발생
        }

        // 3. @RequireRole 체크
        String requiredRole = getRequiredRole(method, clazz);
        if (StringUtils.hasText(requiredRole)) {
            if (!user.hasRole(requiredRole)) {
                log.warn("권한 부족한 사용자 접근: {} (필요 권한: {}, 보유 권한: {}) -> {}",
                        user.getUsername(), requiredRole, user.getRoles(), request.getRequestURI());
                SecurityContext.requireRole(requiredRole); // core 모듈의 예외 발생
            }
        }

        log.debug("인증/권한 확인 완료: {} -> {}", user.getUsername(), request.getRequestURI());
        return true;
    }

    /**
     * PublicEndpoint 어노테이션 확인 (메서드 우선, 클래스 차순)
     */
    private boolean isPublicEndpoint(Method method, Class<?> clazz) {
        return method.isAnnotationPresent(PublicEndpoint.class) ||
               clazz.isAnnotationPresent(PublicEndpoint.class);
    }

    /**
     * RequireRole 어노테이션에서 필요한 권한 추출 (메서드 우선, 클래스 차순)
     */
    private String getRequiredRole(Method method, Class<?> clazz) {
        RequireRole methodRole = method.getAnnotation(RequireRole.class);
        if (methodRole != null) {
            return methodRole.value();
        }

        RequireRole classRole = clazz.getAnnotation(RequireRole.class);
        if (classRole != null) {
            return classRole.value();
        }

        return null;
    }
}