package com.cloudfire.entity;

import java.util.List;
import java.util.Map;

public class AllAreaEntity {
	
	private Map<Integer,String> parentIds;
	private List<AreaBean> areaBean;
	private String error="ªÒ»° ß∞‹";
    private int errorCode=2;
    private List<Area> areas=null;
    
	public Map<Integer, String> getParentIds() {
		return parentIds;
	}
	public void setParentIds(Map<Integer, String> parentIds) {
		this.parentIds = parentIds;
	}
	public List<AreaBean> getAreaBean() {
		return areaBean;
	}
	public void setAreaBean(List<AreaBean> areaBean) {
		this.areaBean = areaBean;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public List<Area> getAreas() {
		return areas;
	}
	public void setAreas(List<Area> areas) {
		this.areas = areas;
	}
}
