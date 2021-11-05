package com.sjq.yygh.hosp.controller;

import com.sjq.yygh.common.result.Result;
import com.sjq.yygh.hosp.service.ScheduleService;
import com.sjq.yygh.model.hosp.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/hosp/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleservice;

    //根据医院编号 和 科室室编号 查询排班信息
    @GetMapping("getScheduleTime/{page}/{limit}/{hoscode}/{depcode}")
    public Result getScheduleByHoscodeAndDepcode(@PathVariable long page,
                                                 @PathVariable long limit,
                                                 @PathVariable String hoscode,
                                                 @PathVariable String depcode){
        Map<String,Object> map = scheduleservice.getruleSchedule(page,limit,hoscode,depcode);
        return Result.ok(map);
    }

    //根据医院编号 和科室编号 和日期查询预约挂号信息
    @GetMapping("getScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public Result getScheduleDetail(@PathVariable String hoscode,
                                    @PathVariable String depcode,
                                    @PathVariable String workDate){
        List<Schedule> list = scheduleservice.getDetailSchedule(hoscode,depcode,workDate);

        return Result.ok(list);
    }



}
