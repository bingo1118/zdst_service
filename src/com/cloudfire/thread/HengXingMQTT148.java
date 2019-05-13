package com.cloudfire.thread;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;


import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.db.SystemConfig;
import com.cloudfire.thread.MessageCallback.ConnectionLostListener;
import com.cloudfire.until.GetTime;

public class HengXingMQTT148 extends TimerTask{
	
	private static Logger logger = Logger.getLogger(HengXingMQTT.class);
//	private static String host = "tcp://47.106.169.75:1883"; // 服务地址
//	private static String host = "tcp://120.77.155.222:1883";
	private static String host = "tcp://119.29.155.148:1883";
//	private static String host = "tcp://193.112.215.201:1883";
	private static String userName = "admin"; // 用户名
	private static String passWord = "password"; // 密码
	private static int keepAlive = 60; // 超时时间
	private static int keepAliveInterval = 20; // 会话心跳时间
	private static String clientId = "HRSST"+System.currentTimeMillis()+""; // 客户端ID
	private static MqttClient client; // 客户端对象
	
	private static int reConnectNum=0;

	@Override
	public void run() {
		connectMQTT();
	}
	
	
	public static void main(String[] args) {
//		connect(true);
//		subScription("123456HRSST",1);
//		subScription("ND/B39HN8/storage_data",1);
//		subScription("ND/B39HN8/alarm",1);
//		subScription("ND//524CKR/sys_para",1);
//		subScription("ND/524CKR/storage_data",1);
//		subScription("ND/524CKR/alarm",1);
			connectMQTT();
		
	}
	
	
	private static void connectMQTT() {
		connect(true);
		List<String> devlist=getWaterDevList();
		if(devlist!=null){
			for (String mac : devlist) {
				if(subScription("ND/"+mac+"/sys_para",1)){
					updateDeviceMac(mac,"1");
				}else{
					updateDeviceMac(mac,"1");
				}
				if(subScription("ND/"+mac+"/storage_data",1)){
					updateDeviceMac(mac,"1");
				}else{
					updateDeviceMac(mac,"0");
				}
				if(subScription("ND/"+mac+"/alarm",1)){
					updateDeviceMac(mac,"1");
				}else{
					updateDeviceMac(mac,"0");
				}
			}
		}
		
	}
	

	public static boolean connect(boolean cleanSession) {
		try {
			clientId = "HRSST"+System.currentTimeMillis(); // 客户端ID
			client = new MqttClient(host, clientId, new MemoryPersistence());
			MqttConnectOptions options = new MqttConnectOptions();
			options.setCleanSession(cleanSession);
			options.setUserName(userName);
			options.setPassword(passWord.toCharArray());
			options.setConnectionTimeout(keepAlive);
			options.setKeepAliveInterval(keepAliveInterval);
			MessageCallback callback=new MessageCallback();
			callback.setConnectionLostListener(new ConnectionLostListener() {
				
				@Override
				public void onConeettionLost() {
					connectMQTT();
				}
			});
			client.setCallback(callback);
			client.connect(options);
			reConnectNum=0;
			logger.debug("==============148mqtt服务连接成功！！！=================");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("==============148mqtt服务连接失败！！！=================");
			if(reConnectNum>60){
				reConnectNum=0;
			}else{
				Timer timer=new Timer();
		          timer.schedule(new TimerTask() {
					@Override
					public void run() {
						connectMQTT();
						reConnectNum++;
					}
				}, 60000);
			}
			return false;
		}
	}
	
	public static boolean subScription(String topic,int Qos) {
		try {
			int[] _Qos = { Qos };
			String[] _topic = { topic };
			client.subscribe(_topic, _Qos);
			logger.debug("==============148:mqtt主题{"+topic+"}订阅 subScription Success=================");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("==============148:mqtt主题{"+topic+"}订阅 subScription Fail=================");
			return false;
		}
	}
	
	public static List<String> getWaterDevList() {
		// TODO Auto-generated method stub
		String sql="select mac from smoke where deviceType=70 or deviceType=69";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		List<String> list =null;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				if(list==null){
					list = new ArrayList<String>();
				}
				list.add(rs.getString(1));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return list;
	}
	
	public static int updateDeviceMac(String deviceMac,String state) {
		int result = 0;
		String sqlstr = "update smoke set electrState = ? where mac = ?";
		String stateTime = GetTime.ConvertTimeByLong();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		try {
			ps.setString(1, state);
			ps.setString(2, deviceMac);
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

}