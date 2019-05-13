package com.cloudfire.entity;

public class EnvironmentEntity {
	
	private String arimac;
	private String co2="";
	private String temperature="";
	private String humidity="";
	private String pm25="";
	private String methanal="";
	private int priority;
	private int priority1;
	private int priority2;
	private byte environmentQuality;
	private String dataTimes;
	
	
	private String Error="";
	private int ErrorCode;
	
	public void setDataTimes(String dataTimes) {
		this.dataTimes = dataTimes;
	}
	
	public String getDataTimes() {
		return dataTimes;
	}
	
	
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public int getPriority() {
		return priority;
	}
	
	public void setPriority2(int priority2) {
		this.priority2 = priority2;
	}
	
	public int getPriority2() {
		return priority2;
	}
	
	public String getError() {
		return Error;
	}
	public void setError(String error) {
		this.Error = error;
	}
	public int getErrorCode() {
		return ErrorCode;
	}
	public void setErrorCode(int errorCode) {
		this.ErrorCode = errorCode;
	}
	public int getPriority1() {
		return priority1;
	}
	public void setPriority1(int priority) {
		this.priority1 = priority;
	}
	
	public String getArimac() {
		return arimac;
	}
	public void setArimac(String arimac) {
		this.arimac = arimac;
	}
	public String getCo2() {
		return co2;
	}
	public void setCo2(String co2) {
		this.co2 = co2;
	}
	public String getTemperature() {
		return temperature;
	}
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}
	public String getHumidity() {
		return humidity;
	}
	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}
	public String getPm25() {
		return pm25;
	}
	public void setPm25(String pm25) {
		this.pm25 = pm25;
	}
	public String getMethanal() {
		return methanal;
	}
	public void setMethanal(String methanal) {
		this.methanal = methanal;
	}
	public byte getEnvironmentQuality() {
		return environmentQuality;
	}
	public void setEnvironmentQuality(byte environmentQuality) {
		this.environmentQuality = environmentQuality;
	}
	
	
	
}
