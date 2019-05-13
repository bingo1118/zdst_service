package com.cloudfire.entity;

import java.io.Serializable;

public class Repeater implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4094584690253678739L;
	private String repeaterMac;
	private long heartime;
	private int netState;
	private int powerState;
	private long powerChangeTime;
//	private boolean lock = false; //是否锁定，不可修改
	public int getPowerState() {
		return powerState;
	}
	public void setPowerState(int powerState) {
		this.powerState = powerState;
	}
	public long getPowerChangeTime() {
		return powerChangeTime;
	}
	public void setPowerChangeTime(long powerChangeTime) {
		this.powerChangeTime = powerChangeTime;
	}
	public String getRepeaterMac() {
		return repeaterMac;
	}
	public void setRepeaterMac(String repeaterMac) {
		this.repeaterMac = repeaterMac;
	}
	public Long getHeartime() {
		return heartime;
	}
	public void setHeartime(long heartime) {
		this.heartime = heartime;
	}
	public int getNetState() {
		return netState;
	}
	public void setNetState(int netState) {
		this.netState = netState;
	}
//	public boolean isLock() {
//		return lock;
//	}
//	public void setLock(boolean lock) {
//		this.lock = lock;
//	}
	@Override
	public String toString() {
		
		return "heartTime:"+heartime+";netState:"+netState;
	}
	
}
