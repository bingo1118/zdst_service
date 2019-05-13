package com.cloudfire.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import com.cloudfire.dao.BindUserIdIosDao;
import com.cloudfire.dao.impl.BindUserIdIosDaoImpl;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.until.Utils;
import com.opensymphony.xwork2.ActionSupport;

public class BindUserIdIosAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{
	
	private static final long serialVersionUID = -4581514309685063419L;
	private final static Log log = LogFactory.getLog(AddSmokeAction.class);
	private HttpServletRequest request;
	private HttpServletResponse response;
	private BindUserIdIosDao mBindUserIdIosDao;
	
	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}

	public void bindUserIdIos(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String ios = this.request.getParameter("ios");
			HttpRsult hr = null;
			Object result = null;
			if(!Utils.isNullStr(userId)||!Utils.isNullStr(ios)){
				hr = new HttpRsult();
				hr.setError("²ÎÊý´íÎó");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mBindUserIdIosDao = new BindUserIdIosDaoImpl();
				hr = mBindUserIdIosDao.bindUserIdIos(userId, ios);
				if(hr==null){
					hr = new HttpRsult();
					hr.setError("Ìí¼ÓÊ§°Ü");
					hr.setErrorCode(2);
					result = hr;
				}else{
					result = hr;
				}
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
