package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.AllElectricInfoEntity;
import com.cloudfire.entity.Electric;

public interface AllElectricInfoDao {
	//userId=13622215085&privilege=&page=
	public AllElectricInfoEntity getAllElectricInfo(String userId,String privilege,String page);
	public List<Electric> getAllElectricInfo(String userId,String privilege);
	//userId=13622215085&privilege=&areaId=&placeTypeId=&page=
	public AllElectricInfoEntity getNeedElectricInfo(String userId,String privilege,String parentId,String page,String areaId,String placeTypeId);
	
}
