package com.cloudfire.entity;

public class StatisticAnalysisEntity {
	private int parentId;
	private int areaid;
	private String areaName;
	private int onNetState;
	private int lossNetState;
	private int areaSum;
	
	public int getAreaSum() {
		return areaSum;
	}
	public void setAreaSum(int areaSum) {
		this.areaSum = areaSum;
	}
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public int getAreaid() {
		return areaid;
	}
	public void setAreaid(int areaid) {
		this.areaid = areaid;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public int getOnNetState() {
		return onNetState;
	}
	public void setOnNetState(int onNetState) {
		this.onNetState = onNetState;
	}
	public int getLossNetState() {
		return lossNetState;
	}
	public void setLossNetState(int lossNetState) {
		this.lossNetState = lossNetState;
	}
	
}
