package com.cloudfire.entity;

/*
 * ˮϵͳͳ�Ʒ���ʵ��
 */
public class WaterBean {
	private  String waterMac;  //�豸id
	private String named;       //�豸����
	private String address;    //�豸��ַ
//	private int deviceType;    //�豸���ͣ�����ȷ����λ
	private String unit;
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	private String value;     //�ϴ���ֵ
	private String time;          //�ϴ�ʱ��  
	public String getWaterMac() {
		return waterMac;
	}
	public void setWaterMac(String waterMac) {
		this.waterMac = waterMac;
	}
	public String getNamed() {
		return named;
	}
	public void setNamed(String named) {
		this.named = named;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
//	public int getDeviceType() {
//		return deviceType;
//	}
//	public void setDeviceType(int deviceType) {
//		this.deviceType = deviceType;
//	}
	public String  getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
}
