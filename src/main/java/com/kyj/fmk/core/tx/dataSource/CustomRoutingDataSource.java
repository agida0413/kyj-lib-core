//package com.kyj.fmk.core.tx.dataSource;
//
//import org.springframework.context.annotation.Profile;
//import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
//
//@Profile("prd")
//public class CustomRoutingDataSource extends AbstractRoutingDataSource {
//    @Override
//    protected Object determineCurrentLookupKey() {
//        return DataSourceContextHolder.getDataSourceType();
//    }
//}
