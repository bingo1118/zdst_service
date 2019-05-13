package com.cloudfire.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import com.cloudfire.dao.CameraPwdDao;
import com.cloudfire.dao.VideoDao;
import com.cloudfire.dao.impl.CameraPwdDaoImpl;
import com.cloudfire.dao.impl.VideoDaoImpl;
import com.cloudfire.entity.HK_response;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.OneNetResponse;
import com.cloudfire.until.HKUtil;
import com.cloudfire.until.Utils;
import com.opensymphony.xwork2.ActionSupport;

public class CameraPwdAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{

	private static final long serialVersionUID = 2742871161658049563L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private CameraPwdDao mCameraPwdDao;
	
	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}
	
	public void changeCameraPwd(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String cameraId = this.request.getParameter("cameraId");
			String cameraPwd = this.request.getParameter("cameraPwd");
			HttpRsult hr = null;
			Object result = null;
			if(!Utils.isNullStr(cameraId)||!Utils.isNullStr(cameraPwd)){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mCameraPwdDao = new CameraPwdDaoImpl();
				hr = mCameraPwdDao.changeCameraPwd(cameraId, cameraPwd);
				if(hr==null){
					hr = new HttpRsult();
					hr.setError("修改摄像机密码失败");
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
	
	//通过别名获取预览url
//	public void getUrlByName() {
//		String name = request.getParameter("name");
//		HttpRsult hr = new HttpRsult();
//		VideoDao vd = new VideoDaoImpl();
//		String indexCode = vd.getIndexByName(name);
//		if (StringUtils.isNotBlank(indexCode)) {
//			HK_response resp = HKUtil.postGetStream(indexCode);
//			if ("0".equals(resp.getCode())){ //成功获取到url
//				hr.setErrorCode(0);
//				hr.setError(resp.getData().get("url"));
//			} else {
//				hr.setErrorCode(1);
//				hr.setError("获取预览流失败");
//			}
//		} else {
//			hr.setError("没有该名字的摄像头");
//			hr.setErrorCode(2);
//		}
//		
//		JSONObject jObject = new JSONObject(hr);
//		try {
//			this.response.getWriter().write(jObject.toString());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
