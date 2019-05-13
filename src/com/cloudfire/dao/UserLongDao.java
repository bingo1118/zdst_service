package com.cloudfire.dao;

import com.cloudfire.entity.HttpRsultUser;
import com.cloudfire.entity.UserEntity;

public interface UserLongDao {
	public UserEntity getUserInfoByUserId(String userId);
	public boolean updateUserTxtState(String userid,int txtState);
	
	public HttpRsultUser getHostInfo(String host); 
	
}
