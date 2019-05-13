package com.cloudfire.entity.query;

import com.cloudfire.until.BaseQuery;

/**
 * @author cheng
 *2017-5-9
 *下午2:13:07
 */
public class AlarmInfoQuery extends BaseQuery{
	
	private static final long serialVersionUID = -2427731900653735635L;
	private String devMac="";	//烟感的named
	private String devName="";	//设备的类型，有1.感烟探测器2.可燃气体探测器，火警   5.电气火灾探测器 （电气火灾报警），7.声光报警器（声光报警），8.手动报警器（手动报警），9.三江设备
	private String alarmTime=""; //设备报警的时间
	private String alarmType=""; //设备报警的类型
	private String alarmAddress="";//发生报警的地方
	private String principal1="";//负责人
	private String principalPhone="";//负责人电话
	private String areaName="";//所属区域
	private String placeName = "";//地点的名字
	private String dealTime=""; //处理时间
	private String repeaterMac=""; //所属中继器
	private String ifDealAlarm=""; //是否已处理
	private String dealPeople=""; //处理人
	private String netState="";
	private String dealDetail=""; //原因
	
	private String mac="";
	private String deviceType;
	
	
	private String begintime;
	private String endtime;
	
	
	
	public String getDealDetail() {
		return dealDetail;
	}
	public void setDealDetail(String dealDetail) {
		this.dealDetail = dealDetail;
	}
	public String getNetState() {
		return netState;
	}
	public void setNetState(String netState) {
		this.netState = netState;
	}
	public String getPlaceName() {
		return placeName;
	}
	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}
	public String getBegintime() {
		return begintime;
	}
	public void setBegintime(String begintime) {
		this.begintime = begintime;
	}
	public String getEndtime() {
		return endtime;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
	public String getDevMac() {
		return devMac;
	}
	public void setDevMac(String devMac) {
		this.devMac = devMac;
	}
	public String getDevName() {
		return devName;
	}
	public void setDevName(String devName) {
		this.devName = devName;
	}
	public String getAlarmTime() {
		return alarmTime;
	}
	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
	}
	public String getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}
	public String getAlarmAddress() {
		return alarmAddress;
	}
	public void setAlarmAddress(String alarmAddress) {
		this.alarmAddress = alarmAddress;
	}
	public String getPrincipal1() {
		return principal1;
	}
	public void setPrincipal1(String principal1) {
		this.principal1 = principal1;
	}
	public String getPrincipalPhone() {
		return principalPhone;
	}
	public void setPrincipalPhone(String principalPhone) {
		this.principalPhone = principalPhone;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public String getDealTime() {
		return dealTime;
	}
	public void setDealTime(String dealTime) {
		this.dealTime = dealTime;
	}
	public String getRepeaterMac() {
		return repeaterMac;
	}
	public void setRepeaterMac(String repeaterMac) {
		this.repeaterMac = repeaterMac;
	}
	public String getIfDealAlarm() {
		return ifDealAlarm;
	}
	public void setIfDealAlarm(String ifDealAlarm) {
		this.ifDealAlarm = ifDealAlarm;
	}
	public String getDealPeople() {
		return dealPeople;
	}
	public void setDealPeople(String dealPeople) {
		this.dealPeople = dealPeople;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	
	
	
}
