package com.kyj.core.ws.interceptor;

import com.kyj.core.ws.config.WebSocketProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * 2025-09-24
 * @author 김용준
 * 웹소켓 핸드셰이크 인터셉터
 * 연결 전 인증 및 검증을 수행
 */
@Slf4j
@RequiredArgsConstructor
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private final WebSocketProperties webSocketProperties;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                 WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.debug("웹소켓 핸드셰이크 시작: {}", request.getURI());

        try {
            // 인증이 활성화된 경우 인증 처리
            if (webSocketProperties.getAuthentication().isEnabled()) {
                if (!authenticateUser(request, attributes)) {
                    log.warn("웹소켓 인증 실패: {}", request.getURI());
                    return false;
                }
            }

            // 세션 정보 설정
            setupSessionAttributes(request, attributes);

            log.debug("웹소켓 핸드셰이크 성공: {}", request.getURI());
            return true;

        } catch (Exception e) {
            log.error("웹소켓 핸드셰이크 중 오류 발생: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                             WebSocketHandler wsHandler, Exception exception) {
        if (exception != null) {
            log.error("웹소켓 핸드셰이크 후 오류: {}", exception.getMessage(), exception);
        } else {
            log.debug("웹소켓 핸드셰이크 완료: {}", request.getURI());
        }
    }

    /**
     * 사용자 인증 처리
     */
    private boolean authenticateUser(ServerHttpRequest request, Map<String, Object> attributes) {
        var authConfig = webSocketProperties.getAuthentication();

        if (authConfig.isUseJwt()) {
            // JWT 토큰 기반 인증
            return authenticateWithJwt(request, attributes, authConfig);
        } else if (authConfig.isUseSession()) {
            // 세션 기반 인증
            return authenticateWithSession(request, attributes);
        }

        // 기본적으로는 통과
        return true;
    }

    /**
     * JWT 토큰 기반 인증
     */
    private boolean authenticateWithJwt(ServerHttpRequest request, Map<String, Object> attributes,
                                       WebSocketProperties.Authentication authConfig) {
        String token = extractToken(request, authConfig);
        if (token == null) {
            log.debug("JWT 토큰이 없음");
            return false;
        }

        // JWT 토큰 검증 로직 (추상화됨)
        try {
            Map<String, Object> userInfo = validateJwtToken(token);
            if (userInfo != null) {
                attributes.putAll(userInfo);
                return true;
            }
        } catch (Exception e) {
            log.debug("JWT 토큰 검증 실패: {}", e.getMessage());
        }

        return false;
    }

    /**
     * 세션 기반 인증
     */
    private boolean authenticateWithSession(ServerHttpRequest request, Map<String, Object> attributes) {
        // 세션 인증 로직 (추상화됨)
        return validateSession(request, attributes);
    }

    /**
     * 세션 속성 설정
     */
    private void setupSessionAttributes(ServerHttpRequest request, Map<String, Object> attributes) {
        // 요청 정보 저장
        attributes.put("remoteAddress", request.getRemoteAddress());
        attributes.put("userAgent", request.getHeaders().getFirst("User-Agent"));
        attributes.put("connectTime", System.currentTimeMillis());

        // URI 정보 저장
        String query = request.getURI().getQuery();
        if (query != null) {
            // 쿼리 파라미터를 파싱하여 속성에 추가
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    attributes.put("param_" + keyValue[0], keyValue[1]);
                }
            }
        }
    }

    /**
     * 토큰 추출 (헤더 또는 파라미터에서)
     */
    private String extractToken(ServerHttpRequest request, WebSocketProperties.Authentication authConfig) {
        // 헤더에서 토큰 추출
        String headerToken = request.getHeaders().getFirst(authConfig.getTokenHeader());
        if (headerToken != null && headerToken.startsWith("Bearer ")) {
            return headerToken.substring(7);
        }

        // 쿼리 파라미터에서 토큰 추출
        String query = request.getURI().getQuery();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2 && authConfig.getTokenParameter().equals(keyValue[0])) {
                    return keyValue[1];
                }
            }
        }

        return null;
    }

    /**
     * JWT 토큰 검증 (추상화된 메서드)
     * 실제 구현에서는 JWT 라이브러리를 사용하여 토큰을 검증
     */
    protected Map<String, Object> validateJwtToken(String token) {
        // 기본 구현은 항상 성공으로 처리
        // 실제 사용 시에는 JWT 검증 로직을 구현해야 함
        log.debug("JWT 토큰 검증 로직이 구현되지 않음 - 기본 통과");
        return Map.of("authenticated", true);
    }

    /**
     * 세션 검증 (추상화된 메서드)
     * 실제 구현에서는 세션 검증 로직을 구현
     */
    protected boolean validateSession(ServerHttpRequest request, Map<String, Object> attributes) {
        // 기본 구현은 항상 성공으로 처리
        // 실제 사용 시에는 세션 검증 로직을 구현해야 함
        log.debug("세션 검증 로직이 구현되지 않음 - 기본 통과");
        return true;
    }
}