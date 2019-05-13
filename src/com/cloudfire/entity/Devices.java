package com.cloudfire.entity;

public class Devices {
	private int id; //设备类型id
	private String deviceName; //设备类型名
	private int num; //设备数量
	private int onNum; //在线设备数量
	public int getOnNum() {
		return onNum;
	}
	public void setOnNum(int onNum) {
		this.onNum = onNum;
	}
	private int offNum; //离线设备数量
	public int getOffNum() {
		return offNum;
	}
	public void setOffNum(int offNum) {
		this.offNum = offNum;
	}
	private int alarmNum; //真实报警数量
	public int getAlarmNum() {
		return alarmNum;
	}
	public void setAlarmNum(int alarmNum) {
		this.alarmNum = alarmNum;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	
	
}
