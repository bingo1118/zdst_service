/**
 * 下午7:17:21
 */
package com.cloudfire.dao;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import com.cloudfire.entity.Water;
import com.cloudfire.entity.WaterBean;
import com.cloudfire.entity.WaterEntity;
import com.cloudfire.entity.query.WaterQuery;

/**
 * @author cheng
 *2017-3-21
 *下午7:17:21
 */
public interface WaterInfoDao {
	
	//添加水压
	public boolean addWaterInfo(String waterRepeater,String waterMac,int status,String value);
	
	//获得水压设备的信息
	public List<WaterQuery> getAllWaterInfo(List<String> areaIds,WaterQuery query);
	public int getAllWaterInfoCount(List<String> areaIds,WaterQuery query);
	
	//获取水压设备记录的数据
	public List<WaterQuery> getAllWaterRecord(WaterQuery query);
	public int getAllWaterRecordCount(WaterQuery query);
	
	
	//获得温湿度设备的信息
	public List<WaterQuery> getAllThInfo(List<String> areaIds,WaterQuery query);
	public int getAllThInfoCount(List<String> areaIds,WaterQuery query);
	
	//获取水压设备记录的数据
	public List<WaterQuery> getAllThRecord(WaterQuery query);
	public int getAllThRecordCount(WaterQuery query);
		
	/**
	 * 根据设备MAC查看水位阈值,判断是水位低或则水位高并返回状态
	 * 207：水位低报警
	 * 208：水位高报警
	 * 0：正常
	 */
	public int getWaterLeve(String waterMac,String values); //根据水位值获取水位设备的报警状态
	public int getWaterGage2(String waterMac, String values); //根据水压值获取水压设备的报警状态
	public int getWaterGage(String waterMac,String values); //根据水压值获取水压设备的报警状态
	/**
	 * 用于保存水位数据，如果和上一条一样，则不保存到数据库
	 */
	public boolean updateWaterLeve(Water water);
	
	public String getGage(String waterMac,int alarmFamily);
	/**
	 * 获取高低水位值
	 */
	public String getWaterLow(String waterMac);
	public String getWaterHigh(String waterMac);
	
	/*
	 * 获取高低水压值
	 */
	public String getWaterLow2(String waterMac);
	public String getWaterHigh2(String waterMac);
	
	public List<Float> getWaterThreshold(String waterMac);

	public String getHighTemperature(String electricMacStr);

	public String getlowTemperature(String electricMacStr);

	public String getHighHumidity(String electricMacStr);

	public String getlowHumidity(String electricMacStr);
	
	/**
	 * 统计分析水系统
	 * @throws ParseException 
	 * @throws SQLException 
	 */
	public List<WaterBean> getWaterStatistic(String startTime,String endTime,String areaId);
	public List<WaterBean> getWaterStatistic2(String startTime,String endTime,String areaId);
	
	/**
	 * 添加修改水压报警阈值
	 * Hvalue:高水压
	 * Lvalue：低水压
	 */
	public int addWaterWaveValue(String repeaterMac,String waterMac,int Hvalue,int Lvalue);
	
	/**
	 * 批量添加水压记录
	 * @param lstWater
	 */
	public void addWaters(List<Water> lstWater); 
	
	public boolean addWaterInfo(String waterRepeater, String waterMac, int status,String value,String time) ;
	
	/**
	 * 获取某个时间点之前的最近一次水压值
	 * @param time
	 * @param mac
	 * @return
	 */
	public String getWaterValue(String time,String mac);
	
	
	/**
	 * 获取固定时间间隔的离散时间点的水系统设备的值,暂定时间间隔为1小时。各时间点为整点
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<Water> getWaterRecords(String startTime,String endTime,String waterMac);
	
	/**
	 * NB水压水位详情列表
	 * @param query
	 * @return
	 */
	public List<WaterQuery> NBWaterDetailList(WaterQuery query);
	

	//根据areaid获取水压设备列表
	public List<WaterEntity> getWatersByAreaid(String areaid);
	//根据areaid获取水位设备列表
	public List<WaterEntity> getWaterLevelsByAreaid(String string);
	
	public WaterEntity getWaterEntity(String waterMac);

	//无线水位探测器
	public List<WaterQuery> getAllWaterRecordList(WaterQuery query);

	/**
	 * 根据mac获取起止时间，校正值，上报时间间隔，主机mac,波动阈值
	 * @param waterMac
	 * @return
	 */
	public Water getWaterByMac(String waterMac);
	
	
	int updateGage(String waterValue, String waterMac, int alarmFamily);

	public boolean addHengxingFalanWaterInfo(String string, String waterMac,
			int waterStatus, String string2, long getDataTime);
	
	
}
