/**
 * обнГ4:02:06
 */
package com.cloudfire.myservice;

import java.util.List;

import com.cloudfire.entity.AlarmInfoEntity;
import com.cloudfire.entity.AlarmInfoEntity_PC;
import com.cloudfire.entity.DealOkAlarmEntity;
import com.cloudfire.entity.query.AlarmInfoQuery;

import common.page.Pagination;

/**
 * @author cheng
 *2017-5-3
 *обнГ4:02:06
 */
public interface AlarmInfoService {
	Pagination selectAlarmInfoListByPage(List<String> areaIds,AlarmInfoEntity_PC query);
	
	Pagination selectAllAlarmInfoListByPage(List<String> areaIds,AlarmInfoQuery query);
	
	Pagination selectAllAlarmInfoListByPage(List<String> areaIds,AlarmInfoEntity query);
	
	Pagination selectAlarmInfoListByMac(AlarmInfoEntity_PC query,String mac,List<String> areaIds);
}
