package com.cloudfire.dao;

import com.cloudfire.entity.AllAlarmEntity;
import com.cloudfire.entity.ElectrAlarmThresholdEntity;
import com.cloudfire.entity.OneAlarmEntity;
import com.cloudfire.entity.THAlarmThresholdEntity;
import com.cloudfire.entity.WaterAckEntity;
import com.cloudfire.entity.WaterAlarmThresholdEntity;

public interface AllAlarmDao {
	//userId=13622215085&privilege=&page=
	public AllAlarmEntity getAdminAllAlarmMsg(String userId,String privilege,String page);
	public AllAlarmEntity getAdminAllAlarmMsg(String userId,String privilege,String page,String smokeMac);
	public int getAdminAllAlarmPageNumber(String userId,String privilege,String startTime, String endTime, String areaID,
			String placeTypeId);
	public AllAlarmEntity getNormalAllAlarmMsg(String userId,String privilege,String page);
	public AllAlarmEntity getNormalAllAlarmMsg(String userId,String privilege,String page,String smokeMac);
	public AllAlarmEntity getAdminNeedAlarm(String userId,String privilege,String page,String startTime,String endTime,String areaId,String placeTypeId,String parentId);
	public AllAlarmEntity getNormalNeedAlarm(String userId,String privilege,String page,String startTime,String endTime);
	public OneAlarmEntity getNormalLastestAlarm(String userId,String privilege);
	public OneAlarmEntity getAdminLastestAlarm(String userId,String privilege);
	public WaterAlarmThresholdEntity getWaterAlarmThreshold(String mac);
	
	public WaterAckEntity getWaterAckEntityThreshold(String mac);
	
	/**
	 * 添加水压报警阈值，207对应低水压报警，208对应高水压报警
	 */
	public boolean updateAlarmThreshold(String mac,String threshold207,String threshold208);
	boolean updateAlarmThreshold(String mac, String threshold207,
			String threshold208, String devtype);
	THAlarmThresholdEntity getTHAlarmThreshold(String mac);
	ElectrAlarmThresholdEntity getElectrAlarmThreshold(String mac);
	public AllAlarmEntity getAdminNeedAlarmMsg(String userId, String privilege,
			String page, String startTime, String endTime, String areaId,
			String placeTypeId, String parentId, String ifdeal, String grade, String distance, String id);
}
