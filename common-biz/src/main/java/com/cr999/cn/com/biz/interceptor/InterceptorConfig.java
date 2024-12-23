package com.cr999.cn.com.biz.interceptor;

import com.cr999.cn.common.ConstantEnum;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
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
        //登录拦截器
        InterceptorRegistration interceptorRegistration = registry.addInterceptor(loginInterceptor);
        //排除不需要拦截的路径
        String value = ConstantEnum.EXCLUDE_PATH_PATTERNS.getValue();
        String[] split = StringUtils.split(value,",");
        List<String> strings = Arrays.asList(split);
        interceptorRegistration.excludePathPatterns(strings);
        ///指定拦截器的执行顺序，值越大越靠后执行
        interceptorRegistration.order(0);
    }
}
