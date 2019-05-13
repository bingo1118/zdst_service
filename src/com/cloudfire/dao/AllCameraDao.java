package com.cloudfire.dao;

import java.util.List;
import java.util.Map;

import com.cloudfire.entity.AllCameraEntity;
import com.cloudfire.entity.CameraBean;
import com.cloudfire.entity.PushAlarmMsgEntity;


public interface AllCameraDao{
	public Map<String,CameraBean> getCameraByMac(List<String> listNum);
	public AllCameraEntity getAllCamera(String userId, String privilege,String page,int everypage);
	public CameraBean getCameraBySmokeMac(String smokeMac);
	public PushAlarmMsgEntity.CameraBean getCameraByCameraId(String cameraId);
	public AllCameraEntity getPrivi1Camera(String userId, String privilege,String page,int everypage);//@@权限1查询绑定摄像机
}
