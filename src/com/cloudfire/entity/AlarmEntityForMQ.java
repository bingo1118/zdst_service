package com.cloudfire.entity;

import java.io.Serializable;
import java.util.Map;

public class AlarmEntityForMQ implements Serializable{
	private static final long serialVersionUID = -3428202730285949147L;
	private String alarmEventId;
	private String deviceId;
	private String happenTime;
	private Map<String,String> alarmContent;
	private String alarmType;
	private String channelsCode;
	private String producerCode;
	public String getAlarmEventId() {
		return alarmEventId;
	}
	public void setAlarmEventId(String alarmEventId) {
		this.alarmEventId = alarmEventId;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getHappenTime() {
		return happenTime;
	}
	public void setHappenTime(String happenTime) {
		this.happenTime = happenTime;
	}
	public Map<String, String> getAlarmContent() {
		return alarmContent;
	}
	public void setAlarmContent(Map<String, String> alarmContent) {
		this.alarmContent = alarmContent;
	}
	public String getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}
	public String getChannelsCode() {
		return channelsCode;
	}
	public void setChannelsCode(String channelsCode) {
		this.channelsCode = channelsCode;
	}
	public String getProducerCode() {
		return producerCode;
	}
	public void setProducerCode(String producerCode) {
		this.producerCode = producerCode;
	}
}
