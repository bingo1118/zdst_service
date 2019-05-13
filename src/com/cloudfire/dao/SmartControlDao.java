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
	 * @param NB��ѯ����������Ϣ
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
	//Ƿ���豸
	public int getSmokeInfoCountByDeviceType(List<String> areaList,
			SmokeQuery query);
	//�����豸
	public int getSmokeInfoCountByDeviceType2(List<String> areaList,
			SmokeQuery query);
	
	public List<SmartControlEntity> getAllDeviceInfo(List<String> areaList);

	public int getAllSmokeInfoCount(List<String> areaList, SmokeQuery query);
	
	public int getAllSmokesInfoCount(List<String> areaList, SmokeQuery query);

	public int getSmokeInfoCount(List<String> areaList, SmokeQuery query);

	/** ����type������������ѯ */
	public List<SearchElectricInfo> searchDeviceByTypeQuery(
			SearchElectricInfo info, List<String> areaList);

	public int searchCountDeviceByTypeQuery(SearchElectricInfo info,
			List<String> areaList);

	/** ��õص������ */
	public List<ShopTypeEntity> getPlaceType(String type, List<String> areaList);
	public List<ShopTypeEntity> getPlaceType(String type, String devType, List<String> areaList);

	public List<ShopTypeEntity> getPlaceTypeNotType(List<String> areaList);

	/** ������� */
	public List<AreaAndRepeater> getAreaAndRepeater(List<String> aids);
	
	public List<AreaAndRepeater> getAreaAndRepeater(String type,List<String> areaList);
	
	public List<AreaAndRepeater> getAreaAndRepeater(String type,String devType,List<String> areaList);

	/** ������򣬲����豸���� */
	public List<AreaAndRepeater> getAreaAndRepeaterNoType(List<String> areaList);

	public List<AreaAndRepeater> getAreaAndRepeaterNoType();

	/** ���������̸����� */
	public List<AreaAndRepeater> getAreaAndRepeaterAndSmokeNumber(
			List<String> areaList, String type);

	public List<AreaAndRepeater> getAreaAndRepeaterAndSmoke(
			List<String> areaList);
	
	/*
	 * �Ż����
	 */

	public List<AreaAndRepeater> getAreaAndSmokeInfo(
			List<String> areaList);
	
	
	/** ����豸���� */
	public List<DealOkAlarmEntity> getDeviceTypeAndName(List<String> areaList);

	/** ������е��豸���� */
	public List<DeviceType> getAllDeviceTypeAndName(List<String> areaList);

	public List<DeviceType> getDeviceTypeSummary(List<String> areaList);

	/** �����豸���͵�id����豸������ */
	public String getDeviceNameByType(String deviceType);

	/** ����豸״̬ */
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
	
	// ˮѹ�豸����ͳ�Ʒ���
	public List<WaterEntity> getWaterInfoAll(String areaid,String waterMac,String start_Time,String end_Time);
	// ˮλ�豸����ͳ�Ʒ���
	public List<WaterEntity> getWaterLeveInfoAll(String areaid,String waterMac,String start_Time,String end_Time);
	
	//�����豸��ȡ�����ˮѹ��¼
	public WaterEntity getWaterOfToday(String  waterMac);
	//�����豸mac��ȡ�����ˮλ��¼
	public WaterEntity getWaterLeveOfToday(String waterMac);
	
	public WaterEntity getAlarmRecord(String mac);
	
	/**
	 *  ��ѯNB�̸�����
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
	
	//δ֪
	List<SmartControlEntity> getUnknowList(List<String> areaIds,SmokeQuery query);
	
	int countUnknow(List<String> areaIds,SmokeQuery query);
	

}
