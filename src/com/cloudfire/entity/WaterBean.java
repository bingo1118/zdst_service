package com.cloudfire.entity;

/*
 * 水系统统计分析实体
 */
public class WaterBean {
	private  String waterMac;  //设备id
	private String named;       //设备名称
	private String address;    //设备地址
//	private int deviceType;    //设备类型，用来确定单位
	private String unit;
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	private String value;     //上传数值
	private String time;          //上传时间  
	public String getWaterMac() {
		return waterMac;
	}
	public void setWaterMac(String waterMac) {
		this.waterMac = waterMac;
	}
	public String getNamed() {
		return named;
	}
	public void setNamed(String named) {
		this.named = named;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
//	public int getDeviceType() {
//		return deviceType;
//	}
//	public void setDeviceType(int deviceType) {
//		this.deviceType = deviceType;
//	}
	public String  getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
}
