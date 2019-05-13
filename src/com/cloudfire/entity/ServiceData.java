package com.cloudfire.entity;

import java.util.Map;

public class ServiceData {
	private Map<String,String> serviceData;		//map<数据类型，数值>
	private String serviceId;					//serviceId
	
	public Map<String, String> getServiceData() {
		return serviceData;
	}
	public void setServiceData(Map<String, String> serviceData) {
		this.serviceData = serviceData;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
}
