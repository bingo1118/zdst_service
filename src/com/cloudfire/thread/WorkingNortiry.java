package com.cloudfire.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cloudfire.dao.FromRepeaterAlarmDao;
import com.cloudfire.dao.GetPushUserIdDao;
import com.cloudfire.dao.MeterInfoDao;
import com.cloudfire.dao.PublicUtils;
import com.cloudfire.dao.PushAlarmMsgDao;
import com.cloudfire.dao.impl.FromRepeaterAlarmDaoImpl;
import com.cloudfire.dao.impl.GetPushUserIdDaoImpl;
import com.cloudfire.dao.impl.PushAlarmMsgDaoImpl;
import com.cloudfire.entity.PushAlarmMsgEntity;
import com.cloudfire.entity.SessionMap;
import com.cloudfire.push.MyThread;
import com.cloudfire.push.TxtThread;
import com.cloudfire.push.WebThread;

public class WorkingNortiry extends Thread{
	private PushAlarmMsgDao mPushAlarmMsgDao;
	private GetPushUserIdDao mGetPushUserIdDao;
	private FromRepeaterAlarmDao mFromRepeaterAlarmDao;
	private String userId;
	private double time;
	public WorkingNortiry(String userId,double time) {
		this.userId = userId;
		this.time = time;
	}
	@Override
	public void run() {
		try {
			System.out.println("join to WorkingNortiry:"+this.time+"  userid="+this.userId);
			for(int i = 1;i<this.time;i++){
				Thread.sleep(1000L);
			}
			System.out.println(">>>>>>>>>>>>>>>>>>>>>i=");
			mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
			mFromRepeaterAlarmDao = new FromRepeaterAlarmDaoImpl();
			mGetPushUserIdDao = new GetPushUserIdDaoImpl();
			PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(userId);
			mFromRepeaterAlarmDao.addnotifyMsg(this.userId,this.userId, 80, 0);
			mGetPushUserIdDao = new GetPushUserIdDaoImpl();
			List<String> userList = new ArrayList<String>();
			userList.add(this.userId);
			if (userList != null && userList.size() > 0) {
				System.out.println("<<<<<<<<<<<<<<<<<userId"+this.userId);
				Map<String, String> iosMap = mGetPushUserIdDao.getIosUser(userList);
				new MyThread(push, userList, iosMap).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
