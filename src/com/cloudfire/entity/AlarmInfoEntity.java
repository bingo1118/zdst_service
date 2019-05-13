package com.cloudfire.entity;

import com.cloudfire.until.BaseQuery;

public class AlarmInfoEntity extends BaseQuery{
	private String devMac;							//对应有线系统的faultCode字段==faultCode
	private String devName;							//对应有线系统的faultInfo字段==faultCmd
	private String alarmTime;						//对应有线系统的faultTime字段==faultTime
	private String alarmType;						//对应有线系统的faultType字段==faultState
	private String alarmAddress;
	private String principal1;//负责人
	private String principalPhone1;//负责人电话
	private String principal2;//负责人
	private String principalPhone2;//负责人电话		
	private String areaName;//所属区域				//对应有线系统的repeaterMac字段==faultCode
	private String named;//店铺的名字					//对应有线系统的faultDevDesc字段==systemTab
	private String cameraChannel;	//web视频字段
	/** 设备的类型smoke表的deviceType */
	private int deviceType;
	private int type;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	/**
	 * @author lzo
	 * 添加经度纬度位置。方便查询地图位置。
	 */
	private String longitude;//经度
	private String latitude; //纬度
	
	
	
	public String getCameraChannel() {
		return cameraChannel;
	}
	public void setCameraChannel(String cameraChannel) {
		this.cameraChannel = cameraChannel;
	}
	public int getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
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
	public String getNamed() {
		return named;
	}
	public void setNamed(String named) {
		this.named = named;
	}
	public String getDevMac() {
		return devMac;
	}
	public void setDevMac(String devMac) {
		this.devMac = devMac;
	}
	public String getDevName() {
		return devName;
	}
	public void setDevName(String devName) {
		this.devName = devName;
	}
	public String getAlarmTime() {
		return alarmTime;
	}
	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
	}
	public String getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}
	public String getAlarmAddress() {
		return alarmAddress;
	}
	public void setAlarmAddress(String alarmAddress) {
		this.alarmAddress = alarmAddress;
	}
	public String getPrincipal1() {
		return principal1;
	}
	public void setPrincipal1(String principal1) {
		this.principal1 = principal1;
	}
	public String getPrincipalPhone1() {
		return principalPhone1;
	}
	public void setPrincipalPhone1(String principalPhone1) {
		this.principalPhone1 = principalPhone1;
	}
	public String getPrincipal2() {
		return principal2;
	}
	public void setPrincipal2(String principal2) {
		this.principal2 = principal2;
	}
	public String getPrincipalPhone2() {
		return principalPhone2;
	}
	public void setPrincipalPhone2(String principalPhone2) {
		this.principalPhone2 = principalPhone2;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	
	
	
}
