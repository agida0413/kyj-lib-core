package com.kyj.core.batch.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;

import java.time.LocalDateTime;

/**
 * 2025-09-24
 * @author 김용준
 * 배치 실행 결과 DTO
 */
@Getter
@Builder
public class BatchExecutionResult {

    /**
     * 실행 ID
     */
    private final Long executionId;

    /**
     * 잡 이름
     */
    private final String jobName;

    /**
     * 배치 상태
     */
    private final BatchStatus batchStatus;

    /**
     * 종료 상태
     */
    private final ExitStatus exitStatus;

    /**
     * 시작 시간
     */
    private final LocalDateTime startTime;

    /**
     * 종료 시간
     */
    private final LocalDateTime endTime;

    /**
     * 처리된 아이템 수
     */
    private final Long processedCount;

    /**
     * 실패한 아이템 수
     */
    private final Long failureCount;

    /**
     * 소요 시간 (밀리초)
     */
    private final Long duration;

    /**
     * 에러 메시지
     */
    private final String errorMessage;

    /**
     * 성공 여부
     */
    public boolean isSuccessful() {
        return batchStatus == BatchStatus.COMPLETED && exitStatus.getExitCode().equals(ExitStatus.COMPLETED.getExitCode());
    }

    /**
     * 실행 중 여부
     */
    public boolean isRunning() {
        return batchStatus == BatchStatus.STARTED;
    }

    /**
     * 실패 여부
     */
    public boolean isFailed() {
        return batchStatus == BatchStatus.FAILED;
    }
}