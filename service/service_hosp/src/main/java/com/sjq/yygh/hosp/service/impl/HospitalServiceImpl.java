package com.sjq.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sjq.yygh.cmn.client.DictFeignClient;
import com.sjq.yygh.hosp.repository.HospitalRepository;
import com.sjq.yygh.hosp.service.HospitalService;
import com.sjq.yygh.model.hosp.Hospital;
import com.sjq.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DictFeignClient dictFeignClient;


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

    @Override
    public Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
//创建pageable对象
        Pageable pageable = PageRequest.of(page-1,limit);
        //创建条件匹配器
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        //hospitalSetQueryVo转换Hospital对象
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo,hospital);
        //创建对象
        Example<Hospital> example = Example.of(hospital,matcher);
        //调用方法实现查询
        Page<Hospital> pages = hospitalRepository.findAll(example, pageable);

        //获取查询list集合，遍历进行医院等级封装
        pages.getContent().stream().forEach(item -> {
            this.setHospitalHosType(item);
        });

        return pages;
    }

    //根据ID更新Hospital的Status参数
    @Override
    public void updateStatus(String id, Integer status) {
        Hospital hosp = hospitalRepository.findById(id).get();
        hosp.setStatus(status);
        hosp.setUpdateTime(new Date());
        hospitalRepository.save(hosp);
    }

    /**
     *
     * @param id 根据id查询hospital信息
     * @return   返回一个封装了hospital信息和rule的map
     */
    @Override
    public Map<String,Object> findHospDetails(String id) {
        Map<String, Object> result = new HashMap<>();
        Hospital hospital = this.setHospitalHosType(hospitalRepository.findById(id).get());
        result.put("hospital", hospital);

//单独处理更直观
        result.put("bookingRule", hospital.getBookingRule());
//不需要重复返回
        hospital.setBookingRule(null);
        return result;

//        Map<String,Object> hospital = new HashMap<String,Object>();
//        Hospital hosp = this.setHospitalHosType(hospitalRepository.findById(id).get());
//        hospital.put("hospital",hosp);
//        hospital.put("bookingRule",hosp.getBookingRule());
//        hosp.setBookingRule(null);
//
//        return hospital;
    }

    private Hospital setHospitalHosType(Hospital hospital) {
        //根据dictCode和value获取医院等级名称
        String hostypeString = dictFeignClient.getName("Hostype", hospital.getHostype());
        //查询省 市  地区
        String provinceString = dictFeignClient.getName(hospital.getProvinceCode());
        String cityString = dictFeignClient.getName(hospital.getCityCode());
        String districtString = dictFeignClient.getName(hospital.getDistrictCode());

        hospital.getParam().put("fullAddress",provinceString+cityString+districtString);
        hospital.getParam().put("hostypeString",hostypeString);
        return hospital;
    }
}
