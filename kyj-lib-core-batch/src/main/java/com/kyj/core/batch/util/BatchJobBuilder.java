package com.kyj.core.batch.util;

import com.kyj.core.batch.config.BatchProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 2025-09-24
 * @author 김용준
 * 배치 잡 빌더 유틸리티 클래스
 * 간편하게 배치 잡을 생성할 수 있도록 도와주는 빌더
 */
@RequiredArgsConstructor
public class BatchJobBuilder {

    private final BatchProperties batchProperties;

    /**
     * 간단한 청크 기반 스텝 빌더
     */
    public <I, O> StepBuilderHelper<I, O> createChunkStep(String stepName,
                                                          JobRepository jobRepository,
                                                          PlatformTransactionManager transactionManager) {
        return new StepBuilderHelper<>(stepName, jobRepository, transactionManager,
                                       batchProperties.getDefaultChunkSize());
    }

    /**
     * 잡 빌더
     */
    public JobBuilderHelper createJob(String jobName, JobRepository jobRepository) {
        return new JobBuilderHelper(jobName, jobRepository);
    }

    /**
     * 스텝 빌더 헬퍼 클래스
     */
    @RequiredArgsConstructor
    public static class StepBuilderHelper<I, O> {
        private final String stepName;
        private final JobRepository jobRepository;
        private final PlatformTransactionManager transactionManager;
        private final int chunkSize;

        public Step build(ItemReader<I> reader,
                         ItemProcessor<I, O> processor,
                         ItemWriter<O> writer) {
            return new StepBuilder(stepName, jobRepository)
                    .<I, O>chunk(chunkSize, transactionManager)
                    .reader(reader)
                    .processor(processor)
                    .writer(writer)
                    .build();
        }

        public Step buildWithoutProcessor(ItemReader<I> reader,
                                        ItemWriter<I> writer) {
            return new StepBuilder(stepName, jobRepository)
                    .<I, I>chunk(chunkSize, transactionManager)
                    .reader(reader)
                    .writer(writer)
                    .build();
        }
    }

    /**
     * 잡 빌더 헬퍼 클래스
     */
    @RequiredArgsConstructor
    public static class JobBuilderHelper {
        private final String jobName;
        private final JobRepository jobRepository;

        public Job build(Step... steps) {
            var jobBuilder = new JobBuilder(jobName, jobRepository);

            if (steps.length > 0) {
                var flowBuilder = jobBuilder.start(steps[0]);
                for (int i = 1; i < steps.length; i++) {
                    flowBuilder = flowBuilder.next(steps[i]);
                }
                return flowBuilder.build();
            }

            // 빈 Job이 필요한 경우 기본 Step을 생성
            throw new IllegalArgumentException("최소 하나의 Step이 필요합니다.");
        }

        public Job buildFlow(Step step) {
            return new JobBuilder(jobName, jobRepository)
                    .start(step)
                    .build();
        }
    }
}