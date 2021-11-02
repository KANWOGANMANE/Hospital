package com.sjq.yygh.cmn.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-cmn")
@Repository
public interface DictFeignClient {

    //根据value值进行查询
    @GetMapping("/admin/cmn/dict/getName/{value}")
    public String getName(@PathVariable("value") String value);

    //根据dict_code和value值进行查询
    @GetMapping("/admin/cmn/dict/getName/{dictcode}/{value}")
    public String getName(@PathVariable("dictcode") String dictcode, @PathVariable("value") String value);

}
