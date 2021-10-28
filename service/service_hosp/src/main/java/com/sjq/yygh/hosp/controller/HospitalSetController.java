package com.sjq.yygh.hosp.controller;

import com.aliyuncs.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sjq.yygh.common.utils.MD5;
import com.sjq.yygh.hosp.service.HospitalSetService;
import com.sjq.yygh.model.hosp.HospitalSet;
import com.sjq.yygh.common.result.*;
import com.sjq.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Api(tags = "医院设置管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
@CrossOrigin //用于设置跨域
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;


    //查询医院设置表所有信息
    //http://localhost:8201/admin/hosp/hospitalSet/findall
    @ApiOperation("查看医院所有数据")
    @GetMapping("findall")
    public Result findall(){
        List<HospitalSet> list = hospitalSetService.list();

        return Result.ok(list);
    }

    //通过id医院设置删除
    @ApiOperation("逻辑删除医院信息")
    @DeleteMapping("{id}")
    public Result removeHospSetbyId(@PathVariable long id){
        boolean b = hospitalSetService.removeById(id);
        if (b){
            return Result.ok();
        }
        else {
            return Result.fail();
        }
    }

    //条件查询带分页
    @ApiOperation("条件查询带分页")
    @PostMapping("/findPage/{current}/{limit}")
    public Result selHospbyWrapper(@PathVariable long current,
                                   @PathVariable long limit,
                                   @RequestBody(required = false) HospitalQueryVo vo){
        String hospname = vo.getHosname();
        String hospid = vo.getHoscode();
        Page<HospitalSet> page = new Page<>(current,limit);
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(hospname)){
            queryWrapper.like("hosname",vo.getHosname());
        }
        if (!StringUtils.isEmpty(hospid)){
            queryWrapper.eq("hoscode",vo.getHoscode());
        }

        Page<HospitalSet> page1 = hospitalSetService.page(page, queryWrapper);

        return Result.ok(page1);
    }



    //添加医院设置
    @PostMapping("saveHosp")
    @ApiOperation("添加医院设置")
    public Result insHosp(@RequestBody HospitalSet hospitalSet){
        hospitalSet.setStatus(1);
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis()+""+random.nextInt(1000)));

        boolean bo = hospitalSetService.save(hospitalSet);
        if (bo){
            return Result.ok();
        }
        else {
            return Result.fail();
        }
    }
//
    //根据id获取医院设置
    //http://localhost:8201/admin/hosp/selHosp/
    @ApiOperation("据id获取医院设置")
    @GetMapping("selHosp/{id}")
    public Result getHospByid(@PathVariable String id){
        HospitalSet rs = hospitalSetService.getById(id);
        List<HospitalSet> ls = new ArrayList<>();
        ls.add(rs);

        if (!ls.isEmpty()){
            return Result.ok(ls);
        }
        else {
            return Result.fail();
        }
    }
//
    //修改医院设置
    @PostMapping("updHosp")
    @ApiOperation("修改医院设置byID")
    public Result updHosp(@RequestBody HospitalSet hospitalSet){
        boolean b = hospitalSetService.updateById(hospitalSet);

        if (b){
            return Result.ok();
        }
        else {
            return Result.fail();
        }
    }

    //批量删除医院设置
    @DeleteMapping("delBathHosp")
    @ApiOperation("批量删除医院设置")
    public Result delBathHosp(@RequestBody List<Long> list){
        boolean b = hospitalSetService.removeByIds(list);
        if (b){
            return Result.ok();
        }
        else {
            return Result.fail();
        }
    }

    //医院锁定和解锁
    @PutMapping("lockHosp/{id}/{status}")
    @ApiOperation("医院锁定和解锁")
    public Result lockHosp(@PathVariable Long id,
                           @PathVariable Integer status){

        HospitalSet hs = hospitalSetService.getById(id);
        hs.setStatus(status);
        hospitalSetService.updateById(hs);

        return Result.ok();
    }

    //发送签名密钥
    @PutMapping("getKey/{id}")
    public Result getkey(@PathVariable Long id){
        HospitalSet byId = hospitalSetService.getById(id);
        String key = byId.getSignKey();
        String hoscode =  byId.getHoscode();

        // TODO: 2021/10/24 发送短信
        return Result.ok();
    }


}

