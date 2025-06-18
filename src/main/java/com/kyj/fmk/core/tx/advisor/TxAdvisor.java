//package com.kyj.fmk.core.tx.advisor;
//
//import com.kyj.fmk.core.tx.dataSource.DataSourceContextHolder;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.After;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
////@Component
////@Aspect
//public class TxAdvisor {
//    public static class DataSourceAspect {
//        @Before("@annotation(ReadOnlyDB)")
//        public void useReadDataSource() {
//            DataSourceContextHolder.setDataSourceType("READ");
//        }
//
//        @After("@annotation(ReadOnlyDB)")
//        public void clearDataSource() {
//            DataSourceContextHolder.clearDataSourceType();
//        }
//        @Before("@annotation(WriteDB)")
//        public void useWriteDataSource() {
//            DataSourceContextHolder.setDataSourceType("WRITE");
//        }
//        @After("@annotation(WriteDB)")
//        public void clearWriteDataSource() {
//            DataSourceContextHolder.clearDataSourceType();
//        }
//
//
//
//
//    }
//    @Order(2) // 트랜잭션 처리는 데이터소스 설정 후
//    public static class TxAspect{
//        @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
//        public Object manageTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
//            return joinPoint.proceed();
//        }
//    }
//}
