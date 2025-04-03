package com.cr999.cn.com.biz.interceptor;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;

/**
 * <p>
 *  File Name: MySqlInterceptor
 *  File Function Description 打印sql脚本，sql执行时间（可在此处登记慢sql语句）
 *  File Flow Description
 *  Version: V1.0
 * </p>
 *
 * @Author wangqiang
 *         <p>
 *         <li>Create Date：2025/3/31-17:23</li>
 *         <li>Revise Records</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>Revise Author: wangqiang </li>
 *         <li>Revise Date: 2025/3/31-17:23</li>
 *         <li>Revise Content: </li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
@Intercepts({
		@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
		@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})

public class MySqlInterceptor implements Interceptor {
	private static final Logger logger = LoggerFactory.getLogger(MySqlInterceptor.class);

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		logger.info("进入MySqlInterceptor");
		long startTime = System.currentTimeMillis();
		Object proceed = invocation.proceed();
		long endTime = System.currentTimeMillis();

		String printSql=null;
		try {
			// 通过generateSql方法拿到最终生成的SQL
			printSql = generateSql(invocation);
		}catch (Exception e){
			e.printStackTrace();
			logger.error("获取sql异常",e);
		}finally {
			// 拼接日志打印过程
			long costTime = endTime - startTime;
			logger.info("\n 执行SQL耗时：{}ms \n 执行SQL：{}",costTime,printSql);
		}
		return proceed;
	}

	private static String generateSql(Invocation invocation){
		// 获取到BoundSql以及Configuration对象
		// BoundSql 对象存储了一条具体的 SQL 语句及其相关参数信息。
		// Configuration 对象保存了 MyBatis 框架运行时所有的配置信息
		MappedStatement statement = (MappedStatement) invocation.getArgs()[0];

		Configuration configuration = statement.getConfiguration();
		BoundSql boundSql = statement.getBoundSql(invocation.getArgs().length>1?invocation.getArgs()[1]:null);

		// 获取参数对象
		Object parameterObject = boundSql.getParameterObject();
		// 获取参数映射
		List<ParameterMapping> params = boundSql.getParameterMappings();
		// 获取到执行的SQL
		String sql = boundSql.getSql();
		// SQL中多个空格使用一个空格代替
		sql = sql.replaceAll("[\\s]+", " ");
		if (!ObjectUtils.isEmpty(params) && !ObjectUtils.isEmpty(parameterObject)){
			// TypeHandlerRegistry 是 MyBatis 用来管理 TypeHandler 的注册器。TypeHandler 用于在 Java 类型和 JDBC 类型之间进行转换
			TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
			// 如果参数对象的类型有对应的 TypeHandler，则使用 TypeHandler 进行处理
			if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())){
				sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(parameterObject)));
			}else {
				// 否则，逐个处理参数映射
				for (ParameterMapping param : params) {
					// 获取参数的属性名
					String propertyName = param.getProperty();
					MetaObject metaObject = configuration.newMetaObject(parameterObject);
					// 检查对象中是否存在该属性的 getter 方法，如果存在就取出来进行替换
					if (metaObject.hasGetter(propertyName)){
						Object obj = metaObject.getValue(propertyName);
						sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
						// 检查 BoundSql 对象中是否存在附加参数。附加参数可能是在动态 SQL 处理中生成的，有的话就进行替换
					}else if (boundSql.hasAdditionalParameter(propertyName)){
						Object obj = boundSql.getAdditionalParameter(propertyName);
						sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
					}else {
						// 如果都没有，说明SQL匹配不上，带上“缺失”方便找问题
						sql = sql.replaceFirst("\\?", "缺失");
					}
				}
			}
		}
		return sql+";";
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