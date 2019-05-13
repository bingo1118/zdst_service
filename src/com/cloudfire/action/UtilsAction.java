package com.cloudfire.action;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.ExportExcelDao;
import com.cloudfire.dao.InfoManagerDao;
import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.RePeaterDataDao;
import com.cloudfire.dao.SmartControlDao;
import com.cloudfire.dao.SmokeLineDao;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.ExportExcelDaoImpl;
import com.cloudfire.dao.impl.InfoManagerDaoImpl;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.cloudfire.dao.impl.RePeaterDataDaoImpl;
import com.cloudfire.dao.impl.SmartControlDaoImpl;
import com.cloudfire.dao.impl.SmokeLineDaoImpl;
import com.cloudfire.dao.impl.UserLongerDaoImpl;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.HttpRsultUser;
import com.cloudfire.entity.LoginEntity;
import com.cloudfire.entity.SmartControlEntity;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.TimerMap;
import com.cloudfire.thread.MQTTMemo;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.Utils;
import com.opensymphony.xwork2.ActionSupport;

public class UtilsAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{
	
	private static final long serialVersionUID = -4581514309685063419L;
	private HttpServletRequest request;
	private HttpServletResponse response;  
	private ExportExcelDao exportExcel;
	private SmokeLineDao mSmokeLineDao;
	private AreaDao mAreaDao;
	private SmartControlDao smartControlDao;
	private TimerMap mTimerMap;
	
	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}
	
	public void getPoolCount(){
		StringBuffer sb = new StringBuffer();
		sb.append("\"total\":"+DBConnectionManager.getIConnectionsCount(1)+",");
		sb.append("\"busy\":"+DBConnectionManager.getIConnectionsCount(2)+",");
		sb.append("\"idle\":"+DBConnectionManager.getIConnectionsCount(3));
		HttpRsult hr = new HttpRsult();
		hr.setError(sb.toString());
		hr.setErrorCode(0);
		JSONObject jObject = new JSONObject(hr);
		try {
			this.response.getWriter().write(jObject.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setUpLoraWan(){
		MQTTMemo memo = new MQTTMemo();
		memo.subscribe();
		memo.publish();
		HttpRsult hr = new HttpRsult();
		hr.setError("starting business!");
		hr.setErrorCode(0);
	}
	public void setUserId(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			HttpRsult hr = null;
			Object result = null;
			if(!Utils.isNullStr(userId)){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				LoginDao mLoginDao = new LoginDaoImpl();
				mLoginDao.updateLoginState(userId, 0, null);
				hr = new HttpRsult();
				hr.setError("设置成功,可以重新登录了。");
				hr.setErrorCode(0);
				result = hr;
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void updateTxt(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		HttpRsult hr = null;
		Object result = null;
		String userid=request.getParameter("userid");
		int txtState=Integer.parseInt(request.getParameter("txtState"));
		int telState=Integer.parseInt(request.getParameter("telState"));
		int sum=txtState+telState;
		UserLongerDaoImpl userimpl =new UserLongerDaoImpl();
		boolean flag=userimpl.updateUserTxtState(userid, sum);
		if(flag) {
			if(sum==1){
				hr = new HttpRsult();
				hr.setError("开通短信通知功能成功");
				hr.setErrorCode(0);
				result = hr;
			}else if(sum==2){
				hr = new HttpRsult();
				hr.setError("开通电话通知功能成功");
				hr.setErrorCode(0);
				result = hr;
			}else if(sum==3){
				hr = new HttpRsult();
				hr.setError("开通短信、电话通知功能成功");
				hr.setErrorCode(0);
				result = hr;
			}else{
				hr = new HttpRsult();
				hr.setError("关闭电话、短信通知功能成功");
				hr.setErrorCode(0);
				result = hr;
			}
			
		} else { 
			hr = new HttpRsult();
			hr.setError("开通/关闭短信通知功能失败");
			hr.setErrorCode(1);
			result = hr;
		}
		
		
		JSONObject jObject = new JSONObject(result);
		try {
			this.response.getWriter().write(jObject.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void updateTxt_area(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		HttpRsult hr = null;
		Object result = null;
		String areaid=request.getParameter("user");
		int txtState=Integer.parseInt(request.getParameter("txt"));
		int telState=Integer.parseInt(request.getParameter("tel"));
		int sum=txtState+telState;
		AreaDao ad =new AreaDaoImpl();
		/*//先根据区域查看该区域之前的电话短信开通情况
		int istxt = ad.getIsTxtById(Integer.parseInt(areaid));*/
		boolean flag=ad.updateAreaTxtState(Integer.parseInt(areaid), sum);
		if(flag) {
			/*if(istxt == 0){
				
			}else if(istxt == 1){
				
			}else if(istxt == 2){
				
			}*/
			if(sum==1){
				hr = new HttpRsult();
				hr.setError("开通短信通知功能成功");
				hr.setErrorCode(0);
				result = hr;
			}else if(sum==2){
				hr = new HttpRsult();
				hr.setError("开通电话通知功能成功");
				hr.setErrorCode(0);
				result = hr;
			}else if(sum==3){
				hr = new HttpRsult();
				hr.setError("开通短信、电话通知功能成功");
				hr.setErrorCode(0);
				result = hr;
			}else{
				hr = new HttpRsult();
				hr.setError("关闭电话、短信通知功能成功");
				hr.setErrorCode(0);
				result = hr;
			}
				/*if(telState==0){
					hr = new HttpRsult();
					hr.setError("关闭电话通知功能成功");
					hr.setErrorCode(0);
					result = hr;
				}
				if(txtState==0){
					hr = new HttpRsult();
					hr.setError("关闭短信通知功能成功");
					hr.setErrorCode(0);
					result = hr;
				}*/
		} else { 
			hr = new HttpRsult();
			hr.setError("开通/关闭短信通知功能失败");
			hr.setErrorCode(1);
			result = hr;
		}
		JSONObject jObject = new JSONObject(result);
		try {
			this.response.getWriter().write(jObject.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void ExportExcel(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			String methodName = request.getParameter("methodName");
			String currentId =  request.getParameter("currentId");
			
			String path = "C:\\excel\\";
			if(methodName.equals("infoWeb")||methodName == "infoWeb"){
				path = path + "infoWeb.xls";
				LoginDao ld = new LoginDaoImpl();
				LoginEntity mLoginEntity = ld.login(currentId);
				if(mLoginEntity!=null){
					int privilege = mLoginEntity.getPrivilege();
					InfoManagerDao companydao = new InfoManagerDaoImpl();
					
					List<SmokeBean> list = new ArrayList<SmokeBean>();
					if(privilege == 4){
						list = companydao.getAllSmokesInfo(null);
					}else{
						list = companydao.getAllSmokesInfo(currentId);
					}
					exportExcel = new ExportExcelDaoImpl();
					exportExcel.exportExcel(path, list);
				}
			}else if(methodName.equals("allDevice")||methodName == "allDevice"){
				path = path + "alldevice.xls";
				LoginDao ld = new LoginDaoImpl();
				LoginEntity mLoginEntity = ld.login(currentId);
				if(mLoginEntity!=null){
					int privilege = mLoginEntity.getPrivilege();
					mAreaDao = new AreaDaoImpl();
					smartControlDao = new SmartControlDaoImpl();
					List<String> areaIds = mAreaDao.getAreaStr(currentId, privilege+"");
					List<SmartControlEntity> list = new ArrayList<SmartControlEntity>();
					list = smartControlDao.getAllDeviceInfo(areaIds);
					exportExcel = new ExportExcelDaoImpl();
					exportExcel.exportExcelByAllDevice(path, list);
				}
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void falushRepeater(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			String repeaterMac = request.getParameter("repeaterMac");
			if(Utils.isNullStr(repeaterMac)){
				RePeaterDataDao rdao = new RePeaterDataDaoImpl();
				String repeaterTime = rdao.getRepeaterTime(repeaterMac);
				if(repeaterTime!=null&&!"".equals(repeaterTime)){
					long beforeTime = Utils.getTimeByStr(repeaterTime);
					boolean result = GetTime.ifOffLine(beforeTime);
					mSmokeLineDao = new SmokeLineDaoImpl();
					if(result){
						mSmokeLineDao.RepeaterOffLine(repeaterMac,0);
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void gotovideo(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			//response.sendRedirect("http://www.baidu.com");
			URL url = new URL("http://127.0.0.1:8088/LogOn/doLogin?userName=admin&password=admin&port=6003");
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setConnectTimeout(10000);
			conn.setRequestMethod("GET");
//			conn.setdoi
			
			
			
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取主机信息进行验证，用作于客户对接
	 */
	public void getHostInfo(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		HttpRsultUser hr = null;
		String host=request.getParameter("host");
		UserLongerDaoImpl userimpl =new UserLongerDaoImpl();
		hr = userimpl.getHostInfo(host);
		if(hr==null) {
			hr = new HttpRsultUser();
			hr.setError("0");
			hr.setErrorCode(0);
		}
		
		JSONObject jObject = new JSONObject(hr);
		try {
			this.response.getWriter().write(jObject.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
