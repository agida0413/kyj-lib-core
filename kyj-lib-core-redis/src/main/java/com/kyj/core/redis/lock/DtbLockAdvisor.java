package com.kyj.core.redis.lock;

//import com.kyj.fmk.core.tx.dataSource.DataSourceContextHolder;

import com.kyj.core.api.CmErrCode;
import com.kyj.core.exception.custom.KyjSysException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 2025-06-18
 * @author 김용준
 * Restful Api에서 사용하는 분산락 어드바이저
 */

@Slf4j
public class DtbLockAdvisor {

    /**
     * 분산락을 제어하기위한 aop
     */
    @Component
    @Aspect
    @RequiredArgsConstructor
    @Order(1)
    public static class DtbLockAspect{

        private  final DtbLock dtbLock;

        @Around("@annotation(com.kyj.core.redis.lock.annotation.DtbLock)")
        public Object manageDtbLock(ProceedingJoinPoint joinPoint) throws Throwable {

            Object result;

            boolean acquired= dtbLock.acquired(joinPoint.getSignature().getName());
            boolean isNotTimeOut = true;
            if(acquired){
                try {
                    result = joinPoint.proceed();
                    isNotTimeOut = dtbLock.isNotTimeOut(joinPoint.getSignature().getName());
                } catch (Exception e) {
                    log.error("분산락 획득에 실패하였습니다.");
                    throw new KyjSysException(CmErrCode.CM002);
                }
                finally {
                    if(isNotTimeOut){
                        dtbLock.deleteLock(joinPoint.getSignature().getName());
                    }else{
                        throw new KyjSysException(CmErrCode.CM002);
                    }
                }
            }else{
                log.error("분산락 획득에 실패하였습니다.");
                throw new KyjSysException(CmErrCode.CM002);
            }



            return result;

        }
    }




}
