package com.kyj.core.batch.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

/**
 * 2025-09-24
 * @author 김용준
 * 추상 아이템 프로세서 클래스
 * 공통적인 처리 로직을 제공하고 하위 클래스에서 구체적인 비즈니스 로직을 구현
 */
@Slf4j
public abstract class AbstractItemProcessor<I, O> implements ItemProcessor<I, O> {

    @Override
    public O process(I item) throws Exception {
        if (item == null) {
            return null;
        }

        try {
            // 전처리
            preProcess(item);

            // 메인 처리 로직
            O result = doProcess(item);

            // 후처리
            postProcess(item, result);

            return result;
        } catch (Exception e) {
            log.error("아이템 처리 중 오류 발생: {}", e.getMessage(), e);
            return handleError(item, e);
        }
    }

    /**
     * 전처리 메서드
     * 필요시 하위 클래스에서 오버라이드
     */
    protected void preProcess(I item) {
        // 기본 구현은 비어있음
    }

    /**
     * 메인 처리 로직 추상 메서드
     * 하위 클래스에서 구체적인 비즈니스 로직을 구현
     */
    protected abstract O doProcess(I item) throws Exception;

    /**
     * 후처리 메서드
     * 필요시 하위 클래스에서 오버라이드
     */
    protected void postProcess(I item, O result) {
        // 기본 구현은 비어있음
    }

    /**
     * 에러 처리 메서드
     * 하위 클래스에서 오버라이드하여 에러 처리 로직을 구현
     * 기본적으로는 null을 반환하여 해당 아이템을 스킵
     */
    protected O handleError(I item, Exception e) {
        log.warn("아이템 처리 실패로 스킵: item={}", item);
        return null;
    }
}