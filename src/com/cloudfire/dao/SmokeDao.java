package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.SmokeInfo;

public interface SmokeDao {
	public List<SmokeBean> getSmokesByAreaid(int areaid);
	
	public SmokeBean getSmokeByMac(String mac);
	
	public SmokeBean getSmokeByMac2(String mac);
	
	public SmokeBean getSmokeByMac3(String mac);
	
	public int getDeviceTypeByMac(String mac);
	
	public  List<Integer> getDevices(String userId,int privilege);
	
	public String getHeartTimeByMac(String mac);
	
	public boolean updateHeartTime(String mac);

	public boolean deleteDevFromSmoke(String smokeMac); 
	/**
	 * 将获取的数据添加到数据库中
	 * @param s
	 * @return
	 */
	public boolean addOrUpdateSmoke(SmokeInfo s);
	/**
	 * 查询客户平台数据总数
	 * @return
	 */
	public int queryCountCustomerSmoke();
	/**
	 * 查询是否显示客户平台数据的参数
	 * @return
	 */
	public int selectIfShow();
	/**
	 * 修改是否显示客户平台数据的参数
	 * @param ifShow
	 * @return
	 */
	public boolean updateIfShow(int ifShow);

	public List<SmokeBean> getSmokesByBuilding(String buildingname);
	
	public String getDeviceTypeNameByType(int deviceType);

	public String getAlarmNameByAlarmType(int alarmtype);

	public int getDeviceMiniType(int deviceType);
}
