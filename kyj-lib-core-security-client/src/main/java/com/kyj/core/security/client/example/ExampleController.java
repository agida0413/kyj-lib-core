//package com.kyj.core.security.client.example;
//
//import com.kyj.core.security.client.annotation.PublicEndpoint;
//import com.kyj.core.security.client.annotation.RequireRole;
//import com.kyj.core.security.client.dto.SecurityUser;
//import com.kyj.core.security.client.service.TokenBlacklistService;
//import com.kyj.core.security.client.util.SecurityContext;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
///**
// * Security Client 사용 예제
// */
//@RestController
//@RequestMapping("/api/security-example")
//@RequiredArgsConstructor
//public class ExampleController {
//
//    private final TokenBlacklistService blacklistService;
//
//    /**
//     * 퍼블릭 엔드포인트 - 인증 불필요
//     */
//    @PublicEndpoint
//    @GetMapping("/public")
//    public String publicEndpoint() {
//        return "누구나 접근 가능한 퍼블릭 엔드포인트입니다.";
//    }
//
//    /**
//     * 인증 필요 엔드포인트 (디폴트)
//     */
//    @GetMapping("/user-info")
//    public SecurityUser getUserInfo() {
//        return SecurityContext.getUser();
//    }
//
//    /**
//     * 관리자 권한 필요
//     */
//    @RequireRole("ADMIN")
//    @GetMapping("/admin-only")
//    public String adminOnly() {
//        return "관리자만 접근 가능: " + SecurityContext.getUsername();
//    }
//
//    /**
//     * 코드에서 직접 권한 체크
//     */
//    @GetMapping("/check-permission")
//    public String checkPermission() {
//        if (!SecurityContext.isAuthenticated()) {
//            return "익명 사용자입니다.";
//        }
//
//        if (SecurityContext.isAdmin()) {
//            return "관리자 사용자: " + SecurityContext.getUsername();
//        }
//
//        return "일반 사용자: " + SecurityContext.getUsername();
//    }
//
//    /**
//     * 현재 사용자 정보 조회
//     */
//    @GetMapping("/current-user")
//    public SecurityUser getCurrentUser() {
//        return SecurityContext.getUser();
//    }
//
//
//
//
//}