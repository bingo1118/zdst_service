package com.cloudfire.entity;

import org.apache.commons.lang3.StringUtils;

import com.cloudfire.until.GetTime;

public class MqttApiEntity {
	private String mac;//�豸MAC	2
	private String deviceId;//�豸���� 3
	private String serviceType;//��������(smoke,gas,electric,water,temphumi) 1
	private String batteryPower;//�����	6
	private String time;//����ʱ�� 5
	private String voltageA = "";//A���ѹ(2)	7
	private String voltageB = "";//B���ѹ(2)	8
	private String voltageC = "";//C���ѹ(2)	9
	private String electricityA = "";//A�����(2)	10
	private String electricityB = "";//B�����(2)	11
	private String electricityC = "";//C�����(2)	12
	private String surplusElectri = "";//©����(2)	13
	private String alarmType = "";		//(0Ϊ����)	4
	private String temperature="";		//�¶�
	private String humidity = "";		//ʪ��
	private String waterLevel = "";		//ˮλ
	private String waterPressure= "";	//ˮѹ
	private String gasMmol = "";		//ȼ��ֵ

	public String getGasMmol() {
		return gasMmol;
	}
	public void setGasMmol(String gasMmol) {
		this.gasMmol = gasMmol;
	}
	public String getTime() {
		if(StringUtils.isBlank(this.time)){
			this.time = GetTime.ConvertTimeByLong();
		}
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getWaterLevel() {
		return waterLevel;
	}
	public void setWaterLevel(String waterLevel) {
		this.waterLevel = waterLevel;
	}
	public String getWaterPressure() {
		return waterPressure;
	}
	public void setWaterPressure(String waterPressure) {
		this.waterPressure = waterPressure;
	}
	public String getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getBatteryPower() {
		return batteryPower;
	}
	public void setBatteryPower(String batteryPower) {
		this.batteryPower = batteryPower;
	}
	public String getVoltageA() {
		return voltageA;
	}
	public void setVoltageA(String voltageA) {
		this.voltageA = voltageA;
	}
	public String getVoltageB() {
		return voltageB;
	}
	public void setVoltageB(String voltageB) {
		this.voltageB = voltageB;
	}
	public String getVoltageC() {
		return voltageC;
	}
	public void setVoltageC(String voltageC) {
		this.voltageC = voltageC;
	}
	public String getElectricityA() {
		return electricityA;
	}
	public void setElectricityA(String electricityA) {
		this.electricityA = electricityA;
	}
	public String getElectricityB() {
		return electricityB;
	}
	public void setElectricityB(String electricityB) {
		this.electricityB = electricityB;
	}
	public String getElectricityC() {
		return electricityC;
	}
	public void setElectricityC(String electricityC) {
		this.electricityC = electricityC;
	}
	public String getSurplusElectri() {
		return surplusElectri;
	}
	public void setSurplusElectri(String surplusElectri) {
		this.surplusElectri = surplusElectri;
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
}
