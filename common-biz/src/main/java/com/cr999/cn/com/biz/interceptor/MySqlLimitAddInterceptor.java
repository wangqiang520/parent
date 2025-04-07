package com.cr999.cn.com.biz.interceptor;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

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
//@Component
public class MySqlLimitAddInterceptor implements Interceptor {

	@Value("${limit.count:0}")
	private long limitCount;

	@Value("${login.address}")
	private String loginAddress;

	//@Resource
	//ConfigurableApplicationContext configurableApplicationContext;

	private static final Logger logger = LoggerFactory.getLogger(MySqlLimitAddInterceptor.class);


	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		logger.info("进入MySqlLimitAddInterceptor");

		StatementHandler statementHandler=(StatementHandler)invocation.getTarget();
		BoundSql boundSql = statementHandler.getBoundSql();
		String originalSql = boundSql.getSql();
		originalSql=originalSql.toLowerCase();
		StringBuffer sqlBuffer=new StringBuffer(originalSql);

		boolean flag=false;
		//自动增加条件where deleted=0，
		if(originalSql.startsWith("select") || originalSql.startsWith("update")){
			int index = originalSql.lastIndexOf(" where ");
			if(index!=-1){
				sqlBuffer.append(" and deleted=0");
				flag=true;
			}else{
				sqlBuffer.append(" where deleted=0");
				flag=true;
			}
		}

		if(limitCount>0){
			if (originalSql.startsWith("select") && !originalSql.contains("limit") && !originalSql.contains("rownum")) {
				sqlBuffer.append(" limit ").append(limitCount);
				flag=true;
			}
		}

		if(flag){
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