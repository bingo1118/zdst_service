package com.cloudfire.entity;

/**
 * @author cheng
 *2017-3-23
 *����11:07:42
 */
public class Water {
	private int type;  //����
	private int alarmType; //��������,218��,209
	private String value=""; //ˮѹֵ
	private String waterTime;//͸��ʱ��
	private String waterMac;//ˮѹ�豸MAC
	
	private String start_Time;//����ǰ�����һ����¼�����ݰ�ʱ��
	private String end_Time;//��¼�����һ����¼��ʱ��
	private String heartTime; //�豸������ʱ��
	
	private float L_value = 0;	//��ˮλ����ֵ
	private float H_value = 0;	//��ˮλ����ֵ
	
	private String adjusted; //У��ֵ
	private int interval; //�ϱ�ʱ����
	private int interval2; //�ɼ�ʱ����
	private int wavevalue; //������ֵ
	private String repeaterMac; //����mac
	private int deviceType; //�豸����
	
	private String recentValue; //�������ֵ
	
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
