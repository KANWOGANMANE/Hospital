package com.sjq.yygh.hosp.controller;

import com.sjq.yygh.hosp.service.HospitalSetService;
import com.sjq.yygh.model.hosp.HospitalSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;


    //查询医院设置表所有信息
    //http://localhost:8201/admin/hosp/hospitalSet/findall
    @GetMapping("findall")
    public List<HospitalSet> findall(){
        List<HospitalSet> list = hospitalSetService.list();

        return list;
    }

}

