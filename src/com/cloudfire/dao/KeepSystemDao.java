package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.AreaAndRepeater;
import com.cloudfire.entity.KeepEntity;

public interface KeepSystemDao {
	public List<KeepEntity> getRepeaterData(String repeater,String smokeMac,List<String> areaIds);
	public List<KeepEntity> getRepeaterData(String repeater,String smokeMac,String areaid);
	public List<KeepEntity> getRepeaterDataBySmoke(String smokeName,String repeater,String areaid);
	public KeepEntity findSmokeBySmokeMac(String smokeMac);
	public int updateSmokeBySmokeMac(KeepEntity entity);
	public List<AreaAndRepeater> getAreaAndRepeater();
	public List<AreaAndRepeater> getAreaAndRepeater(String userId);
	public String getRepeaterOfSmoke(String smokeMac);
	public List<AreaAndRepeater> getAreaAndRepeaterByUserId(String userId);
}
