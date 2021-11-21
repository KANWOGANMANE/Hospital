package com.sjq.yygh.hosp.controller;

import com.sjq.yygh.common.result.Result;
import com.sjq.yygh.hosp.service.HospitalService;
import com.sjq.yygh.model.hosp.Hospital;
import com.sjq.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/hosp/hospital")
public class HospitalController {

    @Autowired
    private HospitalService hospitalservice;

    //
    @GetMapping("list/{page}/{limit}")
    public Result listHosp(@PathVariable Integer page,
                           @PathVariable Integer limit,
                           HospitalQueryVo hospitalQueryVo){

        Page<Hospital> hosppage = hospitalservice.selectHospPage(page, limit,hospitalQueryVo);
            List<Hospital> Content = hosppage.getContent();
            long TotalElements = hosppage.getTotalElements();
        return Result.ok(hosppage);
    }

    //更新医院status
    @GetMapping("UpdateHospStatus/{id}/{status}")
    public Result updateHospStatus(@PathVariable String id, @PathVariable Integer status){
        hospitalservice.updateStatus(id,status);
        return Result.ok();
    }

    //查看医院详情信息
    @GetMapping("showHospDetail/{id}")
    public Result showHospDetail(@PathVariable String id){
        Map<String,Object> hosp = hospitalservice.findHospDetails(id);
        return Result.ok(hosp);
    }



}
