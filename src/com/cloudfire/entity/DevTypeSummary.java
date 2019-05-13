package com.cloudfire.entity;
import java.util.List;

import com.cloudfire.entity.query.DeviceType;

public class DevTypeSummary {
	private String error="";
    private int errorCode;
    private List<DeviceType> deviceType;
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public List<DeviceType> getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(List<DeviceType> deviceType) {
		this.deviceType = deviceType;
	}
}
