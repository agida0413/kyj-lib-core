package com.kyj.core.batch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.Date;

/**
 * 2025-09-24
 * @author 김용준
 * 배치 작업 서비스 구현체
 */
@Slf4j
public class BatchJobServiceImpl implements BatchJobService {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobOperator jobOperator;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public JobExecution runJob(String jobName, JobParameters jobParameters) {
        try {
            Job job = applicationContext.getBean(jobName, Job.class);

            log.info("배치 잡 실행 시작: {}", jobName);
            JobExecution jobExecution = jobLauncher.run(job, jobParameters);
            log.info("배치 잡 실행 완료: {}, 상태: {}", jobName, jobExecution.getStatus());

            return jobExecution;
        } catch (Exception e) {
            log.error("배치 잡 실행 실패: {} - {}", jobName, e.getMessage(), e);
            throw new RuntimeException("배치 잡 실행 실패: " + jobName, e);
        }
    }

    @Override
    public JobExecution runJob(String jobName, Map<String, Object> parameters) {
        JobParametersBuilder builder = new JobParametersBuilder();

        if (parameters != null) {
            parameters.forEach((key, value) -> {
                if (value instanceof String) {
                    builder.addString(key, (String) value);
                } else if (value instanceof Long) {
                    builder.addLong(key, (Long) value);
                } else if (value instanceof Double) {
                    builder.addDouble(key, (Double) value);
                } else if (value instanceof Date) {
                    builder.addDate(key, (Date) value);
                } else {
                    builder.addString(key, value.toString());
                }
            });
        }

        // 실행 시간을 파라미터로 추가하여 중복 실행 방지
        builder.addLong("executionTime", System.currentTimeMillis());

        return runJob(jobName, builder.toJobParameters());
    }

    @Override
    public boolean isJobRunning(String jobName) {
        try {
            return jobExplorer.findRunningJobExecutions(jobName).size() > 0;
        } catch (Exception e) {
            log.error("배치 잡 실행 상태 조회 실패: {} - {}", jobName, e.getMessage());
            return false;
        }
    }

    @Override
    public JobExecution getLastJobExecution(String jobName) {
        try {
            JobInstance lastJobInstance = jobExplorer.getLastJobInstance(jobName);
            if (lastJobInstance != null) {
                return jobExplorer.getLastJobExecution(lastJobInstance);
            }
            return null;
        } catch (Exception e) {
            log.error("배치 잡 실행 기록 조회 실패: {} - {}", jobName, e.getMessage());
            return null;
        }
    }

    @Override
    public boolean stopJob(Long executionId) {
        try {
            return jobOperator.stop(executionId);
        } catch (Exception e) {
            log.error("배치 잡 중지 실패: {} - {}", executionId, e.getMessage());
            return false;
        }
    }
}