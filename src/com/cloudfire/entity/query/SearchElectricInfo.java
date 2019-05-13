/**
 * 上午9:13:54
 */
package com.cloudfire.entity.query;

import com.cloudfire.until.BaseQuery;

/**
 * @author cheng
 *2017-4-5
 *上午9:13:54
 */
public class SearchElectricInfo extends BaseQuery{

	private static final long serialVersionUID = -7366061659115492853L;
	private int row;
	
	private String addSmokeTime="";
    private String address="";
    private String areaName="";
    private int deviceType;
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
    
    private String heartime="";
    
    private String rssivalue = "0";
   
    private int ifAlarm;
    private int alarmType;
    private String alarmName;
    
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
	public String getHeartime() {
		return heartime;
	}
	public void setHeartime(String heartime) {
		this.heartime = heartime;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public String getDevictTypeName() {
		return devictTypeName;
	}
	public void setDevictTypeName(String devictTypeName) {
		this.devictTypeName = devictTypeName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	public int getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
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
    
	//用来判断该设备是否有电压，电流，漏电流，温度数据。
	private boolean data6=false;
	private boolean data7=false;
	private boolean data8=false;
	private boolean data9=false;

	public boolean isData6() {
		return data6;
	}
	public void setData6(boolean data6) {
		this.data6 = data6;
	}
	public boolean isData7() {
		return data7;
	}
	public void setData7(boolean data7) {
		this.data7 = data7;
	}
	public boolean isData8() {
		return data8;
	}
	public void setData8(boolean data8) {
		this.data8 = data8;
	}
	public boolean isData9() {
		return data9;
	}
	public void setData9(boolean data9) {
		this.data9 = data9;
	}
	
    
}
