package com.cloudfire.entity;

public class Devices {
	private int id; //�豸����id
	private String deviceName; //�豸������
	private int num; //�豸����
	private int onNum; //�����豸����
	public int getOnNum() {
		return onNum;
	}
	public void setOnNum(int onNum) {
		this.onNum = onNum;
	}
	private int offNum; //�����豸����
	public int getOffNum() {
		return offNum;
	}
	public void setOffNum(int offNum) {
		this.offNum = offNum;
	}
	private int alarmNum; //��ʵ��������
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
