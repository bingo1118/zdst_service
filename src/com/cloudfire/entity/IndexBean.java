package com.cloudfire.entity;

import java.io.Serializable;
import java.util.List;

public class IndexBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1039050702773110950L;
	private List<StatisticBean> areaSta;
	private List<StatisticBean> waterSta;
	private List<Integer> today;
	private List<Integer> yesterday;
	private List<FireBean> fireSta;
	public List<StatisticBean> getAreaSta() {
		return areaSta;
	}
	public void setAreaSta(List<StatisticBean> areaSta) {
		this.areaSta = areaSta;
	}
	public List<StatisticBean> getWaterSta() {
		return waterSta;
	}
	public void setWaterSta(List<StatisticBean> waterSta) {
		this.waterSta = waterSta;
	}
	public List<Integer> getToday() {
		return today;
	}
	public void setToday(List<Integer> today) {
		this.today = today;
	}
	public List<Integer> getYesterday() {
		return yesterday;
	}
	public void setYesterday(List<Integer> yesterday) {
		this.yesterday = yesterday;
	}
	public List<FireBean> getFireSta() {
		return fireSta;
	}
	public void setFireSta(List<FireBean> fireSta) {
		this.fireSta = fireSta;
	}
}
