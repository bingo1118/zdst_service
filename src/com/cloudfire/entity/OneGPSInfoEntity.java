package com.cloudfire.entity;

public class OneGPSInfoEntity {
	private String error="ªÒ»° ß∞‹";
    private int errorCode=2;
    
    private GPSInfoBean info;
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
	public GPSInfoBean getInfo() {
		return info;
	}
	public void setInfo(GPSInfoBean info) {
		this.info = info;
	}
    

}
