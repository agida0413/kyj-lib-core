package com.kyj.core.exception.handler;

import com.kyj.core.api.ApiResponse;
import com.kyj.core.api.CmErrCode;
import com.kyj.core.exception.custom.KyjBaseException;
import com.kyj.core.exception.custom.KyjBatException;
import com.kyj.core.exception.custom.KyjSysException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * 2025-05-29
 * @author 김용준
 * Restful Api에서 사용하는 글로벌 익셉션 핸들러 (ApiResponse 사용)
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * base 익셉션 핸들러
     * @param ex
     * @return ResponseEntity
     */
    @ExceptionHandler(KyjBaseException.class)
    public ResponseEntity<ApiResponse<?>> handleBaseException(KyjBaseException ex){

       ApiResponse<Void> apiResponse = ErrHelper.determineErrRes(ex);
        return ResponseEntity
                .status(HttpStatus.valueOf(apiResponse.getStatus()))
                .body(apiResponse);
    }


    /**
     * 유효성 익셉션 핸들러
     * @Valid @RequestBody 바인딩 후 유효성/바인딩 오류
     * @param ex
     * @return ResponseEntity
     */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {

        // 타입 오류???
        List<String> list = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {

            if (error.isBindingFailure()) {
                list.add("타입변환에 실패하였습니다.");
            } else {
                list.add(error.getDefaultMessage());
            }
        });
        String msg = list.isEmpty() ? "잘못된 요청입니다." : list.get(0);
        msg = list.get(0);
        log.error("MethodNotArgException 발생");
        ApiResponse<Void> apiResponse = ApiResponse.error()
                .msg(msg)
                .status(HttpStatus.BAD_REQUEST.value())
                .code(CmErrCode.CM002.getCode())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(apiResponse);
    }
    /**
     * 유효성 익셉션 핸들러
     * 전체 예외 (최상위)
     * @param ex
     * @return ResponseEntity
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(Exception ex) {

        ex = new KyjSysException(CmErrCode.CM002);
        ApiResponse<Void> apiResponse = ErrHelper.determineErrRes(ex);
        return ResponseEntity
                .status(HttpStatus.valueOf(apiResponse.getStatus()))
                .body(apiResponse);
    }



    /**
     * 배치 익셉션 핸들러
     * @param ex
     * @return ResponseEntity
     */
    @ExceptionHandler(KyjBatException.class)
    @Deprecated
    public void handleBatException(KyjBatException ex){
        String msg = ErrHelper.determineErrMsg(ex);

    }
}
