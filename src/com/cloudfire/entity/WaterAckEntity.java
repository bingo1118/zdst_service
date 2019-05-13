package com.cloudfire.entity;

public class WaterAckEntity {
	private String waterMac;	//水压设备
	private int deviceType = 0;	//水压78 温湿度79
	private int waveValue = 0;		//波动阈值，单位kp		水压：上报时间 		温湿度：上报时间
	private int ackTimes = 0;		//采集时间段 单位分钟		水压：采集时间		温湿度：采集时间
	private int threshold1 = 0;		//存储阈值1 水压：高水压阈值			温湿度：高温阈值
	private int threshold2 = 0;		//存储阈值2 水压：低水压阈值			温湿度：低温阈值
	private int threshold3 = 0;		//存储阈值3					温湿度：高湿阈值
	private int threshold4 = 0;		//存储阈值4					温湿度：低湿阈值
	private String error="";
    private int errorCode;
	
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
	public int getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}
	public int getThreshold1() {
		return threshold1;
	}
	public void setThreshold1(int threshold1) {
		this.threshold1 = threshold1;
	}
	public int getThreshold2() {
		return threshold2;
	}
	public void setThreshold2(int threshold2) {
		this.threshold2 = threshold2;
	}
	public int getThreshold3() {
		return threshold3;
	}
	public void setThreshold3(int threshold3) {
		this.threshold3 = threshold3;
	}
	public int getThreshold4() {
		return threshold4;
	}
	public void setThreshold4(int threshold4) {
		this.threshold4 = threshold4;
	}
	public String getWaterMac() {
		return waterMac;
	}
	public void setWaterMac(String waterMac) {
		this.waterMac = waterMac;
	}
	public int getWaveValue() {
		return waveValue;
	}
	public void setWaveValue(int waveValue) {
		this.waveValue = waveValue;
	}
	public int getAckTimes() {
		return ackTimes;
	}
	public void setAckTimes(int ackTimes) {
		this.ackTimes = ackTimes;
	}
}
