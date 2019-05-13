package com.cloudfire.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import com.cloudfire.dao.NeedLossSmokeDao;
import com.cloudfire.dao.impl.NeedLossSmokeDaoImpl;
import com.cloudfire.entity.AllSmokeEntity;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.until.Utils;
import com.opensymphony.xwork2.ActionSupport;

public class NeedLossSmokeAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{
	
	private static final long serialVersionUID = 4110157918677908234L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private NeedLossSmokeDao mNeedLossSmokeDao;
	
	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}

	public void getNeedLossSmoke(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String areaId = this.request.getParameter("areaId");
			String page = this.request.getParameter("page");
			String placeTypeId = this.request.getParameter("placeTypeId");
			String appId = this.request.getParameter("appId");		//add by lzo at 2017-5-25
			String parentId = this.request.getParameter("parentId");//@@父区域ID	
			HttpRsult hr = null;
			Object result = null;
			AllSmokeEntity ase = null;
			if(!Utils.isNullStr(userId)||!Utils.isNumeric(privilege)){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mNeedLossSmokeDao = new NeedLossSmokeDaoImpl();
//				ase = mNeedLossSmokeDao.getNeedLossSmoke(userId, privilege, areaId, page, placeTypeId);
				ase = mNeedLossSmokeDao.getNeedLossSmoke(userId, privilege, parentId,areaId, page, placeTypeId,appId);	//update by lzo at 2017-5-25
				if(ase==null){
					hr = new HttpRsult();
					hr.setError("获取烟感失败");
					hr.setErrorCode(2);
					result = hr;
				}else{
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
