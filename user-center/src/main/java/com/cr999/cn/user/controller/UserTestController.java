package com.cr999.cn.user.controller;

import com.cr999.cn.com.biz.annotation.ExtTransactional;
import com.cr999.cn.common.vo.UserVo;
import com.cr999.cn.user.biz.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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


}
