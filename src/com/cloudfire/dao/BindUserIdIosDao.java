package com.cloudfire.dao;

import com.cloudfire.entity.HttpRsult;

public interface BindUserIdIosDao {
	//bindUserIdIos?userId=%@&ios=%@
	public HttpRsult bindUserIdIos(String userId,String ios);
	public int findUserId(String userId,String ios);
}
