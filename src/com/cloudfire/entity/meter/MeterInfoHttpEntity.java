package com.cloudfire.entity.meter;

import java.util.List;

public class MeterInfoHttpEntity {
	private int errorCode;
	private String error;
	private String user;
	private List<MeterInfoEntity> deviceList;
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public List<MeterInfoEntity> getDeviceList() {
		return deviceList;
	}
	public void setDeviceList(List<MeterInfoEntity> deviceList) {
		this.deviceList = deviceList;
	}
	
}
