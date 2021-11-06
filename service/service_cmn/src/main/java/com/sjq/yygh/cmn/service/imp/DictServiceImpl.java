package com.sjq.yygh.cmn.service.imp;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjq.yygh.cmn.listener.DictListener;
import com.sjq.yygh.cmn.mapper.DictMapper;
import com.sjq.yygh.cmn.service.DictService;
import com.sjq.yygh.model.cmn.Dict;
import com.sjq.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.Cacheable;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    //根据数据id查询数据字典
    @Override
    //@Cacheable(value = "dict") //keyGenerator = "keyGenerator",
    public List<Dict> findChildDdata(Long id) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id",id);
        List<Dict> dicts = baseMapper.selectList(queryWrapper);
        for (Dict in:dicts){
            Long inId = in.getId();
            boolean haschild = this.hasChild(id);
            in.setHasChildren(haschild);
        }
        return dicts;
    }

    //导出数据字典
    @Override
    public void exportDict(HttpServletResponse response) {
        try {
            //设置下载信息
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
// 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
//            String fileName = URLEncoder.encode("数据字典", "UTF-8");
            String fileName = "dict";
            response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".xlsx");
            //查询数据库
            List<Dict> dictList = baseMapper.selectList(null);
            List<DictEeVo> dictVoList = new ArrayList<>(dictList.size());
            for(Dict dict : dictList) {
                DictEeVo dictVo = new DictEeVo();
                BeanUtils.copyProperties(dict,dictVo);
                dictVoList.add(dictVo);
            }
            EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("dict").doWrite(dictVoList);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //导入数据字典
    @Override
    @CacheEvict(value = "dict", allEntries=true)
    public void importDict(MultipartFile multipartFile){
        try {
            EasyExcel.read(multipartFile.getInputStream(),DictEeVo.class,new DictListener(baseMapper)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getDictName(String dictCode, String value) {
        //如果dictCode为空，直接根据value查询
        if(StringUtils.isEmpty(dictCode)) {
            //直接根据value查询
            QueryWrapper<Dict> wrapper = new QueryWrapper<>();
            wrapper.eq("value",value);
            Dict dict = baseMapper.selectOne(wrapper);
            return dict.getName();
        } else {//如果dictCode不为空，根据dictCode和value查询
            //根据dictcode查询dict对象，得到dict的id值
            Dict codeDict = this.getDictByDictCode(dictCode);
            Long parent_id = codeDict.getId();
            //根据parent_id和value进行查询
            Dict finalDict = baseMapper.selectOne(new QueryWrapper<Dict>()
                    .eq("parent_id", parent_id)
                    .eq("value", value));
            return finalDict.getName();
        }
//        if (StringUtils.isEmpty(dictcode)) {
//            QueryWrapper<Dict> qw = new QueryWrapper();
//            qw.eq("value", value);
//            Dict dict = baseMapper.selectOne(qw);
//            return dict.getName();
//        }else {
//            QueryWrapper<Dict> qw = new QueryWrapper();
//            qw.eq("dict_code", dictcode);
//            Dict codedict = baseMapper.selectOne(qw);
//            Long parentid = codedict.getParentId();
//            Dict dict = baseMapper.selectOne(new QueryWrapper<Dict>()
//                    .eq("parent_id", parentid)
//                    .eq("value",value));
//            return dict.getName();
//        }
    }

    //1
    @Override
    public List<Dict> findByDictCode(String dictCode) {
        //根据dictcode获取id
        Dict dict = this.getDictByDictCode(dictCode);

        //根据id获取子对象
        List<Dict> childList = this.findChildDdata(dict.getId());
        return childList;
    }

    //判断id是否有子数据
    private boolean hasChild(Long id){
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id",id);
        Integer count = baseMapper.selectCount(queryWrapper);

        return count>0;
    }

    private Dict getDictByDictCode(String dictCode) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("dict_code",dictCode);
        Dict codeDict = baseMapper.selectOne(wrapper);
        return codeDict;
    }

}
