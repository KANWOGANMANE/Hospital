package com.sjq.yygh.user.api;

import com.sjq.yygh.user.controller.BaseController;
import com.sjq.yygh.user.util.WeiXinProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Controller
@RequestMapping("/api/user/login/")
public class WeiXinApiController extends BaseController{

    @Autowired
    private WeiXinProperties wechatLoginProperties;

    //获得二维码
    @GetMapping("getLoginParam")
    public void getLoginParam() throws IOException {
        StringBuilder builder = new StringBuilder(wechatLoginProperties.getLoginUrl());
        builder.append("appid=").append(wechatLoginProperties.getAppId());
        builder.append("&redirect_uri=").append(wechatLoginProperties.getRedirectUrl());
        builder.append("&response_type=code&scope=snsapi_login&state=111111#wechat_redirect");
        response.sendRedirect(builder.toString());
    }

    //回调信息
    @GetMapping("wechatcallback")
    public void wechatcallback(){

    }
}
