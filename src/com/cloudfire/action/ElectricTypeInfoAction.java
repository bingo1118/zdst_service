package com.cloudfire.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import com.cloudfire.dao.ElectricTypeInfoDao;
import com.cloudfire.dao.impl.ElectricTypeInfoDaoImpl;
import com.cloudfire.entity.AllElectricInfoEntity;
import com.cloudfire.entity.ElectricInfo;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.ProofGasHistoryEntity;
import com.cloudfire.until.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.xwork2.ActionSupport;

public class ElectricTypeInfoAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{
	
	private static final long serialVersionUID = 1274720233608567322L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ElectricTypeInfoDao mElectricTypeInfoDao;
	
	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}

	public void getElectricTypeInfo(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			//userId=13622215085&privilege=&smokeMac=59F8250A&electricType=6&electricNum=&page=1
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String smokeMac = this.request.getParameter("smokeMac");
			String electricType = this.request.getParameter("electricType");
			String electricNum = this.request.getParameter("electricNum");
			String electricDate = this.request.getParameter("electricDate");
			String devType = this.request.getParameter("devType");
			String page = this.request.getParameter("page");
			HttpRsult hr = null;
			Object result = null;
			ElectricInfo aeie = null;
			if(!Utils.isNullStr(userId)||!Utils.isNullStr(privilege)||!Utils.isNullStr(smokeMac)||!Utils.isNullStr(electricType)||!Utils.isNullStr(electricNum)){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mElectricTypeInfoDao = new ElectricTypeInfoDaoImpl();
				if(StringUtils.isNotBlank(devType)&&("76".equals(devType)||"77".equals(devType)||"80".equals(devType)||"81".equals(devType))){
					aeie = mElectricTypeInfoDao.getThreeElectricTypeInfo(userId, privilege, smokeMac, electricType, electricNum, page,devType);
				}else if(!Utils.isNullStr(electricDate)){
					aeie = mElectricTypeInfoDao.getElectricTypeInfo(userId, privilege, smokeMac, electricType, electricNum, page);
				}else{
					aeie = mElectricTypeInfoDao.getElectricPCTypeInfo(userId, privilege, 
							smokeMac, electricType, electricNum, page, electricDate);
				}
				
				if(aeie==null){
					hr = new HttpRsult();
					hr.setError("没有电气属性信息");
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
	
	public void getWaterHistoryInfo(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			//userId=13622215085&privilege=&smokeMac=59F8250A&electricType=6&electricNum=&page=1
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String smokeMac = this.request.getParameter("smokeMac");
			String page = this.request.getParameter("page");
			HttpRsult hr = null;
			Object result = null;
			ElectricInfo aeie = null;
			if(!Utils.isNullStr(userId)||!Utils.isNullStr(privilege)||!Utils.isNullStr(smokeMac)){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mElectricTypeInfoDao = new ElectricTypeInfoDaoImpl();
				aeie = mElectricTypeInfoDao.getWaterHistoryInfo(userId, privilege, smokeMac, page);
				if(aeie==null){
					hr = new HttpRsult();
					hr.setError("没有属性信息");
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
	
	public void getTHDevInfoHistoryInfo(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			//userId=13622215085&privilege=&smokeMac=59F8250A&electricType=6&electricNum=&page=1
			String smokeMac = this.request.getParameter("mac");
			String page = this.request.getParameter("page");
			String type=this.request.getParameter("type");
			HttpRsult hr = null;
			Object result = null;
			ElectricInfo aeie = null;
			mElectricTypeInfoDao = new ElectricTypeInfoDaoImpl();
			aeie = mElectricTypeInfoDao.getTHDevInfoHistoryInfo(smokeMac, page,type);
			if(aeie==null){
				hr = new HttpRsult();
				hr.setError("没有属性信息");
				hr.setErrorCode(2);
				result = hr;
			}else{
				result = aeie;
			}
			
			ObjectMapper mapper = new ObjectMapper();  
	        String json = mapper.writeValueAsString(result); 
	        this.response.getWriter().write(json);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void getChuanganHistoryInfo(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			//userId=13622215085&privilege=&smokeMac=59F8250A&electricType=6&electricNum=&page=1
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String smokeMac = this.request.getParameter("smokeMac");
			String electricNum = this.request.getParameter("electricNum");
			String page = this.request.getParameter("page");
			HttpRsult hr = null;
			Object result = null;
			ElectricInfo aeie = null;
			if(!Utils.isNullStr(userId)||!Utils.isNullStr(privilege)||!Utils.isNullStr(smokeMac)){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mElectricTypeInfoDao = new ElectricTypeInfoDaoImpl();
				aeie = mElectricTypeInfoDao.getChuanganHistoryInfo(userId, privilege, smokeMac, page,electricNum);
				if(aeie==null){
					hr = new HttpRsult();
					hr.setError("没有属性信息");
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
	
	public void getGasHistoryInfo(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String smokeMac = this.request.getParameter("smokeMac");
			String page = this.request.getParameter("page");
			HttpRsult hr = null;
			Object result = null;
			ProofGasHistoryEntity aeie = null;
			if(!Utils.isNullStr(userId)||!Utils.isNullStr(privilege)||!Utils.isNullStr(smokeMac)){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mElectricTypeInfoDao = new ElectricTypeInfoDaoImpl();
				aeie = mElectricTypeInfoDao.getProofGasHistoryInfo(userId, privilege, smokeMac, page);
				if(aeie==null){
					hr = new HttpRsult();
					hr.setError("没有属性信息");
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
