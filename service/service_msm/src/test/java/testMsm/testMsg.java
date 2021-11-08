package testMsm;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.tea.*;
import com.aliyun.dysmsapi20170525.*;
import com.aliyun.dysmsapi20170525.models.*;
import com.aliyun.teaopenapi.*;
import com.aliyun.teaopenapi.models.*;
import javassist.tools.reflect.Sample;
import org.junit.Test;

public class testMsg {

    @Test
    public void test() throws Exception {
        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId("LTAI5tRMVamGTM4EsvdXdSat")
                // 您的AccessKey Secret
                .setAccessKeySecret("OaPKUeJcaN2ETa2FqSRxseP5Qylt7n");
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";
        Client client = new Client(config);

        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers("15626853850")
                .setSignName("金码二手兔")
                .setTemplateCode("SMS_192541436")
                .setTemplateParam("{\"code\":\"123456\"}");
        // 复制代码运行请自行打印 API 的返回值
        SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);
        SendSmsResponseBody body = sendSmsResponse.getBody();
        System.out.println("RequestId =>"+ body.getRequestId());
        System.out.println("BizId =>"+ body.getBizId());
        System.out.println("Code =>"+ body.getCode());
        System.out.println("Message=>"+ body.getMessage());
    }
}
