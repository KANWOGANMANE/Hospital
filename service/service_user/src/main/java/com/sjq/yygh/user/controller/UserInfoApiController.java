package com.sjq.yygh.user.controller;

import com.sjq.yygh.common.result.Result;
import com.sjq.yygh.common.utils.AuthContextHolder;
import com.sjq.yygh.model.user.UserInfo;
import com.sjq.yygh.user.service.UserInfoService;
import com.sjq.yygh.vo.user.LoginVo;
import com.sjq.yygh.vo.user.UserAuthVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserInfoApiController {

    @Autowired
    private UserInfoService userInfoService;

    //用户手机号登陆
    @PostMapping("login")
    public Result MobileLogin(@RequestBody LoginVo loginVo) {
        System.out.println("手机号:"+loginVo.getPhone());
        Map<String, Object> map = userInfoService.LonginUserByMobile(loginVo);
        return Result.ok(map);
    }

    //用户认证
    @PostMapping("auth/userAuth")
    public Result userAuth(@RequestBody UserAuthVo userAuthVo, HttpServletRequest request){
        userInfoService.userAuth(AuthContextHolder.getUserId(request),userAuthVo);
        return Result.ok();
    }

    //获取用户id信息
    @GetMapping("auth/getUserInfo")
    public Result getUserInfo(HttpServletRequest request){
        System.out.println(request.toString());
        System.out.println(request);
        Long userId = AuthContextHolder.getUserId(request);
        UserInfo UserInfo = userInfoService.getById(userId);
        return Result.ok(UserInfo);
    }
}