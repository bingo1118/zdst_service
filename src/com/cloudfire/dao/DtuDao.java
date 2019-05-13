package com.cloudfire.dao;

public interface DtuDao {

	 // dtu设备信息入库smoke
	public int updateDtu(String imei,String hearttime,int netState); 
	
	//dtu采集的数据信息入库waterinfo
	public int addDtuData(String imei,int state,float value,int unit,String time);
	
	//将报警数据存入数据库alarm
	public int addAlarmMsg(String imei,String time,int alarmType);
	
	//根据imei获取最近的数据
//	public double getValueByMac(String imei);
	
	//查询dtudatainfo表的信息
//	public List<DtuDataGroup> getDtuData()
}
