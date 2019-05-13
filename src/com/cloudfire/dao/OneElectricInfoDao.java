package com.cloudfire.dao;

import java.util.List;
import java.util.Map;

import com.cloudfire.entity.ElectricEnergyEntity;
import com.cloudfire.entity.OneChuangAnEntity;
import com.cloudfire.entity.OneElectricEntity;
import com.cloudfire.entity.OneTHDeviceInfoEntity;
import com.cloudfire.entity.ThreePhaseElectricEntity;

public interface OneElectricInfoDao {
//	userId=13622215085&privilege=&smokeMac=59F8250A
	public OneElectricEntity getOneElectricInfo(String smokeMac);
	public OneElectricEntity getOneElectricInfo(String smokeMac,String devType);
	
	public int getElectricNum(String smokeMac);
	public Map<Integer,List<String>> getThresholdValues(String smokeMac);
	public OneChuangAnEntity getOneChuangAnEntity(String userId,String privilege, String smokeMac);
	public OneTHDeviceInfoEntity getOneTHDevEntity(String smokeMac);
	
	/**
	 * @param smokeMac ����MAC��ȡNB������������������ 
	 * @return
	 */
	public ThreePhaseElectricEntity getThreePhaseElectricEntity(String smokeMac);
	
	
	/**
	 * @param smokeMac ����MAC��ȡ���ص�������������� 
	 * @return
	 */
	public ElectricEnergyEntity getElectricEnergyEntity(String smokeMac);
	

	/**
	 * @param smokeMac ����MAC��ȡ���ص������� 
	 * @return
	 */
	public ElectricEnergyEntity getElectricEnergyEntitys(String smokeMac,String page);
	
}
