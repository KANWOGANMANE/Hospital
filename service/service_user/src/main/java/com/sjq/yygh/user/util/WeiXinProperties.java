package com.sjq.yygh.user.util;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
public class WeiXinProperties implements InitializingBean {

    @Value("${oauth2.wechat.appId}")
    private String appId;

    @Value("${oauth2.wechat.appSecret}")
    private String appSecret;

    @Value("${oauth2.wechat.loginUrl}")
    private String loginUrl;

    @Value("${oauth2.wechat.accessTokenUrl}")
    private String accessTokenUrl;

    @Value("${oauth2.wechat.redirectUrl}")
    private String redirectUrl;

    @Value("${oauth2.wechat.loginSuccessUrl}")
    private String loginSuccessUrl;

    @Value("${yygh.baseUrl}")
    private String baseurl;

    public static String REGION_Id;
    public static String ACCESS_KEY_ID;
    public static String SECRECT;
    public static String APPID;
    public static String APPsECRET;
    public static String LOGINURL;
    public static String ACCESSTOKENURL;
    public static String REDIRECTURL;
    public static String LOGIN_SUCCESS_URL;
    public static String YYGH_BASE_URL;

    @Override
    public void afterPropertiesSet() throws Exception {
        APPID=appId;
        APPsECRET=appSecret;
        LOGINURL=loginUrl;
        ACCESSTOKENURL=accessTokenUrl;
        REDIRECTURL=redirectUrl;
        LOGIN_SUCCESS_URL =  loginSuccessUrl;
        YYGH_BASE_URL = baseurl;
    }
}