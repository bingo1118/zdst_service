package com.cloudfire.entity;

public class THInfoBeanEntity {
	private int errorCode;
	private String error="";
	private THDevice thdevice;
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
	public THDevice getThdevice() {
		return thdevice;
	}
	public void setThdevice(THDevice thdevice) {
		this.thdevice = thdevice;
	}

}
