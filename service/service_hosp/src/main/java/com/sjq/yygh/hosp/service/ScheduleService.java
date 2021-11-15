package com.sjq.yygh.hosp.service;

import com.sjq.yygh.model.hosp.Schedule;
import com.sjq.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Map;

public interface ScheduleService {
    void save(Map<String, Object> stringObjectMap);

    Page<Schedule> findPageSchedule(int page, int limit, ScheduleQueryVo scheduleQueryVo);

    void remove(String hoscode, String hoScheduleId);

    Map<String, Object> getruleSchedule(long page, long limit, String hoscode, String depcode);

    List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate);

    Map<String, Object> getBookingScheduleRule(int page, int limit, String hoscode, String depcode);

    Schedule getById(String scheduleId);
}
