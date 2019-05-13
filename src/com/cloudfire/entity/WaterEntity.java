package com.cloudfire.entity;

import java.util.List;

public class WaterEntity {
	private String waterMac;
	private String waterName;
	private String deviceType;
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	private int areaid;
	private List<Water> waterList;
	
	public int getAreaid() {
		return areaid;
	}
	public void setAreaid(int areaid) {
		this.areaid = areaid;
	}
	public String getWaterMac() {
		return waterMac;
	}
	public void setWaterMac(String waterMac) {
		this.waterMac = waterMac;
	}
	public String getWaterName() {
		return waterName;
	}
	public void setWaterName(String waterName) {
		this.waterName = waterName;
	}
	public List<Water> getWaterList() {
		return waterList;
	}
	public void setWaterList(List<Water> waterList) {
		this.waterList = waterList;
	}
}
