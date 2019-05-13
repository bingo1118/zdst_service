package com.cloudfire.thread;

import java.util.TimerTask;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import com.cloudfire.db.SystemConfig;
import com.cloudfire.server.LoRaWanIoHandler;
import com.cloudfire.until.GetTime;


public class MQTTMemo extends TimerTask {
	
	private static String hostName= SystemConfig.getConfigInfomation("MQTT_HOST");
	public static String uuid="864814048264641";//868744032516878"application/1/#";868744031339496、869405032168198
	public static String token="864814048264641";//"application/1/#";
	private static String username= "admin";//uuid -admin;  
    private static String password= "FiDDJU1p850Y";//token password;  
    private static String subscribeTopic= uuid ;  
    private static String publisheTopic= "message" ;  
    private static String sendMsg="{\"devices\":\""+uuid+"\",\"payload\":{\"param1\":\"value1\"}}";  
    private static MqttClient client;

	@Override
	public void run() {
		subscribe();
//		publish();
	}
	public static void main(String[] args) {
		new MQTTMemo().subscribe();
		
	}
	public String subscribe(){
		try {
			client = new MqttClient(hostName,"864814048264641");//865820031004117/8AB50335、868744031339496、869405032168198
			client.setCallback(new MqttCallback() {
				
				@Override
				public void messageArrived(String topic, MqttMessage message){
					System.out.println(GetTime.ConvertTimeByLong()+"==Messages received from the server："+message.toString());
//					LoRaWanIoHandler lora = new LoRaWanIoHandler();
//					lora.messageReceived(topic, message);
				}
				
				@Override
				public void deliveryComplete(IMqttDeliveryToken arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void connectionLost(Throwable arg0) {
					// TODO Auto-generated method stub
//					Timer timer = new Timer();
//			        timer.schedule(new MQTTMemo(), 10*1000);	//用于lora wan烟感
//					new MQTTMemo().subscribe();
				}
			});
			MqttConnectOptions conOptions = new MqttConnectOptions();
			conOptions.setUserName(username);
			conOptions.setPassword(password.toCharArray());
			conOptions.setCleanSession(false);
			client.connect(conOptions);
			client.subscribe(subscribeTopic);
			boolean isSuccess = client.isConnected();
			System.out.println("connection status："+isSuccess);
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
		return "success";
	}
	
	public void publish(){
		try {
			MqttTopic topic = client.getTopic(publisheTopic);
			System.out.println("LoraWan服务器已经启动完毕");
			MqttMessage message = new MqttMessage(sendMsg.getBytes());
			message.setQos(1);
			while(true){
				MqttDeliveryToken token = topic.publish(message);
				while(!token.isComplete()){
					token.waitForCompletion(1000);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
