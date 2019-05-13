/**
 * 下午4:23:31
 */
package com.cloudfire.dao.query;

import java.util.List;

import com.cloudfire.entity.CountValue;
import com.cloudfire.entity.query.SearchAnalysisInfo;
import com.cloudfire.entity.query.SearchAnalysisinfo1;
import com.sun.org.apache.bcel.internal.generic.NEW;

/**
 * @author cheng
 *2017-4-14
 *下午4:23:31
 */
public interface SearchAnalysisQuery {
	public Integer getAlarmCountLg(List<String> areaIds,List<String> listalarmType,String type,SearchAnalysisinfo1 query);
	/** 通过用户的id查询他所以拥有的设备 */
	public List<SearchAnalysisInfo> getAnalysisInfoData(List<String> areaIds,String type);
	
	public List<SearchAnalysisInfo> getAnalysisInfoData(List<String> areaIds,CountValue cv);
	
	/**
	 * 复写优化之前SQL语句
	 * @param areaIds
	 * @return
	 */
	public List<SearchAnalysisInfo> getAnalysisInfoData(List<String> areaIds);
	
	/**查询所有的年份 */
	public List<String> getAllYear();
	
	/**初始化报警次数统计图的数据 */
	public Integer getAlarmCount202(List<String> areaIds,String devId,SearchAnalysisinfo1 query);
	public Integer getAlarmCount193(String type ,SearchAnalysisinfo1 query);
	
	
	
	/** 初始化设备类型为5的设备的发生故障的次数 */
	public Integer getAlarmCount36(String type ,SearchAnalysisinfo1 query);
	public Integer getAlarmCount43(String type ,SearchAnalysisinfo1 query);
	public Integer getAlarmCount44(String type ,SearchAnalysisinfo1 query);
	public Integer getAlarmCount45(String type ,SearchAnalysisinfo1 query);
	public Integer getAlarmCount46(String type ,SearchAnalysisinfo1 query);
	public Integer getAlarmCount47(String type ,SearchAnalysisinfo1 query);
	
	
	/** 初始化设备类型为5的设备的个各的平均数据 */	
	public Float getAlarmCount43and44avg(String type ,SearchAnalysisinfo1 query);
	public Float getAlarmCount45avg(String type ,SearchAnalysisinfo1 query);
	public Float getAlarmCount46avg(String type ,SearchAnalysisinfo1 query);
	public Float getAlarmCount47avg(String type ,SearchAnalysisinfo1 query);
}
