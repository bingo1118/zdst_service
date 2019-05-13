package com.cloudfire.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import com.cloudfire.dao.AllCameraDao;
import com.cloudfire.dao.impl.AllCameraDaoImpl;
import com.cloudfire.entity.AllCameraEntity;
import com.cloudfire.entity.AllSmokeEntity;
import com.cloudfire.entity.HttpRsult;
import com.opensymphony.xwork2.ActionSupport;

public class AllCameraAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{

	private static final long serialVersionUID = 3408703400496801175L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private AllCameraDao mAllCameraDao;
	
	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}

	public void getAllCamera(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String page = this.request.getParameter("page");
			String everypagenum = this.request.getParameter("everypagenum");//@@每页设备数
			int everypage=20;
			if(everypagenum!=null){
				if(everypagenum.length()>0){
					everypage=Integer.parseInt(everypagenum);
				}
			}
			AllCameraEntity ace = null;
			HttpRsult hr = null;
			Object result = null;
			if(userId==null||privilege==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mAllCameraDao = new AllCameraDaoImpl();
				if(privilege.equals("1")){
					ace = mAllCameraDao.getPrivi1Camera(userId, privilege, page,everypage);
				}else{
					ace = mAllCameraDao.getAllCamera(userId, privilege, page,everypage);
				}
				if(ace!=null){
					ace.setError("获取摄像头成功");
					ace.setErrorCode(0);
					result = ace;
				}else{
					hr = new HttpRsult();
					hr.setError("获取摄像头失败");
					hr.setErrorCode(2);
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
