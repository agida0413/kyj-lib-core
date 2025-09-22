package com.kyj.core.security.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 인증이 필요없는 퍼블릭 엔드포인트 표시
 *
 * 사용법:
 * @PublicEndpoint
 * @GetMapping("/public/info")
 * public String getPublicInfo() { ... }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PublicEndpoint {
}