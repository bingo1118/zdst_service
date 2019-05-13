package com.cloudfire.entity;

public class BQMacInfo {
	private String bqEleMac;
	private String temperature1;
	private String temperature2;
	private String temperature3;
	private String temperature4;
	private String eleCurrent1;
	private String eleCurrent2;
	private String eleCurrent3;
	private String voltage1;
	private String voltage2;
	private String voltage3;
	private String leakEleCurrent1;
	private String createTimeString;
	public String getBqEleMac() {
		return bqEleMac;
	}
	public void setBqEleMac(String bqEleMac) {
		this.bqEleMac = bqEleMac;
	}
	public String getTemperature1() {
		return temperature1;
	}
	public void setTemperature1(String temperature1) {
		this.temperature1 = temperature1;
	}
	public String getTemperature2() {
		return temperature2;
	}
	public void setTemperature2(String temperature2) {
		this.temperature2 = temperature2;
	}
	public String getTemperature3() {
		return temperature3;
	}
	public void setTemperature3(String temperature3) {
		this.temperature3 = temperature3;
	}
	public String getTemperature4() {
		return temperature4;
	}
	public void setTemperature4(String temperature4) {
		this.temperature4 = temperature4;
	}
	public String getEleCurrent1() {
		return eleCurrent1;
	}
	public void setEleCurrent1(String eleCurrent1) {
		this.eleCurrent1 = eleCurrent1;
	}
	public String getEleCurrent2() {
		return eleCurrent2;
	}
	public void setEleCurrent2(String eleCurrent2) {
		this.eleCurrent2 = eleCurrent2;
	}
	public String getEleCurrent3() {
		return eleCurrent3;
	}
	public void setEleCurrent3(String eleCurrent3) {
		this.eleCurrent3 = eleCurrent3;
	}
	public String getVoltage1() {
		return voltage1;
	}
	public void setVoltage1(String voltage1) {
		this.voltage1 = voltage1;
	}
	public String getVoltage2() {
		return voltage2;
	}
	public void setVoltage2(String voltage2) {
		this.voltage2 = voltage2;
	}
	public String getVoltage3() {
		return voltage3;
	}
	public void setVoltage3(String voltage3) {
		this.voltage3 = voltage3;
	}
	public String getLeakEleCurrent1() {
		return leakEleCurrent1;
	}
	public void setLeakEleCurrent1(String leakEleCurrent1) {
		this.leakEleCurrent1 = leakEleCurrent1;
	}
	public String getCreateTimeString() {
		return createTimeString;
	}
	public void setCreateTimeString(String createTimeString) {
		this.createTimeString = createTimeString;
	}
	public BQMacInfo(String bqEleMac, String temperature1, String temperature2,
			String temperature3, String temperature4, String eleCurrent1,
			String eleCurrent2, String eleCurrent3, String voltage1,
			String voltage2, String voltage3, String leakEleCurrent1,
			String createTimeString) {
		super();
		this.bqEleMac = bqEleMac;
		this.temperature1 = temperature1;
		this.temperature2 = temperature2;
		this.temperature3 = temperature3;
		this.temperature4 = temperature4;
		this.eleCurrent1 = eleCurrent1;
		this.eleCurrent2 = eleCurrent2;
		this.eleCurrent3 = eleCurrent3;
		this.voltage1 = voltage1;
		this.voltage2 = voltage2;
		this.voltage3 = voltage3;
		this.leakEleCurrent1 = leakEleCurrent1;
		this.createTimeString = createTimeString;
	}
	public BQMacInfo() {
		super();
	}
	
	
}
