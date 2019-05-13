package com.cloudfire.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.cloudfire.dao.NFCDao;
import com.cloudfire.dao.impl.NFCDaoImpl;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.until.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.xwork2.ActionSupport;

public class NFCAction extends ActionSupport implements 
ServletRequestAware,ServletResponseAware{
	private static final long serialVersionUID = -9035309455443237581L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private NFCDao nfcdao;
	
	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}
	
	public void ifNFCExist(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			
			String uid=this.request.getParameter("uid");
			Object result = null;
			int exist=1;
			nfcdao=new NFCDaoImpl();
			exist=nfcdao.isExited(uid);
			result=exist;
			ObjectMapper mapper = new ObjectMapper();  
	        String json = mapper.writeValueAsString(result); 
	        this.response.getWriter().write(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
