package com.sjq.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sjq.yygh.hosp.repository.HospitalRepository;
import com.sjq.yygh.hosp.service.HospitalService;
import com.sjq.yygh.model.hosp.Hospital;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;


    @Override
    public void save(Map<String, Object> stringObjectMap) {
        String s = JSONObject.toJSONString(stringObjectMap);
        Hospital hospital = JSONObject.parseObject(s, Hospital.class);

        String hoscode = hospital.getHoscode();
        Hospital hospitalex = hospitalRepository.getHospitalByHoscode(hoscode);

        if (hospitalex != null){
            hospital.setStatus(hospitalex.getStatus());
            hospital.setCreateTime(hospitalex.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }else{
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }

    }

    @Override
    public Hospital getByHoscode(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        return hospital;
    }
}
