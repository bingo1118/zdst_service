package com.cloudfire.action;

import java.util.Timer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import com.cloudfire.entity.TimerMap;
import com.cloudfire.until.LzstoneTimeTask;
import com.opensymphony.xwork2.ActionSupport;

public class TimeAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{
	
	private static final long serialVersionUID = -4581514309685063419L;
	private HttpServletRequest request;
	private HttpServletResponse response;  
	private TimerMap mTimerMap;
	
	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}
	
	public void getTime(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String code = this.request.getParameter("code");
			String result = "error";
			mTimerMap = TimerMap.newInstance();
			if(userId.equals("open")){
				Timer timer = new Timer();     
	            timer.schedule(new LzstoneTimeTask(),30000,1*3000);    
	            result = "open";
	            mTimerMap.addTimer(code, timer);
			}
			
			if(userId.equals("colsed")){
				Timer timer = mTimerMap.getTimer(code);
				if(timer!=null){
					timer.cancel();
					timer=null;
					result = "colsed";
				}
				 
			}
			this.response.getWriter().write(result);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
