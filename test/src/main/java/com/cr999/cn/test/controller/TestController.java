package com.cr999.cn.test.controller;

import com.cr999.cn.com.biz.exception.CustomException;
import com.cr999.cn.common.DataResponse;
import com.cr999.cn.common.enums.ResultEnum;
import com.cr999.cn.entity.User;
import com.cr999.cn.user.biz.service.TestService;
import com.cr999.cn.user.biz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文件描述：
 *
 * @version 1.0
 * @author wangqiang
 * @date 2021/4/1 23:15 
 */

@RestController
@RequestMapping("v1/test1")
public class TestController {
    @Autowired
    TestService testService;
    @Autowired
    UserService userService;
    //@Autowired
    //ZooKeeperUtils zooKeeperUtils;
    @Autowired
    Environment environment;


    @GetMapping("/test1")
    public void test1(){
        throw new CustomException("sss");
    }

    @GetMapping("/test2")
    public void test2(){
        int a=1/0;
    }

    @GetMapping("/test3")
    public DataResponse test3(){
        return new DataResponse<>(ResultEnum.SUCCESS.getCode(),ResultEnum.SUCCESS.getMsg());
    }
    @GetMapping("/test4")
    @Transactional(rollbackFor = Exception.class)
    public void test4(){
        User byId = userService.getById("4");
        byId.setNickName("梅4");
        try {
            userService.updateById(byId);
            System.out.println(1/0);
        }catch (Exception e){

            System.out.println("修改成功");
        }
    }

    @GetMapping("/test5")
    public void test5() {
       // String s = zooKeeperUtils.get("/services", "192.168.208.1:8082");
      //  String port=environment.getProperty("local.server.port");
        //System.out.println("zookeeper value"+s);
        //zooKeeperUtils.createNode("/services/t1","1234","值1234", CreateMode.EPHEMERAL);
    }



}
