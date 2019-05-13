package com.cloudfire.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import com.cloudfire.dao.AllSmokeDao;
import com.cloudfire.dao.impl.AllSmokeDaoImpl;
import com.cloudfire.entity.AllSmokeEntity;
import com.cloudfire.entity.HttpRsult;
import com.opensymphony.xwork2.ActionSupport;

public class GetSmokeBySearchAction extends ActionSupport implements ServletRequestAware,ServletResponseAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2367450445676491229L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private AllSmokeDao mAllSmokeDao;

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub
		this.response=arg0;
	}

	@Override
	public void setServletRequest(HttpServletRequest arg0) {
		// TODO Auto-generated method stub
		this.request=arg0;
	}
	
	public void getSmokeBySearch() {
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String search  = this.request.getParameter("search");
			AllSmokeEntity ase = null;
			HttpRsult hr = null;
			Object result = null;
			if(userId==null||privilege==null||search==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mAllSmokeDao = new AllSmokeDaoImpl();
				ase=mAllSmokeDao.getSuperAllSmokeBySearch(userId, privilege, search);
				if(ase==null){
					hr = new HttpRsult();
					hr.setError("没有烟感");
					hr.setErrorCode(2);
					result = hr;
				}else{
					ase.setError("获取烟感成功");
					ase.setErrorCode(0);
					result = ase;
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
