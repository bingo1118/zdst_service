package com.cloudfire.action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import com.cloudfire.dao.GetSmokeMacByRepeaterDao;
import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.RePeaterDataDao;
import com.cloudfire.dao.ToolDao;
import com.cloudfire.dao.ToolEasyDao;
import com.cloudfire.dao.ToolNanJinDao;
import com.cloudfire.dao.ToolOneNetDao;
import com.cloudfire.dao.WaterInfoDao;
import com.cloudfire.dao.impl.GetSmokeMacByRepeaterDaoImpl;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.cloudfire.dao.impl.NeedSmokeDaoImpl;
import com.cloudfire.dao.impl.RePeaterDataDaoImpl;
import com.cloudfire.dao.impl.ToolDaoImpl;
import com.cloudfire.dao.impl.ToolEasyDaoImpl;
import com.cloudfire.dao.impl.ToolNanJinDaoImpl;
import com.cloudfire.dao.impl.ToolOneNetDaoImpl;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.BQMacEntity;
import com.cloudfire.entity.ElectricHistoryEntity;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.LoginEntity;
import com.cloudfire.entity.RePeaterData;
import com.cloudfire.entity.RePeaterUoolEntity;
import com.cloudfire.entity.SessionMap;
import com.cloudfire.entity.UserMap;
import com.cloudfire.entity.WaterAckEntity;
import com.cloudfire.thread.AckElecContrThread;
import com.cloudfire.until.ClientPackage;
import com.cloudfire.until.Client_Fault_Package;
import com.cloudfire.until.IfStopAlarm;
import com.cloudfire.until.StringUtil;
import com.cloudfire.until.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.xwork2.ActionSupport;

public class AckControlAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{

	private static final long serialVersionUID = -4581514309685063419L;
	private final static Log log = LogFactory.getLog(AckControlAction.class);
	private HttpServletRequest request;
	private HttpServletResponse response;
	private LoginDao login;
	private ToolDao toolDao;
	private GetSmokeMacByRepeaterDao mbrDao;
	private WaterInfoDao wDao;
	private ToolEasyDao toolEasyDao;
	private LoginDao mLoginDao;
	
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void ackControl(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String electricMac = this.request.getParameter("smokeMac");//设备号
			String userId = this.request.getParameter("userId");//用户id
			mbrDao = new GetSmokeMacByRepeaterDaoImpl();
			String repeaterMac = mbrDao.getRepeaterMacBySmokeMac(electricMac);//根据设备号获取主机号
			String eleState = this.request.getParameter("eleState");//标识开或关命令，服务器端1标识开，2表示关，硬件部分0表示开，1表示关。
			HttpRsult hr = null;
			Object result = null;
			if(mLoginDao==null){
				mLoginDao = new LoginDaoImpl();
			}
			LoginEntity user = mLoginDao.login(userId);
			if(electricMac==null||eleState==null||userId==null){ //设备号或    或用户id为空，则参数错误
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else if(user==null||!user.getIsCanCutEletr().equals("1")){
				hr = new HttpRsult();
				hr.setError("该用户无远程控制权限");
				hr.setErrorCode(5);
				result = hr;
			}else{
				if(Utils.isNullStr(electricMac)){//如果设备号不为空
					RePeaterDataDao redao = new RePeaterDataDaoImpl(); 
					int devTypeNum = redao.getDeviceTypeNum(electricMac);//根据mac查询设型号
					RePeaterData mRePeaterData = new RePeaterData();
					mRePeaterData.setRepeatMac(repeaterMac);//设置主机号
					mRePeaterData.setElectricMac(electricMac);//设置设备号
					mRePeaterData.setSeqL((byte)0x01);//递增识别的
					mRePeaterData.setSeqH((byte)0x01);
					//查找全部烟感
					int count = Integer.parseInt(eleState);
					count--;
					byte[] ack;
					if(devTypeNum==3){
						ack = ClientPackage.ackControlAction2(mRePeaterData,count);//@@三相电表
					}else{
						ack = ClientPackage.ackControlAction(mRePeaterData,count);//@@电气设备//count传2就可以关了
					}
					
					IoSession session = SessionMap.newInstance().getSession(repeaterMac);//获取主机的session
					if(session==null){
						session = SessionMap.newInstance().getSession(electricMac);//为空，通过设备号获取
					}
					if(session!=null){//不为空
						UserMap.newInstance().addUser(repeaterMac, userId);//主机号和用户id绑定到UserMap中
						Map<String,Integer> AckEleMap = Utils.AckEleMap;//实例化hashMap存储键位设备号，值为      集合
						AckEleMap.put(electricMac, count);
						
						
						System.out.println("ceshiheza:"+count+"    count:"+AckEleMap.get(electricMac));
						AckElecContrThread ackEle = new AckElecContrThread(ack,session,repeaterMac);//开启线程
						ackEle.start();
						ackEle.join();
						
						hr = new HttpRsult();
						int state = AckEleMap.get(electricMac);
						for(int i=0;i<30;i++){
							Thread.sleep(1000L);
							state = AckEleMap.get(electricMac);
							NeedSmokeDaoImpl nsd=new NeedSmokeDaoImpl();
							int eletrState=nsd.getElectrState(electricMac);//@@获取当前设备状态
							if(state==127){
								hr.setError("失败");
								hr.setErrorCode(1);
								break;
							}
							if(state>count||eletrState==(count+1)){
								hr.setError("操作成功");
								hr.setErrorCode(0);
								//存储切电记录
								if(devTypeNum==3){
									GetSmokeMacByRepeaterDaoImpl.saveElectricHistory(electricMac, repeaterMac, eletrState);
								}
								break;
							}
							if(state==count&&i>=29){
								hr.setError("超时");
								hr.setErrorCode(2);
							}
						}
						if(StringUtil.strIsNullOrEmpty(hr.getError())){
							hr.setError("操作失败");
							hr.setErrorCode(1);
						}
						result = hr;
					}else{
						hr = new HttpRsult();
						hr.setError("设备连接不上，命令失效");
						hr.setErrorCode(3);
						result = hr;
					}
				}
				if(hr==null){
					hr = new HttpRsult();
					hr.setError("命令发送失败");
					hr.setErrorCode(3);
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
	
	/**
	 * 针对lora电气设置相关阈值
	 * 手机设置过压阈值(2byte)欠压阈值(2byte)过流阈值(2byte)漏电阈值(2byte)
	 */
	public void ackControlCvls(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String Overvoltage = this.request.getParameter("Overvoltage");	//过压
			String Undervoltage = this.request.getParameter("Undervoltage");	//欠压
			String Overcurrent = this.request.getParameter("Overcurrent");	//过流
			String Leakage = this.request.getParameter("Leakage");	//漏电阈值
			String repeaterMac = this.request.getParameter("repeaterMac");	//所属主机
			String smokeMac = this.request.getParameter("smokeMac");	//
			String userId = this.request.getParameter("userId");
			String context = "lora电气设置相关阈值";
			HttpRsult hr = null;
			Object result = null;
			if(repeaterMac==null||smokeMac==null||userId==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				if(StringUtils.isNotBlank(Overvoltage)&&StringUtils.isNotBlank(Undervoltage)&&StringUtils.isNotBlank(Overcurrent)&&StringUtils.isNotBlank(Leakage)){
					RePeaterUoolEntity mRePeaterData = new RePeaterUoolEntity();
					mRePeaterData.setRepeatMac(repeaterMac);
					mRePeaterData.setDevMac(smokeMac);
					mRePeaterData.setSeqL((byte)0x01);
					mRePeaterData.setSeqH((byte)0x01);
					int overvoltage = (int)(Float.parseFloat(Overvoltage));
					int undervoltage = (int)(Float.parseFloat(Undervoltage));
					int overcurrent = (int)(Float.parseFloat(Overcurrent)*10);
					int leakage = (int)(Float.parseFloat(Leakage));
					mRePeaterData.setOvervoltage(overvoltage);
					mRePeaterData.setUndervoltage(undervoltage);
					mRePeaterData.setOvercurrent(overcurrent);
					mRePeaterData.setLeakage(leakage);
					//查找全部烟感
					byte[] ack;
					ack = ClientPackage.ackControlCvlsAction(mRePeaterData);
					IoSession session = SessionMap.newInstance().getSession(repeaterMac);
					if(session==null){
						session = SessionMap.newInstance().getSession(smokeMac);
					}
					if(session!=null){
						Map<String,Integer> controlDev = Utils.controlDev;
						controlDev.put(smokeMac, 255);
						AckElecContrThread ackEle = new AckElecContrThread(ack,session,repeaterMac);
						ackEle.start();
						ackEle.join();
						hr = new HttpRsult();
						int state = controlDev.get(smokeMac);
						for(int i=0;i<15;i++){
							Thread.sleep(1000L);
							state = controlDev.get(smokeMac);
							if(state==0){
								toolEasyDao = new ToolEasyDaoImpl();
								toolEasyDao.addOperator(userId, smokeMac, context,"成功");
								hr.setError("成功");
								hr.setErrorCode(0);
								break;
							}else if(state!=255){
								hr.setError("失败");
								hr.setErrorCode(1);
								break;
							}
						}
						if(state==255){
							hr.setError("超时");
							hr.setErrorCode(2);
						}
						result = hr;
					}else{
						hr = new HttpRsult();
						hr.setError("设备连接不上，命令失效");
						hr.setErrorCode(3);
						result = hr;
					}
				}
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void ackNB_IOT_Control(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String NBDevMac = this.request.getParameter("smokeMac");
			String userId = this.request.getParameter("userId");
			String eleState = this.request.getParameter("eleState");
			HttpRsult hr = null;
			Object result = null;
			if(NBDevMac==null||eleState==null||userId==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				if(Utils.isNullStr(NBDevMac)){
					Map<String,Integer> AckEleMap = Utils.AckEleMap;
					AckEleMap.put(NBDevMac, Integer.parseInt(eleState));
					System.out.println("zhixingjieguo:"+eleState);
					hr = new HttpRsult();
					hr.setError("命令已下发");
					hr.setErrorCode(0);
					result = hr;
				}
			}
			if(hr==null){
				hr = new HttpRsult();
				hr.setError("命令发送失败");
				hr.setErrorCode(3);
				result = hr;
			}else{
				result = hr;
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 手机设置水压波动阈值（KP）、和数据上传频率（分钟）
	 */
	public void set_water_wave_Control(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String devMac = this.request.getParameter("smokeMac");
			String waveValue = this.request.getParameter("waveValue");	//波动阈值KP
			String waveTime = this.request.getParameter("waveTime");	//数据上传频率分钟
			
			HttpRsult hr = null;
			Object result = null;
			if(StringUtils.isBlank(waveValue)||StringUtils.isBlank(devMac)||StringUtils.isBlank(waveTime)){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				Map<String,WaterAckEntity> cMap = Utils.objWater;
				WaterAckEntity wa = new WaterAckEntity();
				int WaveValue = Integer.parseInt(waveValue);
				int WaveTime = Integer.parseInt(waveTime);
				wa.setWaterMac(devMac);
				wa.setAckTimes(WaveTime);
				wa.setWaveValue(WaveValue);
				cMap.put(devMac, wa);
				hr = new HttpRsult();   
				hr.setError("命令已下发");
				hr.setErrorCode(0);
				result = hr;
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 手机设置水压波动阈值（KP）、和数据上传频率（分钟）
	 */
	public void set_water_level_Control(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String devMac = this.request.getParameter("smokeMac");
			String h_value = this.request.getParameter("hvalue");//高水压水位阈值
			String l_value = this.request.getParameter("lvalue");//低水压水位阈值
			String waveValue = this.request.getParameter("waveValue");	//上报时间
			String waveTime = this.request.getParameter("waveTime");	//采集时间
			String devType = this.request.getParameter("deviceType");	//设备类型
			
			
			HttpRsult hr = null;
			Object result = null;
			if(StringUtils.isBlank(waveValue)||StringUtils.isBlank(devMac)||StringUtils.isBlank(waveTime)){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				Map<String,WaterAckEntity> cMap = Utils.objWater;
				WaterAckEntity wa = new WaterAckEntity();
				int WaveValue = (int)Float.parseFloat(waveValue);
				int WaveTime = (int)Float.parseFloat(waveTime);
				wa.setWaterMac(devMac);		
				wa.setAckTimes(WaveTime);	//采集时间
				wa.setWaveValue(WaveValue);	//上报时间
				
				if(StringUtils.isNotBlank(devType)){
					if(Integer.parseInt(devType)==48){//水位
						wa.setThreshold2((int)(Float.parseFloat(l_value)*1000));//低水压水位阈值
						wa.setThreshold1((int)(Float.parseFloat(h_value)*1000));//高水压水位阈值
					}else{
						wa.setThreshold2((int)Float.parseFloat(l_value));//低水压水位阈值
						wa.setThreshold1((int)Float.parseFloat(h_value));//高水压水位阈值
					}
				}else{
					wa.setThreshold1((int)Float.parseFloat(h_value));//高水压水位阈值
					wa.setThreshold2((int)Float.parseFloat(l_value));//高水压水位阈值
				}
				
				cMap.put(devMac, wa);
				hr = new HttpRsult();   
				hr.setError("命令已下发");
				hr.setErrorCode(0);
				result = hr;
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void getEleNeedHis(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String electricMac = this.request.getParameter("smokeMac");
			String page  = this.request.getParameter("page");
			mbrDao = new GetSmokeMacByRepeaterDaoImpl();
			HttpRsult hr = null;
			Object result = null;
			if(electricMac==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				List<ElectricHistoryEntity> eleList = mbrDao.getElectricHistory(electricMac, page);
				if(eleList==null||eleList.size()<1){
		            hr = new HttpRsult();
					hr.setError("获取失败");
					hr.setErrorCode(2);
					result = hr;
				}else{
					hr = new HttpRsult();
					hr.setError("请求成功");
					hr.setErrorCode(0);
					hr.setEleList(eleList);
					result = hr;
				}
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 主机复位
	 */
	public void resetRepeater(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String repeaterMac = this.request.getParameter("repeaterMac");
			HttpRsult hr = null;
			Object result = null;
			if(repeaterMac==null||repeaterMac.equals("")){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				if(Utils.isNullStr(repeaterMac)&&Utils.isNullStr(repeaterMac)){
					RePeaterData mRePeaterData = new RePeaterData();
					mRePeaterData.setRepeatMac(repeaterMac);
					mRePeaterData.setSeqL((byte)0x01);
					mRePeaterData.setSeqH((byte)0x01);
					//查找全部烟感
					byte[] ack = ClientPackage.ackResetRepeaterAction(mRePeaterData);
					IoSession session = SessionMap.newInstance().getSession(repeaterMac);
					if(session!=null){
						System.out.println("repeaterMac:"+repeaterMac);
						AckElecContrThread ackEle = new AckElecContrThread(ack,session,repeaterMac);
						ackEle.start();
						hr = new HttpRsult();
						hr.setError("复位命令下发成功");
						hr.setErrorCode(0);
						result = hr;
					}else{
						hr = new HttpRsult();
						hr.setError("设备连接不上，命令失效");
						hr.setErrorCode(3);
						result = hr;
					}
				}
				if(hr==null){
					hr = new HttpRsult();
					hr.setError("命令发送失败");
					hr.setErrorCode(3);
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
	
	/**
	 * 下发声光
	 */
	public void cancelSound(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String repeaterMac = this.request.getParameter("repeaterMac");
			HttpRsult hr = null;
			Object result = null;
			if(repeaterMac==null||repeaterMac.equals("")){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				if(Utils.isNullStr(repeaterMac)&&Utils.isNullStr(repeaterMac)){
					//查找全部烟感
					List<String> soundList = new ArrayList<String>();
					ToolDao td = new ToolDaoImpl();
					soundList = td.getSoundList(repeaterMac);
					byte[] ack = Client_Fault_Package.ackAlarmPackage(repeaterMac,(byte)0x05,soundList);
					IoSession session = SessionMap.newInstance().getSession(repeaterMac);
					if(session!=null){
						AckElecContrThread ackEle = new AckElecContrThread(ack,session,repeaterMac);
						ackEle.start();
						hr = new HttpRsult();
						hr.setError("声光取消命令下发成功");
						hr.setErrorCode(0);
						result = hr;
					}else{
						hr = new HttpRsult();
						hr.setError("设备连接不上，命令失效");
						hr.setErrorCode(3);
						result = hr;
					}
				}
				if(hr==null){
					hr = new HttpRsult();
					hr.setError("命令发送失败");
					hr.setErrorCode(3);
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
	
	/**
	 * @2018-5-14
	 * EASY-IOT电气开关控制下发命令0合闸，1分闸
	 */
	public void EasyIot_Switch_control(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String serverID = this.request.getParameter("serverID");
			String password = this.request.getParameter("password");
			String devSerial = this.request.getParameter("devSerial");
			String value = this.request.getParameter("eleState");
			String userid = this.request.getParameter("userId");
			String context = "EASY-IOT电气开关控制下发命令";
			String appId = this.request.getParameter("appId");
			if(StringUtils.isNotBlank(appId)){
				if(Integer.parseInt(appId)==1){
					serverID = "gzhrdev01";
					password = "pii1icRi";
				}else if(Integer.parseInt(appId)==3){
					serverID = "szhmdev01";
					password = "123456aB";
				}
			}
			
			HttpRsult hr = null;
			Object result = null;
			login= new LoginDaoImpl();
			toolDao = new ToolDaoImpl();
			
			String method = "HR_electric_Switch_control";
			String key = "HR_electric_Switch";
			
			if(mLoginDao==null){
				mLoginDao = new LoginDaoImpl();
			}
			LoginEntity user = mLoginDao.login(userid);
			
			if(serverID==null||password==null||devSerial==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else if(user==null||!user.getIsCanCutEletr().equals("1")){
				hr = new HttpRsult();
				hr.setError("该用户无远程控制权限");
				hr.setErrorCode(5);
				result = hr;
			}else{
				int state = Integer.parseInt(value) - 1;
				String accessToken = login.getEaseIotAccessToKen(serverID, password);
				String bool = toolDao.ackAccessToKen(accessToken, serverID, devSerial, method, key,state);
				if(hr==null){
					hr = new HttpRsult();
				}
				if(bool.equals("成功")){
					Utils.controlDev.put(devSerial, 199);
					for (int i = 0; i < 35; i++) {
						Thread.sleep(1000L);
						if(Utils.controlDev.get(devSerial)==99){	//99代表操作设置成功
							toolEasyDao = new ToolEasyDaoImpl();
							toolEasyDao.addOperator(userid, devSerial, context);
							Utils.controlDev.remove(devSerial);
							hr.setError("成功");
							hr.setErrorCode(0);
							//@@存储切换记录
							mbrDao = new GetSmokeMacByRepeaterDaoImpl();
							ElectricHistoryEntity ehe = new ElectricHistoryEntity();
							ehe.setUserId(userid);
							ehe.setMac(devSerial);
							ehe.setState(Integer.parseInt(value));
							mbrDao.insert_Electric_change_history(ehe);
							break;
						}else if(Utils.controlDev.get(devSerial)==200){
							hr.setError("操作失败");
							hr.setErrorCode(2);
							break;
						}else if(i==34){
							hr.setError("超时");
							hr.setErrorCode(1);
							break;
						}
					}
				}else{
					hr.setError(bool);
					hr.setErrorCode(2);
				}
				result = hr;
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @2018-5-14
	 * EASY-IOT电气阈值下发
	 * Undervoltage_threshold 欠压阈值
	 * Overvoltage_threshold 过压阈值
	 * Overcurrent_threshold 过流阈值
	 * Leakage_current_threshold 漏电阈值
	 */
	public void EasyIot_Uool_control(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String serverID = this.request.getParameter("serverID");
			String password = this.request.getParameter("password");
			String devSerial = this.request.getParameter("devSerial");
			String Undervoltage = this.request.getParameter("Undervoltage");
			String Overvoltage = this.request.getParameter("Overvoltage");
			String Overcurrent = this.request.getParameter("Overcurrent");
			String Leakage = this.request.getParameter("Leakage");
			String appId = this.request.getParameter("appId");
			String userid = this.request.getParameter("userId");
			String context = "EASY-IOT电气阈值下发控制命令";
			if(StringUtils.isNotBlank(appId)){
				if(Integer.parseInt(appId)==1){
					serverID = "gzhrdev01";
					password = "pii1icRi";
				}else if(Integer.parseInt(appId)==3){
					serverID = "szhmdev01";
					password = "123456aB";
				}
			}
			HttpRsult hr = null;
			Object result = null;
			login= new LoginDaoImpl();
			toolDao = new ToolDaoImpl();
			Map<String,Integer> control = new HashMap<String,Integer>();
			int undervoltage = (int)(Float.parseFloat(Undervoltage));
			int overvoltage = (int)(Float.parseFloat(Overvoltage));
			int overcurrent = (int)(Float.parseFloat(Overcurrent)*10);
			int leakage = (int)(Float.parseFloat(Leakage));
			control.put("HR_electric_Undervoltage_threshold", undervoltage);//欠压
			control.put("HR_electric_Overvoltage_threshold", overvoltage);
			control.put("HR_electric_Overcurrent_threshold", overcurrent);
			control.put("HR_electric_Leakage_current_threshold", leakage);
			String method = "HR_electric_Set_threshold";
			
			if(StringUtils.isBlank(serverID)||StringUtils.isBlank(password)||StringUtils.isBlank(devSerial)){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				String accessToken = login.getEaseIotAccessToKen(serverID, password);
				String bool = toolDao.ackAccessToKens(accessToken, serverID, devSerial, method, control);
				if(hr==null){
					hr = new HttpRsult();
				}
				Map<String,Integer> threshold = new HashMap<String,Integer>();
				threshold.put("undervoltage", undervoltage);
				threshold.put("overvoltage", overvoltage);
				threshold.put("overcurrent", overcurrent);
				threshold.put("leakage", leakage);
				System.out.println("shezhi阈值：undervoltage"+undervoltage+" overvoltage="+overvoltage+" overcurrent="+overcurrent+" leakage="+leakage);
				if(bool.equals("成功")){
					Utils.eleThreshold.put(devSerial, threshold);
					for (int i = 0; i < 60; i++) {
						int uo = Utils.eleThreshold.get(devSerial).get("undervoltage");
						int oo = Utils.eleThreshold.get(devSerial).get("overvoltage");
						int uc = Utils.eleThreshold.get(devSerial).get("overcurrent");
						int lk = Utils.eleThreshold.get(devSerial).get("leakage");
						Thread.sleep(1000L);
						if((uo==999)||(oo==999)||(uc==999)||(lk==999)){
							toolEasyDao = new ToolEasyDaoImpl();
							toolEasyDao.addOperator(userid, devSerial, context);
							Utils.eleThreshold.remove(devSerial);
							hr.setError("成功");
							hr.setErrorCode(0);
							break;
						}else if((uo!=undervoltage&&uo!=999)||(oo!=overvoltage&&oo!=999)||(uc!=overcurrent&&uc!=999)||(lk!=leakage&&lk!=999)){
							hr.setError("阈值设置失败");
							hr.setErrorCode(2);
							break;
						}else if(i==59){
							hr.setError("超时");
							hr.setErrorCode(1);
							break;
						}
					}
				}else{
					hr.setError(bool);
					hr.setErrorCode(2);
				}
				result = hr;
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getChuangAnData(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String smokeMac = this.request.getParameter("mac");
			HttpRsult hr = null;
			Object result = null;
				
			byte[] ack;
			ack = ClientPackage.getChuangAnDataAction(smokeMac);
			SessionMap sessionMap=SessionMap.newInstance();
			IoSession session = sessionMap.getSession(smokeMac.toUpperCase());
			if(session!=null){
				if(ack!=null){
					IoBuffer buf = IoBuffer.wrap(ack);
					WriteFuture future = session.write(buf);  //将ack回应给中继
			        future.awaitUninterruptibly(100);
				} 
				hr = new HttpRsult();
				hr.setError("成功");
				hr.setErrorCode(0);
			}else{
				hr = new HttpRsult();
				hr.setError("连接失败");
				hr.setErrorCode(2);
			}
			hr = new HttpRsult();
			hr.setError("成功");
			hr.setErrorCode(0);
			result=hr;
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * NB烟感EASYIOT进行下行控制消音功能
	 */
	public void EasyIot_erasure_control(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String serverID = this.request.getParameter("serverID");
			String password = this.request.getParameter("password");
			String devSerial = this.request.getParameter("devSerial");
			String value = this.request.getParameter("eleState");
			String userid = this.request.getParameter("userId");
			String context = "EASY-IOT烟感消音控制下发命令";
			String appId = this.request.getParameter("appId");
			if(StringUtils.isNotBlank(appId)){
				if(Integer.parseInt(appId)==1){
					serverID = "gzhrdev01";
					password = "pii1icRi";
				}else if(Integer.parseInt(appId)==3){
					serverID = "szhmdev01";
					password = "123456aB";
				}
			}
			
			HttpRsult hr = null;
			Object result = null;
			login= new LoginDaoImpl();
			toolDao = new ToolDaoImpl();
			
			String method = "HR_smoke_cmd";
			String key = "HR_smoke_erasure";
			
			if(serverID==null||password==null||devSerial==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				int state = 1;
				Utils.controlDev.put(devSerial, 199);
				String accessToken = login.getEaseIotAccessToKen(serverID, password);
				String bool = toolDao.ackAccessToKen(accessToken, serverID, devSerial, method, key,state);
				if(hr==null){
					hr = new HttpRsult();
				}
				if(bool.equals("成功")){
					for (int i = 0; i < 25; i++) {
						Thread.sleep(1000L);
						if(Utils.controlDev.get(devSerial)==99){	//99代表操作设置成功
							toolEasyDao = new ToolEasyDaoImpl();
							toolEasyDao.addOperator(userid, devSerial, context);
							Utils.controlDev.remove(devSerial);
							hr.setError("成功");
							hr.setErrorCode(0);
							break;
						}else if(Utils.controlDev.get(devSerial)==200){
							hr.setError("操作失败");
							hr.setErrorCode(2);
							break;
						}else if(i==24){
							hr.setError("超时");
							hr.setErrorCode(1);
							break;
						}
					}
				}else{
					hr.setError(bool);
					hr.setErrorCode(2);
				}
				result = hr;
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * NB电弧下行控制命令功能
	 * 	4	控制消音开
		5	控制消音关
		6	复位
		7	自检
	 */
	public void EasyIot_arc_electric(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String serverID = this.request.getParameter("serverID");
			String password = this.request.getParameter("password");
			String devSerial = this.request.getParameter("devSerial");
			String arcValue = this.request.getParameter("arcValue");
			String userid = this.request.getParameter("userId");
			String context = "EASY-IOT电弧控制下发命令"+arcValue;
			String appId = this.request.getParameter("appId");
			if(StringUtils.isNotBlank(appId)){
				if(Integer.parseInt(appId)==1){
					serverID = "gzhrdev01";
					password = "pii1icRi";
				}else if(Integer.parseInt(appId)==3){
					serverID = "szhmdev01";
					password = "123456aB";
				}
			}
			
			HttpRsult hr = null;
			Object result = null;
			login= new LoginDaoImpl();
			toolDao = new ToolDaoImpl();
			
			String method = "NB_arc_control";
			String key = "NB_arc_dataType";
			
			if(serverID==null||password==null||devSerial==null||StringUtils.isBlank(arcValue)){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				int state = Integer.parseInt(arcValue);
				Utils.controlDev.put(devSerial, 199);
				String accessToken = login.getEaseIotAccessToKen(serverID, password);
				String bool = toolDao.ackAccessToKen(accessToken, serverID, devSerial, method, key,state);
				if(hr==null){
					hr = new HttpRsult();
				}
				if(bool.equals("成功")){
					for (int i = 0; i < 25; i++) {
						Thread.sleep(1000L);
						if(Utils.controlDev.get(devSerial)==99){	//99代表操作设置成功
							toolEasyDao = new ToolEasyDaoImpl();
							toolEasyDao.addOperator(userid, devSerial, context);
							Utils.controlDev.remove(devSerial);
							hr.setError("成功");
							hr.setErrorCode(0);
							break;
						}else if(Utils.controlDev.get(devSerial)==200){
							hr.setError("操作失败");
							hr.setErrorCode(2);
							break;
						}else if(i==24){
							hr.setError("超时");
							hr.setErrorCode(1);
							break;
						}
					}
				}else{
					hr.setError(bool);
					hr.setErrorCode(2);
				}
				result = hr;
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
		南京平台消音
	 */
	public void nanjing_jiade_cancel(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String imeiValue = this.request.getParameter("imeiValue");
			String deviceType = this.request.getParameter("deviceType");
			ToolNanJinDao tnd = new ToolNanJinDaoImpl();
			String deviceId = tnd.getDeviceByImei(imeiValue);
			ToolOneNetDao tond = new ToolOneNetDaoImpl();
			HttpRsult hr = new HttpRsult();
			Object result = null;
			
			if((StringUtils.isBlank(deviceType)||StringUtils.isBlank(imeiValue))||(StringUtils.isBlank(deviceId)&&deviceType.equals("61"))){
				hr.setError("参数错误");
				hr.setErrorCode(1);
			}else{
				mbrDao = new GetSmokeMacByRepeaterDaoImpl();
				switch(deviceType){
				case "61"://南京
					boolean stateValue = tnd.getCancelCounld(deviceId,61);
					if(stateValue){
						mbrDao.chanageElectric(imeiValue, 3);
						hr.setError("成功");
						hr.setErrorCode(0);
					}else{
						hr.setError("失败");
						hr.setErrorCode(2);
					}
					break;
				case "73"://7020燃气
					boolean bool73 = tnd.getCancelCounld(deviceId,73);
					if(bool73){
						mbrDao.chanageElectric(imeiValue, 3);
						hr.setError("成功");
						hr.setErrorCode(0);
					}else{
						hr.setError("失败");
						hr.setErrorCode(2);
					}
					break;
				case "58":
//					ToolOneNetDaoImpl 的sendCmd(String imei,String deviceType
					String stateStr = tond.sendCmd(imeiValue, deviceType);
					if ("0".equals(stateStr)){
						mbrDao.chanageElectric(imeiValue, 3);
						hr.setError("消音成功");
						hr.setErrorCode(0);
					} else{
						hr.setError("消音失败");
						hr.setErrorCode(2);
					}
					break;
				default :
					hr.setError("设备类型错误");
					hr.setErrorCode(2);
					break;
				}
			}
			
			result = hr;
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 南京平台电气1号下发阈值
	 * @2018-8-20
	 * Undervoltage_threshold 欠压阈值
	 * Overvoltage_threshold 过压阈值
	 * Overcurrent_threshold 过流阈值
	 * Leakage_current_threshold 漏电阈值
	 * TemperatureThreshold(2)	CurrentMAX(2)	ShuntRelevance(1)
		温度阈值					最大输入电流		分励关联
	 */
	public void Telegraphy_Uool_control(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			final String imeiValue = this.request.getParameter("imei");
			String Undervoltage = this.request.getParameter("Undervoltage");
			String Overvoltage = this.request.getParameter("Overvoltage");
			String Overcurrent = this.request.getParameter("Overcurrent");
			String Leakage = this.request.getParameter("Leakage");
			String deviceType = this.request.getParameter("deviceType");
			String devCmd = this.request.getParameter("devCmd");
			String userid = this.request.getParameter("userid");
			
			String temperature = this.request.getParameter("Temperature");
			String currentMAX = this.request.getParameter("CurrentMAX");
			String shuntState = this.request.getParameter("ShuntRelevance");
			String repeaterMac = this.request.getParameter("repeaterMac");
			
			String heartime = this.request.getParameter("hearTime");
			
			
			String devSerial = "";
			ToolNanJinDao tnd = new ToolNanJinDaoImpl();
			devSerial = tnd.getDeviceByImei(imeiValue);
			HttpRsult hr = null;
			Object result = null;
			login= new LoginDaoImpl();
			toolDao = new ToolDaoImpl();
			hr = new HttpRsult();
			
			if(mLoginDao==null){
				mLoginDao = new LoginDaoImpl();
			}
			LoginEntity user = mLoginDao.login(userid);
			
			if(StringUtils.isBlank(imeiValue)||StringUtils.isBlank(deviceType)||StringUtils.isBlank(devCmd)){
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else if(user==null||!user.getIsCanCutEletr().equals("1")){
				hr = new HttpRsult();
				hr.setError("该用户无远程控制权限");
				hr.setErrorCode(5);
				result = hr;
			}else{
				boolean stateValue=false;
				final int dCmd = Integer.parseInt(devCmd);
				switch(deviceType){
				case "75"://南京电气1号
					if(StringUtils.isNotBlank(Overcurrent)){
						Overcurrent = (Float.parseFloat(Overcurrent)*10)+"";
					}
					stateValue = tnd.reSetThread(devSerial, 75, dCmd, Leakage, Undervoltage, Overcurrent, Overvoltage);
					if(stateValue&&dCmd!=14){
						mbrDao = new GetSmokeMacByRepeaterDaoImpl();
						if(dCmd==12){
							IfStopAlarm.electricState.put(imeiValue, 1);
							mbrDao.chanageElectric(imeiValue, 3);//设置中
						}else if(dCmd==13){
							IfStopAlarm.electricState.put(imeiValue, 2);
							mbrDao.chanageElectric(imeiValue, 3);//设置中
						}
						for(int i=0;i<30;i++){
							Thread.sleep(2000);
							int state=getDevState(imeiValue);
							System.out.println("ssss"+state+"~~~~~~~"+IfStopAlarm.electricState.get(imeiValue));
							if(state!=3){
								if(IfStopAlarm.electricState.get(imeiValue)!=state){
									//存储切电记录
									if(dCmd==12){
										GetSmokeMacByRepeaterDaoImpl.saveElectricHistoryByUserid(imeiValue, userid, 2);
									}else if(dCmd==13){
										GetSmokeMacByRepeaterDaoImpl.saveElectricHistoryByUserid(imeiValue,userid, 1);
									}
									
									hr.setError("成功");
									hr.setErrorCode(0);
									break;
								}
								
							}
							if(i==29){
								mbrDao.chanageElectric(imeiValue, IfStopAlarm.electricState.get(imeiValue));//恢复原状态
								hr.setError("超时");
								hr.setErrorCode(2);
							}
						}
					}else if(stateValue&&dCmd==14){
						hr.setError("参数设置已下发，请稍后刷新");
						hr.setErrorCode(0);
					}else{
						hr.setError("失败");
						hr.setErrorCode(2);
					}
					break;
				case "77"://南京三相电气
					if(StringUtils.isNotBlank(Overcurrent)){
						Overcurrent = (Float.parseFloat(Overcurrent)*10)+"";
					}
					stateValue = tnd.reSetThread(devSerial, 77, dCmd, Leakage, Undervoltage, Overcurrent, Overvoltage);
					if(stateValue&&dCmd!=14){
						mbrDao = new GetSmokeMacByRepeaterDaoImpl();
						if(dCmd==12){
							IfStopAlarm.electricState.put(imeiValue, 1);
							mbrDao.chanageElectric(imeiValue, 3);//设置中
						}else if(dCmd==13){
							IfStopAlarm.electricState.put(imeiValue, 2);
							mbrDao.chanageElectric(imeiValue, 3);//设置中
						}
						for(int i=0;i<30;i++){
							Thread.sleep(2000);
							int state=getDevState(imeiValue);
							System.out.println("ssss"+state+"~~~~~~~"+IfStopAlarm.electricState.get(imeiValue));
							if(state!=3){
								if(IfStopAlarm.electricState.get(imeiValue)!=state){
									//存储切电记录
									if(dCmd==12){
										GetSmokeMacByRepeaterDaoImpl.saveElectricHistoryByUserid(imeiValue, userid, 2);
									}else if(dCmd==13){
										GetSmokeMacByRepeaterDaoImpl.saveElectricHistoryByUserid(imeiValue,userid, 1);
									}
									
									hr.setError("成功");
									hr.setErrorCode(0);
									break;
								}
								
							}
							if(i==29){
								mbrDao.chanageElectric(imeiValue, IfStopAlarm.electricState.get(imeiValue));//恢复原状态
								hr.setError("超时");
								hr.setErrorCode(2);
							}
						}
					}else if(stateValue&&dCmd==14){
						hr.setError("参数设置已下发，请稍后刷新");
						hr.setErrorCode(0);
					}else{
						hr.setError("失败");
						hr.setErrorCode(2);
					}
					break;
				case "80"://南京U特电气
					stateValue = tnd.reSetThread(devSerial,80, dCmd, Leakage, Undervoltage, Overcurrent, Overvoltage,temperature,currentMAX,shuntState);
					if(stateValue){
						hr.setError("命令已下发");
						hr.setErrorCode(0);
						break;
					}else{
						hr.setError("失败");
						hr.setErrorCode(2);
					}
					break;
				case "81": //lora优特
				case "5":	//lora来源380
					byte[] ack = null;
					switch(dCmd){	//0x31(设置分励) 0x32(静音) 0x33(报警复位)14(阈值) 0xE6(改变心跳时间,秒) 0xE5(重启)
					case 31://0x31(设置分励)
						ack = tnd.ackLoraUte(imeiValue, (byte)0x31, repeaterMac, 31,shuntState, Leakage, Undervoltage, Overcurrent, Overvoltage,temperature,heartime);
						break;
					case 32://0x32(静音)
						ack = tnd.ackLoraUte(imeiValue, (byte)0x32, repeaterMac, 32,shuntState, Leakage, Undervoltage, Overcurrent, Overvoltage,temperature,heartime);
						break;
					case 33://0x33(报警复位)
						ack = tnd.ackLoraUte(imeiValue, (byte)0x33, repeaterMac, 33,shuntState, Leakage, Undervoltage, Overcurrent, Overvoltage,temperature,heartime);
						break;
					case 14://0x14(设置阈值)
						ack = tnd.ackLoraUte(imeiValue, (byte)0x14, repeaterMac, 14,shuntState, Leakage, Undervoltage, Overcurrent, Overvoltage,temperature,heartime);
						break;
					case 15://0xE6(改变心跳时间)
						ack = tnd.ackLoraUte(imeiValue, (byte)0xE6, repeaterMac, 15,shuntState, Leakage, Undervoltage, Overcurrent, Overvoltage,temperature,heartime);
						break;
					case 16://0xE5(重启)
						ack = tnd.ackLoraUte(imeiValue, (byte)0xE5, repeaterMac, 16,shuntState, Leakage, Undervoltage, Overcurrent, Overvoltage,temperature,heartime);
						break;
					}
					byte[] ack4G = ClientPackage.ackTo4GAlarm(ack);
					boolean resultImei = Utils.sendMessageRepeater(repeaterMac, ack4G,imeiValue);
					if(resultImei){//主机在线
						int max = 45;
						while(max>0){
							System.out.println("=======>>>>"+max+" imei:"+imeiValue+" == ==="+Utils.controlDev.get(imeiValue));
							Thread.sleep(1000);
							max--;
							if(Utils.controlDev.get(imeiValue)==0){//后台获取修改成功标志
								hr.setError("成功");
								hr.setErrorCode(0);
								break;
							}else if(Utils.controlDev.get(imeiValue)==2){//后台获取修改成功标志
								hr.setError("失败");
								hr.setErrorCode(2);
								break;
							}
						}
						if(max==0){
							hr.setError("超时");
							hr.setErrorCode(2);
						}
					}else{
						hr.setError("失败");
						hr.setErrorCode(2);
					}
					break;
				case "58":
					break;
				default :
					hr.setError("设备类型错误");
					hr.setErrorCode(2);
					break;
				}
				result = hr;
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
		南京平台消音
	*/
	public void nanjing_ranqi_7020(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String imeiValue = this.request.getParameter("imeiValue");
			String deviceType = this.request.getParameter("deviceType");
			String deviceState = this.request.getParameter("state");
			ToolNanJinDao tnd = new ToolNanJinDaoImpl();
			String deviceId = tnd.getDeviceByImei(imeiValue);
			ToolOneNetDao tond = new ToolOneNetDaoImpl();
			HttpRsult hr = new HttpRsult();
			Object result = null;
			
			if(StringUtils.isBlank(deviceType)||StringUtils.isBlank(imeiValue)||StringUtils.isBlank(deviceState)){
				hr.setError("参数错误");
				hr.setErrorCode(1);
			}else{
				switch(deviceType){
				case "73"://7020燃气
					boolean bool73 = tnd.getCancelCounld(deviceId,73,deviceState);
					if(bool73){
						hr.setError("成功");
						hr.setErrorCode(0);
					}else{
						hr.setError("失败");
						hr.setErrorCode(2);
					}
					break;
				default :
					hr.setError("设备类型错误");
					hr.setErrorCode(2);
					break;
				}
			}
			
			result = hr;
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
		南京平台NB普通水压下发阈值
	*/
	public void nanjing_set_water_data(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String imeiValue = this.request.getParameter("imeiValue");
			String deviceType = this.request.getParameter("deviceType");
			String hight_set = this.request.getParameter("hight_set");	//(2)高压阈值
			String low_set = this.request.getParameter("low_set");//(2)低压阈值
			String collect_time = this.request.getParameter("collect_time");//(2)采集时间
			String send_time = this.request.getParameter("send_time");//(2)上报时间

			ToolNanJinDao tnd = new ToolNanJinDaoImpl();
			String deviceId = tnd.getDeviceByImei(imeiValue);
			ToolOneNetDao tond = new ToolOneNetDaoImpl();
			HttpRsult hr = new HttpRsult();
			Object result = null;
			
			if(StringUtils.isBlank(deviceType)||StringUtils.isBlank(imeiValue)){
				hr.setError("参数错误");
				hr.setErrorCode(1);
			}else{
				switch(deviceType){
				case "78"://普通水压南京平台
					boolean bool73 = tnd.reSet_water_data(deviceId, 78, 0, hight_set, low_set, collect_time, send_time);
					if(bool73){
						hr.setError("成功");
						hr.setErrorCode(0);
					}else{
						hr.setError("失败");
						hr.setErrorCode(2);
					}
					break;
				default :
					hr.setError("设备类型错误");
					hr.setErrorCode(2);
					break;
				}
			}
			
			result = hr;
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	南京平台NB普通温湿度下发阈值
	*/
	public void nanjing_set_TempHumi_data(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String imeiValue = this.request.getParameter("imeiValue");
			String deviceType = this.request.getParameter("deviceType");
			String Hight_HumiSet = this.request.getParameter("Hight_HumiSet");	//(2)高湿度阈值
			String Low_TempSet = this.request.getParameter("Low_TempSet");//(2)低温阈值
			String Hight_TempSet = this.request.getParameter("Hight_TempSet");	//(2)高温阈值
			String Low_HumiSet = this.request.getParameter("Low_HumiSet");//(2)低湿度阈值
			String Tcollect_time = this.request.getParameter("Tcollect_time");//(2)采集时间
			String Tsend_time = this.request.getParameter("Tsend_time");//(2)上报时间
	
			ToolNanJinDao tnd = new ToolNanJinDaoImpl();
			String deviceId = tnd.getDeviceByImei(imeiValue);
			ToolOneNetDao tond = new ToolOneNetDaoImpl();
			HttpRsult hr = new HttpRsult();
			Object result = null;
			
			if(StringUtils.isBlank(deviceType)||StringUtils.isBlank(imeiValue)){
				hr.setError("参数错误");
				hr.setErrorCode(1);
			}else{
				switch(deviceType){
				case "79"://普通温湿度南京平台下发
					boolean bool73 = tnd.reSet_TempHumi_data(deviceId, 79, 0, Low_TempSet, Hight_TempSet, Low_HumiSet, Hight_HumiSet, Tcollect_time, Tsend_time);
					if(bool73){
						hr.setError("成功");
						hr.setErrorCode(0);
					}else{
						hr.setError("失败");
						hr.setErrorCode(2);
					}
					break;
				default :
					hr.setError("设备类型错误");
					hr.setErrorCode(2);
					break;
				}
			}
			
			result = hr;
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private int getDevState(String mac) {
		String sql = "select electrState from smoke  where mac =?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		int state=3;
		try {
			ps = DBConnectionManager.prepare(conn, sql);
			ps.setString(1, mac);
			rs = ps.executeQuery();
			while (rs.next()) {
				state=rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return state;
	}
	
}

class MyElectrTimerTask extends java.util.TimerTask{
    public int i=0 ;
    public String mac="";
    
    public MyElectrTimerTask(String mac){
    	this.mac=mac;
    }
    
    public void run() {
        
    }

	
}
