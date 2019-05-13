package com.cloudfire.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.cloudfire.dao.DvrInfoDao;
import com.cloudfire.dao.impl.DvrInfoDaoImpl;
import com.cloudfire.entity.DvrInfo;
import com.cloudfire.entity.HttpRsult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.xwork2.ActionSupport;

public class DvrInfoAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{
	private static final long serialVersionUID = -4905088036147092147L;
	private DvrInfoDao mDvrInfoDao;
	private HttpServletRequest request;
	private HttpServletResponse response;

	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}

	public void getDvrInfo(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			mDvrInfoDao = new DvrInfoDaoImpl();
			DvrInfo mDvrInfo = mDvrInfoDao.getDvrInfo();
			HttpRsult hr = null;
			Object result = null;
			if(mDvrInfo==null){
				hr = new HttpRsult();
				hr.setError("获取失败");
				hr.setErrorCode(2);
				result = hr;
			}else{
				result = mDvrInfo;
			}
			ObjectMapper mapper = new ObjectMapper();  
	        String json = mapper.writeValueAsString(result); 
	        this.response.getWriter().write(json);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void getDvrInfoByConstructionId(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			int constructionId = Integer.parseInt(this.request.getParameter("constructionId"));
			mDvrInfoDao = new DvrInfoDaoImpl();
			DvrInfo mDvrInfo = mDvrInfoDao.getDvrInfoByConstructionId(constructionId);
			HttpRsult hr = null;
			Object result = null;
			if(mDvrInfo==null){
				hr = new HttpRsult();
				hr.setError("获取失败");
				hr.setErrorCode(2);
				result = hr;
			}else{
				result = mDvrInfo;
			}
			ObjectMapper mapper = new ObjectMapper();  
	        String json = mapper.writeValueAsString(result); 
	        this.response.getWriter().write(json);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
