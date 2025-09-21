package com.kyj.core.security.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 2025-09-21
 * @author 김용준
 * 인증이 필요 없는 퍼블릭 엔드포인트임을 명시하는 어노테이션
 * 기본 정책은 인증 필요이며, 이 어노테이션을 붙인 경우에만 인증 없이 접근 가능
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PublicEndpoint {

    /**
     * 퍼블릭 엔드포인트에 대한 설명
     * @return 설명
     */
    String value() default "";
}