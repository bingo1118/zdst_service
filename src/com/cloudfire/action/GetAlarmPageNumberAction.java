package com.cloudfire.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import com.cloudfire.dao.AllAlarmDao;
import com.cloudfire.dao.impl.AllAlarmDaoImpl;
import com.cloudfire.entity.AlarmPageNumberEntity;
import com.cloudfire.entity.AllAlarmEntity;
import com.cloudfire.entity.HttpRsult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.xwork2.ActionSupport;

public class GetAlarmPageNumberAction extends ActionSupport implements ServletResponseAware,ServletRequestAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = -744489654519400123L;
	private HttpServletResponse response;
	private HttpServletRequest request;
	private AllAlarmDao mAllAlarmDao;

	@Override
	public void setServletRequest(HttpServletRequest arg0) {
		// TODO Auto-generated method stub
		this.request=arg0;
	}

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub
		this.response=arg0;
	}
	
	/**
	 * 
	 */
	public void getAlarmPageNumber(){
		
		this.response.setCharacterEncoding("gbk");
		this.response.setContentType("text/html;charset=utf-8");
		try{
			this.request.setCharacterEncoding("utf-8");
			String userIdString = this.request.getParameter("userId");
			String privilegeString = this.request.getParameter("privilege");
			String startTime = this.request.getParameter("startTime");
			String endTime = this.request.getParameter("endTime");
			String areaId = this.request.getParameter("areaId");
			String placeTypeId = this.request.getParameter("placeTypeId");
			AlarmPageNumberEntity alarmPageNumberEntity=new AlarmPageNumberEntity();;
			HttpRsult hr;
			Object result;
			if(userIdString==null||privilegeString==null){
				hr=new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result=hr;
			}else{
				mAllAlarmDao=new AllAlarmDaoImpl();
				int alarmNumber=mAllAlarmDao.getAdminAllAlarmPageNumber(userIdString,privilegeString,startTime,endTime,areaId,placeTypeId);
				int pageNumber=0;
				if(alarmNumber%20==0){
					pageNumber=alarmNumber/20;
				}
				else{
					pageNumber=alarmNumber/20+1;
				}
				
				alarmPageNumberEntity.setPageNumber(pageNumber);
				alarmPageNumberEntity.setAlarmNumber(alarmNumber);
				alarmPageNumberEntity.setError("获取报警页数成功");
				alarmPageNumberEntity.setErrorCode(0);
				result=alarmPageNumberEntity;
			}
			JSONObject jsonObject=new JSONObject(result);
			this.response.getWriter().write(jsonObject.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		
	}

}
