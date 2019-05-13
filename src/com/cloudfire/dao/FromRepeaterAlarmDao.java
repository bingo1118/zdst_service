package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.AlarmPushOnlyEntity;
import com.cloudfire.entity.RePeaterData;
import com.cloudfire.entity.SmokeBean;

public interface FromRepeaterAlarmDao {
	public boolean addAlarmMsg(String smokeMac,String repeaterMac,int alarmtype,int alarmFamily);
	public boolean addAlarmMsg(String smokeMac,String repeaterMac,int alarmtype,int alarmFamily,Object obj);
	public boolean addAlarmMsg(String smokeMac,String repeaterMac,int alarmtype,String alarmFamily);
	public boolean addAlarmMsg(String smokeMac,String repeaterMac,int alarmtype,String alarmFamily,Object obj);
	public boolean addAlarmMsg(AlarmPushOnlyEntity pushEntity);
	public int getDeviceType(String deviceMac);
	public float getLinkageDistance(String deviceMac);
	public void updateSmokeAlarm(String smokeMac,int type);
	public SmokeBean getSmoke(String deviceMac);
	public List<String> getSmokeInLinkageDistance(String deviceMac,float distance);
	/**
	 * add by lzo at 2017-5-11
	 * @param smokeBean 用作前置机下发区域列表。
	 * @return
	 */
	public SmokeBean getSmokeInfo(SmokeBean smokeBean);
	public void updateSmokeAlarm();
	
	public boolean addnotifyMsg(String smokeMac,String repeaterMac,int alarmtype,int alarmFamily);
	/**
	 * 优特电气
	 */
	public void addThreeElectric(RePeaterData mRePeaterData,int cmd);
	
	public void addElectricEnergy(RePeaterData mRePeaterData,int cmd);
}
