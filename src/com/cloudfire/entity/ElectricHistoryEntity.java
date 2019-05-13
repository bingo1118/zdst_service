package com.cloudfire.entity;

public class ElectricHistoryEntity {
	private String mac;
	private String userId;
	private String userName;
	private int state;
	private String changetime;
	
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getChangetime() {
		return changetime;
	}
	public void setChangetime(String changetime) {
		this.changetime = changetime;
	}
}
