package com.cloudfire.entity;

import java.util.Map;

public class AreaParentEntity {
    private String parentId="";  //һ������id
    private String parentName="";	//һ����������
    private Map<Integer,String> areaIds;	//��������ID--������������
    
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
