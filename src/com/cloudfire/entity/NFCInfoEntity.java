package com.cloudfire.entity;

public class NFCInfoEntity {
	private String uid;		//nfc   MAC
	private String areaid;	//��������ID
	private String areaName;	// ������������
	private String deviceType;	//�豸����ID
	private String deviceTypeName;	//�豸����ID
	private String deviceName;	//�豸����
	private String address;		//��ַ
	private String longitude;	//����
	private String latitude;	//γ��
	private String addTime;		//	ʱ�䣨�豸¼��ʱ�����Ѳ��ʱ�䣩��װ����
	private String memo;		//	��ע��Ϣ
	private Integer devicestate = 0;	//�豸״̬��0����飬1���ϸ�2�����ϸ�
	private String userId;		//����û�
	private String photo1;	
	
	private String producer;	//����
	private String makeTime;	//��������
	private String overTime;	//��������
	private String workerName;	//Ѳ����
	private String workerPhone;	//Ѳ������ϵ��ʽ
	private String endTime;		//���һ��Ѳ��ʱ��
	private String items;//�����Ŀ
	
	
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Integer getDevicestate() {
		return devicestate;
	}
	public void setDevicestate(Integer devicestate) {
		this.devicestate = devicestate;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getAreaid() {
		return areaid;
	}
	public void setAreaid(String areaid) {
		this.areaid = areaid;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getDeviceTypeName() {
		return deviceTypeName;
	}
	public void setDeviceTypeName(String deviceTypeName) {
		this.deviceTypeName = deviceTypeName;
	}
	public String getPhoto1() {
		return photo1;
	}
	public void setPhoto1(String photo1) {
		this.photo1 = photo1;
	}
	public String getProducer() {
		return producer;
	}
	public void setProducer(String producer) {
		this.producer = producer;
	}
	public String getMakeTime() {
		return makeTime;
	}
	public void setMakeTime(String makeTime) {
		this.makeTime = makeTime;
	}
	public String getOverTime() {
		return overTime;
	}
	public void setOverTime(String overTime) {
		this.overTime = overTime;
	}
	public String getWorkerName() {
		return workerName;
	}
	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}
	public String getWorkerPhone() {
		return workerPhone;
	}
	public void setWorkerPhone(String workerPhone) {
		this.workerPhone = workerPhone;
	}
	public String getItems() {
		return items;
	}
	public void setItems(String items) {
		this.items = items;
	}
	
}
