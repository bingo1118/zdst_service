package com.cloudfire.entity;

/**
 * @author lzo
 *	用于封装统计分析的数据
 */
public class CountextAnalyze {
	
	private int errorAlarmNum;//误报
	private int alarmState;//火警
	private int normal ; //正常
	private int lossup;//掉线
	private int faultNub;//故障
	private String devName;//设备名称
	
	
	public String getDevName() {
		return devName;
	}
	public void setDevName(String devName) {
		this.devName = devName;
	}
	public int getErrorAlarmNum() {
		return errorAlarmNum;
	}
	public void setErrorAlarmNum(int errorAlarmNum) {
		this.errorAlarmNum = errorAlarmNum;
	}
	public int getAlarmState() {
		return alarmState;
	}
	public void setAlarmState(int alarmState) {
		this.alarmState = alarmState;
	}
	public int getNormal() {
		return normal;
	}
	public void setNormal(int normal) {
		this.normal = normal;
	}
	public int getLossup() {
		return lossup;
	}
	public void setLossup(int lossup) {
		this.lossup = lossup;
	}
	public int getFaultNub() {
		return faultNub;
	}
	public void setFaultNub(int faultNub) {
		this.faultNub = faultNub;
	}
	
	
}
