package com.cloudfire.push;

import java.util.List;
import java.util.Map;

import com.cloudfire.dao.GetPushUserIdDao;
import com.cloudfire.dao.PushAlarmMsgDao;
import com.cloudfire.dao.impl.GetPushUserIdDaoImpl;
import com.cloudfire.dao.impl.PushAlarmMsgDaoImpl;
import com.cloudfire.entity.PushAlarmMsgEntity;

/**
 * ��ҳ���ֻ�����������
 * @author hr
 *
 */
public class PushToAll {
	private static GetPushUserIdDao mGetPushUserIdDao ;
	private static  PushAlarmMsgDao mPushAlarmMsgDao;
	
	public static void pushToAll(String mac,int alarmType){
			mGetPushUserIdDao = new GetPushUserIdDaoImpl();
			List<String> userList = mGetPushUserIdDao.getAllUser(mac);
			List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(mac); //��ȡuseridsmoke������û����ڶ���֪ͨ
			if(userList!=null&&userList.size()>0){
				//����������Ϣ���߳�  �ֻ�����
				mPushAlarmMsgDao = new  PushAlarmMsgDaoImpl();
				Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList); //��ȡios�û��б�
				PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(mac,alarmType);
				new MyThread(push,userList,iosMap).start();  
				
				//��ҳ���Ϳ���
				new WebThread(userList,mac).start();
				
				//��������
				if (txtUserList != null &&txtUserList.size()>0) {
					new TxtThread(txtUserList,mac).start();        //����֪ͨ���߳�
				}
			}
	}
	
	public static void pushToAll(String mac,int alarmType,String alarmFamily){
		mGetPushUserIdDao = new GetPushUserIdDaoImpl();
		List<String> userList = mGetPushUserIdDao.getAllUser(mac);
		List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(mac); //��ȡuseridsmoke������û����ڶ���֪ͨ
		if(userList!=null&&userList.size()>0){
			//����������Ϣ���߳�  �ֻ�����
			mPushAlarmMsgDao = new  PushAlarmMsgDaoImpl();
			Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList); //��ȡios�û��б�
			PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(mac,alarmType);
			push.setAlarmFamily(alarmFamily);
			new MyThread(push,userList,iosMap).start();  
			
			//��ҳ���Ϳ���
			new WebThread(userList,mac).start();
			
			//��������
			if (txtUserList != null &&txtUserList.size()>0) {
				new TxtThread(txtUserList,mac).start();        //����֪ͨ���߳�
			}
		}
	}
	
}
