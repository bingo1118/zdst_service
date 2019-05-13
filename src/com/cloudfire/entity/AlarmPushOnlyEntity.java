package com.cloudfire.entity;

import org.apache.commons.lang3.StringUtils;

public class AlarmPushOnlyEntity {
	private String smokeMac;
	private int ifDealAlarm = 0;
	private int alarmType = 0;
	private String alarmTime;
	private String alarmFamily = "0";
	private String repeaterMac;
	
	public String getSmokeMac() {
		return smokeMac;
	}
	public void setSmokeMac(String smokeMac) {
		this.smokeMac = smokeMac;
	}
	public int getIfDealAlarm() {
		return ifDealAlarm;
	}
	public void setIfDealAlarm(int ifDealAlarm) {
		this.ifDealAlarm = ifDealAlarm;
	}
	public int getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(int alarmType) {
		this.alarmType = alarmType;
	}
	public String getAlarmTime() {
		return alarmTime;
	}
	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
	}
	public String getAlarmFamily() {
		return alarmFamily;
	}
	public void setAlarmFamily(String alarmFamily) {
		this.alarmFamily = alarmFamily;
	}
	public String getRepeaterMac() {
		return repeaterMac;
	}
	public void setRepeaterMac(String repeaterMac) {
		if(StringUtils.isBlank(repeaterMac)){
			this.repeaterMac = this.getSmokeMac();
		}else{
			this.repeaterMac = repeaterMac;
		}
	}
	
	
}
