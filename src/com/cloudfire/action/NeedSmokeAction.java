package com.cloudfire.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import com.cloudfire.dao.AllSmokeDao;
import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.NeedSmokeDao;
import com.cloudfire.dao.impl.AllSmokeDaoImpl;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.cloudfire.dao.impl.NeedSmokeDaoImpl;
import com.cloudfire.entity.AllCheakItemEntity;
import com.cloudfire.entity.AllEnviDevEntity;
import com.cloudfire.entity.AllNFCInfoEntity;
import com.cloudfire.entity.AllSmokeEntity;
import com.cloudfire.entity.ElevatorInfoBeanEntity;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.LoginEntity;
import com.cloudfire.entity.NFCTraceEntity;
import com.cloudfire.entity.NeedNFCRecordEntity;
import com.cloudfire.entity.OneGPSInfoEntity;
import com.cloudfire.entity.OneGPSTraceEntity;
import com.cloudfire.entity.THInfoBeanEntity;
import com.opensymphony.xwork2.ActionSupport;

public class NeedSmokeAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{

	private static final long serialVersionUID = 904614168196304889L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private NeedSmokeDao mNeedSmokeDao;
	
	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}

	public void getNeedSmoke() {
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String page  = this.request.getParameter("page");
			String areaId = this.request.getParameter("areaId");
			String placeTypeId  = this.request.getParameter("placeTypeId");
			AllSmokeEntity ase = null;
			HttpRsult hr = null;
			Object result = null;
			if(userId==null||privilege==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mNeedSmokeDao = new NeedSmokeDaoImpl();
				ase = mNeedSmokeDao.getNeedSmoke(userId, privilege, page, areaId, placeTypeId);
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
	
	public void getNeedDevice() {
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String page  = this.request.getParameter("page");
			String areaId = this.request.getParameter("areaId");
			String placeTypeId  = this.request.getParameter("placeTypeId");
			AllSmokeEntity ase = null;
			HttpRsult hr = null;
			Object result = null;
			if(userId==null||privilege==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mNeedSmokeDao = new NeedSmokeDaoImpl();
				ase = mNeedSmokeDao.getNeedDevice(userId, privilege, page, areaId, placeTypeId);
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
	
	/**
	 * 用于查询谁被类型10以上的设备
	 */
	public void getNeedSecurity() {
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String page  = this.request.getParameter("page");
			String areaId = this.request.getParameter("areaId");
			String placeTypeId  = this.request.getParameter("placeTypeId");
			AllSmokeEntity ase = null;
			HttpRsult hr = null;
			Object result = null;
			if(userId==null||privilege==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mNeedSmokeDao = new NeedSmokeDaoImpl();
				ase = mNeedSmokeDao.getNeedSecurity(userId, privilege, page, areaId, placeTypeId);
				if(ase==null){
					hr = new HttpRsult();
					hr.setError("没有该类型的设备");
					hr.setErrorCode(2);
					result = hr;
				}else{
					ase.setError("获取该类型设备成功");
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
	
	//@@ by liangbin 2017.07.19
	public void getNeedDev() {
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String page  = this.request.getParameter("page");
			String areaId = this.request.getParameter("areaId");
			String placeTypeId  = this.request.getParameter("placeTypeId");
			String devType = this.request.getParameter("devType");//@@
			String parentId = this.request.getParameter("parentId");//@@父区域
			AllSmokeEntity ase = null;
			HttpRsult hr = null;
			Object result = null;
			if(userId==null||privilege==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mNeedSmokeDao = new NeedSmokeDaoImpl();
				if(privilege.equals("1")){//@@9.29 1级权限查询
					ase = mNeedSmokeDao.getAdminAllSmoke(userId, privilege,page,devType);
				}else{
					ase = mNeedSmokeDao.getNeedDev(userId, privilege, page, areaId, placeTypeId,devType,parentId);
				}
				if(ase==null){
					hr = new HttpRsult();
					hr.setError("没有数据");
					hr.setErrorCode(2);
					result = hr;
				}else{
					ase.setError("获取成功");
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
	
	//@@ liangbin 2017.07.19
	public void getNeedLossDev() {
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String page  = this.request.getParameter("page");
			String areaId = this.request.getParameter("areaId");
			String placeTypeId  = this.request.getParameter("placeTypeId");
			String devType = this.request.getParameter("devType");//@@
			String parentId = this.request.getParameter("parentId");//@@父区域
			AllSmokeEntity ase = null;
			HttpRsult hr = null;
			Object result = null;
			if(userId==null||privilege==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mNeedSmokeDao = new NeedSmokeDaoImpl();
				ase = mNeedSmokeDao.getNeedLossDev(userId, privilege, page, areaId, placeTypeId,devType,parentId);
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
	//@@ by liangbin 2017.07.28
	public void getNeedEnviDev() {
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String page  = this.request.getParameter("page");
			String areaId = this.request.getParameter("areaId");
			String placeTypeId  = this.request.getParameter("placeTypeId");
			AllEnviDevEntity ase = null;
			HttpRsult hr = null;
			Object result = null;
			if(userId==null||privilege==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mNeedSmokeDao = new NeedSmokeDaoImpl();
				ase = mNeedSmokeDao.getNeedEnviDev(userId, privilege, page, areaId, placeTypeId);
				if(ase==null){
					hr = new HttpRsult();
					hr.setError("没有该类型的设备");
					hr.setErrorCode(2);
					result = hr;
				}else{
					ase.setError("获取该类型设备成功");
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
	/**
	 * @author liangbin
	 * 获取GPS设备
	 */
	public void getNeedGPSDev() {
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String page  = this.request.getParameter("page");
			String areaId = this.request.getParameter("areaId");
			String placeTypeId  = this.request.getParameter("placeTypeId");
			AllSmokeEntity ase = null;
			HttpRsult hr = null;
			Object result = null;
			if(userId==null||privilege==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mNeedSmokeDao = new NeedSmokeDaoImpl();
				ase = mNeedSmokeDao.getNeedGPSDev(userId, privilege, page, areaId, placeTypeId);
				if(ase==null){
					hr = new HttpRsult();
					hr.setError("没有设备");
					hr.setErrorCode(2);
					result = hr;
				}else{
					ase.setError("获取成功");
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
	
	/**
	 * @author liangbin
	 * 获取一个GPS设备
	 */
	public void getOneGPSInfo() {
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String mac = this.request.getParameter("mac");
			OneGPSInfoEntity ase = null;
			HttpRsult hr = null;
			Object result = null;
			if(mac==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mNeedSmokeDao = new NeedSmokeDaoImpl();
				ase = mNeedSmokeDao.getOneGPSInfo(mac);
				if(ase==null){
					hr = new HttpRsult();
					hr.setError("没有数据");
					hr.setErrorCode(2);
					result = hr;
				}else{
					ase.setError("获取成功");
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
	
	/**
	 * @author liangbin
	 * 获取一个GPS设备历史轨迹
	 */
	public void getOneGPSTrace() {
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String mac = this.request.getParameter("mac");
			String begintime = this.request.getParameter("begintime");
			String endtime = this.request.getParameter("endtime");
			begintime = begintime + " 00:00:00";
			endtime = endtime + " 23:59:59";
			
			OneGPSTraceEntity ase = null;
			HttpRsult hr = null;
			Object result = null;
			if(mac==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mNeedSmokeDao = new NeedSmokeDaoImpl();
				ase = mNeedSmokeDao.getOneGPSTrace(mac,begintime,endtime);
				if(ase==null){
					hr = new HttpRsult();
					hr.setError("没有数据");
					hr.setErrorCode(2);
					result = hr;
				}else{
					ase.setError("获取成功");
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
	
	/**
	 * add by lzo for nfcinfo table
	 */
	public void getNFCInfo() {
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String page  = this.request.getParameter("page");
			String areaId = this.request.getParameter("areaId");
			String period = this.request.getParameter("period");//@@10.23
			String devicetype = this.request.getParameter("devicetype");//@@11.07
			String devicestate = this.request.getParameter("devicestate");//@@2018.03.05
			AllNFCInfoEntity nfcInfo = null;
			HttpRsult hr = null;
			Object result = null;
			if(userId==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				LoginDao ld = new LoginDaoImpl();
				LoginEntity mLoginEntity = ld.login(userId);
				int privilege = mLoginEntity.getPrivilege();
				AreaDao mAreaDao = new AreaDaoImpl();
				List<String> areaIds = new ArrayList<String>();
				areaIds = mAreaDao.getAreaStr(userId, privilege+"");
				if(areaIds!=null&&areaIds.size()>0){
					mNeedSmokeDao = new NeedSmokeDaoImpl();
					nfcInfo = mNeedSmokeDao.getAllNFCInfo(areaIds,page,areaId,period,devicetype,devicestate);
				}
				if(nfcInfo==null){
					hr = new HttpRsult();
					hr.setError("没有数据");
					hr.setErrorCode(2);
					result = hr;
				}else{
					nfcInfo.setError("获取成功");
					nfcInfo.setErrorCode(0);
					result = nfcInfo;
				}
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/**
	 * add by lzo for nfcinfo table
	 */
	public void getNFCRecord() {
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String uid = this.request.getParameter("uid");
			String page  = this.request.getParameter("page");
			String userId  = this.request.getParameter("userId");
			String privilege  = this.request.getParameter("privilege");
			AllNFCInfoEntity nfcInfo = null;
			HttpRsult hr = null;
			Object result = null;
			if(uid==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mNeedSmokeDao = new NeedSmokeDaoImpl();
				nfcInfo = mNeedSmokeDao.getAllNFCRecord(uid, page,userId,privilege);
				if(nfcInfo==null){
					hr = new HttpRsult();
					hr.setError("没有数据");
					hr.setErrorCode(2);
					result = hr;
				}else{
					nfcInfo.setError("获取成功");
					nfcInfo.setErrorCode(0);
					result = nfcInfo;
				}
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/**
	 * add by lzo for nfcinfo table
	 */
	public void getNFCRecordByCondition() {
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String page  = this.request.getParameter("page");
			String userId  = this.request.getParameter("userId");
			String privilege  = this.request.getParameter("privilege");
			String areaId  = this.request.getParameter("areaId");
			String startdate  = this.request.getParameter("startdate");
			String enddate  = this.request.getParameter("enddate");
			NeedNFCRecordEntity nfcInfo = null;
			HttpRsult hr = null;
			Object result = null;
			if(areaId==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mNeedSmokeDao = new NeedSmokeDaoImpl();
				nfcInfo = mNeedSmokeDao.getAllNFCRecordByCondition(page, userId, privilege, areaId, startdate, enddate);
				if(nfcInfo==null){
					hr = new HttpRsult();
					hr.setError("没有数据");
					hr.setErrorCode(2);
					result = hr;
				}else{
					nfcInfo.setError("获取成功");
					nfcInfo.setErrorCode(0);
					result = nfcInfo;
				}
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void getNFCCheakItems() {
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String devType = this.request.getParameter("devType");
			AllCheakItemEntity nfcInfo = null;
			HttpRsult hr = null;
			Object result = null;
			if(devType==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mNeedSmokeDao = new NeedSmokeDaoImpl();
				nfcInfo = mNeedSmokeDao.getNFCCheakItems(devType);
				if(nfcInfo==null){
					hr = new HttpRsult();
					hr.setError("没有数据");
					hr.setErrorCode(2);
					result = hr;
				}else{
					nfcInfo.setError("获取成功");
					nfcInfo.setErrorCode(0);
					result = nfcInfo;
				}
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
	
	
	/**
	 * @author liangbin
	 * 获取NFC巡检历史轨迹
	 */
	public void getNFCTrace() {
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String areaId = this.request.getParameter("areaId");
			String begintime = this.request.getParameter("begintime");
			String endtime = this.request.getParameter("endtime");
			begintime = begintime + " 00:00:00";
			endtime = endtime + " 23:59:59";
			
			NFCTraceEntity ase = null;
			HttpRsult hr = null;
			Object result = null;
			mNeedSmokeDao = new NeedSmokeDaoImpl();
			ase = mNeedSmokeDao.getNFCTrace(areaId,begintime,endtime);
			if(ase==null){
				hr = new HttpRsult();
				hr.setError("没有数据");
				hr.setErrorCode(2);
				result = hr;
			}else{
				ase.setError("获取成功");
				ase.setErrorCode(0);
				result = ase;
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/*
	 by liaoZhenWei
	 获取电梯设备
	 */
	public void getNeedElevatorDev() {
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String page  = this.request.getParameter("page");
			String areaId = this.request.getParameter("areaId");
			String placeTypeId  = this.request.getParameter("placeTypeId");
			AllSmokeEntity ase = null;
			HttpRsult hr = null;
			Object result = null;
			if(userId==null||privilege==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mNeedSmokeDao = new NeedSmokeDaoImpl();
				ase = mNeedSmokeDao.getNeedElevatorDev(userId, privilege, page, areaId, placeTypeId);
				if(ase==null){
					hr = new HttpRsult();
					hr.setError("没有设备");
					hr.setErrorCode(2);
					result = hr;
				}else{
					ase.setError("获取成功");
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
	
	public void getOneElevatorDev() {
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String mac = this.request.getParameter("mac");
			ElevatorInfoBeanEntity eibe = null;
			HttpRsult hr = null;
			Object result = null;
			if(mac==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mNeedSmokeDao = new NeedSmokeDaoImpl();
				eibe = mNeedSmokeDao.getOneElevatorDev(mac);
				if(eibe==null){
					hr = new HttpRsult();
					hr.setError("没有数据");
					hr.setErrorCode(2);
					result = hr;
				}else{
					eibe.setError("获取成功");
					eibe.setErrorCode(0);
					result = eibe;
				}
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void getTHInfo() {
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String mac = this.request.getParameter("mac");
			THInfoBeanEntity thinfo = null;
			HttpRsult hr = null;
			Object result = null;
			mNeedSmokeDao = new NeedSmokeDaoImpl();
			thinfo = mNeedSmokeDao.getTHDevInfo(mac);
		
			if(thinfo==null){
				hr = new HttpRsult();
				hr.setError("没有数据");
				hr.setErrorCode(2);
				result = hr;
			}else{
				thinfo.setError("获取成功");
				thinfo.setErrorCode(0);
				result = thinfo;
			}
			
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
}
