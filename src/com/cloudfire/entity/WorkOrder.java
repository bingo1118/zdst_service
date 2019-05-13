package com.cloudfire.entity;

public class WorkOrder {
	private String orderId;//工单编号
	private String mac; //报警设备号
	private String deviceType;//设备类型
	private String alarmType;//报警类型
	private String alarmTime;//报警时间
	private String alarmAdress;//报警地点
	private String dealVideo;//处理视频
	private int OrderStatus;//工单状态
	private String dealUserId;//接单人员ID
	
	
	public String getDealUserId() {
		return dealUserId;
	}
	public void setDealUserId(String dealUserId) {
		this.dealUserId = dealUserId;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public String getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}
	public String getAlarmTime() {
		return alarmTime;
	}
	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
	}
	public String getAlarmAdress() {
		return alarmAdress;
	}
	public void setAlarmAdress(String alarmAdress) {
		this.alarmAdress = alarmAdress;
	}
	public String getDealVideo() {
		return dealVideo;
	}
	public void setDealVideo(String dealVideo) {
		this.dealVideo = dealVideo;
	}
	public int getOrderStatus() {
		return OrderStatus;
	}
	public void setOrderStatus(int orderStatus) {
		OrderStatus = orderStatus;
	}
	
	
	
}
