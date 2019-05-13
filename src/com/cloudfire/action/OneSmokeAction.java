package com.cloudfire.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import com.cloudfire.dao.OneSmokeDao;
import com.cloudfire.dao.impl.OneSmokeDaoImpl;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.OneSmokeEntity;
import com.opensymphony.xwork2.ActionSupport;

public class OneSmokeAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{

	private static final long serialVersionUID = -3034902798029598545L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private OneSmokeDao mOneSmokeDao;
	
	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}

	public void getOneSmoke(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String smokeMac  = this.request.getParameter("smokeMac");
			OneSmokeEntity ose = null;
			HttpRsult hr = null;
			Object result = null;
			if(userId==null||privilege==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mOneSmokeDao = new OneSmokeDaoImpl();
				ose = mOneSmokeDao.getOneSmoke(userId, smokeMac, privilege);
				if(ose==null){
					hr = new HttpRsult();
					hr.setError("获取烟感失败");
					hr.setErrorCode(2);
					result = hr;
				}else{
					ose.setError("获取烟感成功");
					ose.setErrorCode(0);
					result = ose;
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
