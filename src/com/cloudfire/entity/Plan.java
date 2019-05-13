package com.cloudfire.entity;

import java.util.List;

public class Plan {
	private int planid;
	private int areaid;
	private String areaName;
	private String planPath;
	private List<PlanPoint> lstpp;
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public int getPlanid() {
		return planid;
	}
	public void setPlanid(int planid) {
		this.planid = planid;
	}
	public int getAreaid() {
		return areaid;
	}
	public void setAreaid(int areaid) {
		this.areaid = areaid;
	}
	public String getPlanPath() {
		return planPath;
	}
	public void setPlanPath(String planPath) {
		this.planPath = planPath;
	}
	public List<PlanPoint> getLstpp() {
		return lstpp;
	}
	public void setLstpp(List<PlanPoint> lstpp) {
		this.lstpp = lstpp;
	}
}
