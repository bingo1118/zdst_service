package com.cloudfire.dao;

import com.cloudfire.entity.WaterAckEntity;

public interface THDeviceInfoDao {
	public void addTHDeviceInfo(String mac,String t,String h);
	
	//�����ֵNBˮѹˮλ����ʪ��
	public void addWaterAckEntity(WaterAckEntity wae);
	
	//�޸���ֵNBˮѹˮλ����ʪ��
	public void updateWaterAckEntity(WaterAckEntity wae);
	
	//�޸���ֵNBˮѹˮλ����ʪ��
	public void updateWaterAckEntity(String mac,int deviceType,int waeValue);
	
	//�޸���ֵNBˮѹˮλ����ʪ��
	public void updateWaterAckEntity(String mac,String deviceType,String waeValue);

}
