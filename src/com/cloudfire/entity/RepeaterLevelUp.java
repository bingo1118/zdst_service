package com.cloudfire.entity;

public class RepeaterLevelUp {
	private String repeaterMac;//主机MAC
	private String areaName;	//区域名称
	private String leveState;	//升级状态（-2：正常、 其他值为升级中 0、完成 -1、失败）
	
	public String getRepeaterMac() {
		return repeaterMac;
	}
	public void setRepeaterMac(String repeaterMac) {
		this.repeaterMac = repeaterMac;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public String getLeveState() {
		return leveState;
	}
	public void setLeveState(String leveState) {
		this.leveState = leveState;
	}
}
