package com.sjq.yygh.oss.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

public class AliyunOssConfig implements InitializingBean {

    @Value("${aliyun.oss.endpoints}")
    private String endpoints;

    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.oss.secret}")
    private String secret;

    @Value("${aliyun.oss.bucket}")
    private String bucket;

    public static String ENDPOINT;
    public static String ACCESS_KEY_ID;
    public static String SECRECT;
    public static String BUCKET;
    @Override
    public void afterPropertiesSet() throws Exception {
        ENDPOINT = endpoints;
        ACCESS_KEY_ID = accessKeyId;
        SECRECT = secret;
        BUCKET = bucket;
    }
}
