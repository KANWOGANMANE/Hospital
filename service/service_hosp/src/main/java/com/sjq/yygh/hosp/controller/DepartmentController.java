package com.sjq.yygh.hosp.controller;

import com.sjq.yygh.common.result.Result;
import com.sjq.yygh.hosp.service.DepartmentService;
import com.sjq.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/hosp/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("getDeptList/{hoscode}")
    public Result getDeptList(@PathVariable String hoscode){

        List<DepartmentVo> deptTree = departmentService.findDeptTree(hoscode);
        return Result.ok(deptTree);
    }

}
