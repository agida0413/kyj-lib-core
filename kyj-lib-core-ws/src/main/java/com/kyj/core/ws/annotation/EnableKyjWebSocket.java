package com.kyj.core.ws.annotation;

import com.kyj.core.ws.config.WebSocketAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 2025-09-24
 * @author 김용준
 * KYJ 웹소켓 모듈을 활성화하는 어노테이션
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(WebSocketAutoConfiguration.class)
public @interface EnableKyjWebSocket {
}