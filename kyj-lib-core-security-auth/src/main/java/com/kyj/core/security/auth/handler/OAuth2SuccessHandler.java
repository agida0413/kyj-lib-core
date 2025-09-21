package com.kyj.core.security.auth.handler;

import com.kyj.core.security.auth.config.AuthSecurityProperties;
import com.kyj.core.security.auth.dto.AuthMemberDTO;
import com.kyj.core.security.auth.dto.CustomOAuth2User;
import com.kyj.core.security.auth.jwt.AuthJWTUtil;
import com.kyj.core.security.auth.service.AuthTokenService;
import com.kyj.core.util.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 2025-09-21
 * @author 김용준
 * OAuth2 로그인 성공 시 JWT 토큰을 발급하는 핸들러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthJWTUtil authJWTUtil;
    private final AuthTokenService authTokenService;
    private final AuthSecurityProperties authSecurityProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        AuthMemberDTO memberDTO = oAuth2User.getAuthMemberDTO();

        log.info("OAuth2 로그인 성공: 사용자명 = {}, 이메일 = {}", memberDTO.getUsername(), memberDTO.getEmail());

        try {
            // JWT 토큰 생성
            String accessToken = authJWTUtil.createJwt(
                    "access",
                    memberDTO.getUsername(),
                    String.valueOf(memberDTO.getUserId()),
                    memberDTO.getEmail(),
                    memberDTO.getRole(),
                    authSecurityProperties.getToken().getAccessTokenExpiry()
            );

            String refreshToken = authJWTUtil.createJwt(
                    "refresh",
                    memberDTO.getUsername(),
                    String.valueOf(memberDTO.getUserId()),
                    memberDTO.getEmail(),
                    memberDTO.getRole(),
                    authSecurityProperties.getToken().getRefreshTokenExpiry()
            );

            // 리프레시 토큰을 Redis에 저장
            authTokenService.addRefreshToken(String.valueOf(memberDTO.getUserId()), refreshToken);

            // 쿠키에 토큰 설정
            CookieUtil.addCookie(response, "Authorization", accessToken,
                (int) (authSecurityProperties.getToken().getAccessTokenExpiry() / 1000));
            CookieUtil.addCookie(response, "refresh", refreshToken,
                (int) (authSecurityProperties.getToken().getRefreshTokenExpiry() / 1000));

            log.info("JWT 토큰 발급 완료: 사용자 = {}", memberDTO.getUsername());

            // 성공 페이지로 리다이렉트
            getRedirectStrategy().sendRedirect(request, response,
                authSecurityProperties.getOauth2().getSuccessRedirectUrl());

        } catch (Exception e) {
            log.error("OAuth2 로그인 성공 처리 중 오류 발생: {}", e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "토큰 발급 중 오류가 발생했습니다.");
        }
    }
}