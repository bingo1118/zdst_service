package com.cloudfire.entity;

import java.io.Serializable;

public class ElectrAlarmThresholdEntity implements Serializable{

	private String error="";
    private int errorCode;
    private String value43;
    private String value44;
    private String value45;
    private String value46;
    private String value47;
    
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
	public String getValue43() {
		return value43;
	}
	public void setValue43(String value43) {
		this.value43 = value43;
	}
	public String getValue44() {
		return value44;
	}
	public void setValue44(String value44) {
		this.value44 = value44;
	}
	public String getValue45() {
		return value45;
	}
	public void setValue45(String value45) {
		this.value45 = value45;
	}
	public String getValue46() {
		return value46;
	}
	public void setValue46(String value46) {
		this.value46 = value46;
	}
	public String getValue47() {
		return value47;
	}
	public void setValue47(String value47) {
		this.value47 = value47;
	}
}

