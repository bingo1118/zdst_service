package com.cloudfire.entity;

public class BQ200Entity {
	private int type;//�������߱���,���� 1.2.3,5
	private int alarmType;//��������
	private String data6="";//��ѹ
	private String data7="";//����
	private String data8="";//©����
	private String alarmData="";//������ֵ
	private int electricDevType;//��������2
	
	private ElectricThresholdBean mElectricThresholdBean;
	
	public int getElectricDevType() {
		return electricDevType;
	}
	public void setElectricDevType(int electricDevType) {
		this.electricDevType = electricDevType;
	}
	public String getData8() {
		return data8;
	}
	public void setData8(String data8) {
		this.data8 = data8;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(int alarmType) {
		this.alarmType = alarmType;
	}
	public String getData6() {
		return data6;
	}
	public void setData6(String data6) {
		this.data6 = data6;
	}
	public String getData7() {
		return data7;
	}
	public void setData7(String data7) {
		this.data7 = data7;
	}
	public String getAlarmData() {
		return alarmData;
	}
	public void setAlarmData(String alarmData) {
		this.alarmData = alarmData;
	}
	public ElectricThresholdBean getmElectricThresholdBean() {
		return mElectricThresholdBean;
	}
	public void setmElectricThresholdBean(
			ElectricThresholdBean mElectricThresholdBean) {
		this.mElectricThresholdBean = mElectricThresholdBean;
	}
	
	
	
}
