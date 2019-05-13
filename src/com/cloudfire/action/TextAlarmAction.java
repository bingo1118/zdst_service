package com.cloudfire.action;

import io.goeasy.GoEasy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.cloudfire.dao.FaultInfoDao;
import com.cloudfire.dao.FromRepeaterAlarmDao;
import com.cloudfire.dao.GetPushUserIdDao;
import com.cloudfire.dao.PushAlarmMsgDao;
import com.cloudfire.dao.SmokeLineDao;
import com.cloudfire.dao.TextAlarmDao;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.FaultInfoDaoImpl;
import com.cloudfire.dao.impl.FromRepeaterAlarmDaoImpl;
import com.cloudfire.dao.impl.GetPushUserIdDaoImpl;
import com.cloudfire.dao.impl.PushAlarmMsgDaoImpl;
import com.cloudfire.dao.impl.SmokeLineDaoImpl;
import com.cloudfire.dwr.push.PushMessageUtil;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.PrinterEntity;
import com.cloudfire.entity.PushAlarmMsgEntity;
import com.cloudfire.push.MyThread;
import com.cloudfire.push.WebThread;
import com.cloudfire.until.Utils;
import com.opensymphony.xwork2.ActionSupport;

public class TextAlarmAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{
	
	private static final long serialVersionUID = -8717399509347003500L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private TextAlarmDao mTextAlarmDao;
	private GetPushUserIdDao mGetPushUserIdDao;
	private AreaDaoImpl areaDaoImpl;
	private String areaid;
	
	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}
	
	public void textAlarm(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String smokeMac = this.request.getParameter("smokeMac");
			String repeaterMac = this.request.getParameter("repeaterMac");
			String info = this.request.getParameter("info");
			HttpRsult hr = null;
			Object result = null;
			if(!Utils.isNullStr(userId)||!Utils.isNullStr(smokeMac)){
				hr = new HttpRsult();
				hr.setError("²ÎÊý´íÎó");
				hr.setErrorCode(1);
				result = hr;
			}else{
				/*if(repeaterMac!=null&&repeaterMac.length()>0){
					AlarmInfoDao dao = new AlarmInfoDaoImpl();
					String time = GetTime.ConvertTimeByLong();
					PrinterEntity mPrinterEntity =dao.getFaltAlarmInfo(repeaterMac);
					mPrinterEntity.setRepeater(repeaterMac);
					mPrinterEntity.setFaultTime(time);
					mGetPushUserIdDao = new GetPushUserIdDaoImpl();
					List<String> pcUsers = mGetPushUserIdDao.getUserByRepeaterMac(repeaterMac);
					if(pcUsers!=null&&pcUsers.size()>0){
						PushAlarmToPCEntity mPushAlarmToPCEntity = new PushAlarmToPCEntity();
						mPushAlarmToPCEntity.setMasterFault(mPrinterEntity);
						mPushAlarmToPCEntity.setDeviceType(221);
						new MyThread(mPushAlarmToPCEntity,pcUsers,null,2).start();
					}
				}*/
				areaDaoImpl = new AreaDaoImpl();
				areaid=areaDaoImpl.getAreaIdByMac(smokeMac);
				SmokeLineDao mSmokeLineDao = new SmokeLineDaoImpl();
				PushAlarmMsgDao mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
				FromRepeaterAlarmDao mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
				PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(smokeMac,202);
				int deviceType = mFromRepeaterAlarmDao.getDeviceType(smokeMac);
				byte[] numOne = push.getPrincipal1Phone().getBytes();
				byte[] numTwo = push.getPrincipal2Phone().getBytes();
				if(deviceType!=5){
					mFromRepeaterAlarmDao.addAlarmMsg(smokeMac, repeaterMac, 202,1);
					mGetPushUserIdDao = new GetPushUserIdDaoImpl();
					List<String> userList = mGetPushUserIdDao.getAllUser(smokeMac);
					
					if(userList!=null&&userList.size()>0){
						//Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList);
						//new MyThread(push,userList,iosMap,2).start();
						new WebThread(userList,areaid).start();
					}
				}
				
				/*mTextAlarmDao = new TextAlarmDaoImpl();
				hr = mTextAlarmDao.textAlarm(userId, privilege, smokeMac, info);
				if(hr==null){
					hr = new HttpRsult();
					hr.setError("·¢ËÍÊ§°Ü");
					hr.setErrorCode(2);
					result = hr;
				}else{
					result = hr;
					OneKeyAlarmEntity okae = mTextAlarmDao.oneKeyAlarm(smokeMac, info, userId);
					if(okae!=null){
						mGetPushUserIdDao = new GetPushUserIdDaoImpl();
						List<String> userList = mGetPushUserIdDao.getOneKeyUser(smokeMac);
						if(userList!=null&&userList.size()>0){
							Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList);
							new MyThread(okae,userList,iosMap).start();
						}
					}
				}*/
			}
//			JSONObject jObject = new JSONObject(result);
//			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void textFaultAlarm(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String smokeMac = this.request.getParameter("smokeMac");
			String repeaterMac = this.request.getParameter("repeaterMac");
			String info = this.request.getParameter("info");
			HttpRsult hr = null;
			Object result = null;
			if(!Utils.isNullStr(userId)||!Utils.isNullStr(smokeMac)){
				hr = new HttpRsult();
				hr.setError("²ÎÊý´íÎó");
				hr.setErrorCode(1);
				result = hr;
			}else{
				if(repeaterMac!=null&&repeaterMac.length()>0){
					PrinterEntity p = new PrinterEntity();
					Map<Integer,Integer> m = new HashMap<Integer,Integer>();
					m.put(0, 1);
					m.put(1, 0);
					m.put(2, 2);
					p.setRepeater(repeaterMac);
					p.setFaultCode("2");
					p.setHwMap(m);
					PrinterEntity mPrinterEntity2 =p;
					mPrinterEntity2.setRepeater(repeaterMac);
					FaultInfoDao fDao = new FaultInfoDaoImpl();
					fDao.insertFaultByHW(mPrinterEntity2);
				}
				/*mTextAlarmDao = new TextAlarmDaoImpl();
				hr = mTextAlarmDao.textAlarm(userId, privilege, smokeMac, info);
				if(hr==null){
					hr = new HttpRsult();
					hr.setError("·¢ËÍÊ§°Ü");
					hr.setErrorCode(2);
					result = hr;
				}else{
					result = hr;
					OneKeyAlarmEntity okae = mTextAlarmDao.oneKeyAlarm(smokeMac, info, userId);
					if(okae!=null){
						mGetPushUserIdDao = new GetPushUserIdDaoImpl();
						List<String> userList = mGetPushUserIdDao.getOneKeyUser(smokeMac);
						if(userList!=null&&userList.size()>0){
							Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList);
							new MyThread(okae,userList,iosMap).start();
						}
					}
				}*/
			}
//			JSONObject jObject = new JSONObject(result);
//			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void textAlarmAck(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("GBK");
			String userId = this.request.getParameter("userId");
			GoEasy goEasy = new GoEasy("BC-b6b5074b53d44f97a010ebcd7d4f5ed2");
			goEasy.publish(userId, userId);
			//PushMessageUtil.sendMessageToOneCallBack(userId,"showMessage", "["+"13622215085"+"] ÏòÄã·¢ËÍ:["+2+"]", "clickEvent");
			/*String smokeMac = this.request.getParameter("smokeMac");
			String repeater = this.request.getParameter("repeater");
			String alarmSerialNumber = this.request.getParameter("alarmSerialNumber");
			HttpRsult hr = null;
			Object result = null;
			if(!Utils.isNullStr(userId)){
				hr = new HttpRsult();
				hr.setError("²ÎÊý´íÎó");
				hr.setErrorCode(1);
				result = hr;
			}else{
				PushAlarmMsgDao mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
				PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(smokeMac,202);
				byte[] numOne = push.getPrincipal1Phone().getBytes();
				byte[] numTwo = push.getPrincipal2Phone().getBytes();
				FromRepeaterAlarmDao mFromRepeaterAlarmDao = new FromRepeaterAlarmDaoImpl();
				mFromRepeaterAlarmDao.addAlarmMsg(smokeMac, repeater, 202,1);
				mGetPushUserIdDao = new GetPushUserIdDaoImpl();
				List<String> userList = mGetPushUserIdDao.getAllUser(smokeMac);
				
				if(userList!=null&&userList.size()>0){
					Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList);
					new MyThread(push,userList,iosMap).start();
				}*/
				
				
				/*
				mTextAlarmDao = new TextAlarmDaoImpl();
				hr = mTextAlarmDao.textAlarmAck(userId, alarmSerialNumber);
				if(hr==null){
					hr = new HttpRsult();
					hr.setError("·¢ËÍÊ§°Ü");
					hr.setErrorCode(2);
					result = hr;
				}else{
					result = hr;
					DisposeAlarmEntity dae = mTextAlarmDao.oneKeyAlarmACK(userId);
					String alarmUserId = Utils.getOneKeyUserId(alarmSerialNumber);
					if(dae!=null&&alarmUserId!=null){
						String alarmTime = GetTime.ConvertTimeByLong();
						dae.setAlarmType(4);
						dae.setDeviceType(6);
						dae.setPolice(userId);
						dae.setTime(alarmTime);
						List<String> listUser = new ArrayList<String>();
						listUser.add(alarmUserId);
						mGetPushUserIdDao = new GetPushUserIdDaoImpl();
						Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(listUser);
						new MyThread(dae,listUser,iosMap).start();
					}
				}*/
//			}
//			JSONObject jObject = new JSONObject(result);
//			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
