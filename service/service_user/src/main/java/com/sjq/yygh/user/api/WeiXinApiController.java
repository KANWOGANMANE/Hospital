package com.sjq.yygh.user.api;

import com.alibaba.fastjson.JSONObject;
import com.sjq.yygh.common.helper.JwtHelper;
import com.sjq.yygh.model.user.UserInfo;
import com.sjq.yygh.user.controller.BaseController;
import com.sjq.yygh.user.service.UserInfoService;
import com.sjq.yygh.user.util.HttpClientUtils;
import com.sjq.yygh.user.util.WeiXinProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.sjq.yygh.common.result.Result;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/api/uncenter/login/")
public class WeiXinApiController extends BaseController{

    @Autowired
    private WeiXinProperties wechatLoginProperties;

    @Autowired
    private UserInfoService userInfoService;

    //获得二维码
    @GetMapping("wx/getLoginParam")
    @ResponseBody
    public Result getLoginParam() throws IOException {
        try {
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("appid", WeiXinProperties.APPID);
            map.put("scope","snsapi_login");
            String redirecturl = WeiXinProperties.REDIRECTURL;
            String encode = URLEncoder.encode(redirecturl, "utf-8");
            map.put("redirect_uri",encode);
            map.put("state",System.currentTimeMillis()+"");
            return Result.ok(map);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    //回调信息
    @GetMapping("wechatcallback")
    public String wechatcallback(String code,String state){
        //根据code、appid、secret获取access_token、openid
        System.out.println("code=>"+code);
        StringBuilder getTokenUrl = new StringBuilder("https://api.weixin.qq.com/sns/oauth2/access_token?appid=");
        getTokenUrl.append(WeiXinProperties.APPID);
        getTokenUrl.append("&secret=").append(WeiXinProperties.APPsECRET);
        getTokenUrl.append("&code=").append(code).append("&grant_type=authorization_code");
        String gettoken_url = new String(getTokenUrl);
        try {
            String accesstokeninfo = HttpClientUtils.get(gettoken_url);
            System.out.println("accesstokeninfo=>" + accesstokeninfo);
            JSONObject jsontokeninfo = JSONObject.parseObject(accesstokeninfo);
            String access_token = jsontokeninfo.getString("access_token");
            String openid = jsontokeninfo.getString("openid");

            //根据openid查找数据库，看用户是否以及注册
            UserInfo userInfos = userInfoService.selectWxInfoOpenid(openid);
            if(userInfos == null){
                //根据openid和 access_token获取扫码人信息
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                String userInfoUrl = String.format(baseUserInfoUrl, access_token, openid);
                String userInfo = HttpClientUtils.get(userInfoUrl);
                System.out.println("userInfo=>" + userInfo);
                JSONObject jsonUserInfo = JSONObject.parseObject(userInfo);
                String nickname = jsonUserInfo.getString("nickname");
                String headimgurl = jsonUserInfo.getString("headimgurl");

                //保存用户数据到数据库里
                userInfos = new UserInfo();
                userInfos.setOpenid(openid);
                userInfos.setNickName(nickname);
                userInfos.setStatus(1);
                userInfoService.save(userInfos);
            }

            Map<String, Object> map = new HashMap<>();
            String name = userInfos.getName();
            if(StringUtils.isEmpty(name)) {
                name = userInfos.getNickName();
            }
            if(StringUtils.isEmpty(name)) {
                name = userInfos.getPhone();
            }
            map.put("name", name);

            if(StringUtils.isEmpty(userInfos.getPhone())) {
                map.put("openid", userInfos.getOpenid());
            } else {
                map.put("openid", "");
            }
            String token = JwtHelper.createToken(userInfos.getId(), name);
            map.put("token", token);
            System.out.println("token=>"+ map.get("token"));
            //跳转到页面
            return "redirect:" + WeiXinProperties.YYGH_BASE_URL +
                    "/weixin/callback?token="+map.get("token")+"&openid=" +
                    map.get("openid") + "&name=" + URLEncoder.encode((String)map.get("name"),"utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
