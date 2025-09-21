package com.kyj.core.security.client.config;

import com.kyj.core.security.client.filter.JwtAuthenticationFilter;
import com.kyj.core.security.client.interceptor.AuthenticationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 2025-09-21
 * @author 김용준
 * Security Client 웹 설정 자동 구성
 */
@AutoConfiguration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "kyj.security.client", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SecurityClientWebConfig implements WebMvcConfigurer {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationInterceptor authenticationInterceptor;

    /**
     * JWT 인증 필터 등록
     */
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilterRegistration() {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(jwtAuthenticationFilter);
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        registration.setName("jwtAuthenticationFilter");
        return registration;
    }

    /**
     * 인증 인터셉터 등록
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/actuator/**",
                        "/health/**",
                        "/error",
                        "/favicon.ico"
                );
    }
}