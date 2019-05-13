package com.cloudfire.entity;

import java.io.Serializable;


public class OneAlarmEntity implements Serializable {
	
	private static final long serialVersionUID = 4367360002284253117L;
	private String error="";
    private int errorCode;
    private AlarmMessageEntity lasteatAlarm;
    
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
	public AlarmMessageEntity getLasteatAlarm() {
		return lasteatAlarm;
	}
	public void setLasteatAlarm(AlarmMessageEntity lasteatAlarm) {
		this.lasteatAlarm = lasteatAlarm;
	}
	
}
