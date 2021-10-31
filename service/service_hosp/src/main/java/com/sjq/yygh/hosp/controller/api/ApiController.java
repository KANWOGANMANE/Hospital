package com.sjq.yygh.hosp.controller.api;

import com.sjq.yygh.common.helper.HttpRequestHelper;
import com.sjq.yygh.common.result.Result;
import com.sjq.yygh.common.utils.MD5;
import com.sjq.yygh.common.utils.ResultCodeEnum;
import com.sjq.yygh.common.utils.YyghException;
import com.sjq.yygh.hosp.service.HospitalService;
import com.sjq.yygh.hosp.service.HospitalSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Autowired
    HospitalService hospitalService;

    @Autowired
    HospitalSetService hospitalSetService;

    //上传医院接口
    @PostMapping("saveHospital")
    public Result saveHosp(HttpServletRequest request){

        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(parameterMap);

        String  sign = (String)stringObjectMap.get("sign");
        String hascode = (String)stringObjectMap.get("hoscode");
        String signkey = hospitalSetService.getSignKey(hascode);
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


}