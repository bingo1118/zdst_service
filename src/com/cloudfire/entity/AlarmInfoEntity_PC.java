package com.cloudfire.entity;

import java.io.Serializable;

import com.cloudfire.until.BaseQuery;

public class AlarmInfoEntity_PC extends BaseQuery {
	
	private String devMac="";	//�̸е�mac��ַ
	private String devName="";	//�豸�����ͣ���1.����̽����2.��ȼ����̽��������   5.��������̽���� ���������ֱ�������7.���ⱨ���������ⱨ������8.�ֶ����������ֶ���������9.�����豸
	private String alarmTime=""; //�豸������ʱ��
	private String alarmType=""; //�豸����������
	private String alarmAddress="";//���������ĵط�
	private String principal1="";//������
	private String principalPhone="";//�����˵绰
	private String areaName="";//��������
	
	private String dealTime=""; //����ʱ��
	private String repeaterMac=""; //�����м���
	private String ifDealAlarm=""; //�Ƿ��Ѵ���
	private String dealPeople=""; //������
	
	private String mac="";
	private String deviceType;
	private String begintime="";
	private String endtime="";
	
	
	
	
	
	
	
	
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
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getDealPeople() {
		return dealPeople;
	}
	public void setDealPeople(String dealPeople) {
		this.dealPeople = dealPeople;
	}
	public String getIfDealAlarm() {
		return ifDealAlarm;
	}
	public void setIfDealAlarm(String ifDealAlarm) {
		this.ifDealAlarm = ifDealAlarm;
	}
	public String getDealTime() {
		return dealTime;
	}
	public void setDealTime(String dealTime) {
		this.dealTime = dealTime;
	}
	public String getRepeaterMac() {
		return repeaterMac;
	}
	public void setRepeaterMac(String repeaterMac) {
		this.repeaterMac = repeaterMac;
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



	public String getPrincipalPhone() {
		return principalPhone;
	}



	public void setPrincipalPhone(String principalPhone) {
		this.principalPhone = principalPhone;
	}



	public String getAreaName() {
		return areaName;
	}



	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}




	@Override
	public String toString() {
		return "AlarmInfoEntity_PC [devMac=" + devMac + ", devName=" + devName
				+ ", alarmTime=" + alarmTime + ", alarmType=" + alarmType
				+ ", alarmAddress=" + alarmAddress + ", principal1="
				+ principal1 + ", principalPhone=" + principalPhone
				+ ", areaName=" + areaName + "]";
	}
	
	
	
	
}
