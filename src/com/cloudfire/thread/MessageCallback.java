package com.cloudfire.thread;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;

import com.cloudfire.dao.FromRepeaterAlarmDao;
import com.cloudfire.dao.GetPushUserIdDao;
import com.cloudfire.dao.PublicUtils;
import com.cloudfire.dao.PushAlarmMsgDao;
import com.cloudfire.dao.RePeaterDataDao;
import com.cloudfire.dao.WaterInfoDao;
import com.cloudfire.dao.impl.FromRepeaterAlarmDaoImpl;
import com.cloudfire.dao.impl.GetPushUserIdDaoImpl;
import com.cloudfire.dao.impl.PublicUtilsImpl;
import com.cloudfire.dao.impl.PushAlarmMsgDaoImpl;
import com.cloudfire.dao.impl.RePeaterDataDaoImpl;
import com.cloudfire.dao.impl.WaterInfoDaoImpl;
import com.cloudfire.entity.DeviceAlarmEntity;
import com.cloudfire.entity.PushAlarmMsgEntity;
import com.cloudfire.push.MyThread;
import com.cloudfire.push.WebThread;
import com.starwsn.protocol.core.Starwsn;



public class MessageCallback implements MqttCallback{
	private Logger log = Logger.getLogger(MessageCallback.class);
	
	private ConnectionLostListener connectionLostListener;
	
	public void setConnectionLostListener(ConnectionLostListener connectionLostListener) {
		this.connectionLostListener = connectionLostListener;
	}
	
	public interface ConnectionLostListener{;
		public void onConeettionLost();
	}
	
	public void connectionLost(Throwable cause) {
		log.debug("==============mqtt server connect!!!=================");
		if(connectionLostListener!=null){
			connectionLostListener.onConeettionLost();
		}
	}

	public void deliveryComplete(IMqttDeliveryToken token) {
		log.debug("==============mqtt消息发布状态："+token.isComplete()+"==============");
	}

	public void messageArrived(String topic, MqttMessage message) {
		try {
			System.out.println("topic:=====" + topic);
			// 消息体
			byte[] payLoadArray = message.getPayload();
			System.out.println("payLoadArray：=====" + bytesToHexString(payLoadArray));
			String jsonStr = Starwsn.messageResolve(topic, payLoadArray);
			System.out.println("恒星水压："+jsonStr);
			dealData(jsonStr);
//			System.out.println("接收消息内容 : " + new String(message.getPayload()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	private static void dealData(String jsonStr) {
		try {
//			System.out.println("^^^***&&&:dealData()");
			PublicUtils utilsDao= new PublicUtilsImpl();
			org.json.JSONObject jsonObject=new org.json.JSONObject(jsonStr);
			String mac=jsonObject.getString("snCode");//@@设备ID
//			System.out.println("^^^***&&&:dealData()=mac="+mac);
			String status=jsonObject.getString("status");//@@发送状态
//			System.out.println("^^^***&&&:dealData()=status="+status);
			utilsDao = new PublicUtilsImpl();
			utilsDao.updateDeviceMac(mac);
			if("success".equals(status)){
//				System.out.println("^^^***&&&:dealData()=success=");
				String msgType=jsonObject.getString("msgType");//@@消息类型
				org.json.JSONArray msg=jsonObject.getJSONArray("msg");//@@消息体
				org.json.JSONObject jsontemp;
				switch(msgType){
				case "1"://@@设备信息
					break;
				case "2"://@@设备监测数据
					WaterInfoDao mWaterInfoDao=new WaterInfoDaoImpl();
					for(int i=0;i<msg.length();i++){
						jsontemp=msg.getJSONObject(i);
						String monitorCode=jsontemp.getString("monitorCode");
						String monitorName=jsontemp.getString("monitorName");
						String monitorData=jsontemp.getString("monitorData");
						String dataUnit=jsontemp.getString("dataUnit");
						String monitorTime=jsontemp.getString("monitorTime");
						if(monitorName.equals("压力采样值")){
							mWaterInfoDao.addWaterInfo("1", mac, 0, Float.parseFloat(monitorData)*1000+"",monitorTime);
						}
						if(monitorName.equals("液位高度")){
							DecimalFormat decimalFormat=new DecimalFormat("0.00");
							mWaterInfoDao.addWaterInfo("2", mac, 0, decimalFormat.format(Float.parseFloat(monitorData)/100.0f),monitorTime);
						}
					}
					break;
				case "3"://@@告警数据
					PushAlarmMsgDao mPushAlarmMsgDao=new PushAlarmMsgDaoImpl();
					for(int i=0;i<msg.length();i++){
						jsontemp=msg.getJSONObject(i);
						String alarmCode=jsontemp.getString("alarmCode");
						String alarmName=jsontemp.getString("alarmName");
						String alarmTypeCode=jsontemp.getString("alarmTypeCode");
						int alarmNum=1;
						RePeaterDataDao redao = new RePeaterDataDaoImpl();
						DeviceAlarmEntity dae = redao.getDeviceEntity(mac);
		    			int deviceTypeback = dae.getDeviceType();
						switch (alarmTypeCode) {
						case "1":
							alarmNum=136;//通信故障
							break;
						case "2":
							if(deviceTypeback==70){
								alarmNum=218;//高水压
							}else if(deviceTypeback==69){
								alarmNum=208;//高水位
							}
							break;
						case "3":
							if(deviceTypeback==70){
								alarmNum=209;//低水压
							}else if(deviceTypeback==69){
								alarmNum=207;//低水位
							}
							break;
						case "4":
							alarmNum=36;//设备故障
							break;
						case "5":
							alarmNum=193;//低电量
							break;
						case "6":
							alarmNum=12;//状态量报警
							break;
						default:
							break;
						}
						String alarmTypeName=jsontemp.getString("alarmTypeName");
						String alarmCommandCode=jsontemp.getString("alarmCommandCode");
						String alarmCommandName=jsontemp.getString("alarmCommandName");
//						String alarmStatus=jsontemp.getString("alarmStatus");
						String alarmValue=jsontemp.getString("alarmValue");
						String alarmTime=jsontemp.getString("alarmTime");
						mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
						FromRepeaterAlarmDao mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
						GetPushUserIdDao mGetPushUserIdDao=new GetPushUserIdDaoImpl();
						PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(mac,alarmNum);
						push.setAlarmFamily((int)(Float.parseFloat(alarmValue)*1000)+"");
						if(deviceTypeback!=5){
							mFromRepeaterAlarmDao.addAlarmMsg(mac, "1", alarmNum,alarmValue);
							mGetPushUserIdDao = new GetPushUserIdDaoImpl();
							List<String> userList = mGetPushUserIdDao.getAllUser(mac);
							System.out.println("MAC="+mac);
							if(userList!=null&&userList.size()>0){
								Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList);
								new MyThread(push,userList,iosMap).start();
								new WebThread(userList,mac).start();
							}
						}
					}
					break;
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	

	
}
