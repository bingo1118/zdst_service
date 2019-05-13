package com.cloudfire.dao;

import com.cloudfire.entity.HttpRsult;

public interface AddCameraDao {
	//cameraId=13622215085&cameraName=&cameraPwd=&cameraAddress=&longitude=&latitude=&principal1=&principal1Phone=&principal2=&principal2Phone=&areaId=&placeTypeId=
	public HttpRsult addCamera(String cameraId,String cameraName,String cameraPwd,String cameraAddress,
			String longitude,String latitude,String principal1,String principal1Phone,String principal2,
			String principal2Phone,String areaId,String placeTypeId,String videoPositon,String videoSize);
	public boolean isExited(String cameraId);
}
