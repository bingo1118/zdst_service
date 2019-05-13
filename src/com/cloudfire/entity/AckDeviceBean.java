package com.cloudfire.entity;

import java.util.List;
import java.util.Map;

public class AckDeviceBean {
	private String deviceMac;	//�����豸
	private String soundMac;	//��Ӧ�豸
	private String repeaterMac;	//�ն��豸
	private String deviceType;	//�豸�ͺ�
	
	private Map<String,List<AckDeviceBean>> ackMap;
	
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public Map<String, List<AckDeviceBean>> getAckMap() {
		return ackMap;
	}
	public void setAckMap(Map<String, List<AckDeviceBean>> ackMap) {
		this.ackMap = ackMap;
	}
	public String getRepeaterMac() {
		return repeaterMac;
	}
	public void setRepeaterMac(String repeaterMac) {
		this.repeaterMac = repeaterMac;
	}
	public String getDeviceMac() {
		return deviceMac;
	}
	public void setDeviceMac(String deviceMac) {
		this.deviceMac = deviceMac;
	}
	public String getSoundMac() {
		return soundMac;
	}
	public void setSoundMac(String soundMac) {
		this.soundMac = soundMac;
	}
}
