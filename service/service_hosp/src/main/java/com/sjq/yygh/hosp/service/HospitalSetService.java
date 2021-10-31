package com.sjq.yygh.hosp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sjq.yygh.model.hosp.HospitalSet;


public interface HospitalSetService extends IService<HospitalSet> {

    String getSignKey(String hascode);
}
