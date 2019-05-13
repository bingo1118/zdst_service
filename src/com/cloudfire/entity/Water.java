package com.cloudfire.entity;

/**
 * @author cheng
 *2017-3-23
 *上午11:07:42
 */
public class Water {
	private int type;  //类型
	private int alarmType; //报警类型,218高,209
	private String value=""; //水压值
	private String waterTime;//透传时间
	private String waterMac;//水压设备MAC
	
	private String start_Time;//缓存前的最后一包记录的数据包时间
	private String end_Time;//记录的最后一包记录的时间
	private String heartTime; //设备的心跳时间
	
	private float L_value = 0;	//低水位报警值
	private float H_value = 0;	//高水位报警值
	
	private String adjusted; //校正值
	private int interval; //上报时间间隔
	private int interval2; //采集时间间隔
	private int wavevalue; //波动阈值
	private String repeaterMac; //主机mac
	private int deviceType; //设备类型
	
	private String recentValue; //最近的数值
	
	public String getRecentValue() {
		return recentValue;
	}
	public void setRecentValue(String recentValue) {
		this.recentValue = recentValue;
	}
	public int getInterval2() {
		return interval2;
	}
	public void setInterval2(int interval2) {
		this.interval2 = interval2;
	}
	public String getHeartTime() {
		return heartTime;
	}
	public void setHeartTime(String heartTime) {
		this.heartTime = heartTime;
	}
	public int getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}
	public int getWavevalue() {
		return wavevalue;
	}
	public void setWavevalue(int wavevalue) {
		this.wavevalue = wavevalue;
	}
	public String getAdjusted() {
		return adjusted;
	}
	public void setAdjusted(String adjusted) {
		this.adjusted = adjusted;
	}
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}	
	public float getL_value() {
		return L_value;
	}
	public void setL_value(float l_value) {
		L_value = l_value;
	}
	public float getH_value() {
		return H_value;
	}
	public void setH_value(float h_value) {
		H_value = h_value;
	}
	public String getRepeaterMac() {
		return repeaterMac;
	}
	public void setRepeaterMac(String repeaterMac) {
		this.repeaterMac = repeaterMac;
	}
	public String getStart_Time() {
		return start_Time;
	}
	public void setStart_Time(String start_Time) {
		this.start_Time = start_Time;
	}
	public String getEnd_Time() {
		return end_Time;
	}
	public void setEnd_Time(String end_Time) {
		this.end_Time = end_Time;
	}
	public String getWaterMac() {
		return waterMac;
	}
	public void setWaterMac(String waterMac) {
		this.waterMac = waterMac;
	}
	public String getWaterTime() {
		return waterTime;
	}
	public void setWaterTime(String waterTime) {
		this.waterTime = waterTime;
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
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
	
}
