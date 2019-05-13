package com.cloudfire.action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.mina.core.session.IoSession;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.ToolDao;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.cloudfire.dao.impl.ToolDaoImpl;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.SessionMap;
import com.cloudfire.thread.AckElecContrThread;
import com.cloudfire.until.Client_Fault_Package;
import com.cloudfire.until.Utils;
import com.opensymphony.xwork2.ActionSupport;

public class ZJAction extends ActionSupport implements ServletRequestAware,ServletResponseAware{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HttpServletRequest request;
	private HttpServletResponse response;

	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}
	
	public void ZJlogin(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String appId= this.request.getParameter("appId");
			String secret= this.request.getParameter("secret");
			JSONObject json = new JSONObject();
			if(appId==null||appId.equals("")){
		        json.put("error_code", 100208);
			}else{
				LoginDao login = new LoginDaoImpl();
		    	ToolDao tool = new ToolDaoImpl();
		    	String accessToken = login.getEaseIotAccessToKen(appId, secret);
				if(accessToken==null||accessToken.equals("")){
					json.put("error_code", 100208);
				}else{
					json.put("scope", "default");
					json.put("tokenType", "bearer");
					json.put("expiresIn", 24*60*60);
					json.put("accessToken", accessToken);
				}
			}
			JSONObject jObject = new JSONObject(json.toString());
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void ZJAddDevice(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String appId= this.request.getParameter("appId");
			String accessToken= this.request.getParameter("accessToken");
			String imei= this.request.getParameter("imei");
			String name=System.currentTimeMillis()+"";
			String deviceType = "NB_Hr002";
			String serviceMode = "CoAP";
			String endUserName = "ÖÇ¼Ò";
			String endUserInfo = "";
			
			String connectPointId = "ctc-nanjing-iot-137";
			String iotserverId = "ctc-nanjing-iot-137";
			
			JSONObject json = new JSONObject();
	    	ToolDao tool = new ToolDaoImpl();
	    	String isSuccess=tool.registeredPlant(iotserverId, accessToken, imei, name, deviceType, 
	    			connectPointId, serviceMode, endUserName,
	    			endUserInfo, "", "", "", "");
			json.put("deviceId", imei);
			if(isSuccess.equals("0")){
				json.put("result", true);
			}else{
				json.put("result", false);
			}
			
			JSONObject jObject = new JSONObject(json.toString());
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void ZJDeleteDevice(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String appId= this.request.getParameter("appId");
			String accessToken= this.request.getParameter("accessToken");
			String imei= this.request.getParameter("imei");
			String iotserverId = "ctc-nanjing-iot-137";
			
			JSONObject json = new JSONObject();
	    	ToolDao tool = new ToolDaoImpl();
	    	String isSuccess=tool.deleteDevice(iotserverId, accessToken, imei);
	    	if(isSuccess.equals("0")){
				json.put("result", true);
			}else{
				json.put("result", false);
			}
			
			JSONObject jObject = new JSONObject(json.toString());
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void ZJSendCommand(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String appId= this.request.getParameter("appId");
			String accessToken= this.request.getParameter("accessToken");
			String imei= this.request.getParameter("deviceId");
			String command= this.request.getParameter("command");
			String key= this.request.getParameter("key");
			String value= this.request.getParameter("value");
			
			JSONObject json = new JSONObject();
	    	ToolDao tool = new ToolDaoImpl();
	    	String isSuccess=tool.ackAccessToKen(accessToken, appId, imei, command, key,Integer.parseInt(value));
			json.put("result", isSuccess);
			
			JSONObject jObject = new JSONObject(json.toString());
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void ZJSubscribeCallback(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String accessToken= this.request.getParameter("accessToken");
			String callbackUrl= this.request.getParameter("callbackUrl");
			boolean isSuccess=true;
			String sql = "INSERT INTO easyiot_callbackurl (company,callbackUrl) VALUES (?,?)  ON DUPLICATE KEY UPDATE callbackUrl=?";
			Connection conn = DBConnectionManager.getConnection();
			PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
			try {
				ps.setString(1, "zj");
				ps.setString(2, callbackUrl);
				ps.setString(3, callbackUrl);
				ps.executeUpdate();
			} catch (Exception e) {
				// TODO: handle exception
				isSuccess=false;
				e.printStackTrace();
			} finally{
				DBConnectionManager.close(ps);
				DBConnectionManager.close(conn);
			}
			
			JSONObject json = new JSONObject();
			json.put("result", isSuccess);
			
			JSONObject jObject = new JSONObject(json.toString());
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
