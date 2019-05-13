package com.cloudfire.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import com.cloudfire.dao.PlaceTypeDao;
import com.cloudfire.dao.impl.PlaceTypeDaoImpl;
import com.cloudfire.entity.AreaIdEntity;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.NFCDeviceTypeEntity;
import com.cloudfire.entity.PlaceTypeEntity;
import com.opensymphony.xwork2.ActionSupport;

public class PlaceTypeIdAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{
	
	private static final long serialVersionUID = 8561418935421306133L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private PlaceTypeDao mPlaceTypeDao;
	
	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}
	
	public void getPlaceTypeId(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String page = this.request.getParameter("page");
			PlaceTypeEntity pte = null;
			HttpRsult hr = null;
			Object result = null;
			if(userId==null||privilege==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mPlaceTypeDao = new PlaceTypeDaoImpl();
				pte = mPlaceTypeDao.getAllShopType(page);
				if(pte==null){
					hr = new HttpRsult();
					hr.setError("获取商铺类型失败");
					hr.setErrorCode(2);
					result = hr;
				}else{
					pte.setError("获取商铺类型成功");
					pte.setErrorCode(0);
					result = pte;
				}
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	//@@ by liangbin 2017.08.16
		public void getNFCDeviceTypeId(){
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("GBK");
			try {
				this.request.setCharacterEncoding("utf-8");
				NFCDeviceTypeEntity pte = null;
				HttpRsult hr = null;
				Object result = null;
				mPlaceTypeDao = new PlaceTypeDaoImpl();
				pte = mPlaceTypeDao.getAllDeviceType();
				if(pte==null){
					hr = new HttpRsult();
					hr.setError("获取类型失败");
					hr.setErrorCode(2);
					result = hr;
				}else{
					pte.setError("获取类型成功");
					pte.setErrorCode(0);
					result = pte;
				}
				
				JSONObject jObject = new JSONObject(result);
				this.response.getWriter().write(jObject.toString());
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
		public void getNeedAdministrationInfo(){
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("GBK");
			try {
				this.request.setCharacterEncoding("utf-8");
				String type = this.request.getParameter("type");
				String father_code = this.request.getParameter("father_code");
				AreaIdEntity pte = null;
				HttpRsult hr = null;
				Object result = null;
				if(type==null){
					hr = new HttpRsult();
					hr.setError("参数错误");
					hr.setErrorCode(1);
					result = hr;
				}else{
					mPlaceTypeDao = new PlaceTypeDaoImpl();
					pte = mPlaceTypeDao.getNeedAdministrationInfo(type,father_code);
					if(pte==null){
						hr = new HttpRsult();
						hr.setError("获取失败");
						hr.setErrorCode(2);
						result = hr;
					}else{
						pte.setError("获取成功");
						pte.setErrorCode(0);
						result = pte;
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
