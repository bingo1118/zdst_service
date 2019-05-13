package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.AllSmokeEntity;
import com.cloudfire.entity.EnvironmentEntity;
import com.cloudfire.entity.EnvironmentHistoryMsgEntity;
import com.cloudfire.entity.SmokeBean;

public interface EnvironmentDao {
	/**
	 * 根据封装的环境探测器透传命令得到数据包来增加或修改数据。
	 * @param environment
	 */
	public void addEnvironmentInfo(EnvironmentEntity environment);
	
	/**
	 * 获取设备类型为11-12-13的设备信息
	 * @return
	 */
	public AllSmokeEntity getNotSmokeMac(String userId, String privilege,String page);
	
	public AllSmokeEntity getSuperNotSmokeMac(String userId, String privilege,String page);
	
	/**
	 * 获取
	 * @param airMac
	 * @return
	 */
	public EnvironmentEntity getEnvironmentEntityInfo(String airMac);
	
	/**
	 * get 7 day data
	 */
	public EnvironmentHistoryMsgEntity getHistoryData(String deviceMac,String timeStart,String timeEnd,int type);
}
