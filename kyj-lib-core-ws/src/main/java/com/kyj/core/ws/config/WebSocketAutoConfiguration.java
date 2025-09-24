package com.kyj.core.ws.config;

import com.kyj.core.ws.handler.DefaultWebSocketHandler;
import com.kyj.core.ws.interceptor.WebSocketHandshakeInterceptor;
import com.kyj.core.ws.service.WebSocketMessageService;
import com.kyj.core.ws.service.WebSocketMessageServiceImpl;
import com.kyj.core.ws.service.WebSocketSessionService;
import com.kyj.core.ws.service.WebSocketSessionServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * 2025-09-24
 * @author 김용준
 * KYJ WebSocket 자동 설정
 */
@Slf4j
@AutoConfiguration
@EnableWebSocketMessageBroker
@EnableConfigurationProperties(WebSocketProperties.class)
@ConditionalOnProperty(prefix = "kyj.websocket", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class WebSocketAutoConfiguration implements WebSocketMessageBrokerConfigurer {

    private final WebSocketProperties webSocketProperties;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 구독 prefix 설정
        String[] subscriptionPrefixes = webSocketProperties.getMessageBroker()
                .getSubscriptionPrefixes().toArray(new String[0]);
        registry.enableSimpleBroker(subscriptionPrefixes);

        // 애플리케이션 destination prefix 설정
        String[] appDestinationPrefixes = webSocketProperties.getMessageBroker()
                .getApplicationDestinationPrefixes().toArray(new String[0]);
        registry.setApplicationDestinationPrefixes(appDestinationPrefixes);

        // 사용자 destination prefix 설정
        registry.setUserDestinationPrefix(webSocketProperties.getMessageBroker().getUserDestinationPrefix());

        log.info("KYJ WebSocket 모듈 - 메시지 브로커 설정 완료");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        var endpointRegistration = registry.addEndpoint(webSocketProperties.getEndpoint());

        // CORS 설정
        if (!webSocketProperties.getAllowedOrigins().isEmpty()) {
            String[] origins = webSocketProperties.getAllowedOrigins().toArray(new String[0]);
            endpointRegistration.setAllowedOriginPatterns(origins);
        } else {
            endpointRegistration.setAllowedOriginPatterns("*");
        }

        // 인터셉터 추가
        endpointRegistration.addInterceptors(webSocketHandshakeInterceptor());

        // SockJS 지원
        if (webSocketProperties.isSockJsEnabled()) {
            endpointRegistration.withSockJS();
        }

        log.info("KYJ WebSocket 모듈 - STOMP 엔드포인트 등록 완료: {}", webSocketProperties.getEndpoint());
    }

    /**
     * 웹소켓 핸드셰이크 인터셉터 빈 등록
     */
    @Bean
    @ConditionalOnMissingBean
    public WebSocketHandshakeInterceptor webSocketHandshakeInterceptor() {
        log.info("KYJ WebSocket 모듈 - 핸드셰이크 인터셉터 등록");
        return new WebSocketHandshakeInterceptor(webSocketProperties);
    }

    /**
     * 기본 웹소켓 핸들러 빈 등록
     */
    @Bean
    @ConditionalOnMissingBean
    public DefaultWebSocketHandler defaultWebSocketHandler() {
        log.info("KYJ WebSocket 모듈 - 기본 핸들러 등록");
        return new DefaultWebSocketHandler();
    }

    /**
     * 웹소켓 세션 서비스 빈 등록
     */
    @Bean
    @ConditionalOnMissingBean
    public WebSocketSessionService webSocketSessionService() {
        log.info("KYJ WebSocket 모듈 - 세션 서비스 등록");
        return new WebSocketSessionServiceImpl(webSocketProperties);
    }

    /**
     * 웹소켓 메시지 서비스 빈 등록
     */
    @Bean
    @ConditionalOnMissingBean
    public WebSocketMessageService webSocketMessageService(WebSocketSessionService sessionService) {
        log.info("KYJ WebSocket 모듈 - 메시지 서비스 등록");
        return new WebSocketMessageServiceImpl(sessionService, webSocketProperties);
    }
}