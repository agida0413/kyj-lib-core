package com.kyj.core.batch.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 2025-09-24
 * @author 김용준
 * KYJ Batch 모듈 설정 properties
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "kyj.batch")
public class BatchProperties {

    /**
     * 배치 모듈 활성화 여부
     */
    private boolean enabled = true;

    /**
     * 기본 청크 사이즈
     */
    private int defaultChunkSize = 1000;

    /**
     * 잡 파라미터 설정
     */
    @NestedConfigurationProperty
    private JobParameters jobParameters = new JobParameters();

    /**
     * 스케줄링 설정
     */
    @NestedConfigurationProperty
    private Scheduling scheduling = new Scheduling();

    /**
     * 알림 설정
     */
    @NestedConfigurationProperty
    private Notification notification = new Notification();

    @Getter
    @Setter
    public static class JobParameters {
        /**
         * 전역 잡 파라미터
         */
        private Map<String, Object> global = new HashMap<>();

        /**
         * 잡별 파라미터
         */
        private Map<String, Map<String, Object>> jobs = new HashMap<>();
    }

    @Getter
    @Setter
    public static class Scheduling {
        /**
         * 스케줄링 활성화 여부
         */
        private boolean enabled = false;

        /**
         * 잡 스케줄 설정
         */
        private Map<String, String> jobs = new HashMap<>();
    }

    @Getter
    @Setter
    public static class Notification {
        /**
         * 알림 활성화 여부
         */
        private boolean enabled = false;

        /**
         * 성공 시 알림 여부
         */
        private boolean notifyOnSuccess = false;

        /**
         * 실패 시 알림 여부
         */
        private boolean notifyOnFailure = true;

        /**
         * 알림 대상
         */
        private List<String> recipients = new ArrayList<>();
    }
}