package com.sjq.yygh.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sjq.yygh.common.result.Result;
import com.sjq.yygh.model.user.UserInfo;
import com.sjq.yygh.user.service.UserInfoService;
import com.sjq.yygh.vo.user.UserInfoQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin/user")
public class UserController {
    @Autowired
    UserInfoService userInfoService;

    @GetMapping("{page}/{limit}")
    public Result List(@PathVariable Long page,
                       @PathVariable Long limit,
                       UserInfoQueryVo userInfoQueryVo) {
        System.out.println("imin");
        Page<UserInfo> pageParam = new Page<>(page,limit);
        Page<UserInfo> pagemodel = userInfoService.selectPage(pageParam,userInfoQueryVo);
        return Result.ok(pagemodel);
    }

    //用户锁定
    @GetMapping("lock/{userId}/{status}")
    public Result lock(@PathVariable Long userId,@PathVariable Integer status) {
        userInfoService.lock(userId,status);
        return Result.ok();
    }

    //用户详情
    @GetMapping("show/{userId}")
    public Result show(@PathVariable Long userId) {
        Map<String,Object> map = userInfoService.show(userId);
        return Result.ok(map);
    }
//
    //认证审批
    @GetMapping("approval/{userId}/{authStatus}")
    public Result approval(@PathVariable Long userId,@PathVariable Integer authStatus) {
        userInfoService.approval(userId,authStatus);
        return Result.ok();
    }
}
