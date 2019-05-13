package com.cloudfire.entity;

import java.util.List;

public class NFCDeviceTypeEntity {
	
	private String error="获取类型失败";
    private int errorCode=2;
    private List<ShopTypeEntity> deviceTypes;
    
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
	public List<ShopTypeEntity> getDeviceType() {
		return deviceTypes;
	}
	public void setDeviceType(List<ShopTypeEntity> deviceTypes) {
		this.deviceTypes = deviceTypes;
	}

}
