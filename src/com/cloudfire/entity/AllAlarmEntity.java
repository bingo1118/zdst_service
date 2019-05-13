package com.cloudfire.entity;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


public class AllAlarmEntity implements Serializable{

	private static final long serialVersionUID = -478780487037383951L;
	private String error="";
    private int errorCode;
    @JsonProperty("Alarm")
    private List<AlarmMessageEntity> alarm;
    
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
	public List<AlarmMessageEntity> getAlarm() {
		return alarm;
	}
	public void setAlarm(List<AlarmMessageEntity> alarm) {
		this.alarm = alarm;
	}
	
    
}
