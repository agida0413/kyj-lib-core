package com.kyj.core.security.client.config;

import com.kyj.core.security.client.interceptor.SecurityInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Security Client Web 설정
 * 인터셉터 자동 등록
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "kyj.security.client", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SecurityClientWebConfig implements WebMvcConfigurer {

    private final SecurityInterceptor securityInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(securityInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/actuator/**",
                        "/health/**",
                        "/favicon.ico",
                        "/error"
                );

        log.info("보안 인터셉터 등록 완료");
    }
}