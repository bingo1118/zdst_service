package com.cloudfire.entity;

/**
 * @author cheng
 *2017-3-20
 *����2:09:39
 */
public class MyDevicesVo {
	/**"�ҵ��豸"�Ļ�����Ϣ
	 * �豸���� 	�豸�ͺ� 	¥��¥�� 	�豸λ�� 	�豸״̬ 	�豸�������� 	��װʱ�� 	����״̬   	����
	 * 
	 *  */
	private String devictType;
	private String devName;
	private String devMac;
	private String floor="";
	private String storeys="";
	private String position="";
	private String netStates;
	private String alarmType;
	private String alarmTime;
	private String time;
	private String ifDealAlarm;
	private String address;
	
	/**
	 * @author lzo
	 * ��ӷ�ҳ�ֶΣ����㴫�͵�json������ȡֵ��
	 */
	private int currentPage = 1; // ��ǰҳ, Ĭ����ʾ��һҳ
	private int pageCount = 10;   // ÿҳ��ʾ������(��ѯ���ص�����), Ĭ��ÿҳ��ʾ10��
	private int totalCount;      // �ܼ�¼��
	private int totalPage;       // ��ҳ�� = �ܼ�¼�� / ÿҳ��ʾ������  (+ 1) Ҳ��ĩҳ
	private String areaId;
	
	
	public String getAreaId() {
		return areaId;
	}
	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getPageCount() {
		return pageCount;
	}
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getDevictType() {
		return devictType;
	}
	public void setDevictType(String devictType) {
		this.devictType = devictType;
	}
	public String getDevName() {
		return devName;
	}
	public void setDevName(String devName) {
		this.devName = devName;
	}
	public String getDevMac() {
		return devMac;
	}
	public void setDevMac(String devMac) {
		this.devMac = devMac;
	}
	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
	}
	public String getStoreys() {
		return storeys;
	}
	public void setStoreys(String storeys) {
		this.storeys = storeys;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getNetStates() {
		return netStates;
	}
	public void setNetStates(String netStates) {
		this.netStates = netStates;
	}
	public String getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}
	public String getAlarmTime() {
		return alarmTime;
	}
	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
	}
	public String getIfDealAlarm() {
		return ifDealAlarm;
	}
	public void setIfDealAlarm(String ifDealAlarm) {
		this.ifDealAlarm = ifDealAlarm;
	}
	
	
	
}
