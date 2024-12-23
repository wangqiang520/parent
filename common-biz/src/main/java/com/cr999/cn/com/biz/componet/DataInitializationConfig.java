package com.cr999.cn.com.biz.componet;

import com.cr999.cn.com.biz.zookeeper.ZooKeeperUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @author wangqiang
 * @version 1.0.0
 * @ClassName DataInitializationConfig.java
 * @Description 项目启动初始化数据
 * @createTime 2023年03月19日 15:23
 */
@Configuration
@Slf4j
public class DataInitializationConfig {

    @Resource
    ConfigurableApplicationContext configurableApplicationContext;

    /**
     * 向zookeeper注册服务
     */
    @PostConstruct
    public void init(){
        String[] beanDefinitionNames = configurableApplicationContext.getBeanDefinitionNames();
        boolean isBeanExists = Arrays.stream(beanDefinitionNames).anyMatch(beanName ->beanName.equals("zooKeeperUtils"));
        if(isBeanExists){
            ZooKeeperUtils zooKeeperUtils=configurableApplicationContext.getBean("zooKeeperUtils",ZooKeeperUtils.class);
            zooKeeperUtils.registerService();
        }
    }
}
