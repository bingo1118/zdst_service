package com.cloudfire.entity;

import java.io.Serializable;

public class WaterAlarmThresholdEntity implements Serializable{

	private String error="";
    private int errorCode;
    private String value207;
    private String value208;
    private String waveValue;
    private String ackTimes;
    
    
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public String getValue208() {
		return value208;
	}
	public void setValue208(String value208) {
		this.value208 = value208;
	}
	public String getValue207() {
		return value207;
	}
	public void setValue207(String value207) {
		this.value207 = value207;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getAckTimes() {
		return ackTimes;
	}
	public void setAckTimes(String ackTimes) {
		this.ackTimes = ackTimes;
	}
	public String getWaveValue() {
		return waveValue;
	}
	public void setWaveValue(String waveValue) {
		this.waveValue = waveValue;
	}
}
