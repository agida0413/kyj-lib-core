package com.kyj.core.security.client.util;

import com.kyj.core.security.client.annotation.PublicEndpoint;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 엔드포인트 어노테이션 매칭 유틸리티
 */
@Slf4j
public class EndpointMatcher {

    /**
     * HandlerMethod 캐시 (성능 최적화)
     */
    private static final ConcurrentMap<String, HandlerMethod> HANDLER_CACHE = new ConcurrentHashMap<>();

    /**
     * 퍼블릭 엔드포인트 캐시 (성능 최적화)
     */
    private static final ConcurrentMap<String, Boolean> PUBLIC_ENDPOINT_CACHE = new ConcurrentHashMap<>();
    /**
     * 퍼블릭 엔드포인트 여부 확인(정적) - 설정 기반
     */
    public static boolean isStaticPublicEndpoint(HttpServletRequest request, List<String> staticEndpoints) {
        String path = request.getRequestURI();

        if (staticEndpoints == null || staticEndpoints.isEmpty()) {
            return false;
        }

        return staticEndpoints.stream()
                .anyMatch(path::startsWith);
    }


    /**
     * 퍼블릭 엔드포인트 여부 확인(어노테이션+정적) - 설정 기반
     */
    public static boolean isPublicEndpoint(HttpServletRequest request, List<String> staticEndpoints) {
        try {
            // 기본 제외 경로들
            if (isStaticPublicEndpoint(request, staticEndpoints)) {
                return true;
            }
            // HandlerMethod 조회를 통한 어노테이션 확인
            HandlerMethod handlerMethod = getHandlerMethod(request);
            if (handlerMethod != null) {
                return isPublicEndpoint(handlerMethod);
            }

            return false;
        } catch (Exception e) {
            log.debug("퍼블릭 엔드포인트 확인 중 오류: {}", e.getMessage());
            return false; // 확인 실패시 인증 필요로 처리
        }
    }

    /**
     * HandlerMethod로 퍼블릭 엔드포인트 여부 확인 (인터셉터용)
     */
    public static boolean isPublicEndpoint(HandlerMethod handlerMethod) {
        if (handlerMethod == null) {
            return false;
        }

        Method method = handlerMethod.getMethod();
        Class<?> clazz = handlerMethod.getBeanType();

        // 메서드 또는 클래스에 @PublicEndpoint 어노테이션이 있는지 확인
        return method.isAnnotationPresent(PublicEndpoint.class) ||
               clazz.isAnnotationPresent(PublicEndpoint.class);
    }



    /**
     * HandlerMethod 조회 (캐시 적용)
     */
    private static HandlerMethod getHandlerMethod(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        String cacheKey = method + ":" + path;

        // 캐시에서 먼저 확인
        HandlerMethod cached = HANDLER_CACHE.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        try {
            WebApplicationContext context = WebApplicationContextUtils
                    .getWebApplicationContext(request.getServletContext());
            if (context == null) {
                log.debug("WebApplicationContext를 찾을 수 없음");
                return null;
            }

            // HandlerMapping을 통해 HandlerMethod 조회
            for (HandlerMapping handlerMapping : context.getBeansOfType(HandlerMapping.class).values()) {
                try {
                    HandlerExecutionChain chain = handlerMapping.getHandler(request);
                    if (chain != null && chain.getHandler() instanceof HandlerMethod handlerMethod) {
                        // 캐시에 저장 (최대 1000개 제한)
                        if (HANDLER_CACHE.size() < 1000) {
                            HANDLER_CACHE.put(cacheKey, handlerMethod);
                        }
                        return handlerMethod;
                    }
                } catch (Exception e) {
                    // 다음 HandlerMapping에서 시도
                    log.trace("HandlerMapping 처리 중 오류: {} - {}",
                             handlerMapping.getClass().getSimpleName(), e.getMessage());
                    continue;
                }
            }

            return null;
        } catch (Exception e) {
            log.warn("HandlerMethod 조회 실패: {} - {}", path, e.getMessage());
            return null;
        }
    }

}