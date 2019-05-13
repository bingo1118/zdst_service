package com.cloudfire.thread;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import com.cloudfire.server.SanjiangIOTHandler;



public class SanJiangMQTTM extends TimerTask{
	
	private static String hostName="tcp://iot.3jyun.com:1884";
	public static String uuid="application/1/#";
	public static String token="application/1/#";
	private static String username= uuid ;  
    private static String password= token ;  
//    private static String subscribeTopic= uuid ;  
//    private static String publisheTopic= "message" ;
    private static String subscribeTopic= "/DataPort/206735fabf97328f/Rcv" ;  
    private static String publisheTopic= "msg1" ; 
    private static String sendMsg="{\"devices\":\""+uuid+"\",\"payload\":{\"param1\":\"value1\"}}";  
    private static MqttClient client;
    
    private String deviceId="206735fabf97328f"; 
    private String deviceSecret="d40647b615a1881e5eae29bd81be4fa6";

    public static void main(String[] args) {
		Timer timer = new Timer(); 
		timer.schedule(new SanJiangMQTTM(), 1000);
	}
    
    @Override
	public void run() {
		subscribe();
//		publish();
	}
	public String subscribe(){
		try {
		
			client = new MqttClient(hostName,System.currentTimeMillis()+"");
			client.setCallback(new MqttCallback() {
				
				@Override
				public void messageArrived(String topic, MqttMessage message){
					try {
						String a=new String(message.getPayload(),"utf-8");
						System.out.println("Messages received from the server："+a);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					SanjiangIOTHandler sj = new SanjiangIOTHandler();
					sj.messageReceived(topic, message);
				}
				
				@Override
				public void deliveryComplete(IMqttDeliveryToken arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void connectionLost(Throwable arg0) {
					// TODO Auto-generated method stub
					System.out.println("break up");
					Timer timer = new Timer();
			        timer.schedule(new SanJiangMQTTM(), 10*1000);	//用于lora wan烟感
				}
			});
			MqttConnectOptions conOptions = new MqttConnectOptions();
			
			username=deviceId+";"+System.currentTimeMillis();
			password=EncoderByMd5(username+deviceSecret);
			
			conOptions.setUserName(username);
			conOptions.setPassword(password.toCharArray());
			conOptions.setCleanSession(false);
			client.connect(conOptions);
			client.subscribe(subscribeTopic,1);
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
			System.out.println("服务器已经启动完毕");
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
	
	public static String EncoderByMd5(String buf) {  
	    try {  
	        MessageDigest digist = MessageDigest.getInstance("MD5");  
	        byte[] rs = digist.digest(buf.getBytes("UTF-8"));  
	        StringBuffer digestHexStr = new StringBuffer();  
	        for (int i = 0; i < 16; i++) {  
	            digestHexStr.append(byteHEX(rs[i]));  
	        }  
	        return digestHexStr.toString();  
	    } catch (Exception e) {  
	        e.printStackTrace(); 
	    }  
	    return null;  
	} 
	public static String byteHEX(byte ib) {  
	    char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };  
	    char[] ob = new char[2];  
	    ob[0] = Digit[(ib >>> 4) & 0X0F];  
	    ob[1] = Digit[ib & 0X0F];  
	    String s = new String(ob);  
	    return s;  
	}  
}
