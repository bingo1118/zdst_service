package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.ElectricHistoryEntity;

public interface GetSmokeMacByRepeaterDao {
	public List<String> getSmokeMacByRepeater(String repeaterMac);
	
	public List<String> getSmokeOnLineMacByRepeater(String repeaterMac);
	
	public String getRepeaterMacBySmokeMac(String smokeMac);
	
	public void updateSmokeInfo(String smokeMac,int eleState);
	
	/**
	 * tianjia dianli xiaxing kongzhi lishijilu
	 * @param ehe
	 */
	public void insert_Electric_change_history(ElectricHistoryEntity ehe);
	
	/**
	 * get eleNeedHis info
	 */
	public List<ElectricHistoryEntity> getElectricHistory(String mac,String page);

	//@@ update eledev elecState by 2018-09-26
	public void chanageElectric(String smokeMac, int eleState);
}
