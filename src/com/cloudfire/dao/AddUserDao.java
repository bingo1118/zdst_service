package com.cloudfire.dao;

import java.util.List;

public interface AddUserDao {
	public boolean ifExitUser(String userId);
	public boolean ifExitUserArea(String areaId,String userId);
	public boolean addUser(String userId,int privilege,String name);
	public boolean addUser(String userId,int privilege,String name,String pwd);
	public boolean updateUser(String userId,int privilege,String name);
	public boolean addUserArea(String userId,Integer[] areaId);
	public boolean addUserArea(String userId,String areaId);
	public boolean deleteUserArea(String userId);
	public boolean updateUser(String username, int privilege, String name,
			String isCanCutEletr);
	
}
