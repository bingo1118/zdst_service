package com.cloudfire.dao;

import com.cloudfire.entity.HttpRsult;

public interface DealAlarmDao {
	public HttpRsult dealAlarm(String userId,String dealPeople,String mac,String dealDetail);
	public HttpRsult dealAlarm(String userId,String dealPeople,String mac,String dealDetail,String alarmTruth);
	public HttpRsult dealAlarm(String userId,String mac); 
	public HttpRsult dealAlarmDetail(String userId,String mac,String dealPeople,String alarmTruth,String dealDetail, String image_path, String video_path);
	
	public HttpRsult dealAlarm(String userId);
	public HttpRsult dealAlarmDetail(String userId,String dealPeople,String alarmTruth,String dealDetail);
	
}
