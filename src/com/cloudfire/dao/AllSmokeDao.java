package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.AllRepeaterEntity;
import com.cloudfire.entity.AllSmokeEntity;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.meter.MeterInfoEntity;
import com.cloudfire.entity.meter.MeterInfoHttpEntity;
import com.cloudfire.entity.meter.MeterReadingHttpEntity;

public interface AllSmokeDao {
	public AllSmokeEntity getAdminAllSmoke(String userId,String privilege,String page);
	public AllSmokeEntity getSuperAllSmoke(String userId,String privilege,String page);
	public List<String> getNormalAllSmoke(String userId,String privilege,String page);
	public AllSmokeEntity getSuperAllSmokeBySearch(String userId,String privilege,String search);
	
	
	public AllSmokeEntity getAdminAllDevice(String userId,String privilege,String page);
	public AllSmokeEntity getSuperAllDevice(String userId,String privilege,String page);
	
	public AllSmokeEntity getAdminAllFaultinfo(String userId,String privilege,String page,String areaId);
	public AllSmokeEntity getSuperAllFaultinfo(String userId,String privilege,String page,String areaId);
	
	// by liao zw 2017.11.6 
	public MeterInfoHttpEntity getMeterDeviceByUser(String user);
	public MeterInfoEntity getMeterInfoByMac(String mac);
	public MeterReadingHttpEntity getMeterReadingByMac(String mac);
	public HttpRsult elecMeterSetSettingByMac(String mac,String voltage,String electricity,String power);
	
	/**
	 * 获取所有主机信息
	 */
	public AllRepeaterEntity getAllRepeaterInfo(String userid,String privilege,String page,String search);
	public AllRepeaterEntity getAllRepeaterInfo(String privilege,String page,String search);
	
	
	/*
	 * 根据地区id和设备状态获取设备列表 by yfs
	 */
	public List<SmokeBean> getSmokeList(String areaId,String state);
	public List<SmokeBean> getSmokeList(String areaId);
	
	/*
	 * 根据设备类型，设备状态获取设备列表 by yfs @18/03/14
	 */
	public List<SmokeBean> getSmokeList(int deviceType,int netState,List<String> areaIds);
	public AllSmokeEntity getSuperAllZDSTDev(String userId, String privilege,
			String page, String devType);
	public AllSmokeEntity getAdminAllZDSTDev(String userId, String privilege,
			String page, String devType);
}
