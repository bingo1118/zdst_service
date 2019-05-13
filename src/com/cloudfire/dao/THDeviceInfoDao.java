package com.cloudfire.dao;

import com.cloudfire.entity.WaterAckEntity;

public interface THDeviceInfoDao {
	public void addTHDeviceInfo(String mac,String t,String h);
	
	//添加阈值NB水压水位、温湿度
	public void addWaterAckEntity(WaterAckEntity wae);
	
	//修改阈值NB水压水位、温湿度
	public void updateWaterAckEntity(WaterAckEntity wae);
	
	//修改阈值NB水压水位、温湿度
	public void updateWaterAckEntity(String mac,int deviceType,int waeValue);
	
	//修改阈值NB水压水位、温湿度
	public void updateWaterAckEntity(String mac,String deviceType,String waeValue);

}
