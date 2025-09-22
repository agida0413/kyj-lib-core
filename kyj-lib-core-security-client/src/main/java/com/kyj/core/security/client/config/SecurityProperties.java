package com.kyj.core.security.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Security Client 설정 프로퍼티
 */
@Data
@ConfigurationProperties(prefix = "kyj.security.client")
public class SecurityProperties {

    /**
     * 보안 모듈 활성화 여부 (기본: true)
     */
    private boolean enabled = true;

    /**
     * AWS API Gateway 사용 여부 (기본: false)
     * true: API Gateway에서 인증 처리, 헤더로 사용자 정보 전달
     * false: 일반 JWT 필터로 동작
     */
    private boolean useApiGateway = false;

    /**
     * 블랙리스트 체크 여부 (기본: true)
     * Redis가 없으면 무시됨
     */
    private boolean enableBlacklist = true;

    /**
     * API Gateway 헤더 설정
     */
    private final ApiGateway apiGateway = new ApiGateway();

    @Data
    public static class ApiGateway {
        /**
         * 사용자 ID 헤더명 (기본: X-User-Id)
         */
        private String userIdHeader = "X-User-Id";

        /**
         * 사용자명 헤더명 (기본: X-Username)
         */
        private String usernameHeader = "X-Username";

        /**
         * 이메일 헤더명 (기본: X-Email)
         */
        private String emailHeader = "X-Email";

        /**
         * 권한 헤더명 (기본: X-Roles)
         */
        private String rolesHeader = "X-Roles";

        /**
         * JWT 토큰 헤더명 (블랙리스트 체크용, 기본: X-Token)
         */
        private String tokenHeader = "X-Token";
    }
}