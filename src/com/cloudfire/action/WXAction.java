package com.cloudfire.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;


import com.cloudfire.dao.AddSmokeDao;
import com.cloudfire.dao.AllAlarmDao;
import com.cloudfire.dao.AllSmokeDao;
import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.BindUserIdIosDao;
import com.cloudfire.dao.GetSmokeMacByRepeaterDao;
import com.cloudfire.dao.KeepSystemDao;
import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.NeedSmokeDao;
import com.cloudfire.dao.OneElectricInfoDao;
import com.cloudfire.dao.SmokeLineDao;
import com.cloudfire.dao.SmokeSummaryDao;
import com.cloudfire.dao.impl.AddSmokeDaoImpl;
import com.cloudfire.dao.impl.AllAlarmDaoImpl;
import com.cloudfire.dao.impl.AllSmokeDaoImpl;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.BindUserIdIosDaoImpl;
import com.cloudfire.dao.impl.GetSmokeMacByRepeaterDaoImpl;
import com.cloudfire.dao.impl.KeepSystemDaoImpl;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.cloudfire.dao.impl.NeedSmokeDaoImpl;
import com.cloudfire.dao.impl.OneElectricInfoDaoImpl;
import com.cloudfire.dao.impl.SmokeSummaryDaoImpl;
import com.cloudfire.db.RedisConnection;
import com.cloudfire.db.RedisOps;
import com.cloudfire.entity.AllAlarmEntity;
import com.cloudfire.entity.AllAreaEntity;
import com.cloudfire.entity.AllRepeaterEntity;
import com.cloudfire.entity.AllSmokeEntity;
import com.cloudfire.entity.AreaIdEntity;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.LoginEntity;
import com.cloudfire.entity.OneAlarmEntity;
import com.cloudfire.entity.OneElectricEntity;
import com.cloudfire.entity.Repeater;
//import com.cloudfire.entity.SmokeMap;
import com.cloudfire.entity.SmokeSummaryEntity;
import com.cloudfire.until.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.xwork2.ActionSupport;

public class WXAction extends ActionSupport implements 
ServletRequestAware,ServletResponseAware{


	private static final long serialVersionUID = 1L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private NeedSmokeDao mNeedSmokeDao;
	private LoginDao loginDao;
	private SmokeSummaryDao mSmokeSummaryDao;
	private AreaDao areaDao;
	private AllAlarmDao mAllAlarmDao;
	private AreaDao mAreaDao;
	private AddSmokeDao mAddSmokeDao;
	private GetSmokeMacByRepeaterDao mGetSmokeMacByRepeaterDao;
	private OneElectricInfoDao mOneElectricInfoDao;
	private AllSmokeDao mAllSmokeDao;

	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}

	public void login(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("UTF-8");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String pwd = this.request.getParameter("pwd");
			String bindCid = this.request.getParameter("cid");	//add by lzz at 2017-5-16	个推cid
			//String alias = this.request.getParameter("alias");	//add by lzz at 2017-5-16	个推别名
			String ios = this.request.getParameter("ios");		//add by lzz at 2017-5-18	个推ios绑定
			String appIdstr = this.request.getParameter("appId");
			String ifregister=this.request.getParameter("ifregister");//@@20180315技威是否注册 0否 1是
			int appId = 0;
			if(Utils.isNullStr(appIdstr)){
				appId = Integer.parseInt(appIdstr);		//add by lzz at 2017-5-19 识别app
			}
			
			
			LoginEntity le = null;
			HttpRsult hr = null;
			Object result = null;
			if(userId!=null&&userId.length()>0){
				loginDao = new LoginDaoImpl();
				le = loginDao.login2(userId,pwd);
				
				if(le==null){
					hr = new HttpRsult();
					hr.setError("没有此用户");
					hr.setErrorCode(2);
					result = hr;
				}else{
					if(Utils.isNullStr(ios)){			//add by lzo at 2017-5-18 用于绑定ios入库
						BindUserIdIosDao bindUserIdIosDao = new BindUserIdIosDaoImpl();
						bindUserIdIosDao.bindUserIdIos(userId, ios);
					}
					loginDao.updateUserAppId(userId, appId);		//add by lzz at 2017-5-19
					loginDao.savePushBindCid(userId, bindCid, 1);	//保存入库
					result = le;
					if(Utils.isNullStr(pwd)&&((LoginEntity)result).getErrorCode()==0){
						loginDao.loginToYooSee(userId, pwd); //add by lzz at 2017-5-15 用于保存密码
					}
				}
			}else{
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	//@@ by liangbin 2017.07.19
	public void getNeedDev() {
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("UTF-8");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String page  = this.request.getParameter("page");
			String areaId = this.request.getParameter("areaId");
			String placeTypeId  = this.request.getParameter("placeTypeId");
			String devType = this.request.getParameter("devType");//@@
			String parentId = this.request.getParameter("parentId");//@@父区域
			AllSmokeEntity ase = null;
			HttpRsult hr = null;
			Object result = null;
			if(userId==null||privilege==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mNeedSmokeDao = new NeedSmokeDaoImpl();
				if(privilege.equals("1")){//@@9.29 1级权限查询
					ase = mNeedSmokeDao.getAdminAllSmoke(userId, privilege,page,devType);
				}else{
					ase = mNeedSmokeDao.getNeedDev(userId, privilege, page, areaId, placeTypeId,devType,parentId);
				}
				if(ase==null){
					hr = new HttpRsult();
					hr.setError("没有数据");
					hr.setErrorCode(2);
					result = hr;
				}else{
					ase.setError("获取成功");
					ase.setErrorCode(0);
					result = ase;
				}
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	//@@ liangbin 2017.07.19
	public void getNeedLossDev() {
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("utf-8");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String page  = this.request.getParameter("page");
			String areaId = this.request.getParameter("areaId");
			String placeTypeId  = this.request.getParameter("placeTypeId");
			String devType = this.request.getParameter("devType");//@@
			String parentId = this.request.getParameter("parentId");//@@父区域
			AllSmokeEntity ase = null;
			HttpRsult hr = null;
			Object result = null;
			if(userId==null||privilege==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mNeedSmokeDao = new NeedSmokeDaoImpl();
				ase = mNeedSmokeDao.getNeedLossDev(userId, privilege, page, areaId, placeTypeId,devType,parentId);
				if(ase==null){
					hr = new HttpRsult();
					hr.setError("没有烟感");
					hr.setErrorCode(2);
					result = hr;
				}else{
					ase.setError("获取烟感成功");
					ase.setErrorCode(0);
					result = ase;
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
			this.response.setCharacterEncoding("utf-8");
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
		
		public void getAreaInfo2(){
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("utf-8");
			try {
				this.request.setCharacterEncoding("utf-8");
				String userId = this.request.getParameter("userId");
				String privilege = this.request.getParameter("privilege");
				HttpRsult hr = null;
				AllAreaEntity ase = null;
				Object result = null;
				if(userId==null){
					hr = new HttpRsult();
					hr.setError("参数错误");
					hr.setErrorCode(1);
					result = hr;
				}else{
					areaDao = new AreaDaoImpl();
					ase = areaDao.getAllAreas(userId, privilege);
					if(ase==null){
						hr = new HttpRsult();
						hr.setError("参数错误");
						hr.setErrorCode(1);
						result = hr;
					}else{
						result=ase;
					}
				}
				JSONObject jObject = new JSONObject(result);
				this.response.getWriter().write(jObject.toString());
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
		public void testAction(){
			this.response.setContentType("application/json");
			this.response.setCharacterEncoding("utf-8");
			System.out.println("uploadDevices:数据上传回调====启用");
			String result = getRequestPostData(this.request);
			System.out.println("数据回调结果："+result);
		}
		
		private String getRequestPostData(HttpServletRequest request) {
			String result = "";
			int contentLength = request.getContentLength()+1;
			System.out.println("数据调用getRequestPostData方法"+contentLength);
			if(contentLength<0){
				return null;
			}
			byte buffer[] = new byte[contentLength];
			try {
				for (int i = 0; i <contentLength; i++) {
					int len = request.getInputStream().read(buffer,0,contentLength-1);
					if(len == -1){
						break;
					}
					i += len;
				}
				result = new String(buffer,"utf-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}
		
		public void getNeedAlarm(){
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("utf-8");
			try {
				this.request.setCharacterEncoding("utf-8");
				String userId = this.request.getParameter("userId");
				String page = this.request.getParameter("page");
				String privilege = this.request.getParameter("privilege");
				String startTime = this.request.getParameter("startTime");
				String endTime = this.request.getParameter("endTime");
				String parentId=this.request.getParameter("parentId");//@@父区域
				String areaId = this.request.getParameter("areaId");
				String placeTypeId = this.request.getParameter("placeTypeId");
				HttpRsult hr = null;
				Object result = null;
				AllAlarmEntity aae = null;
				if(userId==null||privilege==null){
					hr = new HttpRsult();
					hr.setError("参数错误");
					hr.setErrorCode(1);
					result = hr;
				}else{
					mAllAlarmDao = new AllAlarmDaoImpl();
					if(privilege.equals("1")){
						aae = mAllAlarmDao.getNormalNeedAlarm(userId, privilege, page, startTime, endTime);
					}else{
						aae = mAllAlarmDao.getAdminNeedAlarm(userId, privilege, page, startTime, endTime, areaId, placeTypeId,parentId);
					}
					
					if(aae==null){
						hr = new HttpRsult();
						hr.setError("没有报警消息");
						hr.setErrorCode(2);
						result = hr;
					}else{
						aae.setError("获取报警消息成功");
						aae.setErrorCode(0);
						result = aae;
					}
				}
				ObjectMapper mapper = new ObjectMapper();  
		        String json = mapper.writeValueAsString(result); 
		        this.response.getWriter().write(json);
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
		public void getAreaId(){
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("utf-8");
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
			}
				
		}
		
		public void addSmoke(){
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("utf-8");
			try {
				this.request.setCharacterEncoding("utf-8");
				
				String userId = this.request.getParameter("userId");
				String smokeMac = this.request.getParameter("smokeMac");
				String privilege = this.request.getParameter("privilege");
				String smokeName = this.request.getParameter("smokeName");
				String address = this.request.getParameter("address");
				String longitude = this.request.getParameter("longitude");
				String latitude = this.request.getParameter("latitude");
				String placeAddress = this.request.getParameter("placeAddress");
				String placeTypeId = this.request.getParameter("placeTypeId");
				String principal1 = this.request.getParameter("principal1");
				String principal1Phone = this.request.getParameter("principal1Phone");
				String principal2 = this.request.getParameter("principal2");
				String principal2Phone = this.request.getParameter("principal2Phone");
				String areaId = this.request.getParameter("areaId");
				String repeater = this.request.getParameter("repeater");
				String camera = this.request.getParameter("camera");
				String deviceType = this.request.getParameter("deviceType");
				String cameraChannel=this.request.getParameter("cameraChannel");
				String electrState=this.request.getParameter("electrState");//电气开关状态
				
				if(electrState==null||electrState.equals("")){
					electrState="0";
				}
				HttpRsult hr = null;
				Object result = null;
				KeepSystemDao keepSystemDao=new  KeepSystemDaoImpl();
				String oldRepeaterString=keepSystemDao.getRepeaterOfSmoke(smokeMac);
				if(userId==null||smokeMac==null){
					hr = new HttpRsult();
					hr.setError("参数错误");
					hr.setErrorCode(1);
					result = hr;
				}else{
					mAddSmokeDao = new AddSmokeDaoImpl();
					hr = mAddSmokeDao.addSmoke(userId, smokeMac, privilege, smokeName, address, longitude, latitude,
							placeAddress, placeTypeId, principal1, principal1Phone, principal2, principal2Phone, 
							areaId, repeater, camera, deviceType,cameraChannel,electrState,"");
					if(hr==null){
						hr = new HttpRsult();
						hr.setError("添加烟感失败");
						hr.setErrorCode(2);
						result = hr;
					}else{
						mGetSmokeMacByRepeaterDao = new GetSmokeMacByRepeaterDaoImpl();
						if(Utils.isNullStr(repeater)&&!deviceType.equals("9")&&hr.getErrorCode()==0){
							Utils.sendRepeaterList(repeater);
						}
						if(Utils.isNullStr(repeater)&&!repeater.equals(oldRepeaterString)){
							Utils.sendRepeaterList(oldRepeaterString);
						}
						//更新添加设备redis的离线列表
						if(StringUtils.isNotBlank(repeater)){
							Utils.updateOffSmokeList( repeater, deviceType, smokeMac, oldRepeaterString);
						}
//						if(!deviceType.equals("5")){ //非电气设备的添加才要对内存中的smokeMap操作  edit by yfs @12/8/2017 9:16
//							SmokeMap.newInstance().updateSmokeMap(repeater, smokeMac);
							
//							Long addTime = System.currentTimeMillis();
//							mSmokeMap = SmokeMap.newInstance();
//							Map<String, Map<String,Long>> map = mSmokeMap.getSmokeMap();
//							Map<String,Long> smokeMap = map.get(repeater);
//							if(smokeMap == null){
//								Map<String,Long> newSmokes = new HashMap<String,Long>();
//								mSmokeLineDao = new SmokeLineDaoImpl();
//								newSmokes = mSmokeLineDao.RepeaterLoss(repeater);
//								mSmokeMap.addSmokeMap(repeater, newSmokes);
//								smokeMap = map.get(repeater);
//							}
//							if(smokeMap.containsKey(smokeMac)){
//								smokeMap.remove(smokeMac);
//								smokeMap.put(smokeMac, addTime);
//							}else{
//								smokeMap.put(smokeMac, addTime);
//							}
//							if(map.containsKey(repeater)){
//								map.remove(repeater);
//								map.put(repeater, smokeMap);
//							}else{
//								map.put(repeater, smokeMap);
//							}
//						}
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
		
		public void getLastestAlarm(){
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("utf-8");
			try {
				this.request.setCharacterEncoding("utf-8");
				String userId = this.request.getParameter("userId");
				String privilege = this.request.getParameter("privilege");
				HttpRsult hr = null;
				Object result = null;
				OneAlarmEntity aae = null;
				if(userId==null||privilege==null){
					hr = new HttpRsult();
					hr.setError("参数错误");
					hr.setErrorCode(1);
					result = hr;
				}else{
					mAllAlarmDao = new AllAlarmDaoImpl();
					if(privilege.equals("1")){
						aae = mAllAlarmDao.getNormalLastestAlarm(userId, privilege);
					}else{
						aae = mAllAlarmDao.getAdminLastestAlarm(userId, privilege);
					}
					
					if(aae==null){
						hr = new HttpRsult();
						hr.setError("没有报警消息");
						hr.setErrorCode(2);
						result = hr;
					}else{
						aae.setError("获取报警消息成功");
						aae.setErrorCode(0);
						result = aae;
					}
				}
				ObjectMapper mapper = new ObjectMapper();  
		        String json = mapper.writeValueAsString(result); 
		        this.response.getWriter().write(json);
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
		public void getOneElectricInfo(){
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("utf-8");
			try {
				//userId=13622215085&privilege=&smokeMac
				this.request.setCharacterEncoding("utf-8");
				String userId = this.request.getParameter("userId");
				String smokeMac = this.request.getParameter("smokeMac");
				String privilege = this.request.getParameter("privilege");
				String devType = this.request.getParameter("devType");
				HttpRsult hr = null;
				Object result = null;
				OneElectricEntity oee = null;
				if(StringUtils.isBlank(devType)){
					devType = "5";
				}
				if(!Utils.isNullStr(userId)||!Utils.isNullStr(smokeMac)||!Utils.isNullStr(privilege)){
					hr = new HttpRsult();
					hr.setError("参数错误");
					hr.setErrorCode(1);
					result = hr;
				}else{
					mOneElectricInfoDao = new OneElectricInfoDaoImpl();
					oee = mOneElectricInfoDao.getOneElectricInfo(smokeMac);
					if(oee==null){
						hr = new HttpRsult();
						hr.setError("没有数据");
						hr.setErrorCode(2);
						result = hr;
					}else{
						result = oee;
					}
				}
				ObjectMapper mapper = new ObjectMapper();  
		        String json = mapper.writeValueAsString(result); 
		        this.response.getWriter().write(json);
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
		public void getRepeaterInfo() {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("utf-8");
			try {
				this.request.setCharacterEncoding("utf-8");
				String userId = this.request.getParameter("userId");
				String privilege = this.request.getParameter("privilege");
				String page  = this.request.getParameter("page");
				String search  = this.request.getParameter("search");
				AllRepeaterEntity ase = null;
				HttpRsult hr = null;
				Object result = null;
				if(userId==null||privilege==null){
					hr = new HttpRsult();
					hr.setError("参数错误");
					hr.setErrorCode(1);
					result = hr;
				}else{
					mAllSmokeDao = new AllSmokeDaoImpl();
					if(privilege.equals("3")){
						ase = mAllSmokeDao.getAllRepeaterInfo(userId, privilege,page,search);
					}else{
						ase = mAllSmokeDao.getAllRepeaterInfo(privilege,page,search);
					}
					if(ase==null){
						hr = new HttpRsult();
						hr.setError("没有烟感");
						hr.setErrorCode(2);
						result = hr;
					}else{
						ase.setError("获取烟感成功");
						ase.setErrorCode(0);
						result = ase;
					}
				}
				JSONObject jObject = new JSONObject(result);
				this.response.getWriter().write(jObject.toString());
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public void getToken(){
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			try {
				this.request.setCharacterEncoding("utf-8");
//				String signature = this.request.getParameter("signature");
//				String timestamp = this.request.getParameter("timestamp");
//				String nonce = this.request.getParameter("nonce");
				String echostr = this.request.getParameter("echostr");
				
				this.response.getWriter().write(echostr);
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
}
