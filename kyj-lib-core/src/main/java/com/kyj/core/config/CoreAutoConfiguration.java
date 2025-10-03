package com.kyj.core.config;

import com.kyj.core.exception.handler.BaseExceptionPostProcess;
import com.kyj.core.exception.handler.ExceptionPostProcess;
import com.kyj.core.mail.BaseJavaMailSender;
import com.kyj.core.mail.MailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * 2025-09-30
 * @author 김용준
 * Core의 인터페이스들의 기본빈들을 등록해준다.
 */
@AutoConfiguration
@RequiredArgsConstructor
public class CoreAutoConfiguration {

    private final JavaMailSender javaMailSender;

    @Bean
    @ConditionalOnMissingBean(ExceptionPostProcess.class)
    public ExceptionPostProcess exceptionPostProcess(){
        return new BaseExceptionPostProcess();
    }

    /**
     * 메일 서비스
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(MailSender.class)
    public MailSender mailSender(){
        return new BaseJavaMailSender(javaMailSender);
    }

}
