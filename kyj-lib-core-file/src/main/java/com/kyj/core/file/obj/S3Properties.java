package com.kyj.core.file.obj;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "s3.credentials")
public class S3Properties {

    private String accessKey;
    private String secretKey;
    private String region;
    private String bucket;
    private long maxFileSize = 5* 1024 * 1024;






}
