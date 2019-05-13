package com.cloudfire.entity;

import java.util.List;

public class AreaAndRepeater {
	private int areaId;
	private String areaName = "";
	private int smokeNumber = 0;// 某一个区域设备的数量
	private int lossSmokeNumber = 0;//某一个区域掉线数量
	private int onLineSmokeNumber = 0;//某一个区域在线总数
	private int smokeNumbers = 0;// 所有设备的数量 
	private int lossSmokeNumbers = 0;//所有区域掉线总数
	private int onLineSmokeNumbers = 0;//所有区域在线总数
	private int id;
	private List<AreaAndRepeater> list;
	private List<String> listEaraId;
	private int parentId;
	private int p_parentId;
	private String parentAreaName;

	public int getSmokeNumbers() {
		return smokeNumbers;
	}

	public void setSmokeNumbers(int smokeNumbers) {
		this.smokeNumbers = smokeNumbers;
	}

	public int getLossSmokeNumbers() {
		return lossSmokeNumbers;
	}

	public void setLossSmokeNumbers(int lossSmokeNumbers) {
		this.lossSmokeNumbers = lossSmokeNumbers;
	}

	public int getOnLineSmokeNumbers() {
		return onLineSmokeNumbers;
	}

	public void setOnLineSmokeNumbers(int onLineSmokeNumbers) {
		this.onLineSmokeNumbers = onLineSmokeNumbers;
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

	public List<String> getListEaraId() {
		return listEaraId;
	}

	public void setListEaraId(List<String> listEaraId) {
		this.listEaraId = listEaraId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<AreaAndRepeater> getList() {
		return list;
	}

	public void setList(List<AreaAndRepeater> list) {
		this.list = list;
	}

	public int getLossSmokeNumber() {
		return lossSmokeNumber;
	}

	public void setLossSmokeNumber(int lossSmokeNumber) {
		this.lossSmokeNumber = lossSmokeNumber;
	}

	public int getOnLineSmokeNumber() {
		return onLineSmokeNumber;
	}

	public void setOnLineSmokeNumber(int onLineSmokeNumber) {
		this.onLineSmokeNumber = onLineSmokeNumber;
	}

	private List<String> listMac = null;

	public int getAreaId() {
		return areaId;
	}

	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public List<String> getListMac() {
		return listMac;
	}

	public void setListMac(List<String> listMac) {
		this.listMac = listMac;
	}

	public int getSmokeNumber() {
		return smokeNumber;
	}

	public void setSmokeNumber(int smokeNumber) {
		this.smokeNumber = smokeNumber;
	}

}
