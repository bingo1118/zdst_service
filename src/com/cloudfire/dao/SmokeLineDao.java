package com.cloudfire.dao;

import java.util.List;
import java.util.Map;

import com.cloudfire.entity.Repeater;

public interface SmokeLineDao {
	public void RepeaterOffLine(String repeaterMac,int netState);
	public void allRepeaterOffLine();
	public void getAllRepeater();
	public void smokeOffLine(String smokeMac,int netState);
	public  void smokeLossUp(String repeaterMac,String smakeMac,String lossUp);
	
	public void RepeaterOnLine(String repeaterMac,Map<String,Long> list);
	public Map<String,Long> RepeaterLoss(String repeaterMac);
	public void setElectrState(String smokeMac,int state);//@@10.31
	public int updateHeartime(String mac);
	public List<Repeater> getAllRepeaters();
	
	//更新主机下的设备状态  测试字段
	public void RepeaterOffLine2(String repeaterMac,int state);
	//更新设备的状态 测试字段
	public void smokeOffLine2(String mac, int state);
	public void smokeOffLine3(List<String> offline, int netState);
}
