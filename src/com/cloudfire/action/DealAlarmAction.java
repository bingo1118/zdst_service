package com.cloudfire.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import com.cloudfire.dao.DealAlarmDao;
import com.cloudfire.dao.FromRepeaterAlarmDao;
import com.cloudfire.dao.GetPushUserIdDao;
import com.cloudfire.dao.PushAlarmMsgDao;
import com.cloudfire.dao.impl.DealAlarmDaoImpl;
import com.cloudfire.dao.impl.FromRepeaterAlarmDaoImpl;
import com.cloudfire.dao.impl.GetPushUserIdDaoImpl;
import com.cloudfire.dao.impl.PushAlarmMsgDaoImpl;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.PushAlarmMsgEntity;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.push.MyThread;
import com.cloudfire.push.PushtoAPP;
import com.cloudfire.push.TxtThread;
import com.cloudfire.push.WebThread;
import com.cloudfire.until.ClientPackage;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.IfStopAlarm;
import com.cloudfire.until.Utils;
import com.opensymphony.xwork2.ActionSupport;

public class DealAlarmAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{

	private static final long serialVersionUID = 1373599590103252034L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private DealAlarmDao mDealAlarmDao;
	FromRepeaterAlarmDao mFromRepeaterAlarmDao;
	
	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}

	public void dealAlarm(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String smokeMac = this.request.getParameter("smokeMac");
			HttpRsult hr = null;
			Object result = null;
			if(!Utils.isNullStr(userId)||!Utils.isNullStr(smokeMac)){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mDealAlarmDao = new DealAlarmDaoImpl();
				hr = mDealAlarmDao.dealAlarm(userId, smokeMac);
				mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
				SmokeBean mSmokeBean = mFromRepeaterAlarmDao.getSmoke(smokeMac);
				if(mSmokeBean!=null){
					mSmokeBean.setMac(smokeMac);
					mSmokeBean = mFromRepeaterAlarmDao.getSmokeInfo(mSmokeBean);
					if(mSmokeBean.getList().size()>0){
						byte[] ack = ClientPackage.mobileDealAlarm(mSmokeBean,(byte)0x11);
						Utils.sendMessageRepeater(mSmokeBean.getRepeater(), ack);
					}
				}
				if(hr==null){
					hr = new HttpRsult();
					hr.setError("处理报警消息失败");
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
	
	public void dealAlarmDetail(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String smokeMac = this.request.getParameter("smokeMac");
			String dealPeople = this.request.getParameter("dealPeople");
			String alarmTruth = this.request.getParameter("alarmTruth");
			String dealDetail = this.request.getParameter("dealDetail");
			String image_path = this.request.getParameter("image_path");
			String video_path = this.request.getParameter("video_path");
			HttpRsult hr = null;
			Object result = null;
			if(!Utils.isNullStr(userId)||!Utils.isNullStr(smokeMac)){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mDealAlarmDao = new DealAlarmDaoImpl();
				hr = mDealAlarmDao.dealAlarmDetail(userId, smokeMac, dealPeople, alarmTruth, dealDetail,image_path,video_path);
				//声光消音下发
				mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
				SmokeBean mSmokeBean = mFromRepeaterAlarmDao.getSmoke(smokeMac);
				if(mSmokeBean!=null){
					mSmokeBean.setMac(smokeMac);
					mSmokeBean = mFromRepeaterAlarmDao.getSmokeInfo(mSmokeBean);
					if(mSmokeBean.getList().size()>0){
						byte[] ack = ClientPackage.mobileDealAlarm(mSmokeBean,(byte)0x11);
						Utils.sendMessageRepeater(mSmokeBean.getRepeater(), ack);
					}
				}
				if(hr==null){
					hr = new HttpRsult();
					hr.setError("处理报警消息失败");
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
	
	public void makeSureAlarm(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String smokeMac = this.request.getParameter("smokeMac");
			String alarmType=this.request.getParameter("alarmType");
			HttpRsult hr = null;
			Object result = null;
			if(!Utils.isNullStr(userId)||!Utils.isNullStr(smokeMac)){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				GetPushUserIdDao mGetPushUserIdDao = new GetPushUserIdDaoImpl();
				List<String> userList = mGetPushUserIdDao.getMakeSurePushAreaUserIdByMac(smokeMac);
				if(userList!=null&&userList.size()>0){
					PushAlarmMsgDao mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
					PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(smokeMac,Integer.parseInt(alarmType));
					push.setUploadpeople(userId);
					Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList); //获取ios用户列表
					new MyThread(push,userList,iosMap).start();  //开启推送消息的线程
					new WebThread(userList,smokeMac).start();      //短信通知的线程
					
				}
				hr = new HttpRsult();
				hr.setError("上报成功");
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
	
	public void makeSureGetUpload(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String uploadpeolpe = this.request.getParameter("uploadpeolpe");
			HttpRsult hr = null;
			Object result = null;
			if(!Utils.isNullStr(userId)||!Utils.isNullStr(uploadpeolpe)){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				PushtoAPP.PushtoSingle(uploadpeolpe, "上报报警已被确认收到，请知悉！");
				hr = new HttpRsult();
				hr.setError("上报成功");
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
}
