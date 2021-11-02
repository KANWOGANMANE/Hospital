package com.sjq.yygh.cmn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sjq.yygh.common.result.Result;
import com.sjq.yygh.model.cmn.Dict;
import com.sjq.yygh.model.hosp.HospitalSet;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


public interface DictService extends IService<Dict> {

    List<Dict> findChildDdata(Long id);

    void exportDict(HttpServletResponse httpServletResponse);

    void importDict(MultipartFile multipartFile) throws IOException;

    String getDictName(String dictCode, String value);
}
