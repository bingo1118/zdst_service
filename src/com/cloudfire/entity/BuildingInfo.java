package com.cloudfire.entity;

import java.io.Serializable;

public class BuildingInfo implements Serializable{
	private static final long serialVersionUID = -3016366943270695267L;
	private String buildingId;
	private String buildingName;
	private String companyId;
	private String address;
	private String buildingType;
	private String lng;
	private String lat;
	private int deviceNum;
	private int isOnline;
	private int rechargeNum;
	private String regionCode;
	public String getRegionCode() {
		return regionCode;
	}
	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}
	public int getRechargeNum() {
		return rechargeNum;
	}
	public void setRechargeNum(int rechargeNum) {
		this.rechargeNum = rechargeNum;
	}
	public String getBuildingId() {
		return buildingId;
	}
	public void setBuildingId(String buildingId) {
		this.buildingId = buildingId;
	}
	public String getBuildingName() {
		return buildingName;
	}
	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getBuildingType() {
		return buildingType;
	}
	public void setBuildingType(String buildingType) {
		this.buildingType = buildingType;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public int getDeviceNum() {
		return deviceNum;
	}
	public void setDeviceNum(int deviceNum) {
		this.deviceNum = deviceNum;
	}
	public int getIsOnline() {
		return isOnline;
	}
	public void setIsOnline(int isOnline) {
		this.isOnline = isOnline;
	}
}
