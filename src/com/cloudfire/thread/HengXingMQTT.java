package com.cloudfire.thread;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.apache.log4j.Logger;


import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.db.SystemConfig;
import com.cloudfire.thread.MessageCallback.ConnectionLostListener;

public class HengXingMQTT extends TimerTask{
	
	private static Logger logger = Logger.getLogger(HengXingMQTT.class);
//	private static String host = "tcp://47.106.169.75:1883"; // 服务地址
	private static String host = SystemConfig.getConfigInfomation("MQTT_HOST");//"tcp://120.77.155.222:1883";
	private static String userName = "admin"; // 用户名
	private static String passWord = "password"; // 密码
	private static int keepAlive = 60; // 超时时间
	private static int keepAliveInterval = 20; // 会话心跳时间
	private static String clientId = System.currentTimeMillis()+""; // 客户端ID
	private static MqttClient client; // 客户端对象

	@Override
	public void run() {
		connectMQTT();
	}
	
	public static void main(String[] args) {
			connect(true);
			subScription("ND//B39HN8/sys_para",1);
			subScription("ND/B39HN8/storage_data",1);
			subScription("ND/B39HN8/alarm",1);
			subScription("ND//524CKR/sys_para",1);
			subScription("ND/524CKR/storage_data",1);
			subScription("ND/524CKR/alarm",1);
//			connectMQTT();
		
	}
	
	
	private static void connectMQTT() {
		connect(true);
		List<String> devlist=getWaterDevList();
		if(devlist!=null){
			for (String mac : devlist) {
				subScription("ND/"+mac+"/sys_para",1);
				subScription("ND/"+mac+"/storage_data",1);
				subScription("ND/"+mac+"/alarm",1);
			}
		}
		
	}
	

	public static boolean connect(boolean cleanSession) {
		try {
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
			logger.debug("==============mqtt服务连接成功！！！=================");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("==============mqtt服务连接失败！！！=================");
			return false;
		}
	}
	
	public static boolean subScription(String topic,int Qos) {
		try {
			int[] _Qos = { Qos };
			String[] _topic = { topic };
			client.subscribe(_topic, _Qos);
			logger.debug("==============mqtt主题{"+topic+"}订阅成功！！！=================");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("==============mqtt主题{"+topic+"}订阅失败！！！=================");
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

}
