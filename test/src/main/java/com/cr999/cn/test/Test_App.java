package com.cr999.cn.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 文件描述：实现CommandLineRunner接口，把要执行的代码放入到run里，即可在启动后执行
 *
 * @version 1.0
 * @author wangqiang
 * @date 2021/4/1 23:13 
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class},scanBasePackages = {"com.cr999.cn.*"})
public class Test_App implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(Test_App.class);
   /*@Resource
    ConfigurableApplicationContext configurableApplicationContext;*/

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(Test_App.class, args);
        System.out.println("项目启动成功");
    }



    @Override
    public void run(String... args) throws Exception {
        /*String[] beanDefinitionNames = configurableApplicationContext.getBeanDefinitionNames();
        boolean isBeanExists = Arrays.stream(beanDefinitionNames).anyMatch(beanName ->beanName.equals("zooKeeperUtils"));
        if(isBeanExists){
            configurableApplicationContext.getBean(ZooKeeperUtils.class);
            ZooKeeperUtils zooKeeperUtils = configurableApplicationContext.getBean("zooKeeperUtils",ZooKeeperUtils.class);
            zooKeeperUtils.registerService();
        }*/

    }
}
