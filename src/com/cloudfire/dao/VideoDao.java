package com.cloudfire.dao;

import java.util.List;
import java.util.Map;

import com.cloudfire.entity.VideosInArea;

public interface VideoDao {
	public String getVideoByMac(String mac);
	public List<VideosInArea> getVideosInArea(List<String> areaIds);
	public String  getIndexByName(String name);
	public boolean bindVideo(String cameraIndexCode, String smokeMac,String areaId);
	//存在监控点则修改，否则添加到video表
	public boolean updateVideo(String indexCode, String videoName, String areaId);
	//获取监控信息
	public Map<String, String> getVideoInfo(String seqNum);
	

	public boolean addVideoInfo(String id,String indexCode,String areaId,String name);
	public Map<String, String> getVideoInfoByIndexCode(String indexCode);
}
