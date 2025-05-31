package com.kyj.fmk.core.async.notused;

import com.kyj.fmk.core.async.BaseAsyncHandleErr;
import com.kyj.fmk.core.cst.enm.ApiErrCode;
import com.kyj.fmk.core.exception.custom.KyjAsncException;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
/**
 * 2025-05-29
 * @author 김용준
 * 비동기 Task를 리플렉션으로 메소드를 수행할수 있게하는 서비스이다.
 */
//@Service
@RequiredArgsConstructor
public class AsyncRegacyService extends BaseAsyncHandleErr implements AsyncService{
    //비동기 Task Executor
    private final Executor executor;


    /**
     *  비동기 Task를 리플렉션으로 메소드를 수행할수 있게하는 메소드이다.
     * @param target
     * @param methodName
     * @param args
     * @return
     */
    @Override
    public CompletableFuture<Object> invokeAsync(Object target, String methodName, Object... args) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Class<?> clazz = target.getClass();

                // 파라미터 타입 추론
                Class<?>[] paramTypes = Arrays.stream(args)
                        .map(Object::getClass)
                        .toArray(Class<?>[]::new);

                Method method = clazz.getMethod(methodName, paramTypes);
                method.setAccessible(true); // private 메서드 호출 허용

                return method.invoke(target, args);
            } catch (Exception e) {
                throw new KyjAsncException(ApiErrCode.CM006);
            }
        }, executor).exceptionally(
                //예외 핸들링
                ex ->{
//                    handleAsncErr(ex);
            return  null;
        });
    }

}
