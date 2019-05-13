package com.cloudfire.entity;

import java.util.List;
import java.util.Map;

public class DeviceStateEntity {
	private int id = -1;
	private String devName;
	private String systemName;
//	private Map<String,List<String>> dtNames;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDevName() {
		return devName;
	}
	public void setDevName(String devName) {
		this.devName = devName;
	}
	public String getSystemName() {
		return systemName;
	}
	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}
}
