package com.cloudfire.entity;

import org.apache.commons.lang.StringUtils;

import com.cloudfire.until.GetTime;

//3、电气对接模拟封装数据
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
		case "6"://过压
			this.alarmType = "43";
			break;
		case "43"://：过压	（金特莱、三相电表、北秦BQ200-2.0、北秦BQ200-1.0、北秦BQ100-2.0、北秦BQ100-1.0、NB南京平台三相电气、U特电气、NB南京贵州电气）
		case "44"://：欠压 （金特莱、三相电表、北秦BQ200、北秦BQ200-1.0、北秦BQ100-2.0、北秦BQ100-1.0、NB南京平台三相电气、U特电气、NB南京贵州电气）
		case "45"://：过载	（金特莱、成佳、三相电表、北秦BQ200-2.0、北秦BQ200-1.0、北秦BQ100-2.0、北秦BQ100-1.0、NB三相电气 4568端口、NB南京平台三相电气、U特电气、NB南京贵州电气）
		case "46"://：漏电	（金特莱、三相电表、北秦BQ200-2.0、北秦BQ200-1.0、北秦BQ100-2.0、北秦BQ100-1.0、NB三相电气 4568端口、NB南京平台三相电气、U特电气、NB南京贵州电气）
		case "47"://：温度高	（金特莱、北秦BQ200-2.0、北秦BQ200-1.0、北秦BQ100-2.0、北秦BQ100-1.0、NB南京平台三相电气、U特电气）
		case "49"://：电气短路故障	（金特莱、成佳、三相电表、NB三相电气 4568端口、NB南京平台三相电气）
		case "52"://：电气断路	（金特莱、三相电表）
		case "74"://：传感器开路故障	（NB南京平台三相电气、U特电气）
		case "75"://：互感器故障（NB三相电气 4568端口、NB南京平台三相电气）
		case "136"://：故障		（贵州电气、NB南京平台三相电气、NB南京贵州电气）
		case "143"://：过压报警	（贵州电气、NB三相电气 4568端口、NB南京平台三相电气）
		case "144"://：欠压报警	（贵州电气、NB三相电气 4568端口、NB南京平台三相电气）
		case "145"://：过流报警	（贵州电气）
		case "146"://：漏电流	（贵州电气）
		case "147"://：温度报警	（贵州电气）
		case "151"://：缺零	（NB三相电气 4568端口、NB南京平台三相电气）
		case "153"://：缺相	（NB三相电气 4568端口、NB南京平台三相电气、U特电气）
		case "154"://：接地	（NB三相电气 4568端口、NB南京平台三相电气）
		case "155"://：停电	（NB三相电气 4568端口、NB南京平台三相电气）
		case "162"://：错相	（NB南京平台三相电气、U特电气）
			this.alarmType = alarmType;
		break;
		case "36"://：故障	（北秦BQ200-2.0、北秦BQ200-1.0、北秦BQ100-2.0、NB南京平台三相电气）
		case "48"://：合闸失败	（NB三相电气 4568端口、NB南京平台三相电气）
		case "148"://：合闸报警	（贵州电气）
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
