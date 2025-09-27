package com.kyj.core.security.client.controller;

import com.kyj.core.security.client.annotation.PublicEndpoint;
import com.kyj.core.security.client.util.SecurityResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 인증확인 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/auth/client")
public class VerifyController {
    
    @PostMapping("/verify")
    public void verifyAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // JWT 필터에서 이미 인증이 완료된 상태라면 이 엔드포인트에 도달할 수 있음
        SecurityResponseUtil.writeSuccessResponse(response, "인증된 사용자입니다.");
    }
}
