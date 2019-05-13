package com.cloudfire.entity;

import com.cloudfire.until.BaseQuery;

public class LinkAction extends BaseQuery {
	
	private static final long serialVersionUID = 1L;
	
	private int id;

	private String alarmMac;
	
	private int deviceType1;
	
	private String alarmMacType;
	
	private String responseMac;
	
	private int deviceType2;
	
	private String responseMacType;
	
	private String alarmType;
	
	private String action;
	
	private String userid;
	
	private String time;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAlarmMac() {
		return alarmMac;
	}

	public void setAlarmMac(String alarmMac) {
		this.alarmMac = alarmMac;
	}

	public String getResponseMac() {
		return responseMac;
	}

	public void setResponseMac(String responseMac) {
		this.responseMac = responseMac;
	}

	public String getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public int getDeviceType1() {
		return deviceType1;
	}

	public void setDeviceType1(int deviceType1) {
		this.deviceType1 = deviceType1;
	}

	public String getAlarmMacType() {
		return alarmMacType;
	}

	public void setAlarmMacType(String alarmMacType) {
		this.alarmMacType = alarmMacType;
	}

	public int getDeviceType2() {
		return deviceType2;
	}

	public void setDeviceType2(int deviceType2) {
		this.deviceType2 = deviceType2;
	}

	public String getResponseMacType() {
		return responseMacType;
	}

	public void setResponseMacType(String responseMacType) {
		this.responseMacType = responseMacType;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	@Override
	public String toString() {
		return "LinkAction [alarmMac=" + alarmMac + ", deviceType1=" + deviceType1 + ", alarmMacType=" + alarmMacType
				+ ", responseMac=" + responseMac + ", deviceType2=" + deviceType2 + ", responseMacType="
				+ responseMacType + ", alarmType=" + alarmType + ", action=" + action + "]";
	}

}
