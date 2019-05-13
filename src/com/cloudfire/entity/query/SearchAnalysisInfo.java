package com.cloudfire.entity.query;

import java.util.List;

import com.cloudfire.entity.AreaAndRepeater;

/**
 * @author cheng
 *2017-4-14
 *ÏÂÎç4:31:30
 */
public class SearchAnalysisInfo {
	
	private String deviceName="";
	private List<String> list=null;
	
	
	private String year="";
	private String month="";
	private String totalDeviceType;
	
	private int smokeNumber;
	private int lossNumber;
	private int onLineNumber;
/*	private String areaName = "";*/
	private List<AreaAndRepeater> list2 = null;
	
	
	
	
	

	public List<AreaAndRepeater> getList2() {
		return list2;
	}
	public void setList2(List<AreaAndRepeater> list2) {
		this.list2 = list2;
	}
	public int getSmokeNumber() {
		return smokeNumber;
	}
	public void setSmokeNumber(int smokeNumber) {
		this.smokeNumber = smokeNumber;
	}
	public int getLossNumber() {
		return lossNumber;
	}
	public void setLossNumber(int lossNumber) {
		this.lossNumber = lossNumber;
	}
	public int getOnLineNumber() {
		return onLineNumber;
	}
	public void setOnLineNumber(int onLineNumber) {
		this.onLineNumber = onLineNumber;
	}
/*	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}*/
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public List<String> getList() {
		return list;
	}
	public void setList(List<String> list) {
		this.list = list;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getTotalDeviceType() {
		return totalDeviceType;
	}
	public void setTotalDeviceType(String totalDeviceType) {
		this.totalDeviceType = totalDeviceType;
	}
	
	
	
}
