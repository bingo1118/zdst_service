package com.cloudfire.entity;

public class ThreePhaseElectricEntity {
	private String imeiStr = "";//IMEI(15)
	private String imsiStr = "";//IMSI(15)
	private String rssiVal = "";//RSSI(2)
	private String voltageA = "";//A相电压(2)
	private String voltageB = "";//B相电压(2)
	private String voltageC = "";//C相电压(2)
	private String electricityA = "";//A相电流(2)
	private String electricityB = "";//B相电流(2)
	private String electricityC = "";//C相电流(2)
	private String surplusElectri = "";//剩余电流(2)
	private String currentMaximum = "";//剩余电流最大相(1)
	private String runAlarmState = "0";//告警状态	0-无告警，1-有告警；
	private String runGateState;//闸位状态		0-合闸； 1-保留；  2-重合闸；3-闭锁跳闸；(U特电气代表数据)
	private String runCauseState = "0";//跳闸、告警原因： 运行状态字(1) 00000-正常运行， 00010-剩余电流，00100-缺零，00101-过载，00110-短路，00111-缺相， 01000-欠压，    01001-过压，01010-接地，01011-停电，01100-定时试验，01101-远程，    01110-按键试验，01111-闭锁，10010-手动， 10000-互感器故障，10001-合闸失败，10011-设置更改②
	private String heartime = "";
	private String thresholdV = "";
	private String thresholdA = "";
	private String thresholdT = "";
	private String tempValueA="0";
	private String tempValueB="0";
	private String tempValueC="0";
	private String tempValueD="0";
	
	public String getTempValueA() {
		return tempValueA;
	}
	public void setTempValueA(String tempValueA) {
		this.tempValueA = tempValueA;
	}
	public String getTempValueB() {
		return tempValueB;
	}
	public void setTempValueB(String tempValueB) {
		this.tempValueB = tempValueB;
	}
	public String getTempValueC() {
		return tempValueC;
	}
	public void setTempValueC(String tempValueC) {
		this.tempValueC = tempValueC;
	}
	public String getTempValueD() {
		return tempValueD;
	}
	public void setTempValueD(String tempValueD) {
		this.tempValueD = tempValueD;
	}
	public String getThresholdV() {
		return thresholdV;
	}
	public void setThresholdV(String thresholdV) {
		this.thresholdV = thresholdV;
	}
	public String getThresholdA() {
		return thresholdA;
	}
	public void setThresholdA(String thresholdA) {
		this.thresholdA = thresholdA;
	}
	public String getThresholdT() {
		return thresholdT;
	}
	public void setThresholdT(String thresholdT) {
		this.thresholdT = thresholdT;
	}
	public String getHeartime() {
		return heartime;
	}
	public void setHeartime(String heartime) {
		this.heartime = heartime;
	}
	private ElectricThresholdBean electricBean;//阈值
	
	public ElectricThresholdBean getElectricBean() {
		return electricBean;
	}
	public void setElectricBean(ElectricThresholdBean electricBean) {
		this.electricBean = electricBean;
	}
	public String getImeiStr() {
		return imeiStr;
	}
	public void setImeiStr(String imeiStr) {
		this.imeiStr = imeiStr;
	}
	public String getImsiStr() {
		return imsiStr;
	}
	public void setImsiStr(String imsiStr) {
		this.imsiStr = imsiStr;
	}
	public String getRssiVal() {
		return rssiVal;
	}
	public void setRssiVal(String rssiVal) {
		this.rssiVal = rssiVal;
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
	public String getCurrentMaximum() {
		return currentMaximum;
	}
	public void setCurrentMaximum(String currentMaximum) {
		this.currentMaximum = currentMaximum;
	}
	public String getRunAlarmState() {
		return runAlarmState;
	}
	public void setRunAlarmState(String runAlarmState) {
		this.runAlarmState = runAlarmState;
	}
	public String getRunGateState() {
		return runGateState;
	}
	public void setRunGateState(String runGateState) {
		this.runGateState = runGateState;
	}
	public String getRunCauseState() {
		return runCauseState;
	}
	public void setRunCauseState(String runCauseState) {
		this.runCauseState = runCauseState;
	}
}
