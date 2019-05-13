package com.cloudfire.entity;

public class DeviceAlarmEntity {
	private String smokeMac;
	private int alarmthreshold1 = 0;
	private int alarmthreshold2 = 0;
	private int alarmthreshold3;
	private int alarmthreshold4;
	private int currentValue;
	private int deviceType;
	
	public String getSmokeMac() {
		return smokeMac;
	}
	public void setSmokeMac(String smokeMac) {
		this.smokeMac = smokeMac;
	}
	public int getAlarmthreshold1() {
		return alarmthreshold1;
	}
	public void setAlarmthreshold1(int alarmthreshold1) {
		this.alarmthreshold1 = alarmthreshold1;
	}
	public int getAlarmthreshold2() {
		return alarmthreshold2;
	}
	public void setAlarmthreshold2(int alarmthreshold2) {
		this.alarmthreshold2 = alarmthreshold2;
	}
	public int getAlarmthreshold3() {
		return alarmthreshold3;
	}
	public void setAlarmthreshold3(int alarmthreshold3) {
		this.alarmthreshold3 = alarmthreshold3;
	}
	public int getAlarmthreshold4() {
		return alarmthreshold4;
	}
	public void setAlarmthreshold4(int alarmthreshold4) {
		this.alarmthreshold4 = alarmthreshold4;
	}
	public int getCurrentValue() {
		return currentValue;
	}
	public void setCurrentValue(int currentValue) {
		this.currentValue = currentValue;
	}
	public int getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}
	
}
