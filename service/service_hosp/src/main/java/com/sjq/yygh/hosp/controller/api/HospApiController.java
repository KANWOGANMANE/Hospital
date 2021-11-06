package com.sjq.yygh.hosp.controller.api;

import com.sjq.yygh.common.result.Result;
import com.sjq.yygh.hosp.service.DepartmentService;
import com.sjq.yygh.hosp.service.HospitalService;
import com.sjq.yygh.model.hosp.Hospital;
import com.sjq.yygh.vo.hosp.DepartmentVo;
import com.sjq.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp/hospital")
public class HospApiController {

    @Autowired
    private HospitalService hospitalservice;

    @Autowired
    private DepartmentService departmentService;

    //1
    @GetMapping(value = "findHospitalList/{page}/{limit}")
    public Result findHospitalList(@PathVariable Integer page,
                                   @PathVariable Integer limit,
                                   HospitalQueryVo hospital){

        Page<Hospital> hospitals = hospitalservice.selectHospPage(page, limit, hospital);
        List<Hospital> content = hospitals.getContent();
        long totalPages = hospitals.getTotalPages();
        return Result.ok(hospitals);
    }

    //2
    @GetMapping(value = "findByHospName/{hosname}")
    public Result findByHospName(String hosname){
        List<Hospital> lh =  hospitalservice.findListHospname(hosname);
        return Result.ok(lh);
    }

    @GetMapping(value = "department/{hoscode}")
    public Result index(@PathVariable String hoscode){
        List<DepartmentVo> deptTree = departmentService.findDeptTree(hoscode);
        return Result.ok(deptTree);
    }

    @GetMapping(value = "findHospitalDetail/{hoscode}")
    public Result item(@PathVariable String hoscode){
        Map<String,Object> map = hospitalservice.item(hoscode);
        return Result.ok(map);
    }

}
