package com.cloudfire.entity;

import java.io.Serializable;

public class Equipment implements Serializable{
	private static final long serialVersionUID = -2619335722798666017L;
	private String deviceId;
	private String deviceName;
	private String deviceType;  //1 2 3 4 5 99 远程监控(用传) 水 电  预警 充电桩 其他
	private String address; 
	private String lat;
	private String lng;
	private String companyId;
	private String buildingId;
	private String installDate;
	private String producerCode;
	private String equipmentStatus;  //（1在线、2禁用、3离线）
	private String deviceMiniType;
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public String getBuildingId() {
		return buildingId;
	}
	public void setBuildingId(String buildingId) {
		this.buildingId = buildingId;
	}
	public String getInstallDate() {
		return installDate;
	}
	public void setInstallDate(String installDate) {
		this.installDate = installDate;
	}
	public String getEquipmentStatus() {
		return equipmentStatus;
	}
	public void setEquipmentStatus(String equipmentStatus) {
		this.equipmentStatus = equipmentStatus;
	}
	public String getDeviceMiniType() {
		return deviceMiniType;
	}
	public void setDeviceMiniType(String deviceMiniType) {
		this.deviceMiniType = deviceMiniType;
	}
	public String getProducerCode() {
		return producerCode;
	}
	public void setProducerCode(String producerCode) {
		this.producerCode = producerCode;
	} 
	
}
