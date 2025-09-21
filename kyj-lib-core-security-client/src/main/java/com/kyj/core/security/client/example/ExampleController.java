package com.kyj.core.security.client.example;

import com.kyj.core.security.client.annotation.PublicEndpoint;
import com.kyj.core.security.client.annotation.RequireAuth;
import com.kyj.core.security.client.annotation.RequireRole;
import com.kyj.core.security.client.util.SecurityHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 2025-09-21
 * @author 김용준
 * Security Client 모듈 사용 예시 컨트롤러
 *
 * 이 컨트롤러는 예시용으로만 제공되며, 실제 프로젝트에서는 삭제하거나 참고용으로 사용하세요.
 * 프로덕션 환경에서는 @RestController 주석을 해제하여 사용하지 않도록 합니다.
 */
// @RestController  // 프로덕션에서는 주석 처리됨
@RequestMapping("/api/security/example")
public class ExampleController {

    /**
     * 인증이 필요 없는 공개 엔드포인트 (새로운 정책: @PublicEndpoint 필요)
     */
    @PublicEndpoint("누구나 접근 가능한 공개 API")
    @GetMapping("/public")
    public Map<String, Object> publicEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "누구나 접근할 수 있는 공개 엔드포인트입니다.");
        response.put("authenticated", SecurityHolder.isAuthenticated());

        if (SecurityHolder.isAuthenticated()) {
            response.put("username", SecurityHolder.getUsername());
            response.put("userId", SecurityHolder.getUserId());
        }

        return response;
    }

    /**
     * 인증이 필요한 엔드포인트 (새로운 정책: 기본값이므로 어노테이션 불필요)
     */
    @GetMapping("/protected")
    public Map<String, Object> protectedEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "인증된 사용자만 접근할 수 있습니다.");
        response.put("username", SecurityHolder.getUsername());
        response.put("userId", SecurityHolder.getUserId());
        response.put("email", SecurityHolder.getEmail());
        response.put("roles", SecurityHolder.getRoles());

        return response;
    }

    /**
     * 특정 권한이 필요한 엔드포인트
     */
    @RequireRole({"ROLE_ADMIN"})
    @GetMapping("/admin-only")
    public Map<String, Object> adminOnlyEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "관리자만 접근할 수 있습니다.");
        response.put("username", SecurityHolder.getUsername());
        response.put("userId", SecurityHolder.getUserId());

        return response;
    }

    /**
     * 여러 권한 중 하나라도 있으면 접근 가능한 엔드포인트
     */
    @RequireRole(value = {"ROLE_ADMIN", "ROLE_MANAGER"}, requireAll = false)
    @GetMapping("/admin-or-manager")
    public Map<String, Object> adminOrManagerEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "관리자 또는 매니저만 접근할 수 있습니다.");
        response.put("username", SecurityHolder.getUsername());
        response.put("hasAdminRole", SecurityHolder.hasRole("ROLE_ADMIN"));
        response.put("hasManagerRole", SecurityHolder.hasRole("ROLE_MANAGER"));

        return response;
    }

    /**
     * 정적 메서드를 사용한 사용자 정보 조회 예시 (기본 인증 필요)
     */
    @GetMapping("/user-info")
    public Map<String, Object> getUserInfo() {
        // SecurityHolder를 사용하여 정적으로 사용자 정보 조회
        Map<String, Object> userInfo = new HashMap<>();

        userInfo.put("authenticated", SecurityHolder.isAuthenticated());
        userInfo.put("username", SecurityHolder.getUsername());
        userInfo.put("userId", SecurityHolder.getUserId());
        userInfo.put("email", SecurityHolder.getEmail());
        userInfo.put("roles", SecurityHolder.getRoles());

        // 권한 확인 예시
        userInfo.put("isAdmin", SecurityHolder.hasRole("ROLE_ADMIN"));
        userInfo.put("isUser", SecurityHolder.hasRole("ROLE_USER"));

        return userInfo;
    }

    /**
     * 수동으로 인증 확인하는 예시 (퍼블릭이지만 인증 상태 확인)
     */
    @PublicEndpoint("인증 상태 수동 확인")
    @GetMapping("/manual-auth-check")
    public Map<String, Object> manualAuthCheck() {
        Map<String, Object> response = new HashMap<>();

        try {
            // 인증 필수 확인
            SecurityHolder.requireAuthenticated();
            response.put("message", "인증된 사용자입니다.");
            response.put("username", SecurityHolder.getUsername());

        } catch (SecurityException e) {
            response.put("error", e.getMessage());
            response.put("authenticated", false);
        }

        return response;
    }

    /**
     * 수동으로 권한 확인하는 예시 (퍼블릭이지만 권한 확인)
     */
    @PublicEndpoint("권한 상태 수동 확인")
    @GetMapping("/manual-role-check")
    public Map<String, Object> manualRoleCheck() {
        Map<String, Object> response = new HashMap<>();

        try {
            // 특정 권한 필수 확인
            SecurityHolder.requireRole("ROLE_ADMIN");
            response.put("message", "관리자 권한이 있습니다.");
            response.put("username", SecurityHolder.getUsername());

        } catch (SecurityException e) {
            response.put("error", e.getMessage());
            response.put("hasAdminRole", SecurityHolder.hasRole("ROLE_ADMIN"));
        }

        return response;
    }
}