package com.cloudfire.entity;

import java.math.BigDecimal;


public class SmartControlEntity {
	private String smokeMac;
	private String company;
	private String address;
	private String floor;
	private String enterprise;
	private String devLocation;
	private String devType;
	private String devState;
	private String stateTime;
	private String repeaterMac;
	private String placeTypeName;
	private CameraBean camera;
	private int  cameraChannel=255;
	private String rssivalue = "0";
	
	private int row;
	
	private int ifAlarm;
	private int alarmType;
	private String alarmName;
	
	private String freeTime;
	private BigDecimal free;
	private String stopTime;
	private String userId;
	private int deviceTypeId;
	
	public int getIfAlarm() {
		return ifAlarm;
	}
	public void setIfAlarm(int ifAlarm) {
		this.ifAlarm = ifAlarm;
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
	public String getRssivalue() {
		return rssivalue;
	}
	public void setRssivalue(String rssivalue) {
		if(rssivalue==null){
			this.rssivalue = "0";
		}else{
			this.rssivalue = rssivalue;
		}
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public String getPlaceTypeName() {
		return placeTypeName;
	}
	public void setPlaceTypeName(String placeTypeName) {
		this.placeTypeName = placeTypeName;
	}
	public String getSmokeMac() {
		return smokeMac;
	}
	public void setSmokeMac(String smokeMac) {
		this.smokeMac = smokeMac;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
	}
	public String getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(String enterprise) {
		this.enterprise = enterprise;
	}
	public String getDevLocation() {
		return devLocation;
	}
	public void setDevLocation(String devLocation) {
		this.devLocation = devLocation;
	}
	public String getDevType() {
		return devType;
	}
	public void setDevType(String devType) {
		this.devType = devType;
	}
	public String getDevState() {
		return devState;
	}
	public void setDevState(String devState) {
		this.devState = devState;
	}
	public String getStateTime() {
		return stateTime;
	}
	public void setStateTime(String stateTime) {
		this.stateTime = stateTime;
	}
	public String getRepeaterMac() {
		return repeaterMac;
	}
	public void setRepeaterMac(String repeaterMac) {
		this.repeaterMac = repeaterMac;
	}
	
	public int getCameraChannel() {
		return cameraChannel;
	}
	public void setCameraChannel(int cameraChannel) {
		this.cameraChannel = cameraChannel;
	}
	public CameraBean getCamera() {
		return camera;
	}
	public void setCamera(CameraBean camera) {
		this.camera = camera;
	}
	
	public BigDecimal getFree() {
		return free;
	}
	public void setFree(BigDecimal free) {
		this.free = free;
	}
	public String getFreeTime() {
		return freeTime;
	}
	public void setFreeTime(String freeTime) {
		this.freeTime = freeTime;
	}
	public String getStopTime() {
		return stopTime;
	}
	public void setStopTime(String stopTime) {
		this.stopTime = stopTime;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getDeviceTypeId() {
		return deviceTypeId;
	}
	public void setDeviceTypeId(int deviceTypeId) {
		this.deviceTypeId = deviceTypeId;
	}
	
}
