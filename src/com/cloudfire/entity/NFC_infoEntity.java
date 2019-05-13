package com.cloudfire.entity;

import java.util.List;

import com.cloudfire.until.BaseQuery;

public class NFC_infoEntity extends BaseQuery {

	private static final long serialVersionUID = -5657615197212822822L;
	
	private String uid;//varchar(20)uid
	private String areaId;//int(2)区域ID
	private int parentId;
	private String areaName;
	private int deviceType;//int(2)设备类型
	private String deviceName;//varchar(30)设备名称
	private String address;//varchar(50)设备地址
	private String longitude;//varchar(15)经度
	private String latitude;//archar(15)纬度
	private String userId;//varchar(15)登录用户ID
	private String addTime;//varchar(20)添加时间
	private String memo;//varchar(100)备注
	private String endTime;//最后一次巡检时间
	
	private String endTime_1;//varchar(20)维保时间
	private String endTime_2;
	private String devicestate;//varchar(20)维保状态    null、0:待检。1、合格。2、不合格。
	private String nowState;
	private List<String> areaIds;
	private String items;
	
	
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public List<String> getAreaIds() {
		return areaIds;
	}
	public void setAreaIds(List<String> areaIds) {
		this.areaIds = areaIds;
	}
	private CountValue cv;
	
	public CountValue getCv() {
		return cv;
	}
	public void setCv(CountValue cv) {
		this.cv = cv;
	}
	public String getNowState() {
		return nowState;
	}
	public void setNowState(String nowState) {
		this.nowState = nowState;
	}
	public String getEndTime_1() {
		return endTime_1;
	}
	public void setEndTime_1(String endTime_1) {
		this.endTime_1 = endTime_1;
	}
	public String getEndTime_2() {
		return endTime_2;
	}
	public void setEndTime_2(String endTime_2) {
		this.endTime_2 = endTime_2;
	}
	public String getAreaId() {
		return areaId;
	}
	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
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
	public int getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(int deviceType) {
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
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
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
	public String getDevicestate() {
		return devicestate;
	}
	public void setDevicestate(String devicestate) {
		this.devicestate = devicestate;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getItems() {
		return items;
	}
	public void setItems(String items) {
		this.items = items;
	}
	
}
