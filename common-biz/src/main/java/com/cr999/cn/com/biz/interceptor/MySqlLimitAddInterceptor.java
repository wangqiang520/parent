package com.cr999.cn.com.biz.interceptor;

import com.cr999.cn.com.biz.service.SystemParameterService;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Properties;

/**
 * <p>
 *  File Name: MySqlLimitAddInterceptor
 *  File Function Description sql添加Limit
 *  File Flow Description
 *  Version: V1.0
 * </p>
 *
 * @Author wangqiang
 *         <p>
 *         <li>Create Date：2025/4/1-14:40</li>
 *         <li>Revise Records</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>Revise Author: wangqiang </li>
 *         <li>Revise Date: 2025/4/1-14:40</li>
 *         <li>Revise Content: </li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
@Intercepts({
		@Signature(type =StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class MySqlLimitAddInterceptor implements Interceptor {

	public static long limitCount=0;

	@Resource
	ConfigurableApplicationContext configurableApplicationContext;

	private static final Logger logger = LoggerFactory.getLogger(MySqlLimitAddInterceptor.class);


	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		logger.info("进入MySqlLimitAddInterceptor");
		if(limitCount==0){
			//默认值是0，代表不添加limit函数，如果是-1，代表StartupRunner.run方法里面修改了此值，此时需要去查询系统参数表，获取参数值
			return invocation.proceed();
		}

		StatementHandler statementHandler=(StatementHandler)invocation.getTarget();
		BoundSql boundSql = statementHandler.getBoundSql();
		String originalSql = boundSql.getSql();
		originalSql=originalSql.toLowerCase();
		StringBuffer sqlBuffer=new StringBuffer(originalSql);

		//自动增加条件where deleted=0，
		if(originalSql.startsWith("select") || originalSql.startsWith("update")){
			String whereSuffix=originalSql.substring(originalSql.lastIndexOf("where"));
			if(whereSuffix.contains("where")){
				if(!whereSuffix.contains("deleted=")){
					sqlBuffer.append(" and deleted=0");
				}
			}else{
				sqlBuffer.append(" where deleted=0");
			}
		}

		if (originalSql.startsWith("select") && !originalSql.contains("limit") && !originalSql.contains("rownum")) {
			SystemParameterService systemParameterService= (SystemParameterService) configurableApplicationContext.getBean("systemParameterServiceImpl");
			String limit_count = systemParameterService.getParmValue("limit_count", "*");
			limitCount=Integer.valueOf(limit_count);
			sqlBuffer.append(" limit ").append(limitCount);

			Field field = boundSql.getClass().getDeclaredField("sql");
			field.setAccessible(true);
			field.set(boundSql,sqlBuffer.toString());
		}
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {

	}
}