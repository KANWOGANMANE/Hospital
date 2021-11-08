package com.sjq.yygh.user.controller;

import com.sjq.yygh.common.result.Result;
import com.sjq.yygh.user.service.UserInfoService;
import com.sjq.yygh.vo.user.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserInfoApiController {

    @Autowired
    private UserInfoService userInfoService;

    //用户手机号登陆
    @PostMapping("login")
    public Result MobileLogin(@RequestBody LoginVo loginVo) {
        System.out.println("SHOUJI HAO"+loginVo.getPhone());
        Map<String, Object> map = userInfoService.LonginUserByMobile(loginVo);
        return Result.ok(map);
    }
}