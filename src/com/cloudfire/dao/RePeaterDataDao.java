package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.DeviceAlarmEntity;
import com.cloudfire.entity.RePeaterData;
import com.cloudfire.entity.RepeaterBean;

public interface RePeaterDataDao {
	/**
	 * 根据MAC地址查询所属设备类型
	 * 返回int
	 */
	public int getDeviceType(String mac);
	
	/**
	 * 根据MAC地址查询所属设备类型
	 * 返回一个对象
	 */
	public DeviceAlarmEntity getDeviceEntity(String mac);
	
	/**
	 * 根据MAC地址查询所属设备型号
	 * 返回int
	 */
	public int getDeviceTypeNum(String mac);
	
	/**
	 * 根据心跳，存储主机主备电状态
	 */
	public void saveRepeaterInfo(RePeaterData repeaterData);
	
	/**
	 * 用于显示终端信息，包括在线，心跳，主备电状态
	 */
	public List<RepeaterBean> queryRepeaterInfo(RepeaterBean query);
	
	
	int countRepeaterInfo(RepeaterBean query);
	
	/**
	 * 用于添加终端最后心跳时间
	 */
	public void addRepeaterTime(String repeaterMac);
	
	/**
	 * 用于添加终端最后心跳时间
	 */
	public void addRepeaterTime(String repeaterMac,String address);
	
	/**
	 * 根据终端信息返回终端最后心跳时间。
	 */
	public String getRepeaterTime(String repeaterMac);
	
	/**
	 * 根据终端返回ip
	 * @param repeaterMac
	 * @return
	 */
	public String getIpByRepeater(String repeaterMac);
	
	
}
