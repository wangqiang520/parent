package com.cr999.cn.com.biz.interceptor;

import com.cr999.cn.com.biz.service.SystemParameterService;
import com.cr999.cn.common.ConstantEnum;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;


/**
 * @author wangqiang
 * 拦截器配置类
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    LoginInterceptor loginInterceptor;


    /**
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //排除不需要拦截的路径
        String value = ConstantEnum.EXCLUDE_PATH_PATTERNS.getValue();
        String[] split = StringUtils.split(value,",");
        List<String> strings = Arrays.asList(split);

        //登录拦截器
        registry.addInterceptor(loginInterceptor).excludePathPatterns(strings).order(0);



    }
}
