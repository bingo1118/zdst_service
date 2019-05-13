package com.cloudfire.dao;

import java.util.List;
import java.util.Map;

import com.cloudfire.entity.DeviceStateEntity;
import com.cloudfire.entity.SmartControlEntity;
import com.cloudfire.entity.query.SmokeQuery;

public interface DeviceStateDao {
	public List<DeviceStateEntity> getDeviceStates(String user,List<String> aids);
	
	public Map<String,Map<Integer,String>> getDeviceStateToMap(List<String> aids);
	
	public int getAllDeviceCount(List<String> areaList, SmokeQuery query);
	
	public List<SmartControlEntity> getAllDeviceInfo(List<String> areaList,SmokeQuery query);
	
	
}
