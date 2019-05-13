package com.cloudfire.entity.meter;

public class MeterReadingEntity {
	private  String quantity="";
	private String time="";
	
	private String mac="";
	private String voltage="";
	private String electricity="";
	private String power="";
	
	public String getVoltage() {
		return voltage;
	}
	public void setVoltage(String voltage) {
		this.voltage = voltage;
	}
	public String getElectricity() {
		return electricity;
	}
	public void setElectricity(String electricity) {
		this.electricity = electricity;
	}
	public String getPower() {
		return power;
	}
	public void setPower(String power) {
		this.power = power;
	}
	
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
}
