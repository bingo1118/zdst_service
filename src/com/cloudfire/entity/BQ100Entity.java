package com.cloudfire.entity;

import java.util.List;

public class BQ100Entity {
	private List<String> list6=null;//电压值
	private List<String> list7=null;//电流值
	private List<String> list9=null;//温度值
	private List<String> listAlarm = null;	//用于金特莱电气，统计报警列表数量
	private String data8="";//漏电流
	private String data6="";//电压值
	private String data7="";//电流值
	private int type;//2表示心跳2,1表示报警 ,3表示故障
	private int alarmType;//报警类型
	private String alarmData="";//报警阈值
	private ElectricThresholdBean mElectricThresholdBean;

	
	public List<String> getListAlarm() {
		return listAlarm;
	}
	public void setListAlarm(List<String> listAlarm) {
		this.listAlarm = listAlarm;
	}
	public String getData6() {
		return data6;
	}
	public void setData6(String data6) {
		this.data6 = data6;
	}
	public String getData7() {
		return data7;
	}
	public void setData7(String data7) {
		this.data7 = data7;
	}
	public List<String> getList6() {
		return list6;
	}
	public void setList6(List<String> list6) {
		this.list6 = list6;
	}
	public List<String> getList7() {
		return list7;
	}
	public void setList7(List<String> list7) {
		this.list7 = list7;
	}
	public List<String> getList9() {
		return list9;
	}
	public void setList9(List<String> list9) {
		this.list9 = list9;
	}
	public String getData8() {
		return data8;
	}
	public void setData8(String data8) {
		this.data8 = data8;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(int alarmType) {
		this.alarmType = alarmType;
	}
	public String getAlarmData() {
		return alarmData;
	}
	public void setAlarmData(String alarmData) {
		this.alarmData = alarmData;
	}
	public ElectricThresholdBean getmElectricThresholdBean() {
		return mElectricThresholdBean;
	}
	public void setmElectricThresholdBean(
			ElectricThresholdBean mElectricThresholdBean) {
		this.mElectricThresholdBean = mElectricThresholdBean;
	}
	
}
