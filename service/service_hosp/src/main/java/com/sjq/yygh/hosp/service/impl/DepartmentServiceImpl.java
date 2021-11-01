package com.sjq.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sjq.yygh.hosp.repository.DepartmentRepository;
import com.sjq.yygh.hosp.service.DepartmentService;
import com.sjq.yygh.model.hosp.Department;
import com.sjq.yygh.vo.hosp.DepartmentQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import java.util.Date;
import java.util.Map;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void save(Map<String, Object> stringObjectMap) {
        String mapString = JSONObject.toJSONString(stringObjectMap);
        Department department = JSONObject.parseObject(mapString,Department.class);

        Department departmentex = departmentRepository.
                getDepartmentByHoscodeAndDepcode(department.getHoscode(),department.getDepcode());

        if (departmentex !=null){
            departmentex.setUpdateTime(new Date());
            departmentex.setIsDeleted(0);
            departmentRepository.save(departmentex);
        }else{
            department.setCreateTime(new Date());
            department.setIsDeleted(0);
            department.setUpdateTime(new Date());
            departmentRepository.save(department);
        }
    }

    @Override
    public Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentqueryVo) {

        Department departnemt = new Department();
        Pageable pageable = PageRequest.of(page-1,limit);
        BeanUtils.copyProperties(departmentqueryVo,departnemt);
        departnemt.setIsDeleted(0);
        ExampleMatcher match = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);

        Example<Department> example = Example.of(departnemt,match);

        Page<Department> all = departmentRepository.findAll(example, pageable);

        return all;
    }

    @Override
    public void remove(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if(department != null) {
            departmentRepository.deleteById(department.getId());
        }
    }


}
