package com.cloudfire.entity;

public class CountValue {
	private int macNum = 0; // �ն�����&&�豸������������ 2
	private int netStaterNum = 0; // ����״̬����&&�豸��������4
	private int ifDealNum = 0; // ����״̬����&&��������5
	private int noNetStater = 0; // ���ϸ���&&�豸�������� 3
	private int alarmTypeNum = 0; // ��״̬���� 	&&�豸��������7
	private int alarmTruthNum = 0; // ������	&&�豸������6
	private int otherNum = 0;	//&&�������� =��������-������5-6
	
	public int getOtherNum() {
		return otherNum;
	}
	public void setOtherNum(int otherNum) {
		this.otherNum = otherNum;
	}
	public int getMacNum() {
		return macNum;
	}
	public void setMacNum(int macNum) {
		this.macNum = macNum;
	}
	public int getNetStaterNum() {
		return netStaterNum;
	}
	public void setNetStaterNum(int netStaterNum) {
		this.netStaterNum = netStaterNum;
	}
	public int getIfDealNum() {
		return ifDealNum;
	}
	public void setIfDealNum(int ifDealNum) {
		this.ifDealNum = ifDealNum;
	}
	public int getNoNetStater() {
		return noNetStater;
	}
	public void setNoNetStater(int noNetStater) {
		this.noNetStater = noNetStater;
	}
	public int getAlarmTypeNum() {
		return alarmTypeNum;
	}
	public void setAlarmTypeNum(int alarmTypeNum) {
		this.alarmTypeNum = alarmTypeNum;
	}
	public int getAlarmTruthNum() {
		return alarmTruthNum;
	}
	public void setAlarmTruthNum(int alarmTruthNum) {
		this.alarmTruthNum = alarmTruthNum;
	}
	
	
}
