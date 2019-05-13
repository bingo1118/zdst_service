package com.cloudfire.push;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.entity.PushAlarmMqttEntity;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.Utils;

public class WebThread extends Thread {

	private List<String> userList;
	private int type = 0;
	private String mac="";
	private Object obj;
	private int state = 0;
	
	public WebThread(List<String> userList) {
		this.userList = userList;
	}
	public WebThread(List<String> userList ,String mac) {
		this.userList = userList;
		this.mac=mac;
	}
	
	public WebThread(List<String> userList,String mac,int type) {
		this.type = type;
		this.userList = userList;
		this.mac=mac;
	}
	
	public WebThread(List<String> userList,int type) {
		this.type = type;
		this.userList = userList;
	}
	
	public WebThread(String mac,int state) {
		this.mac = mac;
		this.state = state;
	}
	
	@Override
	public void run() {
		
		AreaDaoImpl areaDaoImpl=new AreaDaoImpl();
		String areaid=areaDaoImpl.getAreaIdByMac(mac);
		Utils.sendMessage(areaid, "["+"13622215085"+"] 向你发送:["+type+"]");
		Utils.sendMessage("17484023", "["+"13622215085"+"] 向你发送:["+type+"]");
	}
}
