package com.kyj.core.security.auth.controller;

import com.kyj.core.security.auth.service.AuthTokenService;
import com.kyj.core.security.client.annotation.PublicEndpoint;
import com.kyj.core.security.client.util.SecurityResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 2025-09-21
 * @author 김용준
 * 인증 토큰 관리 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthTokenController {

    private final AuthTokenService authTokenService;

    /**
     * 토큰 재발급
     */
    @PostMapping("/reissue")
    @PublicEndpoint
    public void reissueTokens(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean success = authTokenService.reissueTokens(request, response);

        if (success) {
            SecurityResponseUtil.writeSuccessResponse(response);
        }
        // 실패 시에는 AuthTokenService에서 이미 에러 응답을 작성함
    }

    /**
     * 로그아웃 (토큰 무효화)
     */
    @PostMapping("/logout")
    @PublicEndpoint
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean success = authTokenService.logout(request, response);

        if (success) {
            SecurityResponseUtil.writeSuccessResponse(response);
        }
        // 실패 시에는 AuthTokenService에서 이미 에러 응답을 작성함
    }

    /**
     * 인증 상태 확인
     */
//    @PostMapping("/verify")
//    @PublicEndpoint
//    public void verifyAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        // JWT 필터에서 이미 인증이 완료된 상태라면 이 엔드포인트에 도달할 수 있음
//        SecurityResponseUtil.writeSuccessResponse(response, "인증된 사용자입니다.");
//    }


}