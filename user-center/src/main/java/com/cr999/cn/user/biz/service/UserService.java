package com.cr999.cn.user.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cr999.cn.vo.UserBaseVo;
import com.cr999.cn.vo.UserVo;
import com.cr999.cn.entity.User;

import java.util.List;

/**
 * 文件描述：
 *
 * @version 1.0
 * @author wangqiang
 * @date 2021/4/5 0:16
 */
public interface UserService extends IService<User> {

    /**
     * 查询用户list
     * @param userVo
     * @return
     */

    List<User> getUserList(UserVo userVo);

    /**
     * 用户注册
     * @param vo
     */
    User register(UserVo vo);

    /**
     * 用户登录
     * @param vo
     * @return
     */
    String login(UserVo vo);

    /**
     * 用户注销
     */
    void logout();

    /**
     * 修改用户信息
     */
    void update(UserVo vo);

    /**
     * 当前登录信息
     * @return
     */
    UserBaseVo currentLogin();

}
