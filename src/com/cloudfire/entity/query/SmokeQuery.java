/**
 * ÉÏÎç10:12:53
 */
package com.cloudfire.entity.query;

import com.cloudfire.until.BaseQuery;

/**
 * @author cheng
 *2017-4-27
 *ÉÏÎç10:12:53
 */
public class SmokeQuery extends BaseQuery{
	
	private static final long serialVersionUID = 8515525651489654439L;
	private String deviceType = "";
	private String addSmokeTime="";
    private String address="";
    private String areaName="";

    private String ifDealAlarm="";
    private String mac="";
    private String name="";
    private String netState="";
    private String placeType="";
    private String placeeAddress="";
    private String repeater="";
    
    private String alarmBeginTime;
    private String alarmEndTime;
    
    private String type="";
    private String devictTypeName="";
    
	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getAddSmokeTime() {
		return addSmokeTime;
	}

	public void setAddSmokeTime(String addSmokeTime) {
		this.addSmokeTime = addSmokeTime;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getIfDealAlarm() {
		return ifDealAlarm;
	}

	public void setIfDealAlarm(String ifDealAlarm) {
		this.ifDealAlarm = ifDealAlarm;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNetState() {
		return netState;
	}

	public void setNetState(String netState) {
		this.netState = netState;
	}

	public String getPlaceType() {
		return placeType;
	}

	public void setPlaceType(String placeType) {
		this.placeType = placeType;
	}

	public String getPlaceeAddress() {
		return placeeAddress;
	}

	public void setPlaceeAddress(String placeeAddress) {
		this.placeeAddress = placeeAddress;
	}

	public String getRepeater() {
		return repeater;
	}

	public void setRepeater(String repeater) {
		this.repeater = repeater;
	}

	public String getAlarmBeginTime() {
		return alarmBeginTime;
	}

	public void setAlarmBeginTime(String alarmBeginTime) {
		this.alarmBeginTime = alarmBeginTime;
	}

	public String getAlarmEndTime() {
		return alarmEndTime;
	}

	public void setAlarmEndTime(String alarmEndTime) {
		this.alarmEndTime = alarmEndTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDevictTypeName() {
		return devictTypeName;
	}

	public void setDevictTypeName(String devictTypeName) {
		this.devictTypeName = devictTypeName;
	}
	
	
	

}
