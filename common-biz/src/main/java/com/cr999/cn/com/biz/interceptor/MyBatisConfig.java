package com.cr999.cn.com.biz.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisConfig{

    @Bean
    public MySqlInterceptor mySqlInterceptor() {
        return new MySqlInterceptor();
    }

    @Bean
    public MySqlLimitAddInterceptor mySqlLimitAddInterceptor() {
        return new MySqlLimitAddInterceptor();
    }



}