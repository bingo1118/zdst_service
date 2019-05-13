package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.AreaAndRepeater;
import com.cloudfire.entity.DealOkAlarmEntity;
import com.cloudfire.entity.DeviceCostEntity;
import com.cloudfire.entity.ShopTypeEntity;
import com.cloudfire.entity.SmartControlEntity;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.SmokeElectricInfoEntity;
import com.cloudfire.entity.StaticsAnalysEntity;
import com.cloudfire.entity.Water;
import com.cloudfire.entity.WaterEntity;
import com.cloudfire.entity.query.DeviceNetState;
import com.cloudfire.entity.query.DeviceType;
import com.cloudfire.entity.query.SearchElectricInfo;
import com.cloudfire.entity.query.SmokeQuery;

public interface SmartControlDao {

	public List<SmartControlEntity> smokeInfo(List<String> areaList,
			SmokeQuery query);
	/**
	 * @param NB查询返回数据信息
	 * @return
	 */
	public List<SmartControlEntity> NBsmokeInfo(List<String> areaList,
			SmokeQuery query);

	public StaticsAnalysEntity getAllElectricinfoLg(List<String> areaList,
			List<String> areaIdstr, String StartDate, String EndDate);

	public List<SmartControlEntity> getAllSmokeInfo(List<String> areaList,
			SmokeQuery query);
	
	public List<DeviceCostEntity> getAllSmokesInfo(List<String> areaList,
			SmokeQuery query);
	
	public List<DeviceCostEntity> getSmokeInfoByDeviceType(List<String> areaList,
			SmokeQuery query);
	
	public List<DeviceCostEntity> getSmokeInfoByDeviceType2(List<String> areaList,
			SmokeQuery query);
	//欠费设备
	public int getSmokeInfoCountByDeviceType(List<String> areaList,
			SmokeQuery query);
	//正常设备
	public int getSmokeInfoCountByDeviceType2(List<String> areaList,
			SmokeQuery query);
	
	public List<SmartControlEntity> getAllDeviceInfo(List<String> areaList);

	public int getAllSmokeInfoCount(List<String> areaList, SmokeQuery query);
	
	public int getAllSmokesInfoCount(List<String> areaList, SmokeQuery query);

	public int getSmokeInfoCount(List<String> areaList, SmokeQuery query);

	/** 根据type和其他条件查询 */
	public List<SearchElectricInfo> searchDeviceByTypeQuery(
			SearchElectricInfo info, List<String> areaList);

	public int searchCountDeviceByTypeQuery(SearchElectricInfo info,
			List<String> areaList);

	/** 获得地点的类型 */
	public List<ShopTypeEntity> getPlaceType(String type, List<String> areaList);
	public List<ShopTypeEntity> getPlaceType(String type, String devType, List<String> areaList);

	public List<ShopTypeEntity> getPlaceTypeNotType(List<String> areaList);

	/** 获得区域 */
	public List<AreaAndRepeater> getAreaAndRepeater(List<String> aids);
	
	public List<AreaAndRepeater> getAreaAndRepeater(String type,List<String> areaList);
	
	public List<AreaAndRepeater> getAreaAndRepeater(String type,String devType,List<String> areaList);

	/** 获得区域，不带设备类型 */
	public List<AreaAndRepeater> getAreaAndRepeaterNoType(List<String> areaList);

	public List<AreaAndRepeater> getAreaAndRepeaterNoType();

	/** 获得区域的烟感数量 */
	public List<AreaAndRepeater> getAreaAndRepeaterAndSmokeNumber(
			List<String> areaList, String type);

	public List<AreaAndRepeater> getAreaAndRepeaterAndSmoke(
			List<String> areaList);
	
	/*
	 * 优化语句
	 */

	public List<AreaAndRepeater> getAreaAndSmokeInfo(
			List<String> areaList);
	
	
	/** 获得设备类型 */
	public List<DealOkAlarmEntity> getDeviceTypeAndName(List<String> areaList);

	/** 获得所有的设备类型 */
	public List<DeviceType> getAllDeviceTypeAndName(List<String> areaList);

	public List<DeviceType> getDeviceTypeSummary(List<String> areaList);

	/** 根据设备类型的id获得设备类型名 */
	public String getDeviceNameByType(String deviceType);

	/** 获得设备状态 */
	public List<DeviceNetState> getDeviceNetState(List<String> areaList);

	public List<DeviceNetState> getDeviceNetState();

	public StaticsAnalysEntity getAllElectricinfo(List<String> areaList,
			String areaIdstr, String StartDate, String EndDate);
	public List<SmokeElectricInfoEntity> getAllElectricinfo(List<String> areaList,
			List<String> areaIdstr, String StartDate, String EndDate,List<SmokeBean> lstSmoke);

	public List<SmokeElectricInfoEntity> getElectricinfo(String smokeMac,
			String areaIdstr, String StartDate, String EndDate);
	
	public List<Water> getWaterInfo(String waterMac,String start_Time,String end_Time);
	
	public List<WaterEntity> getWaterInfoByAreaId(String areaid,String waterMac,String start_Time,String end_Time);
	
	public List<WaterEntity> getWaterInfoByAreaId(String areaid,String waterMac,String start_Time,String end_Time,int deviceType);
	
	// 水压设备进行统计分析
	public List<WaterEntity> getWaterInfoAll(String areaid,String waterMac,String start_Time,String end_Time);
	// 水位设备进行统计分析
	public List<WaterEntity> getWaterLeveInfoAll(String areaid,String waterMac,String start_Time,String end_Time);
	
	//根据设备获取当天的水压记录
	public WaterEntity getWaterOfToday(String  waterMac);
	//根据设备mac获取当天的水位记录
	public WaterEntity getWaterLeveOfToday(String waterMac);
	
	public WaterEntity getAlarmRecord(String mac);
	
	/**
	 *  查询NB烟感数据
	 */
	public int getNBSmokeInfoCount(List<String> areaList, SmokeQuery query);
	

	public List<DeviceCostEntity> getAllSmoke();


	public int getSmokeListCount(List<String> areaList, SmokeQuery query);

	

	public int updateCostByMac(String mac,Double cost);
	
	public DeviceCostEntity selectSmokeCost(String mac);
	
	

	public List<SmartControlEntity> getSmokeList(List<String> areaList,SmokeQuery query);
	
	public int countNBWater(List<String> areaList, SmokeQuery query);
	
	public List<SmartControlEntity> NBWaterList(List<String> areaList,SmokeQuery query);
	public List<ShopTypeEntity> getPlaceTypeNotType(String areaIdsStr);
	public List<AreaAndRepeater> getAreaAndRepeaterNoType(String areaIdsStr);
	public List<DeviceType> getAllDeviceTypeAndName(String areaIdsStr);
	public List<DeviceNetState> getDeviceNetState(String areaIdsStr);
	public int getAllSmokeInfoCount(String areaIdsStr, SmokeQuery query);
	public List<SmartControlEntity> getAllSmokeInfo(String areaIdsStr,SmokeQuery query);
	public List<ShopTypeEntity> getPlaceType();
	
	List<AreaAndRepeater> getAreaByUser(String userId, String privilege);
	
	//未知
	List<SmartControlEntity> getUnknowList(List<String> areaIds,SmokeQuery query);
	
	int countUnknow(List<String> areaIds,SmokeQuery query);
	

}
