package com.kyj.core.security.auth.config;

import com.kyj.core.security.client.jwt.AuthJWTUtil;
import com.kyj.core.security.auth.service.AuthTokenService;
import com.kyj.core.security.auth.service.AuthTokenServiceImpl;
import com.kyj.core.security.auth.service.CustomOAuth2UserService;
import com.kyj.core.security.auth.service.AuthMemberService;
import com.kyj.core.security.auth.handler.OAuth2SuccessHandler;
import com.kyj.core.security.auth.handler.CustomAuthenticationEntryPoint;
import com.kyj.core.security.auth.handler.CustomAccessDeniedHandler;
import com.kyj.core.security.auth.controller.AuthTokenController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import com.kyj.core.security.client.config.SecurityProperties;
import com.kyj.core.security.client.util.EndpointMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

/**
 * 인증서버 자동 설정
 */
@Slf4j
@AutoConfiguration
@EnableWebSecurity
@EnableConfigurationProperties({AuthSecurityProperties.class})
@ConditionalOnProperty(prefix = "kyj.security.auth", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AuthSecurityAutoConfiguration {

    /**
     * AuthJWTUtil 빈 등록 (클라이언트 모듈에 없는 경우만)
     */
    @Bean(name = "authJWTUtil")
    @ConditionalOnMissingBean(name = "authJWTUtil")
    public AuthJWTUtil authJWTUtil(@Value("${kyj.security.auth.token.secret}") String secret) {
        log.info("인증서버용 AuthJWTUtil 등록");
        return new AuthJWTUtil(secret);
    }

    /**
     * AuthTokenService 빈 등록
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthTokenService authTokenService(AuthJWTUtil authJWTUtil,
                                           RedisTemplate<String, String> redisTemplate,
                                           AuthMemberService authMemberService) {
        log.info("AuthTokenService 등록");
        return new AuthTokenServiceImpl(redisTemplate, authJWTUtil, authMemberService);
    }

    /**
     * CustomOAuth2UserService 빈 등록
     */
    @Bean
    @ConditionalOnMissingBean
    public CustomOAuth2UserService customOAuth2UserService(AuthMemberService authMemberService) {
        log.info("CustomOAuth2UserService 등록");
        return new CustomOAuth2UserService(authMemberService);
    }

    /**
     * OAuth2SuccessHandler 빈 등록
     */
    @Bean
    @ConditionalOnMissingBean
    public OAuth2SuccessHandler oAuth2SuccessHandler(AuthJWTUtil authJWTUtil,
                                                   AuthTokenService authTokenService,
                                                   AuthSecurityProperties properties) {
        log.info("OAuth2SuccessHandler 등록");
        return new OAuth2SuccessHandler(authJWTUtil, authTokenService, properties);
    }

    /**
     * AuthTokenController 빈 등록
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthTokenController authTokenController(AuthTokenService authTokenService) {
        log.info("AuthTokenController 등록");
        return new AuthTokenController(authTokenService);
    }

    /**
     * OAuth2 인증을 위한 SecurityFilterChain 설정
     */
    @Bean(name = "localAuthSecurityFilterChain")
    @ConditionalOnMissingBean(name = "authSecurityFilterChain")
    @Profile("local")
    public SecurityFilterChain localAuthSecurityFilterChain(
            HttpSecurity http,
            CustomOAuth2UserService customOAuth2UserService,
            OAuth2SuccessHandler oAuth2SuccessHandler,
            CustomAuthenticationEntryPoint authenticationEntryPoint,
            CustomAccessDeniedHandler accessDeniedHandler,
            AuthSecurityProperties authProperties,
            SecurityProperties securityProperties) throws Exception {

        log.info("OAuth2 SecurityFilterChain 등록");


        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOrigins(Arrays.asList(
                                "http://localhost:3000","http://localhost:80"
                        ));
                        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);


                        // 노출할 헤더 (쿠키 + 인증 토큰 등)
                        configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));

                        return configuration;
                    }
                }));

        // @PublicEndpoint 어노테이션 매처 생성
        RequestMatcher publicEndpointMatcher = new RequestMatcher() {
            @Override
            public boolean matches(HttpServletRequest request) {
                return EndpointMatcher.isPublicEndpoint(request, securityProperties.getStaticPublicEndpoints());
            }
        };

        // 퍼블릭 엔드포인트 목록 가져오기
        String[] publicEndpoints = securityProperties.getStaticPublicEndpoints().toArray(new String[0]);

        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService))
                .successHandler(oAuth2SuccessHandler)
                .failureUrl(authProperties.getOauth2().getFailureRedirectUrl()))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/oauth2/**", "/login/**").permitAll()
                .requestMatchers(publicEndpoints).permitAll()
                .requestMatchers(publicEndpointMatcher).permitAll() // @PublicEndpoint 어노테이션 매칭
                .anyRequest().authenticated())
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(authenticationEntryPoint) // 인증 실패 시 JSON 응답
                .accessDeniedHandler(accessDeniedHandler)) // 인가 실패 시 JSON 응답
            .build();
    }


    @Bean(name = "authSecurityFilterChain")
    @ConditionalOnMissingBean(name = "localAuthSecurityFilterChain")
    public SecurityFilterChain authSecurityFilterChain(
            HttpSecurity http,
            CustomOAuth2UserService customOAuth2UserService,
            OAuth2SuccessHandler oAuth2SuccessHandler,
            CustomAuthenticationEntryPoint authenticationEntryPoint,
            CustomAccessDeniedHandler accessDeniedHandler,
            AuthSecurityProperties authProperties,
            SecurityProperties securityProperties) throws Exception {

        log.info("OAuth2 SecurityFilterChain 등록");


        // @PublicEndpoint 어노테이션 매처 생성
        RequestMatcher publicEndpointMatcher = new RequestMatcher() {
            @Override
            public boolean matches(HttpServletRequest request) {
                return EndpointMatcher.isPublicEndpoint(request, securityProperties.getStaticPublicEndpoints());
            }
        };

        // 퍼블릭 엔드포인트 목록 가져오기
        String[] publicEndpoints = securityProperties.getStaticPublicEndpoints().toArray(new String[0]);

        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                        .failureUrl(authProperties.getOauth2().getFailureRedirectUrl()))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/oauth2/**", "/login/**").permitAll()
                        .requestMatchers(publicEndpoints).permitAll()
                        .requestMatchers(publicEndpointMatcher).permitAll() // @PublicEndpoint 어노테이션 매칭
                        .anyRequest().authenticated())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint) // 인증 실패 시 JSON 응답
                        .accessDeniedHandler(accessDeniedHandler)) // 인가 실패 시 JSON 응답
                .build();
    }
}