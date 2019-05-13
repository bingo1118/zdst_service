package com.cloudfire.dao;

import java.util.List;
import java.util.Map;

public interface GetPushUserIdDao {
	public List<String> getPushAreaUserIdByMac(String mac);
	public List<String> getPushUserIdByMac(String mac);
	public List<String> getAllUser(String mac);
	public Map<String,String> getIosUser(List<String> userIdList);
	public List<String> getOneKeyUser(String mac);
	public List<String> getUserByRepeaterMac(String repeaterMac);
	public List<String> getMakeSurePushAreaUserIdByMac(String mac);
	public List<String> getAllRepeatUser(String mac);//@@2018.02.09 获取主机绑定用户
}
