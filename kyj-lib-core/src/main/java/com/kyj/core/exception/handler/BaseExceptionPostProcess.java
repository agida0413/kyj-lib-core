package com.kyj.core.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Component;

/**
 * 2025-05-29
 * @author 김용준
 * Restful Api에서 사용하는 익셉션핸들러에 대한 후처리 Aop에서 사용하는 서비스 구현체
 */
@Component
@Slf4j
public class BaseExceptionPostProcess implements ExceptionPostProcess{

    @Override
    public void proceed(JoinPoint joinPoint) {
            log.info("기본 예외 발생 후처리");
    }
}
