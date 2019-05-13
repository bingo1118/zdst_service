package com.cloudfire.entity;

import java.io.Serializable;

public class StisEntity implements Serializable{
	private static final long serialVersionUID = 9004744348536574626L;
	private String stisDate;
	private String buildingId;
	private int deviceNum=0;
	private int deviceOnlineNum=0;
	private int deviceOfflineNum=0;
	private int deviceFaultNum=0;
	private int deviceAlarmNum=0;
	private int deviceUseNum=0;
	public String getStisDate() {
		return stisDate;
	}
	public void setStisDate(String stisDate) {
		this.stisDate = stisDate;
	}
	public String getBuildingId() {
		return buildingId;
	}
	public void setBuildingId(String buildingId) {
		this.buildingId = buildingId;
	}
	public int getDeviceNum() {
		return deviceNum;
	}
	public void setDeviceNum(int deviceNum) {
		this.deviceNum = deviceNum;
	}
	public int getDeviceOnlineNum() {
		return deviceOnlineNum;
	}
	public void setDeviceOnlineNum(int deviceOnlineNum) {
		this.deviceOnlineNum = deviceOnlineNum;
	}
	public int getDeviceOfflineNum() {
		return deviceOfflineNum;
	}
	public void setDeviceOfflineNum(int deviceOfflineNum) {
		this.deviceOfflineNum = deviceOfflineNum;
	}
	public int getDeviceFaultNum() {
		return deviceFaultNum;
	}
	public void setDeviceFaultNum(int deviceFaultNum) {
		this.deviceFaultNum = deviceFaultNum;
	}
	public int getDeviceAlarmNum() {
		return deviceAlarmNum;
	}
	public void setDeviceAlarmNum(int deviceAlarmNum) {
		this.deviceAlarmNum = deviceAlarmNum;
	}
	public int getDeviceUseNum() {
		return deviceUseNum;
	}
	public void setDeviceUseNum(int deviceUseNum) {
		this.deviceUseNum = deviceUseNum;
	}
}
