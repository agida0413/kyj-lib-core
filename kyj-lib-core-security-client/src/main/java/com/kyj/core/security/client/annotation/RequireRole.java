package com.kyj.core.security.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 특정 권한이 필요한 엔드포인트 표시
 *
 * 사용법:
 * @RequireRole("ADMIN")
 * @PostMapping("/admin/users")
 * public String manageUsers() { ... }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    /**
     * 필요한 권한
     */
    String value();
}