/**
 * 下午3:35:08
 */
package com.cloudfire.entity.query;

import com.cloudfire.until.BaseQuery;

/**
 * @author cheng
 *2017-5-10
 *下午3:35:08
 */
public class WaterQuery extends BaseQuery{

	private static final long serialVersionUID = -6566871546176390237L;
	private String deviceType = "";
	private String addWaterTime="";
    private String address="";
    private String areaName="";
    private String value;
    private String ifDealAlarm="";
    private String mac="";
    private String name="";
    private String netState="";
    private String placeType="";
    private String placeeAddress="";
    private String repeater="";
    private String alarmBeginTime;
    private String alarmEndTime;
    private int isTrue = 0;
    
    private String remark; //数据单位
    private String rssivalue;
    
    private float temperature; //温度
    private float humidity; //湿度
    
    private int ifAlarm;
    private int alarmType;
    private String alarmName;
    
    private String startTime;
    private String endTime;
    
    
    public int getIsTrue() {
		return isTrue;
	}
	public void setIsTrue(int isTrue) {
		this.isTrue = isTrue;
	}
	public float getTemperature() {
		return temperature;
	}
	public int getIfAlarm() {
		return ifAlarm;
	}
	public void setIfAlarm(int ifAlarm) {
		this.ifAlarm = ifAlarm;
	}
	public int getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(int alarmType) {
		this.alarmType = alarmType;
	}
	public String getAlarmName() {
		return alarmName;
	}
	public void setAlarmName(String alarmName) {
		this.alarmName = alarmName;
	}
	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}
	public float getHumidity() {
		return humidity;
	}
	public void setHumidity(float humidity) {
		this.humidity = humidity;
	}
	public String getRssivalue() {
		return rssivalue;
	}
	public void setRssivalue(String rssivalue) {
		if(rssivalue==null){
			this.rssivalue = "0";
		}else{
			this.rssivalue = rssivalue;
		}
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	private String type="";
    private String devictTypeName="";
    
    private String electrState;
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public String getAddWaterTime() {
		return addWaterTime;
	}
	public void setAddWaterTime(String addWaterTime) {
		this.addWaterTime = addWaterTime;
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
	public String getElectrState() {
		return electrState;
	}
	public void setElectrState(String electrState) {
		this.electrState = electrState;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
}
