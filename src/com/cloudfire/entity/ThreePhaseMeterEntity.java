package com.cloudfire.entity;

public class ThreePhaseMeterEntity {
	private int type;//�������߱���,���� 1.2.3,5
	private int alarmType;//��������
	private String data="";//@@����
	private String alarmData="";//������ֵ
	private int electricDevType;//��������2
	private int data_num;//�������ABC
	private int data_type;//@@��������
	private int electric_state;//@@�ֺ�բ״̬ 1 ��բ 2 ��բ
	private int ifalarm;//�Ƿ񱨾� 0�� 1��
	
	private String overV;
	private String downV;
	private String overI;
	private String lossI;
	
	
	
	private ElectricThresholdBean mElectricThresholdBean;
	
	public int getElectricDevType() {
		return electricDevType;
	}
	public void setElectricDevType(int electricDevType) {
		this.electricDevType = electricDevType;
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
	public int getData_num() {
		return data_num;
	}
	public void setData_num(int data_num) {
		this.data_num = data_num;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public int getData_type() {
		return data_type;
	}
	public void setData_type(int data_type) {
		this.data_type = data_type;
	}
	public int getElectric_state() {
		return electric_state;
	}
	public void setElectric_state(int electric_state) {
		this.electric_state = electric_state;
	}
	public int getIfalarm() {
		return ifalarm;
	}
	public void setIfalarm(int ifalarm) {
		this.ifalarm = ifalarm;
	}
	public String getOverV() {
		return overV;
	}
	public void setOverV(String overV) {
		this.overV = overV;
	}
	public String getDownV() {
		return downV;
	}
	public void setDownV(String downV) {
		this.downV = downV;
	}
	public String getOverI() {
		return overI;
	}
	public void setOverI(String overI) {
		this.overI = overI;
	}
	public String getLossI() {
		return lossI;
	}
	public void setLossI(String lossI) {
		this.lossI = lossI;
	}
}
