package com.cloudfire.dao;

import com.cloudfire.entity.OneSmokeEntity;

public interface OneSmokeDao {
	//userId=13622215085&smokeMac=19381731&privilege=
	public OneSmokeEntity getOneSmoke(String userId,String smokeMac,String privilege);
}
