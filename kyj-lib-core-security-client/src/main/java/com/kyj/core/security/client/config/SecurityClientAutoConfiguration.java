package com.kyj.core.security.client.config;

import com.kyj.core.security.client.jwt.AuthJWTUtil;
import com.kyj.core.security.client.filter.ApiGatewaySecurityFilter;
import com.kyj.core.security.client.filter.JwtSecurityFilter;
import com.kyj.core.security.client.interceptor.SecurityInterceptor;
import com.kyj.core.security.client.service.RedisTokenBlacklistService;
import com.kyj.core.security.client.service.TokenBlacklistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Value;

/**
 * Security Client 자동 설정
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(SecurityProperties.class)
@ConditionalOnProperty(prefix = "kyj.security.client", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnMissingBean(name = "authSecurityAutoConfiguration") // 인증서버 설정이 없을 때만 활성화
public class SecurityClientAutoConfiguration {

    /**
     * AuthJWTUtil 빈 등록 (인증서버에 없는 경우만)
     */
    @Bean
    @ConditionalOnMissingBean(name = "authJWTUtil")
    @ConditionalOnProperty(prefix = "kyj.security.client", name = "use-api-gateway", havingValue = "false", matchIfMissing = true)
    public AuthJWTUtil authJWTUtil(@Value("${kyj.security.auth.token.secret}") String secret) {
        log.info("시큐리티 클라이언트용 AuthJWTUtil 등록 (인증서버 모듈 없음)");
        return new AuthJWTUtil(secret);
    }

    /**
     * TokenBlacklistService 빈 등록 (Redis가 있는 경우만)
     */
    @Bean
    @ConditionalOnClass(name = "org.springframework.data.redis.core.RedisTemplate")
    @ConditionalOnMissingBean
    public TokenBlacklistService tokenBlacklistService(RedisTemplate<String, String> redisTemplate) {
        log.info("RedisTokenBlacklistService 등록 - Redis 블랙리스트 기능 활성화");
        return new RedisTokenBlacklistService(redisTemplate);
    }

    /**
     * API Gateway 모드 필터 등록
     */
    @Bean
    @ConditionalOnProperty(prefix = "kyj.security.client", name = "use-api-gateway", havingValue = "true")
    @ConditionalOnMissingBean
    public ApiGatewaySecurityFilter apiGatewaySecurityFilter(SecurityProperties properties,
                                                           TokenBlacklistService blacklistService) {
        log.info("API Gateway 모드 보안 필터 등록");
        return new ApiGatewaySecurityFilter(properties, blacklistService);
    }

    /**
     * API Gateway 모드 필터 등록 (블랙리스트 없이)
     */
    @Bean
    @ConditionalOnProperty(prefix = "kyj.security.client", name = "use-api-gateway", havingValue = "true")
    @ConditionalOnMissingBean({TokenBlacklistService.class, ApiGatewaySecurityFilter.class})
    public ApiGatewaySecurityFilter apiGatewaySecurityFilterWithoutBlacklist(SecurityProperties properties) {
        log.info("API Gateway 모드 보안 필터 등록 (블랙리스트 없음)");
        return new ApiGatewaySecurityFilter(properties, null);
    }

    /**
     * JWT 모드 필터 등록
     */
    @Bean
    @ConditionalOnProperty(prefix = "kyj.security.client", name = "use-api-gateway", havingValue = "false", matchIfMissing = true)
    @ConditionalOnClass(AuthJWTUtil.class)
    @ConditionalOnMissingBean
    public JwtSecurityFilter jwtSecurityFilter(SecurityProperties properties,
                                             AuthJWTUtil authJWTUtil,
                                             TokenBlacklistService blacklistService) {
        log.info("JWT 모드 보안 필터 등록");
        return new JwtSecurityFilter(properties, authJWTUtil, blacklistService);
    }

    /**
     * JWT 모드 필터 등록 (블랙리스트 없이)
     */
    @Bean
    @ConditionalOnProperty(prefix = "kyj.security.client", name = "use-api-gateway", havingValue = "false", matchIfMissing = true)
    @ConditionalOnClass(AuthJWTUtil.class)
    @ConditionalOnMissingBean({TokenBlacklistService.class, JwtSecurityFilter.class})
    public JwtSecurityFilter jwtSecurityFilterWithoutBlacklist(SecurityProperties properties,
                                                              AuthJWTUtil authJWTUtil) {
        log.info("JWT 모드 보안 필터 등록 (블랙리스트 없음)");
        return new JwtSecurityFilter(properties, authJWTUtil, null);
    }

    /**
     * API Gateway 모드 필터 등록
     */
    @Bean
    @ConditionalOnProperty(prefix = "kyj.security.client", name = "use-api-gateway", havingValue = "true")
    public FilterRegistrationBean<ApiGatewaySecurityFilter> apiGatewayFilterRegistration(
            ApiGatewaySecurityFilter filter) {
        FilterRegistrationBean<ApiGatewaySecurityFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        registration.setName("apiGatewaySecurityFilter");
        log.info("API Gateway 보안 필터 등록 완료");
        return registration;
    }

    /**
     * JWT 모드 필터 등록
     */
    @Bean
    @ConditionalOnProperty(prefix = "kyj.security.client", name = "use-api-gateway", havingValue = "false", matchIfMissing = true)
    public FilterRegistrationBean<JwtSecurityFilter> jwtFilterRegistration(JwtSecurityFilter filter) {
        FilterRegistrationBean<JwtSecurityFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        registration.setName("jwtSecurityFilter");
        log.info("JWT 보안 필터 등록 완료");
        return registration;
    }

    /**
     * 보안 인터셉터 등록
     */
    @Bean
    @ConditionalOnMissingBean
    public SecurityInterceptor securityInterceptor(SecurityProperties properties) {
        log.info("보안 인터셉터 등록 완료");
        return new SecurityInterceptor(properties);
    }
}