package com.cloudfire.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import com.cloudfire.dao.BindCameraSmokeDao;
import com.cloudfire.dao.impl.AddCameraDaoImpl;
import com.cloudfire.dao.impl.BindCameraSmokeDaoImpl;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.until.Utils;
import com.opensymphony.xwork2.ActionSupport;

public class BindCameraSmokeAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{

	private static final long serialVersionUID = 4130487874819582809L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private BindCameraSmokeDao mBindCameraSmokeDao;
	
	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}
	
	public void bindCameraSmoke(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String cameraId = this.request.getParameter("cameraId");
			String smoke = this.request.getParameter("smoke");
			String cameraChannel=this.request.getParameter("cameraChannel");
			if(!Utils.isNullStr(cameraChannel)){
				cameraChannel = "0";
			}
			HttpRsult hr = null;
			Object result = null;
			if(cameraId==null||smoke==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mBindCameraSmokeDao = new BindCameraSmokeDaoImpl();
				hr = mBindCameraSmokeDao.bindCameraSmoke(cameraId, smoke, cameraChannel);
				if(hr==null){
					hr = new HttpRsult();
					hr.setError("绑定失败");
					hr.setErrorCode(2);
					result = hr;
				}else{
					result = hr;
				}
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	// by liao zw 2017.11.5  电表
	public  void elecMeterBindDeviceUser(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("gbk");
		try {
			this.request.setCharacterEncoding("utf-8");
			String  user=this.request.getParameter("user");
			String device=this.request.getParameter("device");
			HttpRsult hrHttpRsult=new HttpRsult();
			Object result=null;
			if(user!=null && user.length()>0){
				mBindCameraSmokeDao = new BindCameraSmokeDaoImpl();
				boolean ret=mBindCameraSmokeDao.meterBindUserDevce(user, device);
				if(ret){
					hrHttpRsult.setError("绑定成功");
					hrHttpRsult.setErrorCode(0);
					result=hrHttpRsult;
				}
				else{
					hrHttpRsult.setError("绑定失败");
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
}
