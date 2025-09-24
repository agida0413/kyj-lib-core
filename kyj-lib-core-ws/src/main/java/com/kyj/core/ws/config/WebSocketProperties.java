package com.kyj.core.ws.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 2025-09-24
 * @author 김용준
 * KYJ WebSocket 모듈 설정 properties
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "kyj.websocket")
public class WebSocketProperties {

    /**
     * 웹소켓 모듈 활성화 여부
     */
    private boolean enabled = true;

    /**
     * 연결 엔드포인트
     */
    private String endpoint = "/ws";

    /**
     * SockJS 지원 여부
     */
    private boolean sockJsEnabled = true;

    /**
     * CORS 허용 origins
     */
    private List<String> allowedOrigins = new ArrayList<>();

    /**
     * 메시지 브로커 설정
     */
    @NestedConfigurationProperty
    private MessageBroker messageBroker = new MessageBroker();

    /**
     * 인증 설정
     */
    @NestedConfigurationProperty
    private Authentication authentication = new Authentication();

    /**
     * 세션 관리 설정
     */
    @NestedConfigurationProperty
    private SessionManagement sessionManagement = new SessionManagement();

    /**
     * 메시지 처리 설정
     */
    @NestedConfigurationProperty
    private MessageProcessing messageProcessing = new MessageProcessing();

    @Getter
    @Setter
    public static class MessageBroker {
        /**
         * 구독 prefix
         */
        private List<String> subscriptionPrefixes = List.of("/topic", "/queue");

        /**
         * 애플리케이션 destination prefix
         */
        private List<String> applicationDestinationPrefixes = List.of("/app");

        /**
         * 사용자 destination prefix
         */
        private String userDestinationPrefix = "/user";

        /**
         * 외부 메시지 브로커 사용 여부
         */
        private boolean useExternalBroker = false;

        /**
         * 외부 브로커 설정 (RabbitMQ, ActiveMQ 등)
         */
        private Map<String, Object> externalBrokerConfig = new HashMap<>();
    }

    @Getter
    @Setter
    public static class Authentication {
        /**
         * 인증 활성화 여부
         */
        private boolean enabled = false;

        /**
         * JWT 토큰 기반 인증 사용 여부
         */
        private boolean useJwt = false;

        /**
         * 세션 기반 인증 사용 여부
         */
        private boolean useSession = true;

        /**
         * 토큰 헤더 이름
         */
        private String tokenHeader = "Authorization";

        /**
         * 토큰 파라미터 이름
         */
        private String tokenParameter = "token";
    }

    @Getter
    @Setter
    public static class SessionManagement {
        /**
         * 세션 관리 활성화 여부
         */
        private boolean enabled = true;

        /**
         * 최대 세션 수 (0은 무제한)
         */
        private int maxSessions = 0;

        /**
         * 세션 타임아웃 (초, 0은 무제한)
         */
        private long sessionTimeout = 0;

        /**
         * 세션 정보 저장 방식 (memory, redis)
         */
        private String storageType = "memory";
    }

    @Getter
    @Setter
    public static class MessageProcessing {
        /**
         * 메시지 처리 활성화 여부
         */
        private boolean enabled = true;

        /**
         * 메시지 큐 크기
         */
        private int messageQueueSize = 1000;

        /**
         * 메시지 처리 스레드 수
         */
        private int processingThreads = 5;

        /**
         * 메시지 크기 제한 (bytes)
         */
        private long maxMessageSize = 1024 * 1024; // 1MB

        /**
         * 브로드캐스트 활성화 여부
         */
        private boolean enableBroadcast = true;
    }
}