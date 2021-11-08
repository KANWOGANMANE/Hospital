package com.sjq.yygh.user.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(
        prefix = "oauth2.wechat"
)
@Data
public class WeiXinProperties {
    private String appId;
    private String appSecret;
    private String loginUrl;
    private String accessTokenUrl;
    private String redirectUrl;
    private String loginSuccessUrl;
}