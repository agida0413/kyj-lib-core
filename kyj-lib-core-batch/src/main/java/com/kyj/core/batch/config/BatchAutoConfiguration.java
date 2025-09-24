package com.kyj.core.batch.config;

import com.kyj.core.batch.service.BatchJobService;
import com.kyj.core.batch.service.BatchJobServiceImpl;
import com.kyj.core.batch.util.BatchJobBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 2025-09-24
 * @author 김용준
 * KYJ Batch 자동 설정
 */
@Slf4j
@AutoConfiguration
@EnableBatchProcessing
@EnableConfigurationProperties(BatchProperties.class)
@ConditionalOnProperty(prefix = "kyj.batch", name = "enabled", havingValue = "true", matchIfMissing = true)
public class BatchAutoConfiguration {

    /**
     * 배치 작업 서비스 빈 등록
     */
    @Bean
    @ConditionalOnMissingBean
    public BatchJobService batchJobService() {
        log.info("KYJ Batch 모듈 - BatchJobService 등록");
        return new BatchJobServiceImpl();
    }

    /**
     * 배치 잡 빌더 빈 등록
     */
    @Bean
    @ConditionalOnMissingBean
    public BatchJobBuilder batchJobBuilder(BatchProperties batchProperties) {
        log.info("KYJ Batch 모듈 - BatchJobBuilder 등록");
        return new BatchJobBuilder(batchProperties);
    }
}