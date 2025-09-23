package com.kyj.core.api;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 통합 API 응답 클래스
 * 성공/실패 빌더로 분리하여 가독성 향상
 *
 * @author 김용준
 * @param <T> 응답 데이터 타입
 */
@Getter
public class ApiResponse<T> {

    private final int status;
    private final String msg;
    private final T data;
    private final String code;
    private final String resTime;

    // private 생성자
    private ApiResponse(int status, String msg, T data, String code) {
        this.status = status;
        this.msg = msg;
        this.data = data;
        this.code = code;
        this.resTime = formatTime();
    }

    // =============== 성공 응답 시작점 ===============

    /**
     * 성공 응답 빌더 시작
     * 사용법: ApiResponse.success().data(data).build()
     */
    public static SuccessBuilder success() {
        return new SuccessBuilder();
    }

    // =============== 실패 응답 시작점 ===============

    /**
     * 실패 응답 빌더
     *
     */
    public static ErrorBuilder error() {
        return new ErrorBuilder();
    }

    // =============== 성공 빌더 (data만 설정 가능) ===============

    public static class SuccessBuilder {
        private Object data;
        private String code;
        /**
         * 성공 응답 데이터 설정
         */
        public <T> SuccessBuilder data(T data) {
            this.data = data;
            return this;
        }

        public <T> SuccessBuilder code(String code) {
            this.code = code;
            return this;
        }

        /**
         * 성공 응답 생성 (기본값: 200, "Success Request!")
         */
        public <T> ApiResponse<T> build() {
            return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Success Request!",
                (T) data,
                    code
            );
        }
    }

    // =============== 실패 빌더 (모든 필드 설정 가능) ===============

    public static class ErrorBuilder {
        private int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        private String msg = CmErrCode.CM002.getMsg();
        private Object data;
        private String code = CmErrCode.CM002.getCode();

        /**
         * 에러 메시지 설정
         */
        public ErrorBuilder msg(String msg) {
            this.msg = msg;
            return this;
        }

        /**
         * HTTP 상태 코드 설정
         */
        public ErrorBuilder status(int status) {
            this.status = status;
            return this;
        }

        /**
         * 에러 데이터 설정
         */
        public <T> ErrorBuilder data(T data) {
            this.data = data;
            return this;
        }

        /**
         * 에러 코드 설정
         */
        public ErrorBuilder code(String code) {
            this.code = code;
            return this;
        }


        /**
         * 실패 응답 생성
         */
        public <T> ApiResponse<T> build() {
            return new ApiResponse<>(status, msg, (T) data, code);
        }
    }

    // =============== 편의 메서드 (기존 호환성) ===============

    /**
     * 간단한 성공 응답 (데이터 없음)
     */
    public static <T> ApiResponse<T> ok() {
        return success().build();
    }

    /**
     * 간단한 성공 응답 (데이터 포함)
     */
    public static <T> ApiResponse<T> ok(T data) {
        return success().data(data).build();
    }


    // =============== 유틸 ===============
    /**
     * 성공 응답인지 확인
     */
    public boolean isSuccess() {
        return status >= 200 && status < 300;
    }

    /**
     * 현재 시간 포맷팅
     */
    private static String formatTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }
}
