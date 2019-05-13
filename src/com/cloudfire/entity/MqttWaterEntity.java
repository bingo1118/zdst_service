package com.cloudfire.entity;

import org.apache.commons.lang.StringUtils;

import com.cloudfire.until.GetTime;

//4、水压水位对接模拟封装数据
public class MqttWaterEntity {
	private String serviceType = "water";
	private String deviceId = "78";
	private String mac = "";
	private String alarmType = "";
	private String waterPressure = "";
	private String waterLevel = "";
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
	public String getWaterPressure() {
		return waterPressure;
	}
	public void setWaterPressure(String waterPressure) {
		this.waterPressure = waterPressure;
	}
	public String getWaterLevel() {
		return waterLevel;
	}
	public void setWaterLevel(String waterLevel) {
		this.waterLevel = waterLevel;
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
