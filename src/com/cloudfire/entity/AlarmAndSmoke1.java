package com.cloudfire.entity;

/** 
 * 
 * @author cheng
 *
 */
public class AlarmAndSmoke1 {
	private String dealTime="";   //������ʱ��
	private String dealUserId="";   //�����û���id
	private String dealUserName=""; //�����û�������
	private String alarmTime="";   //�豸����ʱ��
	private String alarmType="";  //�豸��������	
	private String deviceType="";  
	private String alarmAddress=""; //�豸�����ĵص�
	public String getDealTime() {
		return dealTime;
	}
	public void setDealTime(String dealTime) {
		this.dealTime = dealTime;
	}
	public String getDealUserId() {
		return dealUserId;
	}
	public void setDealUserId(String dealUserId) {
		this.dealUserId = dealUserId;
	}
	public String getDealUserName() {
		return dealUserName;
	}
	public void setDealUserName(String dealUserName) {
		this.dealUserName = dealUserName;
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
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public String getAlarmAddress() {
		return alarmAddress;
	}
	public void setAlarmAddress(String alarmAddress) {
		this.alarmAddress = alarmAddress;
	}
	
	
	
}
