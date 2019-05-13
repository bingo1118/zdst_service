package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.DealOkAlarmEntity;
import com.cloudfire.entity.HttpRsult;

public interface IDealOkMsg {
	public int getDealAlarmMsgCount(List<String> areaIds,DealOkAlarmEntity query);
	public List<DealOkAlarmEntity>getokDealAlarmMsg(List<String> areaIds,DealOkAlarmEntity query);
	
}
