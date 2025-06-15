package com.kyj.fmk.core.async;

import com.kyj.fmk.core.cst.enm.ApiErrCode;
import com.kyj.fmk.core.exception.custom.KyjAsncException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
/**
 *  * 2025-05-29
 *  * @author 김용준
 *  * 비동기 Task를 수행할수 있게하는 AOP EXCUTER이다.
 *  */
@Aspect
@Component
@RequiredArgsConstructor
public class AsyncInvokeAspect {

    private final Executor executor;
    private final AsyncHandleErr asyncHandleErr;

    /**
     * 리턴값이 null인 메소드
     * @param joinPoint
     */
    @Around("@annotation(com.kyj.fmk.core.async.AsyncVoidInvoke)") // Pointcut
    public void around(ProceedingJoinPoint joinPoint){
        //Advice로직
        CompletableFuture<Object> future = new CompletableFuture<>();

        executor.execute(() -> {
            try {
                Object result = joinPoint.proceed();
                future.complete(result);
            } catch (Throwable ex) {
                Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
                asyncHandleErr.handleAsncErr(ex, method); // 커스텀 핸들러 호출
                future.completeExceptionally(ex);
            }
        });

        future.thenAccept(result -> {
            System.out.println("비동기 결과 처리: " + result);
        });

    }




}
