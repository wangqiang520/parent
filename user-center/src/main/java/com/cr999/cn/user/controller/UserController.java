package com.cr999.cn.user.controller;

import com.cr999.cn.common.DataResponse;
import com.cr999.cn.common.enums.ResultEnum;
import com.cr999.cn.vo.UserBaseVo;
import com.cr999.cn.vo.UserVo;
import com.cr999.cn.entity.User;
import com.cr999.cn.user.biz.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 文件描述：
 * 用户控制层
 * @version 1.0
 * @author wangqiang
 * @date 2021/4/5 0:23
 */

@Api(value ="用户控制层")
@RestController
@RequestMapping("/v1/user")
public class UserController {

    @Autowired
    UserService userService;

    @ApiOperation(value = "用户注册", notes = "用户注册", httpMethod = "POST")
    @PostMapping("/register")
    public DataResponse register(@Valid @RequestBody UserVo vo, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            StringBuilder msg = new StringBuilder();
            List<ObjectError> errList = bindingResult.getAllErrors();
            errList.forEach(err -> {
                msg.append(err.getDefaultMessage());
                msg.append(",");
            });
            return new DataResponse(ResultEnum.PARAMETER_VALID_ERROR.getCode(),
                    ResultEnum.PARAMETER_VALID_ERROR.getMsg().replaceFirst("%s", msg.toString()));
        }
        User user = userService.register(vo);
        return new DataResponse(ResultEnum.SUCCESS,user);
    }

    @ApiOperation(value = "登录", notes = "登录", httpMethod = "POST")
    @PostMapping("/login")
    public DataResponse login(@RequestBody UserVo userVo){
        String login = userService.login(userVo);
        return new DataResponse(ResultEnum.SUCCESS,login);
    }

    @ApiOperation(value = "用户注销", notes = "用户注销", httpMethod = "GET")
    @GetMapping("/logout")
    public DataResponse logout(){
        userService.logout();
        return new DataResponse(ResultEnum.SUCCESS);
    }

    @ApiOperation(value = "查询用户祥细信息", notes = "查询用户祥细信息", httpMethod = "GET")
    @GetMapping("/getUser/{userId}")
    public DataResponse getUserById(@PathVariable("userId") String userId){
        User result = userService.getById(userId);
        return new DataResponse(ResultEnum.SUCCESS,result);
    }

    @PostMapping("/getUserList")
    public DataResponse getUserList(@RequestBody UserVo vo) {
        List<User> userList = userService.getUserList(vo);
        return new DataResponse(ResultEnum.SUCCESS,userList);
    }
    @PostMapping("/updateUser")
    public DataResponse updateUser(@RequestBody UserVo vo) {
        userService.update(vo);
        return new DataResponse(ResultEnum.SUCCESS);
    }

    @ApiOperation(value = "获取当前登录用户", notes = "获取当前登录用户", httpMethod = "GET")
    @GetMapping("/currentLogin")
    public DataResponse currentLogin(){
        UserBaseVo userBaseVo = userService.currentLogin();
        return new DataResponse(ResultEnum.SUCCESS,userBaseVo);
    }

}
