package com.cloudfire.entity;

import java.util.List;

public class EnvironmentHistoryEntity {
	private int errorCode;
	private String error="";
	private List<EnvironmentHistoryMsgEntity> co2;
	private List<EnvironmentHistoryMsgEntity> pm25;
	private List<EnvironmentHistoryMsgEntity> temperature;
	private List<EnvironmentHistoryMsgEntity>humidity;
	private List<EnvironmentHistoryMsgEntity>mathanal;
	
	public List<EnvironmentHistoryMsgEntity> getCo2() {
		return co2;
	}
	public void setCo2(List<EnvironmentHistoryMsgEntity> co2) {
		this.co2 = co2;
	}
	public List<EnvironmentHistoryMsgEntity> getPm25() {
		return pm25;
	}
	public void setPm25(List<EnvironmentHistoryMsgEntity> pm25) {
		this.pm25 = pm25;
	}
	
	public List<EnvironmentHistoryMsgEntity> getHumidity() {
		return humidity;
	}
	public void setHumidity(List<EnvironmentHistoryMsgEntity> humidity) {
		this.humidity = humidity;
	}
	public List<EnvironmentHistoryMsgEntity> getMathanal() {
		return mathanal;
	}
	public void setMathanal(List<EnvironmentHistoryMsgEntity> mathanal) {
		this.mathanal = mathanal;
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
	public List<EnvironmentHistoryMsgEntity> getTemperature() {
		return temperature;
	}
	public void setTemperature(List<EnvironmentHistoryMsgEntity> temperature) {
		this.temperature = temperature;
	}
	
}
