package com.cloudfire.entity;

public class WorkOrder {
	private String orderId;//�������
	private String mac; //�����豸��
	private String deviceType;//�豸����
	private String alarmType;//��������
	private String alarmTime;//����ʱ��
	private String alarmAdress;//�����ص�
	private String dealVideo;//������Ƶ
	private int OrderStatus;//����״̬
	private String dealUserId;//�ӵ���ԱID
	
	
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
