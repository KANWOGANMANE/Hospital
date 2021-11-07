package com.sjq.yygh.user.service;

import com.sjq.yygh.vo.user.LoginVo;

import java.util.Map;

public interface UserInfoService {
    Map<String, Object> LonginUserByMobile(LoginVo loginVo);
}
