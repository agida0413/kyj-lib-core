package com.kyj.core.file.config;

import com.kyj.core.file.FileService;
import com.kyj.core.file.obj.S3FileService;
import com.kyj.core.file.obj.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@AutoConfiguration
@EnableConfigurationProperties(S3Properties.class)
@RequiredArgsConstructor
public class FileAutoConfiguration {

    private final S3Properties s3Properties;


    @Bean
    @ConditionalOnMissingBean(S3Client.class)
    public S3Client s3Client() {

        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(s3Properties.getAccessKey(), s3Properties.getSecretKey());
        return S3Client.builder()
                .region(Region.of(s3Properties.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .httpClient(UrlConnectionHttpClient.create()) // ← 이 부분 추가
                .build();
    }


    @Bean
    @ConditionalOnMissingBean(FileService.class)
    public FileService fileService(){
        return new S3FileService(s3Client(),s3Properties);
    }
}
