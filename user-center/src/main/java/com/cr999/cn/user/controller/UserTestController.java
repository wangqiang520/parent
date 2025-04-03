package com.cr999.cn.user.controller;

import com.cr999.cn.com.biz.annotation.ExtTransactional;
import com.cr999.cn.com.biz.interceptor.MySqlLimitAddInterceptor;
import com.cr999.cn.com.biz.service.SystemParameterService;
import com.cr999.cn.entity.User;
import com.cr999.cn.user.biz.mapper.UserMapper;
import com.cr999.cn.vo.UserVo;
import com.cr999.cn.entity.SystemParameter;
import com.cr999.cn.user.biz.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Author wangqiang
 * @Date 2021/11/28 0:33
 * @Version 1.0
 **/


@RestController
@RequestMapping("/v1/test")
public class UserTestController {

    @Autowired
    TestService testService;

    @Autowired
    SystemParameterService systemParameterService;

    @PostMapping("/getUserName")
    public String getUserName(){
        return testService.getUserName();
    }

    @PostMapping("/getUserName1")
    public String getUserName1(){
        return testService.getUserName();
    }

    @PostMapping("/updateUser")
    @ExtTransactional
    public String updateUser(@RequestBody UserVo vo){
        testService.update(vo);
        return "11111";
    }

    @PostMapping("/lstSystemParameter")
    public List<SystemParameter> lstSystemParameter(String mainKey,String subKey){
       // systemParameterService.addUpdateSystemParameter(null);
        return systemParameterService.lstSystemParameter(mainKey,subKey,0);
    }
    private static final Logger logger = LoggerFactory.getLogger(MySqlLimitAddInterceptor.class);

    @Autowired
    UserMapper userMapper;
    @GetMapping("/abc")
    public User abc(String id){
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("column1","1");
        map.put("column2","wangqiang");


        long startTime = System.currentTimeMillis();
        User abc = userMapper.abc(map);
        long endTime = System.currentTimeMillis();
        logger.info("\n mapper执行SQL耗时：{}ms \n 执行SQL：{}",endTime-startTime);
        return abc;
    }


}
