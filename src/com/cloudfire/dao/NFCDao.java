package com.cloudfire.dao;

import com.cloudfire.entity.NFCInfoEntity;

public interface NFCDao {
	public int isExited(String uid);
	
	public NFCInfoEntity getNFCInfo(String mac);
}
