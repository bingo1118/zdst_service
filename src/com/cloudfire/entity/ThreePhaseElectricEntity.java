package com.cloudfire.entity;

public class ThreePhaseElectricEntity {
	private String imeiStr = "";//IMEI(15)
	private String imsiStr = "";//IMSI(15)
	private String rssiVal = "";//RSSI(2)
	private String voltageA = "";//A���ѹ(2)
	private String voltageB = "";//B���ѹ(2)
	private String voltageC = "";//C���ѹ(2)
	private String electricityA = "";//A�����(2)
	private String electricityB = "";//B�����(2)
	private String electricityC = "";//C�����(2)
	private String surplusElectri = "";//ʣ�����(2)
	private String currentMaximum = "";//ʣ����������(1)
	private String runAlarmState = "0";//�澯״̬	0-�޸澯��1-�и澯��
	private String runGateState;//բλ״̬		0-��բ�� 1-������  2-�غ�բ��3-������բ��(U�ص�����������)
	private String runCauseState = "0";//��բ���澯ԭ�� ����״̬��(1) 00000-�������У� 00010-ʣ�������00100-ȱ�㣬00101-���أ�00110-��·��00111-ȱ�࣬ 01000-Ƿѹ��    01001-��ѹ��01010-�ӵأ�01011-ͣ�磬01100-��ʱ���飬01101-Զ�̣�    01110-�������飬01111-������10010-�ֶ��� 10000-���������ϣ�10001-��բʧ�ܣ�10011-���ø��Ģ�
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
	private ElectricThresholdBean electricBean;//��ֵ
	
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
