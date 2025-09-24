package com.kyj.core.batch.reader;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.Iterator;
import java.util.List;

/**
 * 2025-09-24
 * @author 김용준
 * 추상 아이템 리더 클래스
 * 공통적인 읽기 로직을 제공하고 하위 클래스에서 구체적인 데이터 소스 처리를 구현
 */
public abstract class AbstractItemReader<T> implements ItemReader<T> {

    protected Iterator<T> iterator;
    protected boolean initialized = false;

    @Override
    public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (!initialized) {
            initialize();
            initialized = true;
        }

        if (iterator != null && iterator.hasNext()) {
            return iterator.next();
        }

        return null; // 더 이상 읽을 데이터가 없음
    }

    /**
     * 초기화 메서드
     * 하위 클래스에서 데이터 소스를 초기화하고 iterator를 설정
     */
    protected void initialize() {
        List<T> data = loadData();
        if (data != null) {
            this.iterator = data.iterator();
        }
    }

    /**
     * 데이터 로드 추상 메서드
     * 하위 클래스에서 구체적인 데이터 소스에서 데이터를 로드하는 로직을 구현
     */
    protected abstract List<T> loadData();

    /**
     * 리더 리셋 메서드
     */
    public void reset() {
        this.initialized = false;
        this.iterator = null;
    }
}