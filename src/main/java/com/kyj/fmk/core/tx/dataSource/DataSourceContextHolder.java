//package com.kyj.fmk.core.tx.dataSource;
//
//import org.springframework.context.annotation.Profile;
//
//@Profile("prd")
//public class DataSourceContextHolder {
//    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();
//
//    public static void setDataSourceType(String dataSourceType) {
//        CONTEXT.set(dataSourceType);
//    }
//
//    public static String getDataSourceType() {
//        return CONTEXT.get();
//    }
//
//    public static void clearDataSourceType() {
//        CONTEXT.remove();
//    }
//}
