package com.cloudfire.entity;

import org.apache.commons.lang.StringUtils;

import com.cloudfire.until.GetTime;

//3�������Խ�ģ���װ����
public class MqttElectricEntity {
	private String serviceType = "electric";
	private String deviceId = "77";
	private String mac = "";
	private String alarmType = "0";
	private String voltageA = "";
	private String voltageB = "";
	private String voltageC = "";
	private String electricityA = "";
	private String electricityB = "";
	private String electricityC = "";
	private String surplusElectri = "";
	private String temperatureA = "";
	private String temperatureB = "";
	private String temperatureC = "";
	private String temperatureN = "";
	private String time = "";
	
	public String getTemperatureA() {
		return temperatureA;
	}
	public void setTemperatureA(String temperatureA) {
		this.temperatureA = temperatureA;
	}
	public String getTemperatureB() {
		return temperatureB;
	}
	public void setTemperatureB(String temperatureB) {
		this.temperatureB = temperatureB;
	}
	public String getTemperatureC() {
		return temperatureC;
	}
	public void setTemperatureC(String temperatureC) {
		this.temperatureC = temperatureC;
	}
	public String getTemperatureN() {
		return temperatureN;
	}
	public void setTemperatureN(String temperatureN) {
		this.temperatureN = temperatureN;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(String alarmType) {
		switch(alarmType){
		case "6"://��ѹ
			this.alarmType = "43";
			break;
		case "43"://����ѹ	��������������������BQ200-2.0������BQ200-1.0������BQ100-2.0������BQ100-1.0��NB�Ͼ�ƽ̨���������U�ص�����NB�Ͼ����ݵ�����
		case "44"://��Ƿѹ ��������������������BQ200������BQ200-1.0������BQ100-2.0������BQ100-1.0��NB�Ͼ�ƽ̨���������U�ص�����NB�Ͼ����ݵ�����
		case "45"://������	�����������ɼѡ�����������BQ200-2.0������BQ200-1.0������BQ100-2.0������BQ100-1.0��NB������� 4568�˿ڡ�NB�Ͼ�ƽ̨���������U�ص�����NB�Ͼ����ݵ�����
		case "46"://��©��	��������������������BQ200-2.0������BQ200-1.0������BQ100-2.0������BQ100-1.0��NB������� 4568�˿ڡ�NB�Ͼ�ƽ̨���������U�ص�����NB�Ͼ����ݵ�����
		case "47"://���¶ȸ�	��������������BQ200-2.0������BQ200-1.0������BQ100-2.0������BQ100-1.0��NB�Ͼ�ƽ̨���������U�ص�����
		case "49"://��������·����	�����������ɼѡ�������NB������� 4568�˿ڡ�NB�Ͼ�ƽ̨���������
		case "52"://��������·	����������������
		case "74"://����������·����	��NB�Ͼ�ƽ̨���������U�ص�����
		case "75"://�����������ϣ�NB������� 4568�˿ڡ�NB�Ͼ�ƽ̨���������
		case "136"://������		�����ݵ�����NB�Ͼ�ƽ̨���������NB�Ͼ����ݵ�����
		case "143"://����ѹ����	�����ݵ�����NB������� 4568�˿ڡ�NB�Ͼ�ƽ̨���������
		case "144"://��Ƿѹ����	�����ݵ�����NB������� 4568�˿ڡ�NB�Ͼ�ƽ̨���������
		case "145"://����������	�����ݵ�����
		case "146"://��©����	�����ݵ�����
		case "147"://���¶ȱ���	�����ݵ�����
		case "151"://��ȱ��	��NB������� 4568�˿ڡ�NB�Ͼ�ƽ̨���������
		case "153"://��ȱ��	��NB������� 4568�˿ڡ�NB�Ͼ�ƽ̨���������U�ص�����
		case "154"://���ӵ�	��NB������� 4568�˿ڡ�NB�Ͼ�ƽ̨���������
		case "155"://��ͣ��	��NB������� 4568�˿ڡ�NB�Ͼ�ƽ̨���������
		case "162"://������	��NB�Ͼ�ƽ̨���������U�ص�����
			this.alarmType = alarmType;
		break;
		case "36"://������	������BQ200-2.0������BQ200-1.0������BQ100-2.0��NB�Ͼ�ƽ̨���������
		case "48"://����բʧ��	��NB������� 4568�˿ڡ�NB�Ͼ�ƽ̨���������
		case "148"://����բ����	�����ݵ�����
			this.alarmType = "0";
			break;
		default:
			this.alarmType = "0";
			break;
		}
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
	public String getTime() {
		if(StringUtils.isBlank(time)){
			time = GetTime.ConvertTimeByLong();
		}
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
}
