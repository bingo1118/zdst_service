package com.cloudfire.entity;

import org.apache.commons.lang.StringUtils;

import com.cloudfire.until.GetTime;

//1、烟感对接模拟封装数据
public class MqttSmokeEntity {
	private String serviceType ="smoke";
	private String deviceId = "61";
	private String mac = "";
	private String alarmType = "";
	private String batteryPower = "";
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
	public String getBatteryPower() {
		return batteryPower;
	}
	public void setBatteryPower(String batteryPower) {
		this.batteryPower = batteryPower;
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
