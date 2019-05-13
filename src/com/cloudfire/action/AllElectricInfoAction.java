package com.cloudfire.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import com.cloudfire.dao.AllElectricInfoDao;
import com.cloudfire.dao.impl.AllElectricInfoDaoImpl;
import com.cloudfire.entity.AllElectricInfoEntity;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.until.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.xwork2.ActionSupport;

public class AllElectricInfoAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{
	
	private static final long serialVersionUID = -3411869640784117792L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private AllElectricInfoDao mAllElectricInfoDao;
	
	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}
	
	public void getAllElectricInfo(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String page = this.request.getParameter("page");
			HttpRsult hr = null;
			Object result = null;
			AllElectricInfoEntity<?> aeie = null;
			if(!Utils.isNullStr(userId)||!Utils.isNullStr(privilege)){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mAllElectricInfoDao = new AllElectricInfoDaoImpl();
				aeie = mAllElectricInfoDao.getAllElectricInfo(userId, privilege, page);
				if(aeie==null){
					hr = new HttpRsult();
					hr.setError("获取失败");
					hr.setErrorCode(2);
					result = hr;
				}else{
					result = aeie;
				}
			}
			ObjectMapper mapper = new ObjectMapper();  
	        String json = mapper.writeValueAsString(result); 
	        this.response.getWriter().write(json);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void getNeedElectricInfo(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("GBK");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String page = this.request.getParameter("page");
			String areaId = this.request.getParameter("areaId");
			String placeTypeId = this.request.getParameter("placeTypeId");
			String parentId = this.request.getParameter("parentId");//@@
			HttpRsult hr = null;
			Object result = null;
			AllElectricInfoEntity aeie = null;
			if(!Utils.isNullStr(userId)||!Utils.isNullStr(privilege)){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mAllElectricInfoDao = new AllElectricInfoDaoImpl();
				if(areaId==null&&parentId==null){
					aeie = mAllElectricInfoDao.getAllElectricInfo(userId, privilege, page);
				}else{
					aeie = mAllElectricInfoDao.getNeedElectricInfo(userId, privilege,parentId, page, areaId, placeTypeId);
				}
				if(aeie==null){
					hr = new HttpRsult();
					hr.setError("获取失败");
					hr.setErrorCode(2);
					result = hr;
				}else{
					result = aeie;
				}
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
