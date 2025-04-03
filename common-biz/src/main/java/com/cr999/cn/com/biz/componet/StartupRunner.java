package com.cr999.cn.com.biz.componet;

import com.cr999.cn.com.biz.interceptor.MySqlInterceptor;
import com.cr999.cn.com.biz.interceptor.MySqlLimitAddInterceptor;
import com.cr999.cn.com.biz.service.SystemParameterService;
import com.cr999.cn.com.biz.zookeeper.ZooKeeperUtils;
import com.cr999.cn.common.ConstantEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * <p>
 *  File Name: StartupRunner
 *  File Function Description: 服务启动完成后，初始化任务
 *  File Flow Description
 *  Version: V1.0
 * </p>
 *
 * @Author wangqiang
 *         <p>
 *         <li>Create Date：2025/3/26-16:20</li>
 *         <li>Revise Records</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>Revise Author: wangqiang </li>
 *         <li>Revise Date: 2025/3/26-16:20</li>
 *         <li>Revise Content: </li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
@Component
public class StartupRunner implements CommandLineRunner {
	private static final Logger logger = LoggerFactory.getLogger(StartupRunner.class);

	@Autowired
	SystemParameterService systemParameterService;

	@Autowired
	RedisUtil redisUtil;

	@Resource
	ConfigurableApplicationContext configurableApplicationContext;

	@Override
	public void run(String... args) throws Exception {
		logger.info("服务启动完成后，初始化任务开始");

		//系统参数保存到redis中
		initSystemParameter();
		//注册服务节点信息
		initZookeepe();
		logger.info("服务启动完成后，初始化任务结束");
	}


	/**
	* @Author 19075
	*         <p>
	*         <li>2025/4/3-10:49</li>
	*         <li>Function Description</li>
	*         CN:系统参数保存到redis中
	*         EN:
	*         <li>Flow Description</li>
	*         CN:
	*         EN:
	*         </p>
	* @param
	* @return
	**/
	private void initSystemParameter(){
		logger.info("初始化系统参数到redis开始");

		//手动删除数据，redis中的数据
		String systemParameterKey= ConstantEnum.SYSTEM_PARAMETER_KEY.getValue();
		redisUtil.del(systemParameterKey);

		systemParameterService.lstSystemParameter(null, null,0);
		//第一次启动时，limitCount默认值是0，代表sql不添加limit函数,后续再查询sql时，发现limitCount！=0就要添加limit函数，所以在此处赋值-1
		MySqlLimitAddInterceptor.limitCount=-1;
		logger.info("初始化系统参数到redis结束");
	}

	/**
	* @Author 19075
	*         <p>
	*         <li>2025/4/3-10:48</li>
	*         <li>Function Description</li>
	*         CN:注册服务节点信息
	*         EN:
	*         <li>Flow Description</li>
	*         CN:
	*         EN:
	*         </p>
	* @param
	* @return
	**/
	private void initZookeepe(){
		logger.info("初始化注册服务节点信息开始");
		String[] beanDefinitionNames = configurableApplicationContext.getBeanDefinitionNames();
		boolean isBeanExists = Arrays.stream(beanDefinitionNames).anyMatch(beanName ->beanName.equals("zooKeeperUtils"));
		if(isBeanExists){
			ZooKeeperUtils zooKeeperUtils=configurableApplicationContext.getBean("zooKeeperUtils",ZooKeeperUtils.class);
			zooKeeperUtils.registerService();
		}
		logger.info("初始化注册服务节点信息结束");
	}
}