package com.sjq.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sjq.yygh.hosp.repository.ScheduleRepository;
import com.sjq.yygh.hosp.service.DepartmentService;
import com.sjq.yygh.hosp.service.HospitalService;
import com.sjq.yygh.hosp.service.ScheduleService;
import com.sjq.yygh.model.hosp.Schedule;
import com.sjq.yygh.vo.hosp.BookingScheduleRuleVo;
import com.sjq.yygh.vo.hosp.ScheduleQueryVo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository schedRepository;

    @Autowired
    private MongoTemplate mongotemplator;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;

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

    @Override
    public Map<String, Object> getruleSchedule(long page, long limit, String hoscode, String depcode) {

        //根据医院编号和科室编号查询
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);

        //根据工作日进行分组
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),//匹配条件
                Aggregation.group("workDate")//分组字段
                .first("workDate").as("workDate")
                //统计预约数量
                .count().as("docCount")
                .sum("reservedNumber").as("reservedNumber")
                .sum("availableNumber").as("availableNumber"),
                Aggregation.sort(Sort.Direction.DESC,"workDate"),
                //分页
                Aggregation.skip((page-1)*limit),
                Aggregation.limit(limit)
        );
        //调用方法进行查询
        AggregationResults<BookingScheduleRuleVo> aggregate = mongotemplator.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> results = aggregate.getMappedResults();

        //分组查询总记录数
        Aggregation totalAgg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );

        AggregationResults<BookingScheduleRuleVo> totalaggregate = mongotemplator.aggregate(totalAgg, Schedule.class, BookingScheduleRuleVo.class);
        int size = totalaggregate.getMappedResults().size();

        //通过日期获取星期
        for(BookingScheduleRuleVo evry:results){
            Date date = evry.getWorkDate();
            String dayOfWeek = this.getDayOfWeek(new DateTime(date));
            evry.setDayOfWeek(dayOfWeek);
        }

        Map<String ,Object> result = new HashMap<String ,Object>();
        result.put("bookingScheduleRuleList",results);
        result.put("total",size);

        HashMap<String ,Object> basemap = new HashMap<String ,Object>();
        String hosName = hospitalService.getHospname(hoscode);
        basemap.put("hosname",hosName);
        result.put("baseMap",basemap);

        return result;
    }

    @Override
    public List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate) {

        //根据参数查询mongodb
        List<Schedule> scheduleList =
                schedRepository.findScheduleByHoscodeAndDepcodeAndWorkDate(hoscode,depcode,new DateTime(workDate).toDate());
        //把得到list集合遍历，向设置其他值：医院名称、科室名称、日期对应星期
        scheduleList.stream().forEach(item->{
            this.packageSchedule(item);
        });
        return scheduleList;

//        List<Schedule> schedulelist =
//                schedRepository.findScheduleByHoscodeAndDepcodeAndWorkdate(hoscode, depcode, new DateTime(workdate).toDate());
//findScheduleByHoscodeAndDepcodeAndWorkDate
//        //遍历schedulelist 把医院的名称、科室名称、星期放进pararm
//        schedulelist.stream().forEach(item ->{
//            this.packageSchedule(item);
//        });
//        return null;
    }

    //封装排班详情信息
    //医院名称、科室名称、日期对应星期
    private void packageSchedule(Schedule schedule){
        //设置医院名称
        schedule.getParam().put("hosname",hospitalService.getHospname(schedule.getHoscode()));
        //设置科室名称
        schedule.getParam().put("depanme",departmentService.getDepname(schedule.getHoscode(),schedule.getDepcode()));
        //设置日期对应星期
        schedule.getParam().put("dayOfWeek",this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
    }

    /**
     * 根据日期获取周几数据
     * @param dateTime
     * @return
     */
    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }
}
