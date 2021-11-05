package com.sjq.yygh.hosp.service;

import com.sjq.yygh.model.hosp.Hospital;
import com.sjq.yygh.vo.hosp.HospitalQueryVo;
import com.sjq.yygh.vo.hosp.HospitalSetQueryVo;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface HospitalService {
    void save(Map<String, Object> stringObjectMap);

    Hospital getByHoscode(String hoscode);

    Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalSetQueryVo);

    void updateStatus(String id, Integer status);

    Map<String,Object> findHospDetails(String id);

    String getHospname(String hoscode);
}
