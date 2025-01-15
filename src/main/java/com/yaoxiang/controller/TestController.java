package com.yaoxiang.controller;

import com.alibaba.fastjson.JSON;
import com.yaoxiang.entity.PushTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {



    @RequestMapping("/v1")
    public Object userList(){
        return  "success";
    }


    @RequestMapping("/v2")
    public Object receive(@RequestBody PushTable obj){
        log.info("接收到的信息:{}", JSON.toJSONString(obj));
        return  "success";
    }
}
