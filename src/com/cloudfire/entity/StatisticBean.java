package com.cloudfire.entity;

public class StatisticBean {
	private String areaId;
	private String areaName;
	
	private int deviceId;
	private String deviceName;
	private int deviceNum; //设备数量
	
	private int alarmTypeId;
	private String alarmTypeName;
	
	private int start;
	private int end;
	
	private CountValue cv;

	
	public int getDeviceNum() {
		return deviceNum;
	}

	public void setDeviceNum(int deviceNum) {
		this.deviceNum = deviceNum;
	}
	public String  getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public int getAlarmTypeId() {
		return alarmTypeId;
	}

	public void setAlarmTypeId(int alarmTypeId) {
		this.alarmTypeId = alarmTypeId;
	}

	public String getAlarmTypeName() {
		return alarmTypeName;
	}

	public void setAlarmTypeName(String alarmTypeName) {
		this.alarmTypeName = alarmTypeName;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public CountValue getCv() {
		return cv;
	}

	public void setCv(CountValue cv) {
		this.cv = cv;
	}
	
}
