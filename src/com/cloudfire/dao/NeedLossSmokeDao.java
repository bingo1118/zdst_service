package com.cloudfire.dao;

import com.cloudfire.entity.AllSmokeEntity;

public interface NeedLossSmokeDao {
	//userId=&privilege=&areaId=&page=&placeTypeId=
//	public AllSmokeEntity getNeedLossSmoke(String userId,String privilege,String areaId,String page,String placeTypeId);
	public AllSmokeEntity getNeedLossSmoke(String userId,String privilege,String parentId,String areaId,String page,String placeTypeId,String appId);	//¸ü¸Ä lzo at 2017-5-25
}
