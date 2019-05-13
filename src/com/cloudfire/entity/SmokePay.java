package com.cloudfire.entity;

public class SmokePay {
	
	private String userId;
	
	private String mac;
	
	private String stopTime;
	
	private int isTxt;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getStopTime() {
		return stopTime;
	}

	public void setStopTime(String stopTime) {
		this.stopTime = stopTime;
	}

	public int getIsTxt() {
		return isTxt;
	}

	public void setIsTxt(int isTxt) {
		this.isTxt = isTxt;
	}
	
}
