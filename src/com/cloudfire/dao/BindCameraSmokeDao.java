package com.cloudfire.dao;

import com.cloudfire.entity.HttpRsult;

public interface BindCameraSmokeDao {
	//cameraId=13622215085&smoke=
	public HttpRsult bindCameraSmoke(String cameraId,String smoke,String cameraChannel);
	// by liao zw 2017.11.5 µç±í
	public  boolean  meterBindUserDevce(String user,String device);
}
