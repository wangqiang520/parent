package com.cr999.cn.user.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cr999.cn.com.biz.componet.BaseContext;
import com.cr999.cn.vo.UserVo;
import com.cr999.cn.entity.User;
import com.cr999.cn.user.biz.mapper.UserMapper;
import com.cr999.cn.user.biz.service.TestService;
import com.cr999.cn.user.biz.service.TokenService;
import com.cr999.cn.user.biz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description TODO
 * @Author wangqiang
 * @Date 2021/11/28 0:29
 * @Version 1.0
 **/
@Service
public class TestServiceImpl extends ServiceImpl<UserMapper, User>  implements TestService {
    @Autowired
    HttpServletRequest request;
    @Autowired
    TokenService tokenService;

    @Autowired
    UserService userService;
    @Autowired
    BaseContext baseContext;

    @Override
    public String getUserName() {
        String userId = baseContext.getUserId();
        return userId;
    }

    @Override
    public void update(UserVo vo) {
        userService.update(vo);
    }

}
