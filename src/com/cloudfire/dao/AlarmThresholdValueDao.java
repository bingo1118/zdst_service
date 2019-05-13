package com.cloudfire.dao;

public interface AlarmThresholdValueDao {
	public void addThresholdValue(String smokeMac,String value1,String value2,String value3,String value4,
			String repeaterMac,int alarmFamily);
	public boolean ifExitValue(String smokeMac,int alarmFamily);
}
