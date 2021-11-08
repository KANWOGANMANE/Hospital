package com.sjq.yygh.msm.controller;

import com.sjq.yygh.common.result.Result;
import com.sjq.yygh.msm.servicec.MsmService;
import com.sjq.yygh.msm.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/msm")
public class MsmApiController {

    @Autowired
    private MsmService msmService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @GetMapping("send/{phone}")
    public Result sendCode(@PathVariable String phone) throws Exception {

        //先在redis查找
        String code = redisTemplate.opsForValue().get(phone);

        //查看是否有验证码
        if( !StringUtils.isEmpty(code)){
            return Result.ok();
        }

        //如果没有就生成，然后发送
        code = RandomUtil.getSixBitRandom();
        boolean success = msmService.send(phone,code);

        //如果success是ture,把code放进redis里设置有效时间
        if(success){
            redisTemplate.opsForValue().set( phone, code,2, TimeUnit.MINUTES);
            return Result.ok();
        }
        else{
            return Result.fail().message("发送短信失败");
        }
    }

}
