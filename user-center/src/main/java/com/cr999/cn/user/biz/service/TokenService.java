package com.cr999.cn.user.biz.service;

import com.cr999.cn.entity.User;

import java.util.Map;

/**
 * 文件描述：
 *
 * @version 1.0
 * @author wangqiang
 * @date 2021/9/28 17:46
 */
public interface TokenService {

    /**
     * 生成token
     * @param data 数据
     * @param expirDate 过期时间
     * @return
     */
    String createToken(String data,String expirDate,String userId);
    String createToken2(User user);

    /**
     * 校验token
     * @param token
     */
    boolean checkToken(String token);
    boolean checkToken2(String token);


    /**
     * 刷新token
     * @param token
     * @return
     */
    String refreshToken(String token);
    String refreshToken2(String token,String account);

    Map<String,Object> getTokenContent(String token);
    User getTokenContent2(String token);

}
