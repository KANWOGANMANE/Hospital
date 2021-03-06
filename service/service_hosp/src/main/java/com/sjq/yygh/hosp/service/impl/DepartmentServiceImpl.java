package com.sjq.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sjq.yygh.hosp.repository.DepartmentRepository;
import com.sjq.yygh.hosp.service.DepartmentService;
import com.sjq.yygh.model.hosp.Department;
import com.sjq.yygh.vo.hosp.DepartmentQueryVo;
import com.sjq.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
        //?????????????????????????????????
        List<DepartmentVo> result = new ArrayList();

        //??????hoscode??????????????????
        Department departmentqr = new Department();
        departmentqr.setHoscode(hoscode);
        Example example = Example.of(departmentqr);
        List<Department> depaList = departmentRepository.findAll(example);

        //??????stream??????????????????bigcode??????,key???bigcode
        Map<String, List<Department>> collect = depaList.stream().collect(Collectors.groupingBy(Department::getBigcode));

        //??????map
        for(Map.Entry<String,List<Department>> entry: collect.entrySet()){
            //???????????????
            String bigcode = entry.getKey();
            //????????????????????????
            List<Department> da = entry.getValue();
            //???????????????
            DepartmentVo dv = new DepartmentVo();
            dv.setDepcode(bigcode);
            dv.setDepname(da.get(0).getBigname());

            //???????????????
            List<DepartmentVo> child = new ArrayList<DepartmentVo>();
            for(Department department2 :da){
                DepartmentVo dDepartmentVo = new DepartmentVo();
                dDepartmentVo.setDepcode(department2.getDepcode());
                dDepartmentVo.setDepname(department2.getDepname());
                child.add(dDepartmentVo);
            }
            dv.setChildren(child);
            result.add(dv);
        }
        return result;
    }

    @Override
    public String getDepname(String hoscode,String depcode) {
        Department dep = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if(dep!=null){
            return dep.getDepname();
        }
        return null;
    }

    @Override
    public Department getDepartment(String hoscode, String depcode) {
        return departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
    }


}
