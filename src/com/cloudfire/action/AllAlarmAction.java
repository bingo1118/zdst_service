package com.cloudfire.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.cloudfire.dao.AllAlarmDao;
import com.cloudfire.dao.impl.AllAlarmDaoImpl;
import com.cloudfire.entity.AllAlarmEntity;
import com.cloudfire.entity.ElectrAlarmThresholdEntity;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.OneAlarmEntity;
import com.cloudfire.entity.THAlarmThresholdEntity;
import com.cloudfire.entity.WaterAckEntity;
import com.cloudfire.entity.WaterAlarmThresholdEntity;
import com.cloudfire.until.IfStopAlarm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.xwork2.ActionSupport;

public class AllAlarmAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{
	
	private static final long serialVersionUID = -4581514309685063419L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private AllAlarmDao mAllAlarmDao;
	
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void getAllAlarm(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String page = this.request.getParameter("page");
			String privilege = this.request.getParameter("privilege");
			HttpRsult hr = null;
			Object result = null;
			AllAlarmEntity aae = null;
			if(userId==null||privilege==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mAllAlarmDao = new AllAlarmDaoImpl();
				if(privilege.equals("1")){
					aae = mAllAlarmDao.getNormalAllAlarmMsg(userId, privilege, page);
				}else if(userId.equals("abcdefg001")){
					aae = null;
				}else{
					aae = mAllAlarmDao.getAdminAllAlarmMsg(userId, privilege, page);
				}
				
				if(aae==null){
					hr = new HttpRsult();
					hr.setError("没有报警消息");
					hr.setErrorCode(2);
					result = hr;
				}else{
					aae.setError("获取报警消息成功");
					aae.setErrorCode(0);
					result = aae;
				}
			}
//			JSONObject jObject = new JSONObject(result);
//			this.response.getWriter().write(jObject.toString());
			ObjectMapper mapper = new ObjectMapper();  
	        String json = mapper.writeValueAsString(result); 
	        this.response.getWriter().write(json);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getNeedAlarm(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("GBK");
			String userId = this.request.getParameter("userId");
			String page = this.request.getParameter("page");
			String privilege = this.request.getParameter("privilege");
			String startTime = this.request.getParameter("startTime");
			String endTime = this.request.getParameter("endTime");
			String parentId=this.request.getParameter("parentId");//@@父区域
			String areaId = this.request.getParameter("areaId");
			String placeTypeId = this.request.getParameter("placeTypeId");
			HttpRsult hr = null;
			Object result = null;
			AllAlarmEntity aae = null;
			if(userId==null||privilege==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mAllAlarmDao = new AllAlarmDaoImpl();
				if(privilege.equals("1")){
					aae = mAllAlarmDao.getNormalNeedAlarm(userId, privilege, page, startTime, endTime);
				}else{
					aae = mAllAlarmDao.getAdminNeedAlarm(userId, privilege, page, startTime, endTime, areaId, placeTypeId,parentId);
				}
				
				if(aae==null){
					hr = new HttpRsult();
					hr.setError("没有报警消息");
					hr.setErrorCode(2);
					result = hr;
				}else{
					aae.setError("获取报警消息成功");
					aae.setErrorCode(0);
					result = aae;
				}
			}
			ObjectMapper mapper = new ObjectMapper();  
	        String json = mapper.writeValueAsString(result); 
	        this.response.getWriter().write(json);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * liangbin 2017.7.19
	 */
	public void getLastestAlarm(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("GBK");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			HttpRsult hr = null;
			Object result = null;
			OneAlarmEntity aae = null;
			if(userId==null||privilege==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mAllAlarmDao = new AllAlarmDaoImpl();
				if(privilege.equals("1")){
					aae = mAllAlarmDao.getNormalLastestAlarm(userId, privilege);
				}else{
					aae = mAllAlarmDao.getAdminLastestAlarm(userId, privilege);
				}
				
				if(aae==null){
					hr = new HttpRsult();
					hr.setError("没有报警消息");
					hr.setErrorCode(2);
					result = hr;
				}else{
					aae.setError("获取报警消息成功");
					aae.setErrorCode(0);
					result = aae;
				}
			}
			ObjectMapper mapper = new ObjectMapper();  
	        String json = mapper.writeValueAsString(result); 
	        this.response.getWriter().write(json);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * liangbin 2017.10.31
	 */
	public void stopAlarm(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("GBK");
			String mac = this.request.getParameter("mac");
			HttpRsult hr = null;
			Object result = null;
			OneAlarmEntity aae = null;
			if(mac==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				IfStopAlarm.map.put(mac, System.currentTimeMillis());
				aae=new OneAlarmEntity();
				aae.setError("停止报警成功");
				aae.setErrorCode(0);
				result = aae;
			}
			ObjectMapper mapper = new ObjectMapper();  
	        String json = mapper.writeValueAsString(result); 
	        this.response.getWriter().write(json);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 设置水位阈值
	 */
	public void reSetAlarmNum(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("GBK");
			String mac = this.request.getParameter("mac");
			
			String threshold207 = this.request.getParameter("threshold207");
			String threshold208 = this.request.getParameter("threshold208");
			String type=this.request.getParameter("type");//@@ 1 温度 2 湿度 null 水压水位阈值
			HttpRsult hr = null;
			Object result = null;
			OneAlarmEntity aae = null;
			if(mac==null||threshold207==null||threshold208==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				AllAlarmDao alarmDao = new AllAlarmDaoImpl();
				aae=new OneAlarmEntity();
				boolean isSuccess=false;
				if(type==null||type.equals("")){
					isSuccess=alarmDao.updateAlarmThreshold(mac, threshold207, threshold208);
				}else{
					isSuccess=alarmDao.updateAlarmThreshold(mac, threshold207, threshold208,type);
				}
				if(isSuccess){
					aae.setError("设置成功");
					aae.setErrorCode(0);
				}else{
					aae.setError("设置失败");
					aae.setErrorCode(1);
				}
				result = aae;
			}
			ObjectMapper mapper = new ObjectMapper();  
	        String json = mapper.writeValueAsString(result); 
	        this.response.getWriter().write(json);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getWaterAlarmThreshold(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String mac = this.request.getParameter("mac");
			String deviceType = this.request.getParameter("deviceType");
			HttpRsult hr = null;
			Object result = null;
			WaterAlarmThresholdEntity aae = null;
			WaterAckEntity wae = null;
			if(StringUtils.isBlank(deviceType)){
				deviceType = "0";
			}
			if(mac==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mAllAlarmDao = new AllAlarmDaoImpl();
				switch(deviceType){
					case "78":
					case "79":
//					case "46":
//					case "47":
						wae = mAllAlarmDao.getWaterAckEntityThreshold(mac);
						result = wae;
						break;
					case "0":
					default :
						aae = mAllAlarmDao.getWaterAlarmThreshold(mac);
						result = aae;
						break;
				}
				
				if(aae==null&&wae==null){
					hr = new HttpRsult();
					hr.setError("无数据");
					hr.setErrorCode(2);
					result = hr;
				}else{
					if(aae!=null){
						aae.setError("获取成功");
						aae.setErrorCode(0);
					}else if(wae!=null){
						wae.setError("获取成功");
						wae.setErrorCode(0);
					}
				}
			}
			ObjectMapper mapper = new ObjectMapper();  
	        String json = mapper.writeValueAsString(result); 
	        this.response.getWriter().write(json);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getElectrAlarmThreshold(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String mac = this.request.getParameter("mac");
			HttpRsult hr = null;
			Object result = null;
			ElectrAlarmThresholdEntity aae = null;
			if(mac==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mAllAlarmDao = new AllAlarmDaoImpl();
				aae = mAllAlarmDao.getElectrAlarmThreshold(mac);
				if(aae==null){
					hr = new HttpRsult();
					hr.setError("无数据");
					hr.setErrorCode(2);
					result = hr;
				}else{
					aae.setError("获取成功");
					aae.setErrorCode(0);
					result = aae;
				}
			}
			ObjectMapper mapper = new ObjectMapper();  
	        String json = mapper.writeValueAsString(result); 
	        this.response.getWriter().write(json);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getTHAlarmThreshold(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String mac = this.request.getParameter("mac");
			HttpRsult hr = null;
			Object result = null;
			THAlarmThresholdEntity aae = null;
			if(mac==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mAllAlarmDao = new AllAlarmDaoImpl();
				aae = mAllAlarmDao.getTHAlarmThreshold(mac);
				if(aae==null){
					hr = new HttpRsult();
					hr.setError("无数据");
					hr.setErrorCode(2);
					result = hr;
				}else{
					aae.setError("获取成功");
					aae.setErrorCode(0);
					result = aae;
				}
			}
			ObjectMapper mapper = new ObjectMapper();  
	        String json = mapper.writeValueAsString(result); 
	        this.response.getWriter().write(json);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//@@获取需要的报警消息2018.08.15
	public void getNeedAlarmMessage(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("GBK");
			String userId = this.request.getParameter("userId");
			String page = this.request.getParameter("page");
			String privilege = this.request.getParameter("privilege");
			String startTime = this.request.getParameter("startTime");
			String endTime = this.request.getParameter("endTime");
			String parentId=this.request.getParameter("parentId");//@@父区域
			String areaId = this.request.getParameter("areaId");
			String placeTypeId = this.request.getParameter("placeTypeId");
			String grade = this.request.getParameter("grade");
			String distance = this.request.getParameter("distance");
			String progress = this.request.getParameter("progress");
			String id = this.request.getParameter("id");
			
			HttpRsult hr = null;
			Object result = null;
			AllAlarmEntity aae = null;
			if(userId==null||privilege==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mAllAlarmDao = new AllAlarmDaoImpl();
				if(privilege.equals("1")){
					aae = mAllAlarmDao.getNormalNeedAlarm(userId, privilege, page, startTime, endTime);
				}else{
					aae = mAllAlarmDao.getAdminNeedAlarmMsg(userId, privilege, page, startTime, endTime, areaId, placeTypeId,parentId,grade,distance,progress,id);
				}
				
				if(aae==null){
					hr = new HttpRsult();
					hr.setError("没有报警消息");
					hr.setErrorCode(2);
					result = hr;
				}else{
					aae.setError("获取报警消息成功");
					aae.setErrorCode(0);
					result = aae;
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
