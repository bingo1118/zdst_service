package com.cloudfire.entity;

import java.math.BigDecimal;

public class Recharge {
	
	private String mac;
	private BigDecimal setFee;
	private BigDecimal BeforeFee;
	private String feetime;
	private String stopTime;
	
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public BigDecimal getSetFee() {
		return setFee;
	}
	public void setSetFee(BigDecimal setFee) {
		this.setFee = setFee;
	}
	public BigDecimal getBeforeFee() {
		return BeforeFee;
	}
	public void setBeforeFee(BigDecimal beforeFee) {
		BeforeFee = beforeFee;
	}
	public String getFeetime() {
		return feetime;
	}
	public void setFeetime(String feetime) {
		this.feetime = feetime;
	}
	public String getStopTime() {
		return stopTime;
	}
	public void setStopTime(String stopTime) {
		this.stopTime = stopTime;
	}
	
}
