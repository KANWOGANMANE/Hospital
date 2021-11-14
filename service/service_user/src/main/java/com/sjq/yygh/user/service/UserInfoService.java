package com.sjq.yygh.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sjq.yygh.model.user.UserInfo;
import com.sjq.yygh.vo.user.LoginVo;
import com.sjq.yygh.vo.user.UserAuthVo;
import com.sjq.yygh.vo.user.UserInfoQueryVo;

import java.util.Map;

public interface UserInfoService extends IService<UserInfo> {
    Map<String, Object> LonginUserByMobile(LoginVo loginVo);

    UserInfo selectWxInfoOpenid(String openid);

    void userAuth(Long userId, UserAuthVo userAuthVo);

    Page<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo);

    void lock(Long userId, Integer status);

    Map<String, Object> show(Long userId);

    void approval(Long userId, Integer authStatus);
}
