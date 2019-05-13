package com.cloudfire.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.entity.AreaIdEntity;
import com.cloudfire.entity.HttpRsult;
import com.opensymphony.xwork2.ActionSupport;

public class HaiManDataAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{
	
	private static final long serialVersionUID = -8059072481196956604L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private AreaDao mAreaDao;
	
	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}
	
	public List<String> ifLoginSuccess(String userid,String password){
		
		return null;
	}
	
	public void HMLogin(){
		/*this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String page = this.request.getParameter("page");
			AreaIdEntity aet = null;
			HttpRsult hr = null;
			Object result = null;
			if(userId==null||privilege==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mAreaDao = new AreaDaoImpl();
				aet = mAreaDao.getAreaByUserId(userId, privilege, page);
				if(aet!=null){
					aet.setError("获取区域id成功");
					aet.setErrorCode(0);
					result = aet;
				}else{
					hr = new HttpRsult();
					hr.setError("获取区域id失败");
					hr.setErrorCode(2);
					result = hr;
				}
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}*/
	}
	
	public void HMloginOut(){
		
	}
	//条件查询获取用户报警消息
	public void HMgetNeedAlarm(){
		
	}
	
	//获取用户报警消息
	public void HMgetAllAlarm(){
		
	}
	
	//添加烟感 
	public void HMaddSmoke(){
		
	}
	
	//根据条件查询用户设备
	public void HMgetNeedDev(){
		
	}
	
	//根据条件查询用户离线设备 
	public void HMgetNeedLossDev(){
		
	}
	
	//获取设备总数，在线数和故障数 
	public void HMgetDevSummary(){
		
	}
	
	//获取所有的商铺类型 
	public void HMgetPlaceTypeId(){
		
	}
	
	//获取区域列表
	public void HmgetAreaId(){
		
	}
	
}
