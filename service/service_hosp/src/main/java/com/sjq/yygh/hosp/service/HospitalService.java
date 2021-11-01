package com.sjq.yygh.hosp.service;

import com.sjq.yygh.model.hosp.Hospital;

import java.util.Map;

public interface HospitalService {
    void save(Map<String, Object> stringObjectMap);

    Hospital getByHoscode(String hoscode);
}
