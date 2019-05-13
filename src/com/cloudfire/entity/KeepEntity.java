package com.cloudfire.entity;

public class KeepEntity {
	private String smokeName="";
	private String smokeMac="";
	private String addTime="";
	private String lineState="";
	private String heartTime="";
	private String repeaterState="";
	private String repeaterMac="";
	private String address="";
	
	/** 2017年6月19日 */
	private String areaName=""; 
	private String principal1;
	private String principal1Phone;
	private String principal2;
	private String principal2Phone;
	private String linkageDistance;
	
	//设备场所类型编号和名称
	private String placeTypeId;
	private String placeName;
	
	//经纬度
	private String longitude="";
	private String latitude="";
	
	public String getPlaceTypeId() {
		return placeTypeId;
	}
	public void setPlaceTypeId(String placeTypeId) {
		this.placeTypeId = placeTypeId;
	}
	public String getPlaceName() {
		return placeName;
	}
	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}
	public String getPrincipal2() {
		return principal2;
	}
	public void setPrincipal2(String principal2) {
		this.principal2 = principal2;
	}
	public String getPrincipal2Phone() {
		return principal2Phone;
	}
	public void setPrincipal2Phone(String principal2Phone) {
		this.principal2Phone = principal2Phone;
	}
	public String getLinkageDistance() {
		return linkageDistance;
	}
	public void setLinkageDistance(String linkageDistance) {
		this.linkageDistance = linkageDistance;
	}
	public String getPrincipal1() {
		return principal1;
	}
	public void setPrincipal1(String principal1) {
		this.principal1 = principal1;
	}
	public String getPrincipal1Phone() {
		return principal1Phone;
	}
	public void setPrincipal1Phone(String principal1Phone) {
		this.principal1Phone = principal1Phone;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public String getRepeaterMac() {
		return repeaterMac;
	}
	public void setRepeaterMac(String repeaterMac) {
		this.repeaterMac = repeaterMac;
	}
	public String getSmokeName() {
		return smokeName;
	}
	public void setSmokeName(String smokeName) {
		this.smokeName = smokeName;
	}
	public String getSmokeMac() {
		return smokeMac;
	}
	public void setSmokeMac(String smokeMac) {
		this.smokeMac = smokeMac;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public String getLineState() {
		return lineState;
	}
	public void setLineState(String lineState) {
		this.lineState = lineState;
	}
	public String getHeartTime() {
		return heartTime;
	}
	public void setHeartTime(String heartTime) {
		this.heartTime = heartTime;
	}
	public String getRepeaterState() {
		return repeaterState;
	}
	public void setRepeaterState(String repeaterState) {
		this.repeaterState = repeaterState;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

}
