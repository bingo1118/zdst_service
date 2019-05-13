package com.cloudfire.dao;

import java.util.List;
import java.util.Map;

public interface IfDealAlarmDao {
	public List<String> getUserSmokeMac(List<String> listNum);
	public List<String> getLostSmokeMac(List<String> listNum);
//	public Map<String,Integer> getIfDealAlarm(List<String> macList);
	public int getDealState(String smokeMac);
}
