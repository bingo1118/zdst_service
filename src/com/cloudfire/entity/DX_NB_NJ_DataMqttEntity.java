package com.cloudfire.entity;

public class DX_NB_NJ_DataMqttEntity {
	
	private String imei;//IMEI��ӦMAC
	private String imsi;//imsi����
	private String address;//��ַ
	private String time;//����豸ʱ��
	private String named;//����
	private String principal1;//��ϵ��
	private String principal1Phone;//��ϵ�˵绰
	private String dataType;//�������ͣ�stDevBattery:�������stDevSignal���ź�ǿ�ȡ�HeartBeat��ȼ��������Alarm��ȼ��������stDevCMD���̸У�	
	private String dataValue;//��Ӧ�������͵�����ֵ��
		/*�̸������⴦��0x00 ��λ	�Ǽ���λ����������(ͬʱ��Ϊ����ʹ��)
		0x01 �澯	�Ǽ��澯
		0x02 �͵�	�Ǽ���ص͵�ѹ
		0x08 ����	�Ǽ�����
		0x0D ����	�Ǽ�����
		0x11 �����ָ�	�Ǽ������ָ�
		0x12 ��ѹ�ָ�	�Ǽ���ص͵�ѹ�ָ�
		0x13 ����	�Ǽ����𱨾�
		0x14 ����ָ�	�Ǽ�����ָ�
		0x06 ����(����)	��������(����)
		��*/
	private String deviceId;//�豸Ψһ��ַ
	
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getNamed() {
		return named;
	}
	public void setNamed(String named) {
		this.named = named;
	}
	public String getPrincipal1() {
		return principal1;
	}
	public void setPrincipal1(String principal1) {
		this.principal1 = principal1;
	}
	public String getPrincipal1Phone() {
		return principal1Phone;
	}
	public void setPrincipal1Phone(String principal1Phone) {
		this.principal1Phone = principal1Phone;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getDataValue() {
		return dataValue;
	}
	public void setDataValue(String dataValue) {
		this.dataValue = dataValue;
	}
}
