package com.cloudfire.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.SmartControlDao;
import com.cloudfire.dao.SmokeSummaryDao;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.SmartControlDaoImpl;
import com.cloudfire.dao.impl.SmokeSummaryDaoImpl;
import com.cloudfire.dao.query.SearchAnalysisQuery;
import com.cloudfire.dao.query.impl.SearchAnalysisQueryImpl;
import com.cloudfire.entity.AlarmAllYearEntity;
import com.cloudfire.entity.AlarmSummaryEntity;
import com.cloudfire.entity.DevTypeSummary;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.SmokeSummaryEntity;
import com.cloudfire.entity.query.DeviceType;
import com.cloudfire.entity.query.SearchAnalysisinfo1;
import com.cloudfire.until.Utils;
import com.opensymphony.xwork2.ActionSupport;

public class SmokeSummaryAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{
		
	private static final long serialVersionUID = 4110157918677908234L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private SmokeSummaryDao mSmokeSummaryDao;
	private SmartControlDao mSmartControlDao;
	private AreaDao mAreaDao;
	private SearchAnalysisQuery mSearchAnalysisQuery;
	
	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}
	
	public void getSmokeSummary(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String areaId = this.request.getParameter("areaId");
			String appId = this.request.getParameter("appId");
			String placeTypeId = this.request.getParameter("placeTypeId");//@@2017.08.11
			//1 消防设备 2电气火灾 3安防设备 4环境监测  6车辆管理 7电梯管理 8离线设备
			String devType = this.request.getParameter("devType");//@@2017.08.11
			if(!Utils.isNullStr(placeTypeId)){
				placeTypeId = "";
			}
			if(!Utils.isNullStr(devType)){
				devType = "";
			}
			HttpRsult hr = null;
			Object result = null;
			SmokeSummaryEntity sse = null;
			if(!Utils.isNullStr(userId)||!Utils.isNumeric(privilege)){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mSmokeSummaryDao = new SmokeSummaryDaoImpl();
				sse = mSmokeSummaryDao.getTotalSmokeSummary(userId,privilege, areaId,appId,placeTypeId,devType);
				if(sse==null){
					hr = new HttpRsult();
					hr.setError("失败");
					hr.setErrorCode(2);
					result = hr;
				}else{
					result = sse;
				}
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	//@@liangbin
	public void getDevSummary(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String areaId = this.request.getParameter("areaId");
			String placeTypeId=this.request.getParameter("placeTypeId");//@@7.20
			String devType = this.request.getParameter("devType");
			String parentId=this.request.getParameter("parentId");//@@9.4
			HttpRsult hr = null;
			Object result = null;
			SmokeSummaryEntity sse = null;
			if(!Utils.isNullStr(userId)||!Utils.isNumeric(privilege)){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mSmokeSummaryDao = new SmokeSummaryDaoImpl();
				sse = mSmokeSummaryDao.getTotalDevSummary(userId,privilege,parentId, areaId,placeTypeId,devType);
				if(sse==null){
					hr = new HttpRsult();
					hr.setError("失败");
					hr.setErrorCode(2);
					result = hr;
				}else{
					result = sse;
				}
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// under by liao zhenwei
	public void getDevTypeSummary(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("gbk");
		try{
			this.request.setCharacterEncoding("utf-8");
			String userId=this.request.getParameter("userId");
			String privilege=this.request.getParameter("privilege");
			HttpRsult hr=null;
			Object result=null;
			List<DeviceType> ld=new ArrayList<>();;
			mAreaDao = new AreaDaoImpl();
			List<String> areIds = mAreaDao.getAreaStr(userId, privilege);
			DevTypeSummary dts=new DevTypeSummary();
			if(!Utils.isNullStr(userId)||!Utils.isNumeric(privilege)){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mSmartControlDao = new SmartControlDaoImpl();
				ld=mSmartControlDao.getDeviceTypeSummary(areIds);
				
				if(ld.size()<=0){
					hr = new HttpRsult();
					hr.setError("失败");
					hr.setErrorCode(2);
					result = hr;
				}else{
					dts.setDeviceType(ld);
					dts.setError("成功");
					dts.setErrorCode(0);
					result = dts;
				}
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	public void getSmokeSummaryTwo(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("gbk");
		try{
			this.request.setCharacterEncoding("utf-8");			
			String areaId=this.request.getParameter("areaId");
			String devType=this.request.getParameter("devType");
			
			Object result=null;
			SmokeSummaryEntity sse=null;
			mSmokeSummaryDao =new  SmokeSummaryDaoImpl();
			sse=mSmokeSummaryDao.getSmokeSummaryTwo(areaId, devType);
			result=sse;
			JSONObject obj=new JSONObject(result);
			this.response.getWriter().write(obj.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void getAlarmSummary (){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("gbk");
		try{
			this.request.setCharacterEncoding("utf-8");			
			String areaId=this.request.getParameter("areaId");
			String devType=this.request.getParameter("devType");
			String year=this.request.getParameter("year");
			Object result=null;
			List<String>areaIds=new ArrayList<String>();
			List<Integer> acl=new ArrayList<>();
			mSearchAnalysisQuery = new SearchAnalysisQueryImpl();
			AlarmSummaryEntity ase=new AlarmSummaryEntity();
			ase.setError("成功");
			ase.setErrorCode(0);
			for(int i=1;i<=12;i++){
				SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
				query.setYear(year);
				query.setMonth(i+"");
				query.setAreaId(Integer.parseInt(areaId));
				Integer alarmCount202 = mSearchAnalysisQuery.getAlarmCount202(areaIds, devType, query);	
				acl.add(alarmCount202);
			}
			ase.setAlarmCountList(acl);
			
			result=ase;
			JSONObject obj=new JSONObject(result);
			this.response.getWriter().write(obj.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public  void getAlarmAllYear(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("gbk");
		try{
			this.request.setCharacterEncoding("utf-8");			
			
			Object result=null;	
			HttpRsult hr=new HttpRsult();			
			mSearchAnalysisQuery = new SearchAnalysisQueryImpl();
			AlarmAllYearEntity aly=new AlarmAllYearEntity();

			List<String> yl= mSearchAnalysisQuery.getAllYear();	
		    if(yl!=null){
		    	aly.setError("成功");
		    	aly.setErrorCode(0);
		    	aly.setYearList(yl);
		    	result=aly;
		    }
		    else{
		    	hr.setError("失败");
		    	hr.setErrorCode(1);
		    	result=hr;
		    }
		
			JSONObject obj=new JSONObject(result);
			this.response.getWriter().write(obj.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//  up by liao zhenwei

	
	public void getNFCSummary(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String areaId = this.request.getParameter("areaId");
			String period = this.request.getParameter("period");//@@10.23
			String devicetype = this.request.getParameter("devicetype");//@@11.07
			HttpRsult hr = null;
			Object result = null;
			SmokeSummaryEntity sse = null;
				mSmokeSummaryDao = new SmokeSummaryDaoImpl();
				sse = mSmokeSummaryDao.getTotalNFCSummary(userId,privilege,areaId,period,devicetype);
				if(sse==null){
					hr = new HttpRsult();
					hr.setError("失败");
					hr.setErrorCode(2);
					result = hr;
				}else{
					result = sse;
				}
			
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
