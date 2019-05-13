package com.cloudfire.action;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.Timer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;

import com.cloudfire.dao.AddSmokeDao;
import com.cloudfire.dao.GetSmokeMacByRepeaterDao;
import com.cloudfire.dao.KeepSystemDao;
import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.SmokeLineDao;
import com.cloudfire.dao.ToolDao;
import com.cloudfire.dao.ToolNanJinDao;
import com.cloudfire.dao.impl.AddSmokeDaoImpl;
import com.cloudfire.dao.impl.GetSmokeMacByRepeaterDaoImpl;
import com.cloudfire.dao.impl.KeepSystemDaoImpl;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.cloudfire.dao.impl.SmokeLineDaoImpl;
import com.cloudfire.dao.impl.ToolDaoImpl;
import com.cloudfire.dao.impl.ToolNanJinDaoImpl;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.db.RedisConnection;
import com.cloudfire.db.RedisOps;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.Repeater;
//import com.cloudfire.entity.SmokeMap;
import com.cloudfire.thread.HengXingMQTT;
import com.cloudfire.thread.HengXingMQTT148;
import com.cloudfire.thread.HengxingFalanWaterThread;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.OneNetHttpMethod;
import com.cloudfire.until.Utils;
import com.gexin.rp.sdk.base.uitls.RandomUtil;
import com.opensymphony.xwork2.ActionSupport;

public class AddSmokeAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{

	private static final long serialVersionUID = -4581514309685063419L;
	private final static Log log = LogFactory.getLog(AddSmokeAction.class);
	private HttpServletRequest request;
	private HttpServletResponse response;
	private AddSmokeDao mAddSmokeDao;
//	private SmokeMap mSmokeMap;	
	private SmokeLineDao mSmokeLineDao;
	private GetSmokeMacByRepeaterDao mGetSmokeMacByRepeaterDao;
	
	HttpRsult hr = null;
	
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void addSmoke(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		Object result = null;
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
			String image=this.request.getParameter("image");
			
			
			if(electrState==null||electrState.equals("")){
				electrState="0";
			}
			
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
						areaId, repeater, camera, deviceType,cameraChannel,electrState,image);
				if(hr==null){
					hr = new HttpRsult();
					hr.setError("添加烟感失败");
					hr.setErrorCode(2);
					result = hr;
				}else{
					if(areaId.equals("2364")) {
						ToolNanJinDao nanJingDao = new ToolNanJinDaoImpl();
						String deviceId = nanJingDao.getDeviceByImei(smokeMac);
						Map<String,String> map = new HashMap<String,String>();
						map.put("imei", smokeMac);
						map.put("deviceId", deviceId);
						OneNetHttpMethod.getMap("http://47.95.43.248:51091/zhiminkeji/saveDeviceId", map);
					}
					
					mGetSmokeMacByRepeaterDao = new GetSmokeMacByRepeaterDaoImpl();
					if(Utils.isNullStr(repeater)&&!deviceType.equals("9")&&hr.getErrorCode()==0){
						Utils.sendRepeaterList(repeater);
					}
					if(Utils.isNullStr(repeater)&&!repeater.equals(oldRepeaterString)){
						Utils.sendRepeaterList(oldRepeaterString);
					}
					//更新添加设备redis的离线列表
					if(StringUtils.isNotBlank(repeater)){
						updateOffSmokeList( repeater, deviceType, smokeMac, oldRepeaterString);
					}
//					if(!deviceType.equals("5")){ //非电气设备的添加才要对内存中的smokeMap操作  edit by yfs @12/8/2017 9:16
//						SmokeMap.newInstance().updateSmokeMap(repeater, smokeMac);
//						Long addTime = System.currentTimeMillis();
//						mSmokeMap = SmokeMap.newInstance();
//						Map<String, Map<String,Long>> map = mSmokeMap.getSmokeMap();
//						Map<String,Long> smokeMap = map.get(repeater);
//						if(smokeMap == null){
//							Map<String,Long> newSmokes = new HashMap<String,Long>();
//							mSmokeLineDao = new SmokeLineDaoImpl();
//							newSmokes = mSmokeLineDao.RepeaterLoss(repeater);
//							mSmokeMap.addSmokeMap(repeater, newSmokes);
//							smokeMap = map.get(repeater);
//						}
//						if(smokeMap.containsKey(smokeMac)){
//							smokeMap.remove(smokeMac);
//							smokeMap.put(smokeMac, addTime);
//						}else{
//							smokeMap.put(smokeMac, addTime);
//						}
//						if(map.containsKey(repeater)){
//							map.remove(repeater);
//							map.put(repeater, smokeMap);
//						}else{
//							map.put(repeater, smokeMap);
//						}
//					}
					/*Iterator<String> it = smokeMap.keySet().iterator();
					while(it.hasNext()){
						String mac = (String)it.next();
					}*/
					result = hr;
				}
			}
			if(deviceType.equals("69")||deviceType.equals("70")){
				HengXingMQTT.subScription("ND/"+smokeMac+"/sys_para",1);
				HengXingMQTT.subScription("ND/"+smokeMac+"/storage_data",1);
				HengXingMQTT.subScription("ND/"+smokeMac+"/alarm",1);
				HengXingMQTT148.subScription("ND/"+smokeMac+"/sys_para",1);
				HengXingMQTT148.subScription("ND/"+smokeMac+"/storage_data",1);
				HengXingMQTT148.subScription("ND/"+smokeMac+"/alarm",1);
			}
			if(deviceType.equals("68")){
				Timer timer = new Timer();
				timer.schedule(new HengxingFalanWaterThread(), 1000);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			result = hr;
			JSONObject jObject = new JSONObject(result);
			try {
				this.response.getWriter().write(jObject.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addSmoke_ytr(){//伊特若
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		Object result = null;
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
			String uuid=this.request.getParameter("uuid");//社会单位
			
			
			if(electrState==null||electrState.equals("")){
				electrState="0";
			}
			
			KeepSystemDao keepSystemDao=new  KeepSystemDaoImpl();
			String oldRepeaterString=keepSystemDao.getRepeaterOfSmoke(smokeMac);
			if(userId==null||smokeMac==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);                 
				result = hr;
			}else{
				mAddSmokeDao = new AddSmokeDaoImpl();
				hr = mAddSmokeDao.addSmoke_ytr(userId, smokeMac, privilege, smokeName, address, longitude, latitude,
						placeAddress, placeTypeId, principal1, principal1Phone, principal2, principal2Phone, 
						areaId, repeater, camera, deviceType,cameraChannel,electrState);
				if(hr==null){
					hr = new HttpRsult();
					hr.setError("添加烟感失败");
					hr.setErrorCode(2);
					result = hr;
				}else{
					if(uuid!=null&&uuid.length()>0){
						String fdevice_uuid = RandomUtil.randomUUID();
						//添加设备到依特若平台
						Utils.addSmokeInfo(null, uuid, fdevice_uuid, smokeName, smokeMac, address);
					}
	
					mGetSmokeMacByRepeaterDao = new GetSmokeMacByRepeaterDaoImpl();
					if(Utils.isNullStr(repeater)&&!deviceType.equals("9")&&hr.getErrorCode()==0){
						Utils.sendRepeaterList(repeater);
					}
					if(Utils.isNullStr(repeater)&&!repeater.equals(oldRepeaterString)){
						Utils.sendRepeaterList(oldRepeaterString);
					}
					//更新添加设备redis的离线列表
					if(StringUtils.isNotBlank(repeater)){
						updateOffSmokeList( repeater, deviceType, smokeMac, oldRepeaterString);
					}
//					if(!deviceType.equals("5")){ //非电气设备的添加才要对内存中的smokeMap操作  edit by yfs @12/8/2017 9:16
//						SmokeMap.newInstance().updateSmokeMap(repeater, smokeMac);
//						Long addTime = System.currentTimeMillis();
//						mSmokeMap = SmokeMap.newInstance();
//						Map<String, Map<String,Long>> map = mSmokeMap.getSmokeMap();
//						Map<String,Long> smokeMap = map.get(repeater);
//						if(smokeMap == null){
//							Map<String,Long> newSmokes = new HashMap<String,Long>();
//							mSmokeLineDao = new SmokeLineDaoImpl();
//							newSmokes = mSmokeLineDao.RepeaterLoss(repeater);
//							mSmokeMap.addSmokeMap(repeater, newSmokes);
//							smokeMap = map.get(repeater);
//						}
//						if(smokeMap.containsKey(smokeMac)){
//							smokeMap.remove(smokeMac);
//							smokeMap.put(smokeMac, addTime);
//						}else{
//							smokeMap.put(smokeMac, addTime);
//						}
//						if(map.containsKey(repeater)){
//							map.remove(repeater);
//							map.put(repeater, smokeMap);
//						}else{
//							map.put(repeater, smokeMap);
//						}
//					}
					/*Iterator<String> it = smokeMap.keySet().iterator();
					while(it.hasNext()){
						String mac = (String)it.next();
					}*/
					result = hr;
				}
			}
			if(deviceType.equals("69")||deviceType.equals("70")){
				HengXingMQTT.subScription("ND/"+smokeMac+"/sys_para",1);
				HengXingMQTT.subScription("ND/"+smokeMac+"/storage_data",1);
				HengXingMQTT.subScription("ND/"+smokeMac+"/alarm",1);
				HengXingMQTT148.subScription("ND/"+smokeMac+"/sys_para",1);
				HengXingMQTT148.subScription("ND/"+smokeMac+"/storage_data",1);
				HengXingMQTT148.subScription("ND/"+smokeMac+"/alarm",1);
			}
			if(deviceType.equals("68")){
				Timer timer = new Timer();
				timer.schedule(new HengxingFalanWaterThread(), 1000);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			result = hr;
			JSONObject jObject = new JSONObject(result);
			try {
				this.response.getWriter().write(jObject.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addNFC(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String uid = this.request.getParameter("uid");
//			String privilege = this.request.getParameter("privilege");
			String smokeName = this.request.getParameter("smokeName");
			String address = this.request.getParameter("address");
			String longitude = this.request.getParameter("longitude");
			String latitude = this.request.getParameter("latitude");
			String areaId = this.request.getParameter("areaId");
			String deviceType = this.request.getParameter("deviceType");
			String memo = this.request.getParameter("memo");
			String producer=this.request.getParameter("producer");
			String makeTime=this.request.getParameter("makeTime");
			String workerPhone=this.request.getParameter("workerPhone");
			String makeAddress=this.request.getParameter("makeAddress");//@@生产地址
			HttpRsult hr = null;
			Object result = null;
			if(userId==null||uid==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mAddSmokeDao = new AddSmokeDaoImpl();
				hr = mAddSmokeDao.addNFC(userId, uid,areaId,deviceType, smokeName,
						address, longitude, latitude, memo,producer,makeTime,workerPhone,makeAddress);
				if(hr==null){
					hr = new HttpRsult();
					hr.setError("添加烟感失败");
					hr.setErrorCode(2);
					result = hr;
				}else{
					result = hr;
				}
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void addNFCRecord(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String UUID = this.request.getParameter("uid");
			String longitude = this.request.getParameter("longitude");
			String latitude = this.request.getParameter("latitude");
			String devicestate = this.request.getParameter("devicestate");
			String memo = this.request.getParameter("memo");
			String photo1 = this.request.getParameter("photo1");
			String items= this.request.getParameter("items");
//			File dir=new File(request.getSession().getServletContext().getRealPath("")).getParentFile();
			if(photo1!=null&&!photo1.equals("")){
				photo1="nfcimages\\"+photo1;
			}else{
				photo1="";
			}
			
			String endTime = GetTime.ConvertTimeByLong();
			HttpRsult hr = null;
			Object result = null;
			
			String workerPhone=getWorkerPhone(UUID);
			
			
			if(userId==null||UUID==null||devicestate==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else if(workerPhone!=null&&!workerPhone.equals("")&&!workerPhone.equals(userId)){//@@判别是否为绑定的巡检账号
				hr = new HttpRsult();
				hr.setError("添加失败，该账号无该设备绑定权限");
				hr.setErrorCode(3);
				result = hr;
			}else{
				mAddSmokeDao = new AddSmokeDaoImpl();
				if(items==null||items.length()==0){
					items="{}";
				}
				hr = mAddSmokeDao.addNFCRecord(UUID, longitude, latitude, userId, endTime, devicestate, memo,photo1,items);
				if(hr==null){
					hr = new HttpRsult();
					hr.setError("添加设备失败");
					hr.setErrorCode(2);
					result = hr;
				}else{
					result = hr;
				}
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private String getWorkerPhone(String uUID) {
		String workerPhone=null;
		String sql = "select workerPhone from nfcinfo where uid=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, uUID);
			rs = ps.executeQuery();
			if(rs.next()){
				workerPhone = rs.getString("workerPhone");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return workerPhone;
	}

	//by liao zw 2017.11.5 添加电表设备
	public  void meterAddDevice(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("gbk");
		try {
			this.request.setCharacterEncoding("utf-8");
			String  mac=this.request.getParameter("mac");
			String name=this.request.getParameter("name");
			String address=this.request.getParameter("address");
			HttpRsult hrHttpRsult=new HttpRsult();
			Object result=null;
			if(mac!=null && mac.length()>0){
				mAddSmokeDao=new AddSmokeDaoImpl();
				boolean ret=mAddSmokeDao.meterAddDevice(mac, name,address);
				if(ret){
					hrHttpRsult.setError("添加设备成功");
					hrHttpRsult.setErrorCode(0);
					result=hrHttpRsult;
				}
				else{
					hrHttpRsult.setError("添加设备失败");
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
	
	public void addHeiMenSmoke(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
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
			} else{
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
						updateOffSmokeList( repeater, deviceType, smokeMac, oldRepeaterString);
					}
//					if(!deviceType.equals("5")){ //非电气设备的添加才要对内存中的smokeMap操作  edit by yfs @12/8/2017 9:16
//						Long addTime = System.currentTimeMillis();
//						mSmokeMap = SmokeMap.newInstance();
//						Map<String, Map<String,Long>> map = mSmokeMap.getSmokeMap();
//						Map<String,Long> smokeMap = map.get(repeater);
//						if(smokeMap == null){
//							Map<String,Long> newSmokes = new HashMap<String,Long>();
//							mSmokeLineDao = new SmokeLineDaoImpl();
//							newSmokes = mSmokeLineDao.RepeaterLoss(repeater);
//							for (Entry<String, Long> time:newSmokes.entrySet()) {
//								if(time.getKey().length()==8){
//									Map<String,Long> newSmokes2 = new HashMap<String,Long>();
//									newSmokes2.put(time.getKey(), time.getValue());
//									mSmokeMap.addSmokeMap(repeater, newSmokes2);
//								}
//							}
//							smokeMap = map.get(repeater);
//						}
//						if(smokeMap.containsKey(smokeMac)){
//							smokeMap.remove(smokeMac);
//							smokeMap.put(smokeMac, addTime);
//						}else{
//							smokeMap.put(smokeMac, addTime);
//						}
//						if(map.containsKey(repeater)){
//							map.remove(repeater);
//							map.put(repeater, smokeMap);
//						}else{
//							map.put(repeater, smokeMap);
//						}
//					}
					/*Iterator<String> it = smokeMap.keySet().iterator();
					while(it.hasNext()){
						String mac = (String)it.next();
					}*/
					if(hr.getErrorCode()==0){
						LoginDao login = new LoginDaoImpl();
				    	ToolDao tool = new ToolDaoImpl();
				    	String accessToken = login.getEaseIotAccessToKen("hanrun01","hanrun01","http://59.36.137.251:5001/");
				    	String isAddtoIOT="";
				    	switch(deviceType){
				    		case "41":
				    			isAddtoIOT=tool.registeredPlant("ctc-nanjing-iot-137", accessToken,
						    			smokeMac, smokeName, "Smoke", "ctc-nanjing-iot-137", 
						    			"PSM", userId, userId, placeAddress,
						    			longitude, latitude, "","http://59.36.137.251:5001/");
				    			break;
				    		case "45":
				    			isAddtoIOT=tool.registeredPlant("ctc-nanjing-iot-137", accessToken,
						    			smokeMac, smokeName, "NS2CG", "ctc-nanjing-iot-137", 
						    			"PSM", userId, userId, placeAddress,
						    			longitude, latitude, "","http://59.36.137.251:5001/");
				    			break;
				    	}
				    	if(!isAddtoIOT.equals("成功")){
				    		hr = new HttpRsult();
				    		if(isAddtoIOT.contains("1004")){
				    			hr.setError("该设备已在IOT平台注册");
								hr.setErrorCode(5);
				    		}else{
				    			hr.setError(isAddtoIOT+"，数据已保存到本地平台");
								hr.setErrorCode(5);
				    		}
						}
					}
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
	
	public void addAdministrationInfo(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String fsocial_uuid = RandomUtil.randomUUID(); 
			String fsocial_name = this.request.getParameter("fsocial_name");
			String fprovince_code = this.request.getParameter("fprovince_code");
			String fcity_code = this.request.getParameter("fcity_code");
			String fcounty_code = this.request.getParameter("fcounty_code");
			String ftown_code = this.request.getParameter("ftown_code");
			String faddress = this.request.getParameter("faddress");
			String flink_man = this.request.getParameter("flink_man");
			String ftel_no = this.request.getParameter("ftel_no");
			String flongitude = this.request.getParameter("flongitude");
			String flatitude=this.request.getParameter("flatitude");
			String funit_type=this.request.getParameter("funit_type");
			String fis_active=this.request.getParameter("fis_active");
			HttpRsult hr = null;
			Object result = null;
			
			mAddSmokeDao = new AddSmokeDaoImpl();
			Utils.addDev(fsocial_uuid, fsocial_name, fprovince_code, fcity_code, fcounty_code, ftown_code, faddress, flink_man, ftel_no, flongitude, flatitude, funit_type, fis_active);
			hr = mAddSmokeDao.addAdministrationInfo(fsocial_uuid, fsocial_name, fprovince_code, fcity_code, fcounty_code, ftown_code, faddress, flink_man, ftel_no, flongitude, flatitude, funit_type, fis_active);
			if(hr==null){
				hr = new HttpRsult();
				hr.setError("添加失败");
				hr.setErrorCode(2);
				result = hr;
			}else{
				result = hr;
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ToolNanJinDao nanJingDao = new ToolNanJinDaoImpl();
		String smokeMac="868744032626073";
		String deviceId = nanJingDao.getDeviceByImei(smokeMac);
		Map<String,String> map = new HashMap<String,String>();
		map.put("imei", smokeMac);
		map.put("deviceId", deviceId);
		String url="http://127.0.0.1:8080/zhiminkeji/saveDeviceId";
		OneNetHttpMethod.getMap(url, map);
//		OneNetHttpMethod.postMap("http://47.95.43.248:51091/zhiminkeji/saveDeviceId", map);
	}
	
	private void  updateOffSmokeList(String repeater,String deviceType,String smokeMac,String oldRepeaterString){
		try{
			//若主机是新加的，添加到redis
			Jedis jedis = RedisConnection.getJedis();
			if (jedis!=null) {
				String requestId = UUID.randomUUID().toString().replace("-", "");
				
				if (!deviceType.equals("5")){
					if (!RedisOps.exist(jedis,"R"+repeater)) {
						Repeater rep  = new Repeater();
						rep.setNetState(0);
						rep.setHeartime(0);
						rep.setRepeaterMac(repeater);
						rep.setPowerChangeTime(0);
						rep.setPowerState(0);
						RedisOps.setObject(jedis,"R"+repeater, rep);
						
						List<String> offMacs = new ArrayList<String>();
						offMacs.add(smokeMac);
						RedisOps.setList(jedis,repeater, offMacs);
					} else {
						//对旧的主机下离线列表进行处理
						if (StringUtils.isNotBlank(oldRepeaterString)&&!repeater.equals(oldRepeaterString)){
							while(!RedisOps.tryGetDistributedLock(jedis, "L"+oldRepeaterString, requestId, 10000)){
								try {
									Thread.currentThread().sleep(300);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							List<String> oldOffMacs = RedisOps.getList(jedis,oldRepeaterString);
							Iterator<String> it = oldOffMacs.iterator();
							boolean changed = false;
							while(it.hasNext()){
								if (it.next().equals(smokeMac)){
									it.remove();
									changed = true;
									break;
								}
							}
							if (changed){
								RedisOps.setList(jedis,oldRepeaterString,oldOffMacs);
							}
							RedisOps.releaseDistributedLock(jedis, "L"+oldRepeaterString, requestId);
						}
						
						//对新主机下离线列表进行处理
						while(!RedisOps.tryGetDistributedLock(jedis, "L"+repeater, requestId, 10000)){
							try {
								Thread.currentThread().sleep(300);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						List<String> newOffMacs = RedisOps.getList(jedis,repeater);
						Iterator<String> it = newOffMacs.iterator();
						boolean changed = true;
						while(it.hasNext()){
							if (it.next().equals(smokeMac)){
								changed = false;
								break;
							}
						}
						if (changed){
							newOffMacs.add(smokeMac);
							RedisOps.setList(jedis,repeater,newOffMacs);
						}
						RedisOps.releaseDistributedLock(jedis, "L"+repeater, requestId);
						
					}
				}
				
				jedis.close();
			}
				
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
}
