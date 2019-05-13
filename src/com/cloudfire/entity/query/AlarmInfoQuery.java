package com.cloudfire.entity.query;

import com.cloudfire.until.BaseQuery;

/**
 * @author cheng
 *2017-5-9
 *����2:13:07
 */
public class AlarmInfoQuery extends BaseQuery{
	
	private static final long serialVersionUID = -2427731900653735635L;
	private String devMac="";	//�̸е�named
	private String devName="";	//�豸�����ͣ���1.����̽����2.��ȼ����̽��������   5.��������̽���� ���������ֱ�������7.���ⱨ���������ⱨ������8.�ֶ����������ֶ���������9.�����豸
	private String alarmTime=""; //�豸������ʱ��
	private String alarmType=""; //�豸����������
	private String alarmAddress="";//���������ĵط�
	private String principal1="";//������
	private String principalPhone="";//�����˵绰
	private String areaName="";//��������
	private String placeName = "";//�ص������
	private String dealTime=""; //����ʱ��
	private String repeaterMac=""; //�����м���
	private String ifDealAlarm=""; //�Ƿ��Ѵ���
	private String dealPeople=""; //������
	private String netState="";
	private String dealDetail=""; //ԭ��
	
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
