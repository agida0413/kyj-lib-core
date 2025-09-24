package com.kyj.core.batch.service;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;

import java.util.Map;

/**
 * 2025-09-24
 * @author 김용준
 * 배치 작업 서비스 인터페이스
 */
public interface BatchJobService {

    /**
     * 배치 잡 실행
     */
    JobExecution runJob(String jobName, JobParameters jobParameters);

    /**
     * 배치 잡 실행 (파라미터 맵으로)
     */
    JobExecution runJob(String jobName, Map<String, Object> parameters);

    /**
     * 배치 잡 실행 상태 조회
     */
    boolean isJobRunning(String jobName);

    /**
     * 배치 잡 실행 기록 조회
     */
    JobExecution getLastJobExecution(String jobName);

    /**
     * 배치 잡 중지
     */
    boolean stopJob(Long executionId);
}