package com.cloudfire.entity;

public class BuildingBean {
	private String buildingName;
	private CountValue cv; //
	private String longtitude;
	private String latitude;
	public String getBuildingName() {
		return buildingName;
	}
	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}
	public CountValue getCv() {
		return cv;
	}
	public void setCv(CountValue cv) {
		this.cv = cv;
	}
	public String getLongtitude() {
		return longtitude;
	}
	public void setLongtitude(String longtitude) {
		this.longtitude = longtitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
}
