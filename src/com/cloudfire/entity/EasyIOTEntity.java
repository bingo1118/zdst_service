package com.cloudfire.entity;

import java.util.List;

public class EasyIOTEntity {
	private String devSerial;	//设备IEME
	private String createTime; 	//创建时间
	private String iotEventTime="";//数据时间
	private String dataType="";//设备类型
	private String rssivalue = "";//rssi值信号强度
	private List<ServiceData> serviceData;	//serviceData集合数据
	
	public String getRssivalue() {
		return rssivalue;
	}
	public void setRssivalue(String rssivalue) {
		this.rssivalue = rssivalue;
	}
	public String getDevSerial() {
		return devSerial;
	}
	public void setDevSerial(String devSerial) {
		this.devSerial = devSerial;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getIotEventTime() {
		return iotEventTime;
	}
	public void setIotEventTime(String iotEventTime) {
		this.iotEventTime = iotEventTime;
	}
	public List<ServiceData> getServiceData() {
		return serviceData;
	}
	public void setServiceData(List<ServiceData> serviceData) {
		this.serviceData = serviceData;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
}
