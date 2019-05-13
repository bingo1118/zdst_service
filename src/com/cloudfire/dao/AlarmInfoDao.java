package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.AlarmInfoEntity;
import com.cloudfire.entity.AlarmInfoEntity_PC;
import com.cloudfire.entity.PrinterEntity;
import com.cloudfire.entity.query.AlarmInfoQuery;

public interface AlarmInfoDao {
	
	/** 处理报警记录的已经处理 */
	public List<AlarmInfoEntity> getAlarmInfo(List<String> areaId);
	
	public List<AlarmInfoEntity> getAlarmInfoByDev(String dev,String alarmTime);
	
	public int getAllAlarmInfoCount(List<String> areaId,AlarmInfoEntity query);
	
	public int getAlarmInfoCount(String mac,AlarmInfoEntity_PC query,List<String> areaIds);
	
	public List<AlarmInfoQuery> getAllAlarmInfoByMac(String mac,AlarmInfoEntity_PC query,List<String> areaIds);
	
	public List<AlarmInfoEntity> getAllAlarmInfo(List<String> areaId,AlarmInfoEntity query);
	
	public List<AlarmInfoEntity> getAlarmInfoByFault(List<String> areaId);
	
	public PrinterEntity getFaltAlarmInfo(String repeaterMac);
	
	public boolean updateFaultInfo(String dealUser,String dealText, String repeaterMac,String faultCode,String faultTime);
	
	public boolean updateAlarmInfo(String dealUser,String dealText, String repeaterMac,String alarmTime,String alarmType);

	
	public AlarmInfoEntity getAlarmInfoByMac(String mac);

	public List<AlarmInfoEntity> getNeedDealAlarmInfo(String repeaterMac, String faultCode);

	public AlarmInfoEntity getRecentAlarmInfo(String mac);
	
}
