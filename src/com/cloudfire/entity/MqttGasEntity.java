package com.cloudfire.entity;

import org.apache.commons.lang.StringUtils;

import com.cloudfire.until.GetTime;

//2、燃气对接模拟封装数据
public class MqttGasEntity {
	private String serviceType = "gas";
	private String deviceId = "72";
	private String mac = "";
	private String alarmType = "0";
	private String gasMmol = "0.0";
	private String time = "";
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}
	public String getGasMmol() {
		return gasMmol;
	}
	public void setGasMmol(String gasMmol) {
		this.gasMmol = gasMmol;
	}
	public String getTime() {
		if(StringUtils.isBlank(time)){
			time = GetTime.ConvertTimeByLong();
		}
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
}
