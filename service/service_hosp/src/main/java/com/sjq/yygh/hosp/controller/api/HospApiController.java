package com.sjq.yygh.hosp.controller.api;

import com.sjq.yygh.common.result.Result;
import com.sjq.yygh.hosp.service.DepartmentService;
import com.sjq.yygh.hosp.service.HospitalService;
import com.sjq.yygh.hosp.service.HospitalSetService;
import com.sjq.yygh.hosp.service.ScheduleService;
import com.sjq.yygh.model.hosp.Hospital;
import com.sjq.yygh.model.hosp.Schedule;
import com.sjq.yygh.vo.hosp.DepartmentVo;
import com.sjq.yygh.vo.hosp.HospitalQueryVo;
import com.sjq.yygh.vo.hosp.ScheduleOrderVo;
import com.sjq.yygh.vo.order.SignInfoVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private HospitalSetService hospitalSetService;

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

    //??????????????????????????????
    @ApiOperation(value = "???????????????????????????")
    @GetMapping("auth/getBookingScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getBookingSchedule(
            @ApiParam(name = "page", value = "????????????", required = true)
            @PathVariable Integer page,
            @ApiParam(name = "limit", value = "???????????????", required = true)
            @PathVariable Integer limit,
            @ApiParam(name = "hoscode", value = "??????code", required = true)
            @PathVariable String hoscode,
            @ApiParam(name = "depcode", value = "??????code", required = true)
            @PathVariable String depcode) {
        return Result.ok(scheduleService.getBookingScheduleRule(page, limit, hoscode, depcode));
    }

    //????????????????????????
    @ApiOperation(value = "??????????????????")
    @GetMapping("auth/findScheduleList/{hoscode}/{depcode}/{workDate}")
    public Result findScheduleList(
            @ApiParam(name = "hoscode", value = "??????code", required = true)
            @PathVariable String hoscode,
            @ApiParam(name = "depcode", value = "??????code", required = true)
            @PathVariable String depcode,
            @ApiParam(name = "workDate", value = "????????????", required = true)
            @PathVariable String workDate) {
        return Result.ok(scheduleService.getDetailSchedule(hoscode, depcode, workDate));
    }

    @ApiOperation(value = "????????????id??????????????????")
    @GetMapping("getSchedule/{scheduleId}")
    public Result getSchedule(
            @ApiParam(name = "scheduleId", value = "??????id", required = true)
            @PathVariable String scheduleId) {
        return Result.ok(scheduleService.getById(scheduleId));
    }

    @ApiOperation(value = "????????????id????????????????????????")
    @GetMapping("inner/getScheduleOrderVo/{scheduleId}")
    public ScheduleOrderVo getScheduleOrderVo(
            @ApiParam(name = "scheduleId", value = "??????id", required = true)
            @PathVariable("scheduleId") String scheduleId) {
        return scheduleService.getScheduleOrderVo(scheduleId);
    }

    @ApiOperation(value = "????????????????????????")
    @GetMapping("inner/getSignInfoVo/{hoscode}")
    public SignInfoVo getSignInfoVo(
            @ApiParam(name = "hoscode", value = "??????code", required = true)
            @PathVariable("hoscode") String hoscode) {
        return hospitalSetService.getSignInfoVo(hoscode);
    }
}
