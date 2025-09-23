package com.kyj.core.api;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 2025-05-29
 * @author 김용준
 * Restful Api에서 사용하는 응답 wrapper BASE 클래스
 *
 * @deprecated ApiResponse 클래스 사용
 *
 */
@Deprecated
@Getter
@Setter
public abstract class BaseResponse<T> {
    //상태코드 200,400 ..
    private int status;
    //응답메시지 실패시 에러메시지 등
    private String msg;
    //응답 데이터
    private T data;
    //response 시간 DateFormat 해야함
    private final String resTime = this.formattedTime();
    //응답코드
    private String code;

    /**
     * 응답시간 format method
     */
    private String formattedTime(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(dateTimeFormatter);
    }
}
