package com.kyj.core.security.client.util;

import com.kyj.core.security.auth.util.SecurityResponseUtil;
import com.kyj.core.security.client.annotation.PublicEndpoint;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import java.lang.reflect.Method;

/**
 * 엔드포인트 어노테이션 매칭 유틸리티
 */
@Slf4j
public class EndpointMatcher {

    /**
     * 퍼블릭 엔드포인트 여부 확인
     */
    public static boolean isPublicEndpoint(HttpServletRequest request) {
        try {
            // 기본 제외 경로들
            String path = request.getRequestURI();
            if (path.startsWith("/actuator") ||
                path.startsWith("/health") ||
                path.startsWith("/favicon.ico") ||
                path.startsWith("/error")) {
                return true;
            }

            // HandlerMethod 조회를 통한 어노테이션 확인
            HandlerMethod handlerMethod = getHandlerMethod(request);
            if (handlerMethod != null) {
                Method method = handlerMethod.getMethod();
                Class<?> clazz = handlerMethod.getBeanType();

                // 메서드 또는 클래스에 @PublicEndpoint 어노테이션이 있는지 확인
                return method.isAnnotationPresent(PublicEndpoint.class) ||
                       clazz.isAnnotationPresent(PublicEndpoint.class);
            }

            return false;
        } catch (Exception e) {
            log.debug("퍼블릭 엔드포인트 확인 중 오류: {}", e.getMessage());
            return false; // 확인 실패시 인증 필요로 처리
        }
    }

    /**
     * HandlerMethod 조회
     */
    private static HandlerMethod getHandlerMethod(HttpServletRequest request) {
        try {
            WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
            if (context == null) {
                return null;
            }


            // HandlerMapping을 통해 HandlerMethod 조회
            for (HandlerMapping handlerMapping : context.getBeansOfType(HandlerMapping.class).values()) {
                try {
                    HandlerExecutionChain chain = handlerMapping.getHandler(request);
                    if (chain != null && chain.getHandler() instanceof HandlerMethod) {
                        return (HandlerMethod) chain.getHandler();
                    }
                } catch (Exception e) {
                    // 다음 HandlerMapping
                        continue;
                }
            }

            return null;
        } catch (Exception e) {
            log.debug("HandlerMethod 조회 실패: {}", e.getMessage());
            return null;
        }
    }
}