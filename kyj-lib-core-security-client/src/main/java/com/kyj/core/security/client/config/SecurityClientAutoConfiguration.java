package com.kyj.core.security.client.config;

import com.kyj.core.security.client.filter.JwtAuthenticationFilter;
import com.kyj.core.security.client.interceptor.AuthenticationInterceptor;
import com.kyj.core.security.client.jwt.ClientJWTUtil;
import com.kyj.core.security.client.service.SecurityClientService;
import com.kyj.core.security.client.service.TokenBlacklistService;
import com.kyj.core.security.client.util.SecurityContextUtil;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 2025-09-21
 * @author 김용준
 * Security Client 모듈 자동 설정
 */
@AutoConfiguration
@ConditionalOnClass({ClientJWTUtil.class, RedisTemplate.class})
@ConditionalOnProperty(prefix = "kyj.security.client", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SecurityClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TokenBlacklistService tokenBlacklistService(RedisTemplate<String, String> redisTemplate) {
        return new TokenBlacklistService(redisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityClientService securityClientService(ClientJWTUtil clientJWTUtil, TokenBlacklistService tokenBlacklistService) {
        return new SecurityClientService(clientJWTUtil, tokenBlacklistService);
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityContextUtil securityContextUtil(SecurityClientService securityClientService) {
        return new SecurityContextUtil(securityClientService);
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtAuthenticationFilter jwtAuthenticationFilter(SecurityClientService securityClientService) {
        return new JwtAuthenticationFilter(securityClientService);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor();
    }
}