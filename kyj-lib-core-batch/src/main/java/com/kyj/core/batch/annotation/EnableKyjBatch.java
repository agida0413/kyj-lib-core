package com.kyj.core.batch.annotation;

import com.kyj.core.batch.config.BatchAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 2025-09-24
 * @author 김용준
 * KYJ 배치 모듈을 활성화하는 어노테이션
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(BatchAutoConfiguration.class)
public @interface EnableKyjBatch {
}