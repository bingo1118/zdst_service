package com.cloudfire.entity;

import java.util.Map;

public class FireBean {
	private int deviceType;
	private String deviceName;
	
	private Map<String,Integer> alarmMap;

	public int getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public Map<String, Integer> getAlarmMap() {
		return alarmMap;
	}

	public void setAlarmMap(Map<String, Integer> alarmMap) {
		this.alarmMap = alarmMap;
	}
}
