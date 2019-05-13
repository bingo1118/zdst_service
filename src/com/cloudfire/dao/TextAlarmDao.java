package com.cloudfire.dao;

import com.cloudfire.entity.DisposeAlarmEntity;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.OneKeyAlarmEntity;

public interface TextAlarmDao {
	//userId=13622215085&privilege=&smokeMac=&info=
	public HttpRsult textAlarm(String userId,String privilege,String smokeMac,String info);
//	userId=13622215085&alarmSerialNumber=
	public HttpRsult textAlarmAck(String userId,String alarmSerialNumber);
	public OneKeyAlarmEntity oneKeyAlarm(String smokeMac,String info,String userId);
	public DisposeAlarmEntity oneKeyAlarmACK(String userId);
}
