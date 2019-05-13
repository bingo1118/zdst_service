package com.cloudfire.dao;

import java.util.Calendar;
import java.util.List;

import com.cloudfire.entity.CountValue;
import com.cloudfire.entity.FireBean;
import com.cloudfire.entity.StatisticAnalysisEntity;
import com.cloudfire.entity.StatisticBean;

public interface StatisticDao {
	
	/*根据设备所属系统获取在线统计数据*/
	public StatisticBean getStatistic(int deviceType, List<String> areaIds );
	//获取所有设备的在线，离线统计
	public CountValue getStatistic(List<String> areaIds); 
	
	/*根据areaId获取在线统计数据*/
	public StatisticBean getStatistic2(String areaId);
	public List<StatisticBean> getStatistic2(List<String> areaId);
	
	/*根据时间段获取统计电气设备数据*/
	public List<Integer> getStatistic3(Calendar calendar,int flag,List<String> areaIds);
	
	
	/*根据设备类型获取报警类型及报警次数*/
	public List<FireBean> getAlarmCountByDeviceType(List<String> areaIds);
	
	
	/**
	 * 统计每个区域的在线和离线数
	 */
	public List<StatisticAnalysisEntity> getStatistNum(List<String> areaIds,String parentId);
	public List<StatisticAnalysisEntity> getStatistNum2(List<String> areaIds);
}
