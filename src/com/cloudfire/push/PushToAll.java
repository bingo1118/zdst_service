package com.cloudfire.push;

import java.util.List;
import java.util.Map;

import com.cloudfire.dao.GetPushUserIdDao;
import com.cloudfire.dao.PushAlarmMsgDao;
import com.cloudfire.dao.impl.GetPushUserIdDaoImpl;
import com.cloudfire.dao.impl.PushAlarmMsgDaoImpl;
import com.cloudfire.entity.PushAlarmMsgEntity;

/**
 * 网页，手机，短信推送
 * @author hr
 *
 */
public class PushToAll {
	private static GetPushUserIdDao mGetPushUserIdDao ;
	private static  PushAlarmMsgDao mPushAlarmMsgDao;
	
	public static void pushToAll(String mac,int alarmType){
			mGetPushUserIdDao = new GetPushUserIdDaoImpl();
			List<String> userList = mGetPushUserIdDao.getAllUser(mac);
			List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(mac); //获取useridsmoke表里的用户用于短信通知
			if(userList!=null&&userList.size()>0){
				//开启推送消息的线程  手机推送
				mPushAlarmMsgDao = new  PushAlarmMsgDaoImpl();
				Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList); //获取ios用户列表
				PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(mac,alarmType);
				new MyThread(push,userList,iosMap).start();  
				
				//网页推送开启
				new WebThread(userList,mac).start();
				
				//短信推送
				if (txtUserList != null &&txtUserList.size()>0) {
					new TxtThread(txtUserList,mac).start();        //短信通知的线程
				}
			}
	}
	
	public static void pushToAll(String mac,int alarmType,String alarmFamily){
		mGetPushUserIdDao = new GetPushUserIdDaoImpl();
		List<String> userList = mGetPushUserIdDao.getAllUser(mac);
		List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(mac); //获取useridsmoke表里的用户用于短信通知
		if(userList!=null&&userList.size()>0){
			//开启推送消息的线程  手机推送
			mPushAlarmMsgDao = new  PushAlarmMsgDaoImpl();
			Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList); //获取ios用户列表
			PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(mac,alarmType);
			push.setAlarmFamily(alarmFamily);
			new MyThread(push,userList,iosMap).start();  
			
			//网页推送开启
			new WebThread(userList,mac).start();
			
			//短信推送
			if (txtUserList != null &&txtUserList.size()>0) {
				new TxtThread(txtUserList,mac).start();        //短信通知的线程
			}
		}
	}
	
}
