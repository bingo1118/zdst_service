package com.cloudfire.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.cloudfire.dao.FromRepeaterAlarmDao;
import com.cloudfire.dao.GetPushUserIdDao;
import com.cloudfire.dao.PublicUtils;
import com.cloudfire.dao.PushAlarmMsgDao;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.FromRepeaterAlarmDaoImpl;
import com.cloudfire.dao.impl.GetPushUserIdDaoImpl;
import com.cloudfire.dao.impl.PublicUtilsImpl;
import com.cloudfire.dao.impl.PushAlarmMsgDaoImpl;
import com.cloudfire.entity.LoRaWanMap;
import com.cloudfire.entity.PushAlarmMsgEntity;
import com.cloudfire.push.MyThread;
import com.cloudfire.push.WebThread;
import com.cloudfire.until.IntegerTo16;
import com.cloudfire.until.base.Base64Utils;

public class LoRaWanIoHandler {
	private final static Log log = LogFactory.getLog(LoRaWanIoHandler.class);
	private PushAlarmMsgDao mPushAlarmMsgDao;
	private GetPushUserIdDao mGetPushUserIdDao;
	private FromRepeaterAlarmDao mFromRepeaterAlarmDao;
	private LoRaWanMap mLoRaWanMap;
	private PublicUtils utilsDao;
	private AreaDaoImpl areaDaoImpl;
	private String areaid;

	public void messageReceived(String topic, MqttMessage message){
		// TODO Auto-generated method stub
		String messages = message.toString();
		Map<String,String> loraMap = new HashMap<String,String>();
		System.out.println(messages);
		loraMap = Base64Utils.mqttBase(messages);
		String loraMac = "";
		String cmd = "";
		for(String key:loraMap.keySet()){
			System.out.println("key:"+key+loraMap.get(key));
			loraMac = key;
			cmd = loraMap.get(key);
		}
		String out = Base64Utils.base64ToHex(cmd).trim();
		String src = "";
		String[] outs = out.split(",");
		for (int i = 0; i < outs.length; i++) {
			src = src+outs[i].substring(3);
		}
		int cmd1 = IntegerTo16.hexStringToAlgorism(src);
		System.out.println(cmd1+":cmd1");
    	switch (cmd1) {
    	case 203:	//设备--》发送心跳
    		long nowTime = System.currentTimeMillis();
    		mLoRaWanMap = LoRaWanMap.newInstance();
    		mLoRaWanMap.addTime(loraMac, nowTime);
    		utilsDao = new PublicUtilsImpl();
			utilsDao.updateDeviceMac(loraMac);
			break;
    	case 202:	//设备--报警
    	case 206:	//低电压
    		String mac = loraMac;
			if(mac==null){
				break;
			}
			areaDaoImpl = new AreaDaoImpl();
			areaid=areaDaoImpl.getAreaIdByMac(mac);
			utilsDao = new PublicUtilsImpl();
			utilsDao.updateDeviceMac(loraMac);
			mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
			mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
			int deviceType = mFromRepeaterAlarmDao.getDeviceType(mac);
			PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(mac,cmd1);
//			byte[] numOne = push.getPrincipal1Phone().getBytes();
//			byte[] numTwo = push.getPrincipal2Phone().getBytes();
			if(deviceType!=5){
				mFromRepeaterAlarmDao.addAlarmMsg(mac, "", cmd1,1);
				mGetPushUserIdDao = new GetPushUserIdDaoImpl();
				List<String> userList = mGetPushUserIdDao.getAllUser(mac);
				System.out.println("MAC="+mac);
				if(userList!=null&&userList.size()>0){
					Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList);
					new MyThread(push,userList,iosMap).start();
					new WebThread(userList,areaid).start();
				}
			}
			break;
		}
	}
}
