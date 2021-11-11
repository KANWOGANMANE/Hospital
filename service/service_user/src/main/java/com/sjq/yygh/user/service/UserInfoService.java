package com.sjq.yygh.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sjq.yygh.model.user.UserInfo;
import com.sjq.yygh.vo.user.LoginVo;

import java.util.Map;

public interface UserInfoService extends IService<UserInfo> {
    Map<String, Object> LonginUserByMobile(LoginVo loginVo);

    UserInfo selectWxInfoOpenid(String openid);
}
