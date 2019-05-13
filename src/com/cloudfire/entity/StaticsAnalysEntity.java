package com.cloudfire.entity;

import java.util.List;

public class StaticsAnalysEntity {
	private String mac;				//�����豸MAC
	private String netState;		//����״̬
	private int sum;				//�ܹ��豸����
	private int netStateOnLine;		//��������
	private int netStateIsLoss;		//��������
	private int ifAlarm;			//��������
	private int alarmSum;			//��������
	private int overvoltage;		//��ѹ���� 43
	private int undervoltage;		//Ƿѹ���� 44
	private int overcurrent;		//�������� 45
	private int leakage;			//©�������� 46
	private int temperature;		//�¶ȱ������� 47
	private int brokedown;             //����36
	private int close;              //��բ48

	private List<SmokeElectricInfoEntity> seeList;
	
	public int getBrokedown() {
		return brokedown;
	}
	public void setBrokedown(int brokedown) {
		this.brokedown = brokedown;
	}
	public int getClose() {
		return close;
	}
	public void setClose(int close) {
		this.close = close;
	}
	public List<SmokeElectricInfoEntity> getSeeList() {
		return seeList;
	}
	public void setSeeList(List<SmokeElectricInfoEntity> seeList) {
		this.seeList = seeList;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getNetState() {
		return netState;
	}
	public void setNetState(String netState) {
		this.netState = netState;
	}
	public int getSum() {
		return sum;
	}
	public void setSum(int sum) {
		this.sum = sum;
	}
	public int getNetStateOnLine() {
		return netStateOnLine;
	}
	public void setNetStateOnLine(int netStateOnLine) {
		this.netStateOnLine = netStateOnLine;
	}
	public int getNetStateIsLoss() {
		return netStateIsLoss;
	}
	public void setNetStateIsLoss(int netStateIsLoss) {
		this.netStateIsLoss = netStateIsLoss;
	}
	public int getIfAlarm() {
		return ifAlarm;
	}
	public void setIfAlarm(int ifAlarm) {
		this.ifAlarm = ifAlarm;
	}
	public int getAlarmSum() {
		return alarmSum;
	}
	public void setAlarmSum(int alarmSum) {
		this.alarmSum = alarmSum;
	}
	public int getOvervoltage() {
		return overvoltage;
	}
	public void setOvervoltage(int overvoltage) {
		this.overvoltage = overvoltage;
	}
	public int getUndervoltage() {
		return undervoltage;
	}
	public void setUndervoltage(int undervoltage) {
		this.undervoltage = undervoltage;
	}
	public int getOvercurrent() {
		return overcurrent;
	}
	public void setOvercurrent(int overcurrent) {
		this.overcurrent = overcurrent;
	}
	public int getLeakage() {
		return leakage;
	}
	public void setLeakage(int leakage) {
		this.leakage = leakage;
	}
	public int getTemperature() {
		return temperature;
	}
	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}
}
