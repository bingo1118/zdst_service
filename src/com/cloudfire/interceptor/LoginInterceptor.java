package com.cloudfire.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class LoginInterceptor extends HandlerInterceptorAdapter{

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		String userId = (String) request.getSession().getAttribute("userId");
		if (userId == null ){
			response.sendRedirect(request.getContextPath()+java.io.File.separator+"index.jsp");
			return false;
		} 
		return super.preHandle(request, response, handler);
	}
}
