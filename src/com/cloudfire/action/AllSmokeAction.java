package com.cloudfire.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import com.cloudfire.dao.AllSmokeDao;
import com.cloudfire.dao.impl.AllSmokeDaoImpl;
import com.cloudfire.entity.AllRepeaterEntity;
import com.cloudfire.entity.AllSmokeEntity;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.meter.MeterInfoEntity;
import com.cloudfire.entity.meter.MeterInfoHttpEntity;
import com.cloudfire.entity.meter.MeterReadingHttpEntity;
import com.cloudfire.entity.meter.MeterSettingHttpEntity;
import com.cloudfire.until.Utils;
import com.opensymphony.xwork2.ActionSupport;

public class AllSmokeAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{
	private static final long serialVersionUID = 1691133228840008587L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private AllSmokeDao mAllSmokeDao;

	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}

	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}
	
	public void getAllSmoke() {
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String page  = this.request.getParameter("page");
			
			AllSmokeEntity ase = null;
			HttpRsult hr = null;
			Object result = null;
			if(userId==null||privilege==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mAllSmokeDao = new AllSmokeDaoImpl();
				if(privilege.equals("1")){
					ase = mAllSmokeDao.getAdminAllSmoke(userId, privilege,page);
				}else{
					ase = mAllSmokeDao.getSuperAllSmoke(userId, privilege,page);
				}
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
	
	public void getNeedZDSTDev() {
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String page  = this.request.getParameter("page");
			String devType  = this.request.getParameter("devType");
			
			AllSmokeEntity ase = null;
			HttpRsult hr = null;
			Object result = null;
			if(userId==null||privilege==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mAllSmokeDao = new AllSmokeDaoImpl();
				if(privilege.equals("1")){
					ase = mAllSmokeDao.getAdminAllZDSTDev(userId, privilege,page,devType);
				}else{
					ase = mAllSmokeDao.getSuperAllZDSTDev(userId, privilege,page,devType);
				}
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
	
	public void getAllFaultinfo() {
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String page  = this.request.getParameter("page");
			String areaId = this.request.getParameter("areaId");
			AllSmokeEntity ase = null;
			HttpRsult hr = null;
			Object result = null;
			if((userId==null||privilege==null)&&!Utils.isNullStr(page)){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mAllSmokeDao = new AllSmokeDaoImpl();
				if(privilege.equals("1")){
					ase = mAllSmokeDao.getAdminAllFaultinfo(userId, privilege,page,areaId);
				}else{
					ase = mAllSmokeDao.getSuperAllFaultinfo(userId, privilege,page,areaId);
				}
				if(ase==null){
					hr = new HttpRsult();
					if(Integer.parseInt(page) == 1){
						hr.setError("没有数据");
						hr.setErrorCode(1);
					}else{
						hr.setError("没有更多数据");
						hr.setErrorCode(7);
					}
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
	
	
	public void getAllDevice() {
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String page  = this.request.getParameter("page");
			AllSmokeEntity ase = null;
			HttpRsult hr = null;
			Object result = null;
			if(userId==null||privilege==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mAllSmokeDao = new AllSmokeDaoImpl();
				if(privilege.equals("1")){
					ase = mAllSmokeDao.getAdminAllDevice(userId, privilege,page);
				}else{
					ase = mAllSmokeDao.getSuperAllDevice(userId, privilege,page);
				}
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
	
	// by liao zw  15.11.6
	public  void elecMeterGetDeviceByUser(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("gbk");
		try {
			this.request.setCharacterEncoding("utf-8");
			String  user=this.request.getParameter("user");
			
			HttpRsult hrHttpRsult=new HttpRsult();
			Object result=null;
			if(user!=null && user.length()>0){
				mAllSmokeDao = new AllSmokeDaoImpl();
				MeterInfoHttpEntity mhe=mAllSmokeDao.getMeterDeviceByUser(user);
				if(mhe!=null){
					mhe.setError("用户获取设备成功");
					mhe.setErrorCode(0);
					mhe.setUser(user);
					result=mhe;
				}
				else{
					hrHttpRsult.setError("用户获取设备失败");
					hrHttpRsult.setErrorCode(2);
					result=hrHttpRsult;
				}
			}
			else{
				hrHttpRsult.setError("参数错误");
				hrHttpRsult.setErrorCode(1);
				result=hrHttpRsult;
			}
			JSONObject jsonObject=new JSONObject(result);
			this.response.getWriter().write(jsonObject.toString());
		} catch (Exception e) {
	
			e.printStackTrace();
		}
		
	}
	
	public  void elecMeterGetReadingByMac(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("gbk");
		try {
			this.request.setCharacterEncoding("utf-8");
			String  mac=this.request.getParameter("mac");
			
			HttpRsult hrHttpRsult=new HttpRsult();
			Object result=null;
			if(mac!=null && mac.length()>0){
				mAllSmokeDao = new AllSmokeDaoImpl();
				MeterReadingHttpEntity mrhe=mAllSmokeDao.getMeterReadingByMac(mac);
				if(mrhe!=null){
					mrhe.setError("用户获取设备成功");
					mrhe.setErrorCode(0);					
					result=mrhe;
				}
				else{
					hrHttpRsult.setError("用户获取设备失败");
					hrHttpRsult.setErrorCode(2);
					result=hrHttpRsult;
				}
			}
			else{
				hrHttpRsult.setError("参数错误");
				hrHttpRsult.setErrorCode(1);
				result=hrHttpRsult;
			}
			JSONObject jsonObject=new JSONObject(result);
			this.response.getWriter().write(jsonObject.toString());
		} catch (Exception e) {
	
			e.printStackTrace();
		}
		
	}
	public  void elecMeterGetSettingByMac(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("gbk");
		try {
			this.request.setCharacterEncoding("utf-8");
			String  mac=this.request.getParameter("mac");
			
			HttpRsult hrHttpRsult=new HttpRsult();
			Object result=null;
			if(mac!=null && mac.length()>0){
				mAllSmokeDao = new AllSmokeDaoImpl();
				MeterSettingHttpEntity mshe= new MeterSettingHttpEntity();
				MeterInfoEntity mie=mAllSmokeDao.getMeterInfoByMac(mac);
				mshe.setError("用户获取设备成功");
				mshe.setErrorCode(0);
				mshe.setSetting(mie);
				result=mshe;				
			}
			else{
				hrHttpRsult.setError("参数错误");
				hrHttpRsult.setErrorCode(1);
				result=hrHttpRsult;
			}
			JSONObject jsonObject=new JSONObject(result);
			this.response.getWriter().write(jsonObject.toString());
		} catch (Exception e) {
	
			e.printStackTrace();
		}		
	}
	
	public  void elecMeterSetSettingByMac(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("gbk");
		try {
			this.request.setCharacterEncoding("utf-8");
			String  mac=this.request.getParameter("mac");
			String ifSendVoltage=this.request.getParameter("ifSendVoltage");
			String ifSendElectricity=this.request.getParameter("ifSendElectricity");
			String ifSendPower=this.request.getParameter("ifSendPower");
			
			HttpRsult hrHttpRsult=new HttpRsult();
			Object result=null;
			if(mac!=null && mac.length()>0){
				mAllSmokeDao = new AllSmokeDaoImpl();				
				hrHttpRsult=mAllSmokeDao.elecMeterSetSettingByMac(mac, ifSendVoltage, ifSendElectricity, ifSendPower);
			}
			else{
				hrHttpRsult.setError("参数错误");
				hrHttpRsult.setErrorCode(1);				
			}
			result=hrHttpRsult;
			JSONObject jsonObject=new JSONObject(result);
			this.response.getWriter().write(jsonObject.toString());
		} catch (Exception e) {
	
			e.printStackTrace();
		}		
	}
	
	public void getRepeaterInfo() {
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String page  = this.request.getParameter("page");
			String search  = this.request.getParameter("search");
			AllRepeaterEntity ase = null;
			HttpRsult hr = null;
			Object result = null;
			if(userId==null||privilege==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mAllSmokeDao = new AllSmokeDaoImpl();
				if(privilege.equals("3")){
					ase = mAllSmokeDao.getAllRepeaterInfo(userId, privilege,page,search);
				}else{
					ase = mAllSmokeDao.getAllRepeaterInfo(privilege,page,search);
				}
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
			e.printStackTrace();
		}
	}
}
