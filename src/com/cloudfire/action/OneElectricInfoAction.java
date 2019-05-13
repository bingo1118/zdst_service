package com.cloudfire.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.cloudfire.dao.OneElectricInfoDao;
import com.cloudfire.dao.impl.OneElectricInfoDaoImpl;
import com.cloudfire.entity.ElectricEnergyEntity;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.OneChuangAnEntity;
import com.cloudfire.entity.OneElectricEntity;
import com.cloudfire.entity.OneTHDeviceInfoEntity;
import com.cloudfire.until.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.xwork2.ActionSupport;

public class OneElectricInfoAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{
	private static final long serialVersionUID = -9035309455443237581L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private OneElectricInfoDao mOneElectricInfoDao;
	
	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}
	
	public void getOneElectricInfo(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			//userId=13622215085&privilege=&smokeMac
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String smokeMac = this.request.getParameter("smokeMac");
			String privilege = this.request.getParameter("privilege");
			String devType = this.request.getParameter("devType");
			HttpRsult hr = null;
			Object result = null;
			OneElectricEntity oee = null;
			if(!Utils.isNullStr(userId)||!Utils.isNullStr(smokeMac)||!Utils.isNullStr(privilege)){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mOneElectricInfoDao = new OneElectricInfoDaoImpl();
				if((StringUtils.isNotBlank(devType)&&"76".equals(devType))||("77".equals(devType)||("80".equals(devType))||("81".equals(devType)))){
					oee = mOneElectricInfoDao.getOneElectricInfo(smokeMac,devType);
				}else{
					oee = mOneElectricInfoDao.getOneElectricInfo(smokeMac);
				}
				if(oee==null){
					hr = new HttpRsult();
					hr.setError("没有数据");
					hr.setErrorCode(2);
					result = hr;
				}else{
					result = oee;
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
	
	//获取电气电率数据
	public void getOneEnergyEntity(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String smokeMac = this.request.getParameter("smokeMac");
			String privilege = this.request.getParameter("privilege");
			HttpRsult hr = null;
			Object result = null;
			ElectricEnergyEntity eee = null;
			if(!Utils.isNullStr(userId)||!Utils.isNullStr(smokeMac)){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mOneElectricInfoDao = new OneElectricInfoDaoImpl();
					eee = mOneElectricInfoDao.getElectricEnergyEntity(smokeMac);
				if(eee==null){
					hr = new HttpRsult();
					hr.setError("没有数据");
					hr.setErrorCode(2);
					result = hr;
				}else{
					result = eee;
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
	
	public void getOneChuangAnInfo(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			//userId=13622215085&privilege=&smokeMac
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String smokeMac = this.request.getParameter("smokeMac");
			String privilege = this.request.getParameter("privilege");
			HttpRsult hr = null;
			Object result = null;
			OneChuangAnEntity oce = null;
			if(!Utils.isNullStr(userId)||!Utils.isNullStr(smokeMac)||!Utils.isNullStr(privilege)){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mOneElectricInfoDao = new OneElectricInfoDaoImpl();
				oce = mOneElectricInfoDao.getOneChuangAnEntity(userId, privilege, smokeMac);
				if(oce==null){
					hr = new HttpRsult();
					hr.setError("没有数据");
					hr.setErrorCode(2);
					result = hr;
				}else{
					result = oce;
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
	
	public void getOneTHDeviceInfo(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			//userId=13622215085&privilege=&smokeMac
			this.request.setCharacterEncoding("utf-8");
			String smokeMac = this.request.getParameter("mac");
			HttpRsult hr = null;
			Object result = null;
			OneTHDeviceInfoEntity oce = null;
			
			mOneElectricInfoDao = new OneElectricInfoDaoImpl();
			oce = mOneElectricInfoDao.getOneTHDevEntity(smokeMac);
			if(oce==null){
				hr = new HttpRsult();
				hr.setError("没有数据");
				hr.setErrorCode(2);
				result = hr;
			}else{
				result = oce;
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
