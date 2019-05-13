package com.cloudfire.entity;

import com.cloudfire.until.BaseQuery;

public class RepeaterBean extends BaseQuery {

	private static final long serialVersionUID = -2366525225266437335L;
	
	private String repeaterMac;
	private String repeaterTime; //主机状态改变时间
	private int netstate = 0;	//0离线，1在线
	private String netStates;
	private int hoststate = 0;	//0未开通，1，主电，2，备电，3，主备电
	private String hostStates;
	private String deviceType;
	private String heartime; //心跳时间
	
	public String getHeartime() {
		return heartime;
	}
	public void setHeartime(String heartime) {
		this.heartime = heartime;
	}
	public String getNetStates() {
		return netStates;
	}
	public void setNetStates(String netStates) {
		this.netStates = netStates;
	}
	public String getHostStates() {
		return hostStates;
	}
	public void setHostStates(String hostStates) {
		this.hostStates = hostStates;
	}
	public String getRepeaterMac() {
		return repeaterMac;
	}
	public void setRepeaterMac(String repeaterMac) {
		this.repeaterMac = repeaterMac;
	}
	public String getRepeaterTime() {
		return repeaterTime;
	}
	public void setRepeaterTime(String repeaterTime) {
		this.repeaterTime = repeaterTime;
	}
	public int getNetstate() {
		return netstate;
	}
	public void setNetstate(int netstate) {
		this.netstate = netstate;
	}
	public int getHoststate() {
		return hoststate;
	}
	public void setHoststate(int hoststate) {
		this.hoststate = hoststate;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
}
