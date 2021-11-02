package com.sjq.yygh.hosp.controller;

import com.sjq.yygh.common.result.Result;
import com.sjq.yygh.hosp.service.HospitalService;
import com.sjq.yygh.model.hosp.Hospital;
import com.sjq.yygh.vo.hosp.HospitalQueryVo;
import com.sjq.yygh.vo.hosp.HospitalSetQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/hosp/hospital")
@CrossOrigin
public class HospitalController {

    @Autowired
    private HospitalService hospitalservice;

    @GetMapping("list/{page}/{limit}")
    public Result listHosp(@PathVariable Integer page,
                           @PathVariable Integer limit,
                           HospitalQueryVo hospitalQueryVo){

        Page<Hospital> hosppage = hospitalservice.selectHospPage(page, limit,hospitalQueryVo);

        return Result.ok(hosppage);
    }

}
