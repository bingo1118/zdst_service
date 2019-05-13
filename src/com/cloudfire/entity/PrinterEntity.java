package com.cloudfire.entity;

import java.util.Map;

public class PrinterEntity {
	private String faultCode;
	private String faultInfo;
	private String faultDevDesc;
	private String faultType ;
	private String repeater;
	private String faultTime;
	private Map<Integer,Integer> hwMap; //海湾报警对应数据
	private int openState = 0;			//海湾开机状态
	private int areaCode = 0;			//海湾区号
	
	public int getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(int areaCode) {
		this.areaCode = areaCode;
	}
	public int getOpenState() {
		return openState;
	}
	public void setOpenState(int openState) {
		this.openState = openState;
	}
	public Map<Integer, Integer> getHwMap() {
		return hwMap;
	}
	public void setHwMap(Map<Integer, Integer> hwMap) {
		this.hwMap = hwMap;
	}
	public String getFaultCode() {
		return faultCode;
	}
	public void setFaultCode(String faultCode) {
		this.faultCode = faultCode;
	}
	public String getFaultInfo() {
		return faultInfo;
	}
	public void setFaultInfo(String faultInfo) {
		this.faultInfo = faultInfo;
	}
	public String getFaultDevDesc() {
		return faultDevDesc;
	}
	public void setFaultDevDesc(String faultDevDesc) {
		this.faultDevDesc = faultDevDesc;
	}
	public String getFaultType() {
		return faultType;
	}
	public void setFaultType(String faultType) {
		this.faultType = faultType;
	}
	public String getRepeater() {
		return repeater;
	}
	public void setRepeater(String repeater) {
		this.repeater = repeater;
	}
	public String getFaultTime() {
		return faultTime;
	}
	public void setFaultTime(String faultTime) {
		this.faultTime = faultTime;
	}
	
	
}
