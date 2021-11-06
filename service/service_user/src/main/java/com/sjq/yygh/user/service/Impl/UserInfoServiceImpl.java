package com.sjq.yygh.user.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjq.yygh.model.user.UserInfo;
import com.sjq.yygh.user.mapper.UserInfoMapper;
import com.sjq.yygh.user.service.UserInfoService;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper,UserInfo> implements UserInfoService{

}
