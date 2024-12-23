package com.cr999.cn.com.biz.zookeeper;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Data
@Component
@ConfigurationProperties(prefix = "curator")
@ConditionalOnProperty(name = "curator.enable", havingValue = "true")//条件依赖，当name为havingValue时，创建bean实例
public class CuratorConfig {
    private static final Logger logger = LoggerFactory.getLogger(CuratorConfig.class);

    //@Value("${curator.retryCount}")
    private int retryCount;
    private int elapsedTimeMs;
    private String connectionString;
    private int sessionTimeoutMs;
    private int connectionTimeoutMs;
    private boolean enable;
    @Autowired
    private Environment environment;

    @Bean
    public CuratorFramework curatorFramework() {
        if(enable){
            CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(connectionString, sessionTimeoutMs, connectionTimeoutMs, new RetryNTimes(retryCount, elapsedTimeMs));
            curatorFramework.start();
            logger.info("zookeeper 连接成功");
            return curatorFramework;
        }
        return null;
    };

    @Bean
    public ZooKeeperUtils zooKeeperTemplate(CuratorFramework client){
        if(enable){
            ZooKeeperUtils zooKeeperUtils = new ZooKeeperUtils(client);
            return zooKeeperUtils;
        }
       return null;
    }




}
