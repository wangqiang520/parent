package com.cr999.cn.user.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cr999.cn.vo.UserVo;
import com.cr999.cn.entity.User;

public interface TestService extends IService<User> {
    String getUserName();
    void update(UserVo vo);
}
