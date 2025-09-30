package com.kyj.core.config;

import com.kyj.core.exception.handler.BaseExceptionPostProcess;
import com.kyj.core.exception.handler.ExceptionPostProcess;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
/**
 * 2025-09-30
 * @author 김용준
 * Core의 인터페이스들의 기본빈들을 등록해준다.
 */
@AutoConfiguration
public class CoreAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ExceptionPostProcess.class)
    public ExceptionPostProcess exceptionPostProcess(){
        return new BaseExceptionPostProcess();
    }
}
