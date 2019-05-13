package com.cloudfire.entity;

import java.util.Map;

public class AreaParentEntity {
    private String parentId="";  //一级区域id
    private String parentName="";	//一级区域名称
    private Map<Integer,String> areaIds;	//二级区域ID--二级区域名称
    
	public Map<Integer, String> getAreaIds() {
		return areaIds;
	}

	public void setAreaIds(Map<Integer, String> areaIds) {
		this.areaIds = areaIds;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

}
