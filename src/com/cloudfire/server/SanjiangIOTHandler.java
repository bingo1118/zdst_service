package com.cloudfire.server;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;

import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.FromRepeaterAlarmDao;
import com.cloudfire.dao.GetPushUserIdDao;
import com.cloudfire.dao.PublicUtils;
import com.cloudfire.dao.PushAlarmMsgDao;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.FromRepeaterAlarmDaoImpl;
import com.cloudfire.dao.impl.GetPushUserIdDaoImpl;
import com.cloudfire.dao.impl.PublicUtilsImpl;
import com.cloudfire.dao.impl.PushAlarmMsgDaoImpl;
import com.cloudfire.entity.PushAlarmMsgEntity;
import com.cloudfire.push.MyThread;
import com.cloudfire.push.WebThread;
import com.cloudfire.until.Utils;

public class SanjiangIOTHandler {
	private final static Log log = LogFactory.getLog(LoRaWanIoHandler.class);
	private PushAlarmMsgDao mPushAlarmMsgDao;
	private GetPushUserIdDao mGetPushUserIdDao;
	private FromRepeaterAlarmDao mFromRepeaterAlarmDao;
	private String areaid="";
	
	private String mac;
	private String alarmType;
	private String streamId;
	private int alarmNum;

	
	public void messageReceived(String topic, MqttMessage message){
		// TODO Auto-generated method stub
		String messages = message.toString();
		org.json.JSONObject jsonObject;
		try {
			String a=new String(message.getPayload(),"utf-8");
			jsonObject = new org.json.JSONObject(a);
			mac = jsonObject.getString("deviceId");
			streamId=jsonObject.getString("streamId");
			PublicUtils utilsDao= new PublicUtilsImpl();
			utilsDao.updateDeviceMac(mac);//@@更新心跳时间
			if(areaid!=null&&!areaid.equals("")){
				alarmType=jsonObject.getString("type");
				switch(alarmType){
					case "":
					case "alarm":
						if(streamId.equals("self_check")){
							alarmNum=67;
						}else{
							alarmNum=202;
						}
						utilsDao = new PublicUtilsImpl();
						utilsDao.updateDeviceMac(mac);
						mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
						mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
						int deviceType = mFromRepeaterAlarmDao.getDeviceType(mac);
						PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(mac,alarmNum);
						if(deviceType!=5){
							mFromRepeaterAlarmDao.addAlarmMsg(mac, "1", alarmNum,1);
							mGetPushUserIdDao = new GetPushUserIdDaoImpl();
							List<String> userList = mGetPushUserIdDao.getAllUser(mac);
							System.out.println("MAC="+mac);
							if(userList!=null&&userList.size()>0){
								Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList);
								new MyThread(push,userList,iosMap).start();
								new WebThread(userList,mac).start();
							}
						}
						break;
					case "reset":
						break;
					case "clean":
						break;
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

}
