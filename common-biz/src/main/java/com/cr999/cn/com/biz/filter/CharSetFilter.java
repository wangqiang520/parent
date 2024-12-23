package com.cr999.cn.com.biz.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @author 王强
 * 字符编码过滤器
 */
//@Component
//@WebFilter(filterName="CharSetFilter",urlPatterns="/*")
//@Order(2)//指定过过滤器的执行顺序，值越大越靠后执行
public class CharSetFilter implements Filter{

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("创建字符编码过滤器");
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		HttpServletRequest req=(HttpServletRequest) arg0;
		HttpServletResponse resp=(HttpServletResponse) arg1;
		
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		//请求旅行
		arg2.doFilter(req, resp);
	}
	
	@Override
	public void destroy() {
		System.out.println("销毁字符编码过滤器");
	}

}
