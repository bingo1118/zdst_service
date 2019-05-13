package com.cloudfire.entity;

public class SmokeElectricInfoEntity {
	private String smokeMac;		//设备MAC
	private String alarmType;		//报警类型
	private String alarmTime;		//报警时间
	private String named;
	private int alarmFamily;		//报警具体类型 漏电、欠压。。。
	private String alarmTruth;		//报警详情
	private int ifAlarm;			//是否报警状态
	private int alarmSum;			//按alarmFamily来统计次数。
	private int netState;			//在线状态
	private int overvoltage;		//过压报警 43
	private int undervoltage;		//欠压报警 44
	private int overcurrent;		//过流报警 45
	private int leakage;			//漏电流报警 46
	private int temperature;		//温度报警数量 47
	private int brokedown;             //故障36
	private int close;              //合闸48
	
	
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
	public void setNamed(String named) {
		this.named = named;
	}
	public String getNamed() {
		return named;
	}
	public void setIfAlarm(int ifAlarm) {
		this.ifAlarm = ifAlarm;
	}
	public int getIfAlarm() {
		return ifAlarm;
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
	public void setNetState(int netState) {
		this.netState = netState;
	}
	public int getNetState() {
		return netState;
	}
	public String getSmokeMac() {
		return smokeMac;
	}
	public void setSmokeMac(String smokeMac) {
		this.smokeMac = smokeMac;
	}
	public String getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}
	public String getAlarmTime() {
		return alarmTime;
	}
	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
	}
	public int getAlarmFamily() {
		return alarmFamily;
	}
	public void setAlarmFamily(int alarmFamily) {
		this.alarmFamily = alarmFamily;
	}
	public String getAlarmTruth() {
		return alarmTruth;
	}
	public void setAlarmTruth(String alarmTruth) {
		this.alarmTruth = alarmTruth;
	}
	public int getAlarmSum() {
		return alarmSum;
	}
	public void setAlarmSum(int alarmSum) {
		this.alarmSum = alarmSum;
	}
}
