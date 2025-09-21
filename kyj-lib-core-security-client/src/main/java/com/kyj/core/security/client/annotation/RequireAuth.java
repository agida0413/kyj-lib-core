package com.kyj.core.security.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 2025-09-21
 * @author 김용준
 * 인증이 필요한 컨트롤러나 메서드에 사용하는 어노테이션
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAuth {

    /**
     * 필요한 권한들
     * @return 권한 배열
     */
    String[] roles() default {};

    /**
     * 모든 권한이 필요한지, 하나만 있으면 되는지
     * @return true면 모든 권한 필요, false면 하나라도 있으면 됨
     */
    boolean requireAll() default false;
}