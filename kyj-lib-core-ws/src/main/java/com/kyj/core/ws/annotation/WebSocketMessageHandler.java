package com.kyj.core.ws.annotation;

import java.lang.annotation.*;

/**
 * 2025-09-24
 * @author 김용준
 * 웹소켓 메시지 핸들러를 표시하는 어노테이션
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebSocketMessageHandler {

    /**
     * 처리할 메시지 경로
     */
    String value() default "";

    /**
     * 메시지 타입
     */
    String messageType() default "default";

    /**
     * 인증 필요 여부
     */
    boolean requireAuth() default false;

    /**
     * 세션별 처리 여부
     */
    boolean perSession() default true;
}