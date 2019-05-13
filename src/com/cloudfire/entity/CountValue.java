package com.cloudfire.entity;

public class CountValue {
	private int macNum = 0; // 终端总数&&设备正常运行数量 2
	private int netStaterNum = 0; // 正常状态数量&&设备故障数量4
	private int ifDealNum = 0; // 报警状态个数&&报警总数5
	private int noNetStater = 0; // 故障个数&&设备掉线数量 3
	private int alarmTypeNum = 0; // 火警状态数量 	&&设备报火警数量7
	private int alarmTruthNum = 0; // 误报数量	&&设备误报数量6
	private int otherNum = 0;	//&&其它数量 =报警总数-火警总数5-6
	
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
