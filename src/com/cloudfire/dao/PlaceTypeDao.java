package com.cloudfire.dao;

import java.util.Map;

import com.cloudfire.entity.AreaIdEntity;
import com.cloudfire.entity.NFCDeviceTypeEntity;
import com.cloudfire.entity.PlaceTypeEntity;

public interface PlaceTypeDao {
	public PlaceTypeEntity getAllShopType(String page);
	public Map<String, String> getShopTypeById();
	
	public NFCDeviceTypeEntity getAllDeviceType();
	public AreaIdEntity getNeedAdministrationInfo(String type,
			String father_code);
	public PlaceTypeEntity getAllShopTypeByUserId(String userId);
}
