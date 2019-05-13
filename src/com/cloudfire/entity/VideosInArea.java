package com.cloudfire.entity;

import java.util.List;

public class VideosInArea {
	private int areaId;
	private String areaName;
	private List<Camera> cameras;
	public int getAreaId() {
		return areaId;
	}
	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public List<Camera> getCameras() {
		return cameras;
	}
	public void setCameras(List<Camera> cameras) {
		this.cameras = cameras;
	}
	
}
