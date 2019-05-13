package com.cloudfire.entity;

/**
 * 诚佳电气实体类
 * @author hr
 *
 */
public class ChJEntity {  
	private int type;// 心跳或者报警 1，2
	private int threshold; //设备规格  //0x0A 10A; 0x14 20A;0x20 30A 
	private int alarmType;//报警类型 //0xCB 心跳；0xC2  过流报警；0xC3  短路报警；0xC4  过热报警；
	private String data7="";//电流
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getThreshold() {
		return threshold;
	}
	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}
	public int getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(int alarmType) {
		this.alarmType = alarmType;
	}
	public String getData7() {
		return data7;
	}
	public void setData7(String data7) {
		this.data7 = data7;
	}

	
}
