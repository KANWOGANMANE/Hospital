package com.sjq.yygh.msm.servicec.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.client.utils.JSONUtils;
import com.aliyun.dysmsapi20170525.Client;
import com.sjq.yygh.msm.servicec.MsmService;
import com.sjq.yygh.msm.utils.ConstantPropertiesUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.aliyun.dysmsapi20170525.models.*;
import com.aliyun.teaopenapi.models.*;
import java.util.HashMap;
import java.util.Map;


@Service
public class MsmServiceImpl implements MsmService {

    //发送短信验证码
    @Override
    public boolean send(String phone, Object code) throws Exception {
        //判断phone是否为空
        if (StringUtils.isEmpty(phone)) {
            return false;
        }

        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId(ConstantPropertiesUtils.ACCESS_KEY_ID)
                // 您的AccessKey Secret
                .setAccessKeySecret(ConstantPropertiesUtils.SECRECT);
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";
        Client client = new Client(config);
        Map<String, Object> tempcode = new HashMap<String, Object>();
        tempcode.put("code",code);
        String stringcode = JSONObject.toJSONString(tempcode);
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers(phone)
                .setSignName("金码二手兔")
                .setTemplateCode("SMS_192541436")
                .setTemplateParam(stringcode);
        // 复制代码运行请自行打印 API 的返回值
        SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);
        SendSmsResponseBody body = sendSmsResponse.getBody();
        System.out.println("RequestId =>"+ body.getRequestId());
        System.out.println("BizId =>"+ body.getBizId());
        System.out.println("Code =>"+ body.getCode());
        System.out.println("Message=>"+ body.getMessage());
        if(body !=null ){
            return true;
        }
        else{
            return false;
        }
    }
}

