package com.kyj.core.security.client.interceptor;

import com.kyj.core.security.client.annotation.PublicEndpoint;
import com.kyj.core.security.client.annotation.RequireRole;
import com.kyj.core.security.client.config.SecurityProperties;
import com.kyj.core.security.client.dto.SecurityUser;
import com.kyj.core.security.client.util.EndpointMatcher;
import com.kyj.core.security.client.util.SecurityContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class SecurityInterceptor implements HandlerInterceptor {

    private final SecurityProperties properties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        // HandlerMethod가 아닌 경우 통과
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        Method method = handlerMethod.getMethod();
        Class<?> clazz = handlerMethod.getBeanType();

        // 1. @PublicEndpoint 확인 - 퍼블릭 엔드포인트는 권한 체크 불필요
        if (EndpointMatcher.isPublicEndpoint(request, properties.getStaticPublicEndpoints())) {
            log.debug("퍼블릭 엔드포인트 접근: {}", request.getRequestURI());
            return true;
        }

        // 2. 사용자 정보 조회 (필터에서 이미 인증 체크 완료)
        SecurityUser user = SecurityContext.getUser();

        // 3. @RequireRole 체크
        String[] requiredRoles = getRequiredRoles(method, clazz);
        if (requiredRoles != null && requiredRoles.length > 0) {
            if (!user.hasAnyRole(requiredRoles)) {
                log.warn("권한 부족한 사용자 접근: {} (필요 권한: {}, 보유 권한: {}) -> {}",
                        "***", String.join(",", requiredRoles), user.getRoles() != null ? "***" : null, request.getRequestURI());
                SecurityContext.requireRole(requiredRoles[0]); //하나만 넘김 
            }
        }

        log.debug("인증/권한 확인 완료: {} -> {}", "***", request.getRequestURI());
        return true;
    }


    /**
     * RequireRole 어노테이션에서 필요한 권한 배열 추출 (메서드 우선, 클래스 차순)
     */
    private String[] getRequiredRoles(Method method, Class<?> clazz) {
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