package com.cloudfire.push;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.json.JSONObject;

import com.cloudfire.dao.SmokeDao;
import com.cloudfire.dao.impl.SmokeDaoImpl;
import com.cloudfire.entity.AlarmEntityForMQ;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.until.GetTime;
import com.rabbitmq.client.*;

//http://122.228.28.27:8190 
//mqUser
//mq258456
public class RBMQClient {
	private static String host="122.228.28.27";//"localhost";//
	private static int port=5672;
	private static String exchanges="pubinfo.alarm";
	private static String username="pubinfoAlarm";//"guest";//
	private static String password="alarm13579";//"guest";//
	private static String producerCode = "GRXF";
	private static String[] queues = {"remoteMonitoring","wisdomWater","wisdomElectricity","wisdomWarning","chargingPile","other"};
	private static String[] queueNames = {"Rbt_alarm_1_queue","Rbt_alarm_2_queue","Rbt_alarm_3_queue","Rbt_alarm_4_queue","Rbt_alarm_5_queue","Rbt_alarm_6_queue"};
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		//模拟推送消息
//		AlarmEntityForMQ ae = new AlarmEntityForMQ();
//		ae.setAlarmEventId("92315647");
//		ae.setAlarmType("8991");
//		ae.setChannelsCode("564322");
//		ae.setDeviceId("895643231");
//		ae.setHappenTime("2019-01-24 15:00:00");
//		ae.setProducerCode(producerCode);
//		Map<String,String> alarmContent = new HashMap<String,String>();
//		alarmContent.put("电压过压", "400V");
//		alarmContent.put("电流过载", "100A");
//		ae.setAlarmContent(alarmContent);
//		String message = (new JSONObject(ae)).toString();
//		System.out.println( message);
//		send(message,5);

		//接收消息
		receive();
		
		
	
	}

	public static void send(String message,int queue_index){
		String  QUEUE_NAME = queueNames[queue_index];
		Connection connection;

		try {
			ConnectionFactory cf = new ConnectionFactory();
			cf.setHost(host);
			cf.setPort(port);
			cf.setUsername(username);
			cf.setPassword(password);
			
			connection = cf.newConnection();
			  // 创建一个频道 
	        Channel channel = connection.createChannel();
	        //指定一个交换
	        channel.exchangeDeclare(exchanges, "direct",true);
	        // 指定一个队列  
	        //channel.queueDeclare(String queuename,boolean durable,boolean )
//	        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
	        // 往队列中发出一条消息 
//	        Map<String,Object> headers = new HashMap<String,Object>();
//	        headers.put("", "x-java-serialized-object");
//	        BasicProperties basic = new BasicProperties().builder().contentType("text/plain").build();
//	        channel.basicPublish(exchanges, QUEUE_NAME, basic, message.getBytes()); 
	        int i=0;
			while(i<2){
				channel.basicPublish(exchanges, QUEUE_NAME, null, message.getBytes("UTF-8"));
				i++;
				try {
					Thread.sleep(6000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
	      
	        System.out.println(" [x] Sent '" + message + "'");
	        // 关闭频道和连接  
	        channel.close();
	        connection.close(); 
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@SuppressWarnings("deprecation")
	public static void receive(){
		ConnectionFactory cf = new ConnectionFactory();
		cf.setHost(host);
		cf.setPort(port);
		cf.setUsername(username);
		cf.setPassword(password);
		try {
			Connection conn = cf.newConnection();
			Channel channel  = conn.createChannel();
			// 声明队列，主要为了防止消息接收者先运行此程序，队列还不存在时创建队列。  
			String  QUEUE_NAME = "Rbt_alarm_4_queue";
	        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
	        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
	      // 创建队列消费者  
	        QueueingConsumer consumer = new QueueingConsumer(channel);
	        // 指定消费队列
	        channel.basicConsume(QUEUE_NAME, true, consumer);
	        while (true) {  //消费者程序运行开着 如果生产者新增了数据会自动获取
	          Thread.sleep(500);
	             // nextDelivery是一个阻塞方法（内部实现其实是阻塞队列的take方法）  
	          QueueingConsumer.Delivery delivery = consumer.nextDelivery();
	          String message = new String(delivery.getBody(),"UTF-8");
	          System.out.println("'[x] Received '" + message );
	        }
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	public static  void PushToDianXin(String smokeMac,int alarmtype) {
		SmokeDao sd = new SmokeDaoImpl();
		SmokeBean smoke = sd.getSmokeByMac3(smokeMac);
		if (smoke.getParentId() == 191 || smoke.getParentId() == 192) { //国瑞消防的需要推rbmq，伊特诺的也要推
			int deviceType =smoke.getDeviceType();
			int deviceMiniType = sd.getDeviceMiniType(deviceType);
			String deviceTypeName = smoke.getDeviceTypeName();
			String alarmName = sd.getAlarmNameByAlarmType(alarmtype);
			switch(alarmtype) {
			case 202:
				alarmtype=401;
				break;
			case 193:
				alarmtype=404;
				break;
			default:
				alarmtype=499;
			}
			AlarmEntityForMQ ae = new AlarmEntityForMQ();
			ae.setAlarmEventId(UUID.randomUUID().toString().replace("-", ""));
			ae.setAlarmType(alarmtype+"");
			ae.setChannelsCode("1");
			ae.setDeviceId(smokeMac);
			ae.setHappenTime(GetTime.ConvertTimeByLong());
			switch(smoke.getParentId()) {
			case 191: //国瑞消防
				ae.setProducerCode("GRXF");
				break;
			case 192:// 伊特诺
				ae.setProducerCode("YTN");
				break;
			}
		
			Map<String,String> alarmContent = new HashMap<String,String>();
			alarmContent.put(alarmName, deviceTypeName);
//			alarmContent.put("电压过压", "400V");
//			alarmContent.put("电流过载", "100A");
			ae.setAlarmContent(alarmContent);
			String message = (new JSONObject(ae)).toString();
			int queue_index = 5;
			if (deviceMiniType < 99 ){
				queue_index = deviceMiniType - 1;
			}
			RBMQClient.send(message, queue_index);
		}
		
	}
}
