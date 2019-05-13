package com.cloudfire.entity;

public class PushAlarmMqttEntity {
	private String mac;//mac地址
	private String address;//地址
	private String time;//时间
	private int netState;//在线状态，1在线，0离线
	private String named;//名称
	private String principal1;//联系人
	private String principal1Phone;//联系人电话
	private int deviceType;//设备类型ID
	private String deviceName;//设备类型名称 
	private int alarmType;//报警类型ID
	private String alarmName;//报警类型名称
	private int areaid;//区域ID
	private String areaName;//区域名称 
	private String alarmTime;//报警时间
	private Object obj;
	
	public Object getObj() {
		return obj;
	}
	public void setObj(Object obj) {
		this.obj = obj;
	}
	
	public String getAlarmTime() {
		return alarmTime;
	}
	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getNetState() {
		return netState;
	}
	public void setNetState(int netState) {
		this.netState = netState;
	}
	public String getNamed() {
		return named;
	}
	public void setNamed(String named) {
		this.named = named;
	}
	public String getPrincipal1() {
		return principal1;
	}
	public void setPrincipal1(String principal1) {
		this.principal1 = principal1;
	}
	public String getPrincipal1Phone() {
		return principal1Phone;
	}
	public void setPrincipal1Phone(String principal1Phone) {
		this.principal1Phone = principal1Phone;
	}
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
	public int getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(int alarmType) {
		this.alarmType = alarmType;
	}
	public String getAlarmName() {
		return alarmName;
	}
	public void setAlarmName(String alarmName) {
		this.alarmName = alarmName;
	}
	public int getAreaid() {
		return areaid;
	}
	public void setAreaid(int areaid) {
		this.areaid = areaid;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
}
