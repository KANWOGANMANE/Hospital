package com.sjq.yygh.msm.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConstantPropertiesUtils implements InitializingBean {

    @Value("${aliyun.sms.regionId}")
    private String regionid;

    @Value("${aliyun.sms.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.sms.secret}")
    private String secret;

    public static String REGION_Id;
    public static String ACCESS_KEY_ID;
    public static String SECRECT;

    @Override
    public void afterPropertiesSet() throws Exception {
        REGION_Id=regionid;
        ACCESS_KEY_ID=accessKeyId;
        SECRECT=secret;
    }
}
