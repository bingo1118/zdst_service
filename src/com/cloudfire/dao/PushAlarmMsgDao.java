package com.cloudfire.dao;

import com.cloudfire.entity.PushAlarmMsgEntity;

public interface PushAlarmMsgDao {
	public PushAlarmMsgEntity getPushAlarmMsg(String mac,int alarmType);
	
	public PushAlarmMsgEntity getPushAlarmMsg(String userId);
}
