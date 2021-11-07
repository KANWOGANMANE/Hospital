package com.sjq.yygh.hosp.controller.api;

import com.sjq.yygh.common.result.ResultCodeEnum;
import com.sjq.yygh.hosp.service.ScheduleService;
import com.sjq.yygh.model.hosp.Schedule;
import com.sjq.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;
import com.sjq.yygh.common.helper.HttpRequestHelper;
import com.sjq.yygh.common.result.Result;
import com.sjq.yygh.common.utils.MD5;
import com.sjq.yygh.common.utils.YyghException;
import com.sjq.yygh.hosp.service.DepartmentService;
import com.sjq.yygh.hosp.service.HospitalService;
import com.sjq.yygh.hosp.service.HospitalSetService;
import com.sjq.yygh.common.result.ResultCodeEnum;
import com.sjq.yygh.model.hosp.Department;
import com.sjq.yygh.model.hosp.Hospital;
import com.sjq.yygh.vo.hosp.DepartmentQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;

    //删除排班
    @PostMapping("schedule/remove")
    public Result removeSchedule(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(parameterMap);

        String hoscode = (String)stringObjectMap.get("hoscode");
        String hoScheduleId = (String)stringObjectMap.get("hosScheduleId");

        // TODO: 2021/11/1 签名校验

        scheduleService.remove(hoscode,hoScheduleId);

        return Result.ok();
    }

    //查询排班
    @PostMapping("schedule/list")
    public Result findSchedule(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(parameterMap);

        String hoscode = (String)stringObjectMap.get("hoscode");
        String depcode = (String)stringObjectMap.get("depcode");
        int page = StringUtils.isEmpty(stringObjectMap.get("page"))?1:Integer.parseInt((String)stringObjectMap.get("page"));
        int limit = StringUtils.isEmpty(stringObjectMap.get("limit"))?1:Integer.parseInt((String)stringObjectMap.get("limit"));

        // TODO: 2021/11/1 签名校验

        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode(hoscode);
        scheduleQueryVo.setDepcode(depcode);
        Page<Schedule> pageschedule = scheduleService.findPageSchedule(page,limit,scheduleQueryVo);

        return Result.ok(pageschedule);
    }


    //上传排班
    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request){

        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(parameterMap);

        //签名校验
        // TODO: 2021/11/1  签名校验
//        String hoscode =(String) stringObjectMap.get("hoscode");
//        String  sign = (String)stringObjectMap.get("sign");
//        String signkey = hospitalSetService.getSignKey(hoscode);
//        //把传过来的数据进行MD5加密
//        String encrypt = MD5.encrypt(signkey);
//        //校验签名是否相等
//        if(!sign.equals(encrypt)) {
//            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
//        }


        scheduleService.save(stringObjectMap);
        return Result.ok();
    }

    //查询医院信息
    @PostMapping("hospital/show")
    public Result gethospital(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(parameterMap);

        //获取医院编号验证签名
        String hoscode =(String) stringObjectMap.get("hoscode");
        String  sign = (String)stringObjectMap.get("sign");
        String signkey = hospitalSetService.getSignKey(hoscode);
        //把传过来的数据进行MD5加密
        String encrypt = MD5.encrypt(signkey);
        //校验签名是否相等
        if(!sign.equals(encrypt)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        //调用方法进行查询
        Hospital hospital = hospitalService.getByHoscode(hoscode);
        return Result.ok(hospital);
    }

    //上传医院接口
    @PostMapping("saveHospital")
    public Result saveHosp(HttpServletRequest request){

        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(parameterMap);

        //签名校验
        String  sign = (String)stringObjectMap.get("sign");
        String hoscode = (String)stringObjectMap.get("hoscode");
        String signkey = hospitalSetService.getSignKey(hoscode);
        String encrypt = MD5.encrypt(signkey);
        if(!sign.equals(encrypt)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        String logoData = (String)stringObjectMap.get("logoData");
        logoData = logoData.replaceAll(" ","+");
        stringObjectMap.put("logoData",logoData);

        hospitalService.save(stringObjectMap);

        return Result.ok();
    }

    //上传科室
    @PostMapping("saveDepartment")
    public Result saveDept(HttpServletRequest request){

        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(parameterMap);

        String  sign = (String)stringObjectMap.get("sign");
        String hoscode = (String)stringObjectMap.get("hoscode");
        String signkey = hospitalSetService.getSignKey(hoscode);
        String encrypt = MD5.encrypt(signkey);
        if(!sign.equals(encrypt)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        departmentService.save(stringObjectMap);

        return Result.ok();
    }

    //查询科室
    @PostMapping("department/list")
    public Result findDept(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(parameterMap);

        String hoscode = (String)stringObjectMap.get("hoscode");
        int page = StringUtils.isEmpty(stringObjectMap.get("page"))?1:Integer.parseInt((String)stringObjectMap.get("page"));
        int limit = StringUtils.isEmpty(stringObjectMap.get("limit"))?1:Integer.parseInt((String)stringObjectMap.get("limit"));


        // TODO: 2021/11/1 签名校验
        //签名校验
//        String  sign = (String)stringObjectMap.get("sign");
//        String signkey = hospitalSetService.getSignKey(hoscode);
//        String encrypt = MD5.encrypt(signkey);
//        if(!sign.equals(encrypt)) {
//            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
//        }

        DepartmentQueryVo departmentqueryVo = new DepartmentQueryVo();
        departmentqueryVo.setHoscode(hoscode);
        Page<Department> pagedepartment = departmentService.findPageDepartment(page,limit,departmentqueryVo);

        return Result.ok(pagedepartment);
    }

    //删除科室
    @PostMapping("department/remove")
    public Result removeDept(HttpServletRequest request){

        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(parameterMap);

        String hoscode = (String)stringObjectMap.get("hoscode");
        String depcode = (String)stringObjectMap.get("depcode");
        // TODO: 2021/11/1 签名校验
//        String  sign = (String)stringObjectMap.get("sign");
//        String signkey = hospitalSetService.getSignKey(hoscode);
//        String encrypt = MD5.encrypt(signkey);
//        if(!sign.equals(encrypt)) {
//            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
//        }

        departmentService.remove(hoscode,depcode);
        return Result.ok();
    }


}