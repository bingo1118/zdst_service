package com.cloudfire.entity;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author lzo
 *	yongzuoyu quyu erji chaxun de fengzhuanglei; 
 */
public class AreaBeanEntity {
	private String areaName;
	private int areaId;
	private int parentId;
	private int p_parentId;
	private String parentAreaName;
	private List<String> areaList;
	private Set<String> areaSet;
	private List<AreaBeanEntity> abeList;
	private Map<Integer,String> areaMap;
	
	public Map<Integer, String> getAreaMap() {
		return areaMap;
	}
	public void setAreaMap(Map<Integer, String> areaMap) {
		this.areaMap = areaMap;
	}
	public List<AreaBeanEntity> getAbeList() {
		return abeList;
	}
	public void setAbeList(List<AreaBeanEntity> abeList) {
		this.abeList = abeList;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public int getAreaId() {
		return areaId;
	}
	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public int getP_parentId() {
		return p_parentId;
	}
	public void setP_parentId(int p_parentId) {
		this.p_parentId = p_parentId;
	}
	public String getParentAreaName() {
		return parentAreaName;
	}
	public void setParentAreaName(String parentAreaName) {
		this.parentAreaName = parentAreaName;
	}
	public List<String> getAreaList() {
		return areaList;
	}
	public void setAreaList(List<String> areaList) {
		this.areaList = areaList;
	}
	public Set<String> getAreaSet() {
		return areaSet;
	}
	public void setAreaSet(Set<String> areaSet) {
		this.areaSet = areaSet;
	}
}
