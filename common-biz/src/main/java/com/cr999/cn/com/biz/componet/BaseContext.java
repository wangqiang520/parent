package com.cr999.cn.com.biz.componet;

import com.alibaba.fastjson.JSONObject;
import com.cr999.cn.com.biz.exception.CustomException;
import com.cr999.cn.common.ConstantEnum;
import com.cr999.cn.common.ResultEnum;
import com.cr999.cn.common.vo.UserBaseVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description TODO
 * @Author wangqiang
 * @Date 2021/11/28 2:03
 * @Version 1.0
 **/
@Component
public class BaseContext {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private TokenUtil tokenUtil;
    String prefix = ConstantEnum.USER_TOKEN_PREFIX.getValue();

    /**
     * 获取当前登录的token
     * @return
     */
    public String getToken(){
        return request.getHeader(prefix);
    }

    /**
     * 获取当前登录的用户id
     * @return
     */
    public String getUserId(){
        String token = getToken();
        String data = tokenUtil.getTokenContent(token);
        if(StringUtils.isBlank(data)){
            throw new CustomException(ResultEnum.GET_CURRENT_LOIN_PERSON_ERROR.getMsg(),ResultEnum.GET_CURRENT_LOIN_PERSON_ERROR.getCode());
        }
        UserBaseVo userBaseVo = JSONObject.parseObject(data, UserBaseVo.class);
        if(userBaseVo==null){
            throw new CustomException(ResultEnum.GET_CURRENT_LOIN_PERSON_ERROR.getMsg(),ResultEnum.GET_CURRENT_LOIN_PERSON_ERROR.getCode());
        }
        return userBaseVo.getId();
    }
}
