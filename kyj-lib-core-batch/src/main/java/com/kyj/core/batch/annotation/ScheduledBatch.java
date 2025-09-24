package com.kyj.core.batch.annotation;

import java.lang.annotation.*;

/**
 * 2025-09-24
 * @author 김용준
 * 스케줄링된 배치 작업을 표시하는 어노테이션
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ScheduledBatch {

    /**
     * 배치 잡 이름
     */
    String jobName();

    /**
     * Cron 표현식
     */
    String cron() default "";

    /**
     * 고정 지연 시간 (밀리초)
     */
    long fixedDelay() default -1;

    /**
     * 고정 간격 (밀리초)
     */
    long fixedRate() default -1;

    /**
     * 초기 지연 시간 (밀리초)
     */
    long initialDelay() default 0;

    /**
     * 스케줄링 활성화 여부
     */
    boolean enabled() default true;
}