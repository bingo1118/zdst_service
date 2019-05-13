package com.cloudfire.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.entity.AreaIdEntity;
import com.cloudfire.entity.HttpRsult;
import com.opensymphony.xwork2.ActionSupport;

public class AreaIdAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{
	
	private static final long serialVersionUID = -8059072481196956604L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private AreaDao mAreaDao;
	
	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}
	
	public void getAreaId(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String page = this.request.getParameter("page");
			AreaIdEntity aet = null;
			HttpRsult hr = null;
			Object result = null;
			if(userId==null||privilege==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mAreaDao = new AreaDaoImpl();
				aet = mAreaDao.getAreaByUserId(userId, privilege, page);
				if(aet!=null){
					aet.setError("获取区域id成功");
					aet.setErrorCode(0);
					result = aet;
				}else{
					hr = new HttpRsult();
					hr.setError("获取区域id失败");
					hr.setErrorCode(2);
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
	
	public void getDXAreaId(){ //对接电信管控平台获取areaId
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String page = this.request.getParameter("page");
			AreaIdEntity aet = null;
			HttpRsult hr = null;
			Object result = null;
			if(userId==null||privilege==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mAreaDao = new AreaDaoImpl();
				aet = mAreaDao.getDXAreaId(userId, privilege, page);
				if(aet!=null){
					aet.setError("获取区域id成功");
					aet.setErrorCode(0);
					result = aet;
				}else{
					hr = new HttpRsult();
					hr.setError("获取区域id失败");
					hr.setErrorCode(2);
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
