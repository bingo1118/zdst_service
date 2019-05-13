package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.ElectricBean;
import com.cloudfire.entity.ElectricEnergyEntity;
import com.cloudfire.entity.ElectricInfo;
import com.cloudfire.entity.ProofGasHistoryEntity;
import com.cloudfire.entity.ThreePhaseElectricEntity;

public interface ElectricTypeInfoDao {
	//userId=13622215085&privilege=&smokeMac=59F8250A&electricType=6&electricNum=&page=1
	public ElectricInfo getElectricTypeInfo(String userId,String privilege,String smokeMac,
			String electricType,String electricNum,String page);
	
	public ElectricInfo getThreeElectricTypeInfo(String userId,String privilege,String smokeMac,
			String electricType,String electricNum,String page,String devType);
	
	
	public void addElectricInfo(List<String> list6,List<String> list7,String list8,
			List<String> list9,String electricMac,String repeaterMac,int electricDevType);
	public void addAlarmElectric(List<String> list,String electricMac,String repeaterMac,
			int electricDevType,int electricType);
	public ElectricInfo getElectricPCTypeInfo(String userId,String privilege,String smokeMac,
			String electricType,String electricNum,String page,String electricDate);
	public ElectricInfo<ElectricBean> getWaterHistoryInfo(String userId,
			String privilege, String smokeMac, String page);//@@2017.12.15
	public void addThreePhaseElectricInfo(String data, String electricMac,
			String repeaterMac, int data_type,int data_num);//@@2018.01.16
	public void addThreePhaseElectricInfo(ThreePhaseElectricEntity threeElectric);
	public ElectricInfo getChuanganHistoryInfo(String userId, String privilege,
			String smokeMac, String page, String electricNum);//@@2018.04.10
	public ElectricInfo getTHDevInfoHistoryInfo(String smokeMac, String page,
			String type);
	public ProofGasHistoryEntity getProofGasHistoryInfo(String userId, String privilege,
			String smokeMac, String page);
	
	/**
	 * U�ص����������ݴ���
	 */
	public boolean saveElectricEnergyEntity(ElectricEnergyEntity eee);
	
	/**
	 * U�ص����������ݴ���
	 */
	public boolean saveElectricEnergyEntity(ElectricEnergyEntity eee,String hearTime);
	
	/**
	 * ��ȡU�ص��������������һ��
	 */
	public boolean getElectricEnergyEntity(ElectricEnergyEntity eee);
	
	/**
	 * ��ȡU�ص����������ݼ���
	 */
	public boolean getElectricEnergyEntitys(ElectricEnergyEntity eee);
	
}
