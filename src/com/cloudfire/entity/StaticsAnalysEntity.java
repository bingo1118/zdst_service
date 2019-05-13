package com.cloudfire.entity;

import java.util.List;

public class StaticsAnalysEntity {
	private String mac;				//电气设备MAC
	private String netState;		//在线状态
	private int sum;				//总共设备数量
	private int netStateOnLine;		//在线数量
	private int netStateIsLoss;		//离线数量
	private int ifAlarm;			//报警数量
	private int alarmSum;			//报警总数
	private int overvoltage;		//过压报警 43
	private int undervoltage;		//欠压报警 44
	private int overcurrent;		//过流报警 45
	private int leakage;			//漏电流报警 46
	private int temperature;		//温度报警数量 47
	private int brokedown;             //故障36
	private int close;              //合闸48

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
