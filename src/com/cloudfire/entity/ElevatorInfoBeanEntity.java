package com.cloudfire.entity;

public class ElevatorInfoBeanEntity {
	private int errorCode;
	private String error="";
	private ElevatorInfoBean elevator;
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
	public ElevatorInfoBean getElevator() {
		return elevator;
	}
	public void setElevator(ElevatorInfoBean elevator) {
		this.elevator = elevator;
	}
}
