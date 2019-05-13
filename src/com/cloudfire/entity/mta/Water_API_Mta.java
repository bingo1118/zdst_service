package com.cloudfire.entity.mta;

/**
 * @author zhong
 *	2018-12-28
 */
public class Water_API_Mta {
	private String value=""; //水压水位值
	private String waterTime;//透传时间
	private String waterMac;//水压设备MAC
	private int deviceType; //设备类型
	private String alarmType;//报警类型
	
	public String getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(String alarmType) {
		if("219".equals(alarmType)){
			alarmType = "0";
		}
		this.alarmType = alarmType;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getWaterTime() {
		return waterTime;
	}
	public void setWaterTime(String waterTime) {
		this.waterTime = waterTime;
	}
	public String getWaterMac() {
		return waterMac;
	}
	public void setWaterMac(String waterMac) {
		this.waterMac = waterMac;
	}
	public int getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}
	
}
