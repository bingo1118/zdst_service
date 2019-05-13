package com.cloudfire.entity;

public class WaterAckEntity {
	private String waterMac;	//ˮѹ�豸
	private int deviceType = 0;	//ˮѹ78 ��ʪ��79
	private int waveValue = 0;		//������ֵ����λkp		ˮѹ���ϱ�ʱ�� 		��ʪ�ȣ��ϱ�ʱ��
	private int ackTimes = 0;		//�ɼ�ʱ��� ��λ����		ˮѹ���ɼ�ʱ��		��ʪ�ȣ��ɼ�ʱ��
	private int threshold1 = 0;		//�洢��ֵ1 ˮѹ����ˮѹ��ֵ			��ʪ�ȣ�������ֵ
	private int threshold2 = 0;		//�洢��ֵ2 ˮѹ����ˮѹ��ֵ			��ʪ�ȣ�������ֵ
	private int threshold3 = 0;		//�洢��ֵ3					��ʪ�ȣ���ʪ��ֵ
	private int threshold4 = 0;		//�洢��ֵ4					��ʪ�ȣ���ʪ��ֵ
	private String error="";
    private int errorCode;
	
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public int getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}
	public int getThreshold1() {
		return threshold1;
	}
	public void setThreshold1(int threshold1) {
		this.threshold1 = threshold1;
	}
	public int getThreshold2() {
		return threshold2;
	}
	public void setThreshold2(int threshold2) {
		this.threshold2 = threshold2;
	}
	public int getThreshold3() {
		return threshold3;
	}
	public void setThreshold3(int threshold3) {
		this.threshold3 = threshold3;
	}
	public int getThreshold4() {
		return threshold4;
	}
	public void setThreshold4(int threshold4) {
		this.threshold4 = threshold4;
	}
	public String getWaterMac() {
		return waterMac;
	}
	public void setWaterMac(String waterMac) {
		this.waterMac = waterMac;
	}
	public int getWaveValue() {
		return waveValue;
	}
	public void setWaveValue(int waveValue) {
		this.waveValue = waveValue;
	}
	public int getAckTimes() {
		return ackTimes;
	}
	public void setAckTimes(int ackTimes) {
		this.ackTimes = ackTimes;
	}
}
