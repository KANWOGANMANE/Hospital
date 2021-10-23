package com.sjq.yygh.hosp.service.impl;

import com.sjq.yygh.hosp.mapper.HospitalSetMapper;
import com.sjq.yygh.hosp.service.HospitalSetService;
import com.sjq.yygh.model.hosp.HospitalSet;
import com.sjq.yygh.vo.order.SignInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.Query;

@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {

}
