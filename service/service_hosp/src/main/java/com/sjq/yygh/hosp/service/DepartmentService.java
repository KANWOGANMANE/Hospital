package com.sjq.yygh.hosp.service;

import org.springframework.data.domain.Page;
import com.sjq.yygh.model.hosp.Department;
import com.sjq.yygh.vo.hosp.DepartmentQueryVo;

import java.util.Map;

public interface DepartmentService {
    void save(Map<String, Object> stringObjectMap);

    Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentqueryVo);

    void remove(String hoscode, String depcode);
}