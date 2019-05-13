package com.cloudfire.entity.meter;

public class MeterInfoEntity {
	private String mac="";
	private String name="";
	private String address="";	
	private int ifSendVoltage;
	private int ifSendElectricity;
	private int ifSendPower;
	
	
	public int getIfSendVoltage() {
		return ifSendVoltage;
	}
	public void setIfSendVoltage(int ifSendVoltage) {
		this.ifSendVoltage = ifSendVoltage;
	}
	public int getIfSendElectricity() {
		return ifSendElectricity;
	}
	public void setIfSendElectricity(int ifSendElectricity) {
		this.ifSendElectricity = ifSendElectricity;
	}
	public int getIfSendPower() {
		return ifSendPower;
	}
	public void setIfSendPower(int ifSendPower) {
		this.ifSendPower = ifSendPower;
	}
	
	
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
}
