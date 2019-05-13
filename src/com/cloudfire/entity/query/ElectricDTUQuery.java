package com.cloudfire.entity.query;

import com.cloudfire.until.BaseQuery;

public class ElectricDTUQuery extends BaseQuery {
	
	private static final long serialVersionUID = 8221925989832621173L;
	
	private int id;
	private String smokeMac = "";
	private String electricValue1 = "";
	private String electricValue2 = "";
	private String electricValue3 = "";
	private String electricValue4 = "";
	private String electricTime = "";
	private String electricType = "";
	private String electricDevType = "";
	private String netstate = "";
	
	public String getNetstate() {
		return netstate;
	}
	
	public void setNetstate(String netstate) {
		this.netstate = netstate;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSmokeMac() {
		return smokeMac;
	}
	public void setSmokeMac(String smokeMac) {
		this.smokeMac = smokeMac;
	}
	public String getElectricValue1() {
		return electricValue1;
	}
	public void setElectricValue1(String electricValue1) {
		this.electricValue1 = electricValue1;
	}
	public String getElectricValue2() {
		return electricValue2;
	}
	public void setElectricValue2(String electricValue2) {
		this.electricValue2 = electricValue2;
	}
	public String getElectricValue3() {
		return electricValue3;
	}
	public void setElectricValue3(String electricValue3) {
		this.electricValue3 = electricValue3;
	}
	public String getElectricValue4() {
		return electricValue4;
	}
	public void setElectricValue4(String electricValue4) {
		this.electricValue4 = electricValue4;
	}
	public String getElectricTime() {
		return electricTime;
	}
	public void setElectricTime(String electricTime) {
		this.electricTime = electricTime;
	}
	public String getElectricType() {
		return electricType;
	}
	public void setElectricType(String electricType) {
		this.electricType = electricType;
	}
	public String getElectricDevType() {
		return electricDevType;
	}
	public void setElectricDevType(String electricDevType) {
		this.electricDevType = electricDevType;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
