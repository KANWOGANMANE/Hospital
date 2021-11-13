package com.sjq.yygh.user.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjq.yygh.common.helper.JwtHelper;
import com.sjq.yygh.common.result.ResultCodeEnum;
import com.sjq.yygh.common.utils.YyghException;
import com.sjq.yygh.enums.AuthStatusEnum;
import com.sjq.yygh.model.user.UserInfo;
import com.sjq.yygh.user.mapper.UserInfoMapper;
import com.sjq.yygh.user.service.UserInfoService;
import com.sjq.yygh.vo.user.LoginVo;
import com.sjq.yygh.vo.user.UserAuthVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper,UserInfo> implements UserInfoService{

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public Map<String, Object> LonginUserByMobile(LoginVo loginVo) {

        //从loginvo获取手机 号和验证码
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();

        //判断手机号验证码是否为空
        if(StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)){
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }

        //判断手机验证码和输入的验证码是否一致replace
        String rediscode = redisTemplate.opsForValue().get(phone);
        System.out.println("code:"+code);
        System.out.println("rediscode:"+rediscode);
        //取到code的值不相等
        if(!code.equals(rediscode)) {
            throw new YyghException(ResultCodeEnum.CODE_ERROR);
        }

        UserInfo userInfo = null;
        if(!StringUtils.isEmpty(loginVo.getOpenid())) {
            userInfo = this.selectWxInfoOpenid(loginVo.getOpenid());
            if(null != userInfo) {
                userInfo.setPhone(loginVo.getPhone());
                this.updateById(userInfo);
            } else {
                throw new YyghException(ResultCodeEnum.DATA_ERROR);
            }
        }

        if(userInfo == null){
            //判断是否第一次登陆
            QueryWrapper<UserInfo> qw = new QueryWrapper();
            qw.eq("phone",phone);
            userInfo = baseMapper.selectOne(qw);
            //第一次登陆，就把数据存放到数据库
            if(userInfo == null){
                userInfo = new UserInfo();
                userInfo.setName("");
                userInfo.setPhone(phone);
                userInfo.setStatus(1);
                baseMapper.insert(userInfo);
            }
        }

        //判断User状态
        if(userInfo.getStatus() == 0){
           throw new YyghException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }

        //不是第一次登陆，封装数据，直接返回用户名称和token
        Map<String, Object> map  = new HashMap<String, Object>();
        String name = userInfo.getName();
        if(StringUtils.isEmpty(name)){
            name = userInfo.getNickName();
        }
        if (StringUtils.isEmpty(name)){
            name = userInfo.getPhone();
        }
        map.put("name", name);

        //生成token
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token",token);
        System.out.println(map.get("token"));

        return map;
    }

    @Override
    public UserInfo selectWxInfoOpenid(String openid) {
        QueryWrapper<UserInfo> qw = new QueryWrapper<>();
        qw.eq("openid",openid);
        UserInfo userInfo = baseMapper.selectOne(qw);
        return userInfo;
    }

    @Override
    public void userAuth(Long userId, UserAuthVo userAuthVo) {
        //根据userid查询用户
        UserInfo userInfo = baseMapper.selectById(userId);
        //进行信息更新
        userInfo.setName(userAuthVo.getName());
        userInfo.setCertificatesType(userAuthVo.getCertificatesType());
        userInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
        userInfo.setCertificatesUrl(userAuthVo.getCertificatesUrl());
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());
        baseMapper.updateById(userInfo);
    }
}
