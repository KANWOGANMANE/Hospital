package com.sjq.yygh.hosp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sjq.yygh.model.hosp.HospitalSet;
import com.sjq.yygh.vo.order.SignInfoVo;


public interface HospitalSetService extends IService<HospitalSet> {

    String getSignKey(String hascode);

    SignInfoVo getSignInfoVo(String hoscode);
}
