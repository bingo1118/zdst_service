package com.cloudfire.entity;

/**
 * @author lzo
 *	���ڷ�װͳ�Ʒ���������
 */
public class CountextAnalyze {
	
	private int errorAlarmNum;//��
	private int alarmState;//��
	private int normal ; //����
	private int lossup;//����
	private int faultNub;//����
	private String devName;//�豸����
	
	
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
