package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.Expire;

public interface ExpireDao {
	public Expire getExpireByRegisterCode(String registerCode);
	public int updateExpire(String cur_innerIp,String cur_outerIp,String validTime,int state,String registerCode);
	public int updateExpire(String innerIp,String cur_outerIp,String validTIme,String registerCode);
	public int updateExpire(String registeCode,int state);
	public int updateValidTime(String validTime, String registerCode);
	public List<Expire> getAllExpire();
}
