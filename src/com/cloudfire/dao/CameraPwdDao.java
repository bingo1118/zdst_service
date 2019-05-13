package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.CameraBean;
import com.cloudfire.entity.HttpRsult;

public interface CameraPwdDao {
	//cameraId=2121638&cameraPwd=123
	public HttpRsult changeCameraPwd(String cameraId,String cameraPwd);
	
	public List<CameraBean> getCameraByAreaId(List<String> areaList); 
}
