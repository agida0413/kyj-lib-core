package com.kyj.core.security.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 2025-09-21
 * @author 김용준
 * 인증 서버 보안 설정 프로퍼티
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "kyj.security.auth")
public class AuthSecurityProperties {

    /**
     * 토큰 관련 설정
     */
    private Token token = new Token();

    /**
     * OAuth2 관련 설정
     */
    private OAuth2 oauth2 = new OAuth2();


    @Getter
    @Setter
    public static class Token {
        /**
         * 액세스 토큰 만료 시간 (밀리초)
         */
        private Long accessTokenExpiry = 3600000L; // 1시간

        /**
         * 리프레시 토큰 만료 시간 (밀리초)
         */
        private Long refreshTokenExpiry = 86400000L; // 24시간

        /**
         * JWT 시크릿 키 (YML에서 필수 설정)
         */
        private String secret;
    }

    @Getter
    @Setter
    public static class OAuth2 {
        /**
         * 로그인 성공 후 리다이렉트 URL
         */
        private String successRedirectUrl = "http://localhost:3000/auth/success";

        /**
         * 로그인 실패 후 리다이렉트 URL
         */
        private String failureRedirectUrl = "http://localhost:3000/auth/failure";

        /**
         * 로그아웃 후 리다이렉트 URL
         */
        private String logoutRedirectUrl = "http://localhost:3000";

        /**
         * 지원되는 OAuth2 제공자들
         */
//        private String[] supportedProviders = {"google", "naver", "kakao"};
    }


}