package com.sjq.yygh.cmn.controller;

import com.sjq.yygh.cmn.service.DictService;
import com.sjq.yygh.common.result.Result;
import com.sjq.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Api("数据字典接口")
@RestController
@RequestMapping("/admin/cmn/dict")
@CrossOrigin
public class DictController {

    @Autowired
    private DictService dictService;

    //根据数据id查询数据列表
    @ApiOperation("根据数据id查询子数据列表")
    @GetMapping("findChildDdata/{id}")
    public Result findChildData(@PathVariable Long id){
        List<Dict> result = dictService.findChildDdata(id);
        return Result.ok(result);
    }

    //数据字典导出操作
    @GetMapping("exportData")
    public void exportDict(HttpServletResponse httpServletResponse){
        dictService.exportDict(httpServletResponse);
    }

    //数据字典导入操作
    @PostMapping("importData")
    public Result importDict(MultipartFile file) throws IOException {
        dictService.importDict(file);
        return Result.ok();
    }


    //根据dictcode和value查询
    @GetMapping("getName/{dictCode}/{value}")
    public String getName(@PathVariable String dictCode,
                          @PathVariable String value) {
        String dictName = dictService.getDictName(dictCode,value);
        return dictName;
    }

    //根据value查询
    @GetMapping("getName/{value}")
    public String getName(@PathVariable String value) {
        String dictName = dictService.getDictName("",value);
        return dictName;
    }



//    //根据value值进行查询
//    @GetMapping("getName/{value}")
//    public String getName(@PathVariable String value){
//        String dictname = dictService.getDictName("",value);
//        return dictname;
//    }
//
//    //根据dict_code和value值进行查询
//    @GetMapping("getName/{dictcode}/{value}")
//    public String getName(@PathVariable String dictcode,
//                          @PathVariable String value){
//
//        String dictname = dictService.getDictName(dictcode,value);
//
//        return dictname;
//    }

}
