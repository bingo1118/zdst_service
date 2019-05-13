package com.cloudfire.entity;

import java.util.List;

public class ElectricThresholdBean {
	private String unpressure="" ;//欠压
	private String overpressure="";//过压
	private String overCurrent="";//过流
	private String leakCurrent="";//漏电流
	private List<String> temperatures=null;//温度
	public String getUnpressure() {
		return unpressure;
	}
	public void setUnpressure(String unpressure) {
		this.unpressure = unpressure;
	}
	public String getOverpressure() {
		return overpressure;
	}
	public void setOverpressure(String overpressure) {
		this.overpressure = overpressure;
	}
	public String getOverCurrent() {
		return overCurrent;
	}
	public void setOverCurrent(String overCurrent) {
		this.overCurrent = overCurrent;
	}
	public String getLeakCurrent() {
		return leakCurrent;
	}
	public void setLeakCurrent(String leakCurrent) {
		this.leakCurrent = leakCurrent;
	}
	public List<String> getTemperatures() {
		return temperatures;
	}
	public void setTemperatures(List<String> temperatures) {
		this.temperatures = temperatures;
	}
	
}
