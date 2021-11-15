package com.sjq.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.sjq.yygh.common.result.ResultCodeEnum;
import com.sjq.yygh.common.utils.YyghException;
import com.sjq.yygh.hosp.repository.ScheduleRepository;
import com.sjq.yygh.hosp.service.DepartmentService;
import com.sjq.yygh.hosp.service.HospitalService;
import com.sjq.yygh.hosp.service.ScheduleService;
import com.sjq.yygh.model.hosp.BookingRule;
import com.sjq.yygh.model.hosp.Department;
import com.sjq.yygh.model.hosp.Hospital;
import com.sjq.yygh.model.hosp.Schedule;
import com.sjq.yygh.vo.hosp.BookingScheduleRuleVo;
import com.sjq.yygh.vo.hosp.ScheduleQueryVo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


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

    //------------------------
    @Override
    public Map<String, Object> getBookingScheduleRule(int page, int limit, String hoscode, String depcode) {
        Map<String, Object> result = new HashMap<>();

        //获取预约规则
        Hospital hospital = hospitalService.getByHoscode(hoscode);
        if(null == hospital) {
            throw new YyghException(ResultCodeEnum.DATA_ERROR);
        }
        BookingRule bookingRule = hospital.getBookingRule();

        //获取可预约日期分页数据
        IPage iPage = this.getListDate(page, limit, bookingRule);
        //当前页可预约日期
        List<Date> dateList = iPage.getRecords();
        //获取可预约日期科室剩余预约数
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode).and("workDate").in(dateList);
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")//分组字段
                        .first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("availableNumber").as("availableNumber")
                        .sum("reservedNumber").as("reservedNumber")
        );
        AggregationResults<BookingScheduleRuleVo> aggregationResults = mongotemplator.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> scheduleVoList = aggregationResults.getMappedResults();
        //获取科室剩余预约数

        //合并数据 将统计数据ScheduleVo根据“安排日期”合并到BookingRuleVo
        Map<Date, BookingScheduleRuleVo> scheduleVoMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(scheduleVoList)) {
            scheduleVoMap = scheduleVoList.stream().collect(Collectors.toMap(BookingScheduleRuleVo::getWorkDate, BookingScheduleRuleVo -> BookingScheduleRuleVo));
        }
        //获取可预约排班规则
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = new ArrayList<>();
        for(int i=0, len=dateList.size(); i<len; i++) {
            Date date = dateList.get(i);

            BookingScheduleRuleVo bookingScheduleRuleVo = scheduleVoMap.get(date);
            if(null == bookingScheduleRuleVo) { // 说明当天没有排班医生
                bookingScheduleRuleVo = new BookingScheduleRuleVo();
                //就诊医生人数
                bookingScheduleRuleVo.setDocCount(0);
                //科室剩余预约数  -1表示无号
                bookingScheduleRuleVo.setAvailableNumber(-1);
            }
            bookingScheduleRuleVo.setWorkDate(date);
            bookingScheduleRuleVo.setWorkDateMd(date);
            //计算当前预约日期为周几
            String dayOfWeek = this.getDayOfWeek(new DateTime(date));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);

            //最后一页最后一条记录为即将预约   状态 0：正常 1：即将放号 -1：当天已停止挂号
            if(i == len-1 && page == iPage.getPages()) {
                bookingScheduleRuleVo.setStatus(1);
            } else {
                bookingScheduleRuleVo.setStatus(0);
            }
            //当天预约如果过了停号时间， 不能预约
            if(i == 0 && page == 1) {
                DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
                if(stopTime.isBeforeNow()) {
                    //停止预约
                    bookingScheduleRuleVo.setStatus(-1);
                }
            }
            bookingScheduleRuleVoList.add(bookingScheduleRuleVo);
        }

        //可预约日期规则数据
        result.put("bookingScheduleList", bookingScheduleRuleVoList);
        result.put("total", iPage.getTotal());
        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        //医院名称
        baseMap.put("hosname", hospitalService.getHospname(hoscode));
        //科室
        Department department =departmentService.getDepartment(hoscode, depcode);
        //大科室名称
        baseMap.put("bigname", department.getBigname());
        //科室名称
        baseMap.put("depname", department.getDepname());
//月
        baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
//放号时间
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
//停号时间
        baseMap.put("stopTime", bookingRule.getStopTime());
        result.put("baseMap", baseMap);
        return result;
//        Map<String, Object> result = new HashMap<String, Object>();
//        Hospital hospital = hospitalService.getByHoscode(hoscode);
//        if(hospital == null) {
//            throw new YyghException(ResultCodeEnum.DATA_ERROR);
//        }
//        BookingRule bookingRule = hospital.getBookingRule();
//        IPage iPage =  this.getListDate(page,limit,bookingRule);
//        List<Date> dateList = iPage.getRecords();
//
//        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode)
//                .and("workDate").in(dateList);
//        Aggregation aggregation = Aggregation.newAggregation(
//                Aggregation.match(criteria),
//                Aggregation.group("workDate").first("workDate").as("workDate")
//                .count().as("docCount")
//                .sum("availableNumber").as("availableNumber")
//                .sum("reservedNumber").as("reservedNumber")
//        );
//        AggregationResults<BookingScheduleRuleVo> aggresult = mongotemplator.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
//        List<BookingScheduleRuleVo> mappedResults = aggresult.getMappedResults();
//
//        Map<Date,BookingScheduleRuleVo> schedulevomap = new HashMap<Date,BookingScheduleRuleVo>();
//        if(!CollectionUtils.isEmpty(mappedResults)){
//            schedulevomap = mappedResults.stream().
//                    collect(Collectors.toMap(BookingScheduleRuleVo::getWorkDate,
//                            BookingScheduleRuleVo->BookingScheduleRuleVo));
//        }
//
//        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = new ArrayList<BookingScheduleRuleVo>();
//        for(int i=0,len=dateList.size();i<len;i++){
//            Date date = dateList.get(i);
//            BookingScheduleRuleVo bookingScheduleRuleVo = schedulevomap.get(date);
//            if(bookingScheduleRuleVo==null){
//                bookingScheduleRuleVo = new BookingScheduleRuleVo();
//                //就诊医生人数
//                bookingScheduleRuleVo.setDocCount(0);
//                //科室剩余预约数  -1表示无号
//                bookingScheduleRuleVo.setAvailableNumber(-1);
//            }
//            bookingScheduleRuleVo.setWorkDate(date);
//            bookingScheduleRuleVo.setWorkDateMd(date);
//            //计算当前预约日期为周几
//            String dayOfWeek = this.getDayOfWeek(new DateTime(date));
//            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
//            if(i == len-1 && page == iPage.getPages()) {
//                bookingScheduleRuleVo.setStatus(1);
//            } else {
//                bookingScheduleRuleVo.setStatus(0);
//            }
//
//            if(i == 0 && page == 1) {
//                DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
//                if(stopTime.isBeforeNow()) {
//                    //停止预约
//                    bookingScheduleRuleVo.setStatus(-1);
//                }
//            }
//            bookingScheduleRuleVoList.add(bookingScheduleRuleVo);
//        }
//        //可预约日期规则数据
//        result.put("bookingScheduleList", bookingScheduleRuleVoList);
//        result.put("total", iPage.getTotal());
//        //其他基础数据
//        Map<String, String> baseMap = new HashMap<>();
//        //医院名称
//        baseMap.put("hosname", hospitalService.getHospname(hoscode));
//        //科室
//        Department department =departmentService.getDepartment(hoscode, depcode);
//        //大科室名称
//        baseMap.put("bigname", department.getBigname());
//        //科室名称
//        baseMap.put("depname", department.getDepname());
////月
//        baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
////放号时间
//        baseMap.put("releaseTime", bookingRule.getReleaseTime());
////停号时间
//        baseMap.put("stopTime", bookingRule.getStopTime());
//        result.put("baseMap", baseMap);
//        return result;
    }

    @Override
    public Schedule getById(String scheduleId) {
        Schedule schedule = schedRepository.findById(scheduleId).get();
        return this.packageSchedule(schedule);
    }

    //获取可预约分页数据
    private IPage getListDate(int page, int limit, BookingRule bookingRule) {
        //获取当天放号时间
        DateTime relaseTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());
        //获取预约周期
        Integer cycle = bookingRule.getCycle();
        if (relaseTime.isBeforeNow()) {
            cycle++;
        }
        //获取可预约日期，最后一天
        List<Date> dateList = new ArrayList<Date>();
        for (int i = 0; i < cycle; i++) {
            DateTime curDateTime = new DateTime().plusDays(i);
            String s = curDateTime.toString("yyyy-MM-dd");
            dateList.add(new DateTime(s).toDate());
        }
        //
        List<Date> pageDateList = new ArrayList<Date>();
        int start = (page-1)*limit;
        int end = (page-1)*limit+limit;
        if(end>dateList.size()) {
            end = dateList.size();
        }
        for (int i = start ; i<end ; i++) {
            pageDateList.add(dateList.get(i));
        }
        IPage<Date> ipage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page,7,dateList.size());
        ipage.setRecords(pageDateList);
        return ipage;
    }

    /**
     * 将Date日期（yyyy-MM-dd HH:mm）转换为DateTime
     */
    private DateTime getDateTime(Date date, String timeString) {
        String dateTimeString = new DateTime(date).toString("yyyy-MM-dd") + " "+ timeString;
        DateTime dateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeString);
        return dateTime;
    }


    //封装排班详情信息
    //医院名称、科室名称、日期对应星期
    private Schedule packageSchedule(Schedule schedule){
        //设置医院名称
        schedule.getParam().put("hosname",hospitalService.getHospname(schedule.getHoscode()));
        //设置科室名称
        schedule.getParam().put("depanme",departmentService.getDepname(schedule.getHoscode(),schedule.getDepcode()));
        //设置日期对应星期
        schedule.getParam().put("dayOfWeek",this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
        return schedule;
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
