/**
 * 上午11:32:37
 */
package com.cloudfire.entity;

import java.util.List;

/**
 * @author cheng
 *2017-6-8
 *上午11:32:37
 */
public class Area {
	private String areaId="";
    private String areaName="";
    private String province="";
    private String city="";
    private String town="";
//    private String currentId="";
    private List<Area> areas=null;//@@8.31 二级区域列表
    private int isParentArea=0;//@@是否为父区域 0否1是
	public String getAreaId() {
		return areaId;
	}
	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getTown() {
		return town;
	}
	public void setTown(String town) {
		this.town = town;
	}
//	public String getCurrentId() {
//		return currentId;
//	}
//	public void setCurrentId(String currentId) {
//		this.currentId = currentId;
//	}
	public List<Area> getAreas() {
		return areas;
	}
	public void setAreas(List<Area> areas) {
		this.areas = areas;
	}
	public int getIsParentArea() {
		return isParentArea;
	}
	public void setIsParentArea(int isParentArea) {
		this.isParentArea = isParentArea;
	}
    
    
}
