package com.cloudfire.entity;

import java.util.List;

public class PrinterAlarmResult {
	private String error="»ñÈ¡±¨¾¯Ê§°Ü";
	private int errorCode=1;
	private List<PrinterEntity> alarm;
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
	public List<PrinterEntity> getAlarm() {
		return alarm;
	}
	public void setAlarm(List<PrinterEntity> alarm) {
		this.alarm = alarm;
	}
	
	
}
