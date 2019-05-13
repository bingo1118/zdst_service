package com.cloudfire.entity;

import com.cloudfire.until.BaseQuery;

public class MyDevicesEntityQuery extends BaseQuery{
	private static final long serialVersionUID = -7616015392915937062L;
	/**"我的设备"的基本信息
	 * 设备名称 	设备型号 	楼栋楼层 	设备位置 	设备状态 	设备故障类型 	安装时间 	处理状态   	操作
	 * 
	 *  */
	private String devictType;
	private String devName;
	private String devMac;
	private String floor="";
	private String storeys="";
	private String position="";
	private String netStates;
	private String alarmType;
	private String alarmTime;
	private String time;
	private String ifDealAlarm;
	private String address;
	
	private String areaId;
	private String userId;
	private String J_xl_1;
	private String J_xl_2;
	
	
	public String getJ_xl_1() {
		return J_xl_1;
	}
	
	public String getJ_xl_2() {
		return J_xl_2;
	}
	
	public void setJ_xl_1(String j_xl_1) {
		this.J_xl_1 = j_xl_1;
	}
	
	public void setJ_xl_2(String j_xl_2) {
		this.J_xl_2 = j_xl_2;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getAreaId() {
		return areaId;
	}
	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getDevictType() {
		return devictType;
	}
	public void setDevictType(String devictType) {
		this.devictType = devictType;
	}
	public String getDevName() {
		return devName;
	}
	public void setDevName(String devName) {
		this.devName = devName;
	}
	public String getDevMac() {
		return devMac;
	}
	public void setDevMac(String devMac) {
		this.devMac = devMac;
	}
	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
	}
	public String getStoreys() {
		return storeys;
	}
	public void setStoreys(String storeys) {
		this.storeys = storeys;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getNetStates() {
		return netStates;
	}
	public void setNetStates(String netStates) {
		this.netStates = netStates;
	}
	public String getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}
	public String getAlarmTime() {
		return alarmTime;
	}
	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
	}
	public String getIfDealAlarm() {
		return ifDealAlarm;
	}
	public void setIfDealAlarm(String ifDealAlarm) {
		this.ifDealAlarm = ifDealAlarm;
	}
	
	
	
}
