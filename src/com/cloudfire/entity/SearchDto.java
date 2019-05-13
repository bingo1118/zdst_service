package com.cloudfire.entity;

public class SearchDto {
	private String companyName;
	private String floor1;
	private String floor2;
	private String macStatus;
	private String fire1;
	private String fire2;
	private int deviceType;

	
	public int getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getFloor1() {
		return floor1;
	}
	public void setFloor1(String floor1) {
		this.floor1 = floor1;
	}
	public String getFloor2() {
		return floor2;
	}
	public void setFloor2(String floor2) {
		this.floor2 = floor2;
	}
	public String getMacStatus() {
		return macStatus;
	}
	public void setMacStatus(String macStatus) {
		this.macStatus = macStatus;
	}
	public String getFire1() {
		return fire1;
	}
	public void setFire1(String fire1) {
		this.fire1 = fire1;
	}
	public String getFire2() {
		return fire2;
	}
	public void setFire2(String fire2) {
		this.fire2 = fire2;
	}
	
	

}
