package com.kyj.fmk.core.exception.handler;

import com.kyj.fmk.core.cst.dto.ResApiErrDTO;
import com.kyj.fmk.core.exception.custom.KyjAsncException;
import com.kyj.fmk.core.exception.custom.KyjBaseException;
import com.kyj.fmk.core.exception.custom.KyjBatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;

/**
 * 2025-05-29
 * @author 김용준
 * Restful Api에서 사용하는 글로벌 익셉션 핸들러
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * base 익셉션 핸들러
     * @param ex
     * @return ResponseEntity
     */
    @ExceptionHandler(KyjBaseException.class)
    public ResponseEntity<ResApiErrDTO<?>> handleBaseException(KyjBaseException ex){

       ResApiErrDTO<Void> resApiErrDTO = ErrHelper.determineErrRes(ex);
        return ResponseEntity
                .status(HttpStatus.valueOf(resApiErrDTO.getStatus()))
                .body(resApiErrDTO);
    }
    /**
     * 배치 익셉션 핸들러
     * @param ex
     * @return ResponseEntity
     */
    @ExceptionHandler(KyjBatException.class)
    public void handleBatException(KyjBatException ex){
      String msg = ErrHelper.determineErrMsg(ex);

    }

    /**
     * 유효성 익셉션 핸들러
     * @Valid @RequestBody 바인딩 후 유효성/바인딩 오류
     * @param ex
     * @return ResponseEntity
     */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResApiErrDTO<?>> handleValidationException(MethodArgumentNotValidException ex) {
        // 타입 오류???
        List<String> list = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
           list.add(error.getDefaultMessage());
        });
        String msg = list.get(0);

        ResApiErrDTO<Void> resApiErrDTO = new ResApiErrDTO<>(msg,HttpStatus.BAD_REQUEST.value());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(resApiErrDTO);
    }



    /**
     * 유효성 익셉션 핸들러
     * @RequestParam, @PathVariable 변환 실패
     * @param ex
     * @return ResponseEntity
     */

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public void handleValidationException(MethodArgumentTypeMismatchException ex) {

        System.out.println("ex = " + ex);
    }


    /**
     * 유효성 익셉션 핸들러
     * JSON 자체 파싱 실패
     * @param ex
     * @return ResponseEntity
     */

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public void handleValidationException(HttpMessageNotReadableException ex) {
        System.out.println("ex = " + ex);

//
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST.value())
//                .body(resApiErrDTO);
    }






}
