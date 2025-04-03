package com.cr999.cn.com.biz.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.override.PageMapperMethod;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.cr999.cn.com.biz.service.SystemParameterService;
import com.cr999.cn.common.ConstantEnum;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.*;
import java.util.regex.Matcher;

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

	//@Value("${limit.count}")
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
		String sql=originalSql.toLowerCase();
		String modifiedSql=originalSql;
		if (sql.startsWith("select") && !sql.contains("limit") && !sql.contains("rownum")) {
			SystemParameterService systemParameterService= (SystemParameterService) configurableApplicationContext.getBean("systemParameterServiceImpl");
			String limit_count = systemParameterService.getParmValue("limit_count", "*");
			limitCount=Integer.valueOf(limit_count);

			modifiedSql=originalSql+" limit "+ limitCount;

			Field field = boundSql.getClass().getDeclaredField("sql");
			field.setAccessible(true);
			field.set(boundSql,modifiedSql);
		}
		return invocation.proceed();
	}
	public void aa(Invocation invocation){
		StatementHandler statementHandler=(StatementHandler)invocation.getTarget();
		BoundSql boundSql = statementHandler.getBoundSql();
		String originalSql = boundSql.getSql();

		MapperMethod.ParamMap parameterObject = (MapperMethod.ParamMap) boundSql.getParameterObject();
		QueryWrapper queryWrapper = (QueryWrapper) parameterObject.get("param1");
		Map paramNameValuePairsMap = queryWrapper.getParamNameValuePairs();
		List<ParameterMapping> params = boundSql.getParameterMappings();
		for(ParameterMapping param:params){
			String property = param.getProperty();
			property=property.substring(property.lastIndexOf(".")+1);
			Object obj = paramNameValuePairsMap.get(property);
			originalSql = originalSql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
		}

		System.out.println(originalSql);
	}


	private static String getParameterValue(Object object) {
		String value = "";
		if (object instanceof String){
			value = "'" + object.toString() + "'";
		}else if (object instanceof Date){
			DateFormat format = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
			value = "'" + format.format((Date) object) + "'";
		} else if (!ObjectUtils.isEmpty(object)) {
			value = object.toString();
		}
		return value;
	}
	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {

	}
}