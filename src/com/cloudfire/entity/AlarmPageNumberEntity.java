package com.cloudfire.entity;

public class AlarmPageNumberEntity {
	private String error="";
	private int errorCode;
	private int pageNumber;
	private int alarmNumber;
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
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
	public int getAlarmNumber() {
		return alarmNumber;
	}
	public void setAlarmNumber(int alarmNumber) {
		this.alarmNumber = alarmNumber;
	}
	
	
}
