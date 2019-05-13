package com.cloudfire.entity;

import org.apache.commons.lang.StringUtils;

import com.cloudfire.until.GetTime;

//5、温湿度对接模拟封装数据
public class MqttTempHumiEntity {
	private String serviceType = "temphumi";
	private String deviceId = "19";
	private String mac = "";
	private String alarmType = "0";
	private String temperature = "";
	private String humidity = "";
	private String time = "";
	
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}
	public String getTemperature() {
		return temperature;
	}
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}
	public String getHumidity() {
		return humidity;
	}
	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}
	public String getTime() {
		if(StringUtils.isBlank(time)){
			time = GetTime.ConvertTimeByLong();
		}
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
}
