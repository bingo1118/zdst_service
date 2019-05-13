package com.cloudfire.entity;

import com.cloudfire.until.BaseQuery;

public class DealOkAlarmEntity extends BaseQuery{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mac;
	private String named="";
	private String devMac;	//烟感的mac
	private String devName;	//烟感的名字 :根据devType决定
	private String dealTime;//处理报警的时间
	private String dealUserId;//处理用户的id
	private String dealUserName;//处理用户的名字
	private String alarmTime;  //报警的时间
	private String alarmType; //报警类型
	private String alarmAddress;//报警的地方:smoke.address
	//alarm.dealTime,alarm.dealUserId,alarm.dealPeople,alarm.alarmType,smoke.deviceType,smoke.address,alarm.dealDetail ,smoke.mac,count(alarm.alarmTime)
	private String dealPeople;
	private String dealDetail;
	private String alarmCount;
	
	private String deviceType;//设备的类型
	
	private String begintime;
	private String endtime;
	
	
			
	public String getNamed() {
		return named;
	}
	public void setNamed(String named) {
		this.named = named;
	}
	public String getAlarmTime() {
		return alarmTime;
	}
	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
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
	public String getBegintime() {
		return begintime;
	}
	public void setBegintime(String begintime) {
		this.begintime = begintime;
	}
	public String getEndtime() {
		return endtime;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
	
	public String getAlarmCount() {
		return alarmCount;
	}
	public void setAlarmCount(String alarmCount) {
		this.alarmCount = alarmCount;
	}
	public String getDealPeople() {
		return dealPeople;
	}
	public void setDealPeople(String dealPeople) {
		this.dealPeople = dealPeople;
	}
	public String getDealDetail() {
		return dealDetail;
	}
	public void setDealDetail(String dealDetail) {
		this.dealDetail = dealDetail;
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
	/*public String getAlarmTime() {
		return alarmTime;
	}
	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
	}*/
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
	
	
}
