package com.cloudfire.entity;

import java.util.List;
import java.util.Map;

public class AreaBean {
	private String areaId="";
    private String areaName="";
    private String parentId="";
    private String parentName="";
    private Map<Integer,String> parentIds;
    
	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public Map<Integer, String> getParentIds() {
		return parentIds;
	}

	public void setParentIds(Map<Integer, String> parentIds) {
		this.parentIds = parentIds;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

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
}
