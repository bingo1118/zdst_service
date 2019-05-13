package com.cloudfire.dao;

import java.util.List;
import java.util.Map;

import com.cloudfire.entity.VideosInArea;

public interface VideoDao {
	public String getVideoByMac(String mac);
	public List<VideosInArea> getVideosInArea(List<String> areaIds);
	public String  getIndexByName(String name);
	public boolean bindVideo(String cameraIndexCode, String smokeMac,String areaId);
	//���ڼ�ص����޸ģ�������ӵ�video��
	public boolean updateVideo(String indexCode, String videoName, String areaId);
	//��ȡ�����Ϣ
	public Map<String, String> getVideoInfo(String seqNum);
	

	public boolean addVideoInfo(String id,String indexCode,String areaId,String name);
	public Map<String, String> getVideoInfoByIndexCode(String indexCode);
}
