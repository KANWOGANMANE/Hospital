package com.sjq.yygh.msm.servicec;

import com.sjq.yygh.vo.msm.MsmVo;

public interface MsmService {
    boolean send(String phone, Object code) throws Exception;

    //mq发送短信接口
    boolean send(MsmVo msmVo) throws Exception;


}
