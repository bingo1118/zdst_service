package com.cloudfire.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import com.cloudfire.dao.AddCameraDao;
import com.cloudfire.dao.impl.AddCameraDaoImpl;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.until.Utils;
import com.opensymphony.xwork2.ActionSupport;

public class AddCameraAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{

	private static final long serialVersionUID = 4110157918677908234L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private AddCameraDao mAddCameraDao;
	
	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}

	
	public void addCamera(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
//cameraId=13622215085&cameraName=&cameraPwd=&cameraAddress=&longitude=&latitude=&principal1=&principal1Phone=&principal2=&principal2Phone=&areaId=&placeTypeId=
			this.request.setCharacterEncoding("utf-8");
			String cameraId = this.request.getParameter("cameraId");
			String cameraName = this.request.getParameter("cameraName");
			String cameraPwd = this.request.getParameter("cameraPwd");
			String cameraAddress = this.request.getParameter("cameraAddress");
			String longitude = this.request.getParameter("longitude");
			String latitude = this.request.getParameter("latitude");
			String principal1 = this.request.getParameter("principal1");
			String principal1Phone = this.request.getParameter("principal1Phone");
			String principal2 = this.request.getParameter("principal2");
			String principal2Phone = this.request.getParameter("principal2Phone");
			String areaId = this.request.getParameter("areaId");
			String placeTypeId = this.request.getParameter("placeTypeId");
			String videoPositon=this.request.getParameter("videoPositon");
			String videoSize=this.request.getParameter("videoSize");
			if(!Utils.isNullStr(videoPositon)){
				videoPositon = "0";
			}
			if(!Utils.isNullStr(videoSize)){
				videoSize = "2";
			}
			
			HttpRsult hr = null;
			Object result = null;
			if(cameraId==null||cameraPwd==null){
				hr = new HttpRsult();
				hr.setError("²ÎÊý´íÎó");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mAddCameraDao = new AddCameraDaoImpl();
				hr = mAddCameraDao.addCamera(cameraId, cameraName, cameraPwd, cameraAddress, longitude, latitude, principal1, principal1Phone, principal2, principal2Phone, areaId, placeTypeId, videoPositon, videoSize);
				if(hr==null){
					hr = new HttpRsult();
					hr.setError("Ìí¼ÓÉãÏñ»úÊ§°Ü");
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
}
