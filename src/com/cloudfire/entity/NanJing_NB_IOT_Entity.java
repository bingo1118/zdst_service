package com.cloudfire.entity;

import java.util.List;
import java.util.Map;

public class NanJing_NB_IOT_Entity {
	private String deviceId;	//�豸IEME
	private String serviceId; 	//����ID
	private String serviceType;	//��������
	private String gatewayId;
	private Map<String,String> data;//����
	private String status;		//data����ֵ������ȼ���е�MAC��ַ
	private int mqttState = 0; 	//���ڶԽ��ֶ�������־
	private int alarmType = 0;	//����״̬
	private int alarmData = 0;	//����ֵ
	private String alarmValue;	//����ֵ
	private String imeiVal;		//imeiֵ
	private String imsiVal;		//imsiֵ
	private String rsiiVal;		//rsiiֵ
	private String electrState;	//����״̬
	private String batteryPower;	//��ص���
	private ElectricThresholdBean etb;	//������ֵ
	private ThreePhaseElectricEntity electric = null;	//�������
	private Water water = null;	//ˮѹ
	private THDevice thDevice = null;	//��ʪ��
	private WaterAckEntity waterEntity = null;	//��ͨˮѹˮλ
	private ElectricEnergyEntity energyEntity = null;//U�ص���
	private String hearTime = "";
	
 	public String getHearTime() {
		return hearTime;
	}
	public void setHearTime(String hearTime) {
		this.hearTime = hearTime;
	}
	public ElectricEnergyEntity getEnergyEntity() {
		return energyEntity;
	}
	public void setEnergyEntity(ElectricEnergyEntity energyEntity) {
		this.energyEntity = energyEntity;
	}
	public int getMqttState() {
		return mqttState;
	}
	public void setMqttState(int mqttState) {
		this.mqttState = mqttState;
	}
	public WaterAckEntity getWaterEntity() {
		return waterEntity;
	}
	public void setWaterEntity(WaterAckEntity waterEntity) {
		this.waterEntity = waterEntity;
	}
	public THDevice getThDevice() {
		return thDevice;
	}
	public void setThDevice(THDevice thDevice) {
		this.thDevice = thDevice;
	}
	public Water getWater() {
		return water;
	}
	public void setWater(Water water) {
		this.water = water;
	}
	public ThreePhaseElectricEntity getElectric() {
		return electric;
	}
	public void setElectric(ThreePhaseElectricEntity electric) {
		this.electric = electric;
	}
	public String getElectrState() {
		return electrState;
	}
	public void setElectrState(String electrState) {
		this.electrState = electrState;
	}
	public String getAlarmValue() {
		return alarmValue;
	}
	public void setAlarmValue(String alarmValue) {
		this.alarmValue = alarmValue;
	}
	public ElectricThresholdBean getEtb() {
		return etb;
	}
	public void setEtb(ElectricThresholdBean etb) {
		this.etb = etb;
	}
	public String getRsiiVal() {
		return rsiiVal;
	}
	public void setRsiiVal(String rsiiVal) {
		this.rsiiVal = rsiiVal;
	}
	public String getBatteryPower() {
		return batteryPower;
	}
	public void setBatteryPower(String batteryPower) {
		this.batteryPower = batteryPower;
	}
	public String getImsiVal() {
		return imsiVal;
	}
	public void setImsiVal(String imsiVal) {
		this.imsiVal = imsiVal;
	}
	public String getImeiVal() {
		return imeiVal;
	}
	public void setImeiVal(String imeiVal) {
		this.imeiVal = imeiVal;
	}
	public String getGatewayId() {
		return gatewayId;
	}
	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}
	public int getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(int alarmType) {
		this.alarmType = alarmType;
	}
	public int getAlarmData() {
		return alarmData;
	}
	public void setAlarmData(int alarmData) {
		this.alarmData = alarmData;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public Map<String, String> getData() {
		return data;
	}
	public void setData(Map<String, String> data) {
		this.data = data;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
