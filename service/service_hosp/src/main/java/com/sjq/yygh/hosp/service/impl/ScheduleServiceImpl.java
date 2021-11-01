package com.sjq.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sjq.yygh.hosp.repository.ScheduleRepository;
import com.sjq.yygh.hosp.service.ScheduleService;
import com.sjq.yygh.model.hosp.Department;
import com.sjq.yygh.model.hosp.Schedule;
import com.sjq.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository schedRepository;

    //上传排班
    @Override
    public void save(Map<String, Object> stringObjectMap) {
        String mapString = JSONObject.toJSONString(stringObjectMap);
        Schedule schedule = JSONObject.parseObject(mapString,Schedule.class);

        Schedule scheduleex = schedRepository.getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(),schedule.getHosScheduleId());

        if (scheduleex != null){
            scheduleex.setUpdateTime(new Date());
            scheduleex.setIsDeleted(0);
            scheduleex.setStatus(1);
            schedRepository.save(scheduleex);
        }else{
            schedule.setCreateTime(new Date());
            schedule.setIsDeleted(0);
            schedule.setUpdateTime(new Date());
            schedule.setStatus(1);
            schedRepository.save(schedule);
        }

    }

    //查询排班
    @Override
    public Page<Schedule> findPageSchedule(int page, int limit, ScheduleQueryVo scheduleQueryVo) {

        Schedule schedule = new Schedule();
        Pageable pageable = PageRequest.of(page-1,limit);
        BeanUtils.copyProperties(scheduleQueryVo,schedule);
        schedule.setIsDeleted(0);
        schedule.setStatus(1);
        ExampleMatcher match = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);

        Example<Schedule> example = Example.of(schedule,match);

        Page<Schedule> all = schedRepository.findAll(example, pageable);

        return all;
    }

    @Override
    public void remove(String hoscode, String hoScheduleId) {
        Schedule scex = schedRepository.getScheduleByHoscodeAndHosScheduleId(hoscode, hoScheduleId);

        if(scex != null) {
            schedRepository.deleteById(scex.getId());
        }


    }

}
