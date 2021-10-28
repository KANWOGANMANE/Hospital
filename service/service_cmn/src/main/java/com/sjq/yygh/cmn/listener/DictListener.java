package com.sjq.yygh.cmn.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.sjq.yygh.cmn.mapper.DictMapper;
import com.sjq.yygh.model.cmn.Dict;
import com.sjq.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;

public class DictListener extends AnalysisEventListener<DictEeVo> {

    private DictMapper dictMapper;

    public DictListener(DictMapper dictMapper){
        this.dictMapper=dictMapper;
    }

    //每行读取
    @Override
    public void invoke(DictEeVo data, AnalysisContext context) {

        Dict dict = new Dict();
        BeanUtils.copyProperties(data,dict);
        dictMapper.insert(dict);

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }
}
