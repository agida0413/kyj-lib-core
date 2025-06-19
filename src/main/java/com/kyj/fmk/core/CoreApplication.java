package com.kyj.fmk.core;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.net.URL;

@SpringBootApplication
@EnableCaching // 상위애플리케이션에서 정의
public class CoreApplication {

	public static void main(String[] args) {
        Resource[] resources = null;
        try {
            resources = new PathMatchingResourcePatternResolver()
                    .getResources("classpath:mapper/**/*.xml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
		System.out.println("resources = " + resources.length);
        for (Resource resource : resources) {
			System.out.println("로드된 매퍼 XML: " + resource.getFilename());
            try {
                System.out.println("resource.getURL() = " + resource.getURL());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
		SpringApplication.run(CoreApplication.class, args);
	}

}
