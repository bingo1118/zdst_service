package com.cloudfire.entity;

public class THDevice {
	
	private String humidity;	//ʪ��
	private String temperature;	//�¶�
	private int alarmType_T=0;	//�¶ȱ������� 
	private int alarmType_H=0;	//ʪ�ȱ������� 
	private String alarmValue_T;  //����ʱ���¶�ֵ
	private String alarmValue_H;	//����ʱʪ��ֵ
	private String devMac;		//�豸��MAC��ַ
	
	public String getDevMac() {
		return devMac;
	}
	public void setDevMac(String devMac) {
		this.devMac = devMac;
	}
	public String getTemperature() {
		return temperature;
	}
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}
	public String getHumidity() {
		return humidity;
	}
	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}
	public int getAlarmType_T() {
		return alarmType_T;
	}
	public void setAlarmType_T(int alarmType) {
		this.alarmType_T = alarmType;
	}
	public String getAlarmValue_T() {
		return alarmValue_T;
	}
	public void setAlarmValue_T(String alarmValue) {
		this.alarmValue_T = alarmValue;
	}
	public int getAlarmType_H() {
		return alarmType_H;
	}
	public void setAlarmType_H(int alarmType_H) {
		this.alarmType_H = alarmType_H;
	}
	public String getAlarmValue_H() {
		return alarmValue_H;
	}
	public void setAlarmValue_H(String alarmValue_H) {
		this.alarmValue_H = alarmValue_H;
	}

}
