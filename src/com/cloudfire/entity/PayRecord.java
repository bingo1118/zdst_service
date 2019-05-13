package com.cloudfire.entity;

import java.math.BigDecimal;

public class PayRecord {
	
	private int id;
	private String mac;
	private BigDecimal free;
	private String payTime;
	private String userId;
	private String stopTime;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public BigDecimal getFree() {
		return free;
	}
	public void setFree(BigDecimal free) {
		this.free = free;
	}
	public String getPayTime() {
		return payTime;
	}
	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getStopTime() {
		return stopTime;
	}
	public void setStopTime(String stopTime) {
		this.stopTime = stopTime;
	}
	
}
