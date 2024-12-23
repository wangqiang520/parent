package com.cr999.cn.com.biz.interceptor;

import com.cr999.cn.com.biz.componet.RedisUtil;
import com.cr999.cn.com.biz.componet.TokenUtil;
import com.cr999.cn.com.biz.exception.CustomException;
import com.cr999.cn.common.ConstantEnum;
import com.cr999.cn.common.ResultEnum;
import com.cr999.cn.common.vo.UserBaseVo;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @author 王强
 * 登录过滤器
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {
    private Logger logger= LoggerFactory.getLogger(LoginInterceptor.class);

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private TokenUtil tokenUtil;
    @Value("${login.address}")
    private String loginAddress;

    /**
     * 调用目标方法之前被调用
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String servletPath = request.getServletPath();
        String token = request.getHeader(ConstantEnum.USER_TOKEN_PREFIX.getValue());

        /*String excludePathPatternsValue = ConstantEnum.EXCLUDE_PATH_PATTERNS.getValue();
        String[] split = StringUtils.split(excludePathPatternsValue,",");
        boolean contains = Arrays.asList(split).contains(servletPath);
        if(contains){
            return true;
        }*/
        if(StringUtils.isBlank(token)){
            logger.error("拦截到的路径："+servletPath);
            throw new CustomException(ResultEnum.TOKEN_NULL_ERROR.getMsg(),ResultEnum.TOKEN_NULL_ERROR.getCode());
        }
        //如果token不存在，说明已经不能重新刷新token，可能token已经过期，或者是通过退出当前登录用户，删除了token
        boolean isToken = redisUtil.hasKey(ConstantEnum.USER_TOKEN_PREFIX_.getValue() + token);
        if(!isToken){
            //todo
            logger.warn("重定向到："+loginAddress);
            response.sendRedirect(loginAddress);
            return false;
            //throw new CustomException(ResultEnum.TOKEN_EXPIRED_ERROR.getMsg(),ResultEnum.TOKEN_EXPIRED_ERROR.getCode());
        }
        //1、判断token是否已经过期，
        if(tokenUtil.checkToken(token)){
            return true;
        }
        return false;
    }

    /**
     * 调用目标方法之后被调用，如果有异常则不调用
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * 调用目标访方法之后被调用，有没有异常都会被调用,Exception 参数里放着异常信息
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
