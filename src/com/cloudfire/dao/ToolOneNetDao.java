package com.cloudfire.dao;

import com.cloudfire.action.MsgOneNetEntity;
import com.cloudfire.entity.OneNetEntity;


public interface ToolOneNetDao {
	
	/**
	 * 添加设备到OneNet平台
	 * 返回设备dev_id值
	 */
	public String insertOneNetDev(String imei,String imsi,String title,String deviceType);
	
	/**
	 * 根据IMEI、IMSI、值保存数据库
	 */
	public void addOneNetTable(String imei,String imsi,String devId);
	
	//保存设备的ds_id
	public void updateDs(String imei,String ds_id);
	
	//根据device_id获取设备的imei+ds_id
	public MsgOneNetEntity getMsgByDeviceId(String device_id);
	
	//根据device_id获取设备的imei+ds_id
	public MsgOneNetEntity getMsgByImei(String imei);
	
	/**
	 * 根据JSON解析返回对象值，包括设备devid,和value值，以及其他值
	 */
	public OneNetEntity getOneNetValues(String jsonString);
	
	/**
	 * 根据device_id 获取设备imei
	 * @param device_id
	 * @return
	 */
	public String getMacByDeviceId(String device_id);
	
	/**
	 * 删除OneNet平台上的设备
	 * @param imei
	 * @return
	 */
	public boolean delDeviceByMac(String imei);
	
	/**
	 * 根据imei获取设备的device_id
	 * @param imei
	 * @return
	 */
	public String getDeviceIdByImei(String imei);

	/**
	 * 消音命令
	 * @param imei 嘉德的移动烟感
	 * @param deviceType
	 * @return
	 */
	public String sendCmd(String imei, String deviceType);
	
	/**
	 * 保存设备电量信息，同时判断是否是电量回复
	 */
	public int updatePower(String mac,String power);
	
	//保存设备电量信息
	public int updatePower2(String mac,String power);

	//更新电量信息及低电量状态
	public int updatePower(String mac, String string, int voltageState);
}

