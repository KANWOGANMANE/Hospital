package com.sjq.yygh.hosp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sjq.yygh.hosp.mapper.HospitalSetMapper;
import com.sjq.yygh.hosp.service.HospitalSetService;
import com.sjq.yygh.model.hosp.HospitalSet;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.management.Query;

@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {

    @Override
    public String getSignKey(String hascode) {
        QueryWrapper<HospitalSet> qa = new QueryWrapper();
        qa.eq("hoscode",hascode);
        HospitalSet one = baseMapper.selectOne(qa);
        return one.getSignKey();
    }
}
