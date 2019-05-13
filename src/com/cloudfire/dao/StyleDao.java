package com.cloudfire.dao;

import com.cloudfire.entity.Style;

public interface StyleDao {
	
	boolean isExistUser(String userId);
	
	Style getStyle(String userId);

}
