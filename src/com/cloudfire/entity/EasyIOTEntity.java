package com.cloudfire.entity;

import java.util.List;

public class EasyIOTEntity {
	private String devSerial;	//�豸IEME
	private String createTime; 	//����ʱ��
	private String iotEventTime="";//����ʱ��
	private String dataType="";//�豸����
	private String rssivalue = "";//rssiֵ�ź�ǿ��
	private List<ServiceData> serviceData;	//serviceData��������
	
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
