package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.AlarmInfoEntity_PC;
import com.cloudfire.entity.DealOkAlarmEntity;
import com.cloudfire.entity.query.AlarmInfoQuery;

public interface IAlarmInfoDao_PC {
	
	public int getAllAlarmInfoCount(List<String> areaIds,AlarmInfoQuery query);
	public List<AlarmInfoQuery> getAllAlarmInfoMsg(List<String> areaIds,AlarmInfoQuery query);
	
	
	public int getAlarmInfoCount(List<String> areaIds,AlarmInfoEntity_PC query);
	/** 根据用户的userId 和权限查询报警的信息 */
	public List<AlarmInfoEntity_PC> getAlarmInfo(AlarmInfoEntity_PC query,List<String> areaId);
	
}
