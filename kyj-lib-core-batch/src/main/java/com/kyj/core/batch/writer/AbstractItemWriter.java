package com.kyj.core.batch.writer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * 2025-09-24
 * @author 김용준
 * 추상 아이템 라이터 클래스
 * 공통적인 쓰기 로직을 제공하고 하위 클래스에서 구체적인 출력 처리를 구현
 */
@Slf4j
public abstract class AbstractItemWriter<T> implements ItemWriter<T> {

    @Override
    public void write(Chunk<? extends T> chunk) throws Exception {
        if (chunk == null || chunk.isEmpty()) {
            return;
        }

        List<? extends T> items = chunk.getItems();
        log.debug("아이템 쓰기 시작: {} 개", items.size());

        try {
            // 전처리
            preWrite(items);

            // 메인 쓰기 로직
            doWrite(items);

            // 후처리
            postWrite(items);

            log.debug("아이템 쓰기 완료: {} 개", items.size());
        } catch (Exception e) {
            log.error("아이템 쓰기 중 오류 발생: {}", e.getMessage(), e);
            handleError(items, e);
            throw e;
        }
    }

    /**
     * 전처리 메서드
     * 필요시 하위 클래스에서 오버라이드
     */
    protected void preWrite(List<? extends T> items) {
        // 기본 구현은 비어있음
    }

    /**
     * 메인 쓰기 로직 추상 메서드
     * 하위 클래스에서 구체적인 출력 로직을 구현
     */
    protected abstract void doWrite(List<? extends T> items) throws Exception;

    /**
     * 후처리 메서드
     * 필요시 하위 클래스에서 오버라이드
     */
    protected void postWrite(List<? extends T> items) {
        // 기본 구현은 비어있음
    }

    /**
     * 에러 처리 메서드
     * 하위 클래스에서 오버라이드하여 에러 처리 로직을 구현
     */
    protected void handleError(List<? extends T> items, Exception e) {
        log.error("아이템 쓰기 실패 처리: {} 개", items.size());
    }
}