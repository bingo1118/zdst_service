package com.cloudfire.entity;

import java.util.List;
import java.util.Map;

/**
 * @author lzo
 *	有线装置设备信息
 */
public class FaultDataEntity {
	
	private String repeaterMac; //有线传输终端mac
	private String tabbingCmd = "0";	//类型标志
	private int infoNum = 0;		//信息数目
	private String systemTab = "0";	//系统标志
	private String systemAddress = "0";	//系统地址
	private String unitAddress = "0";	//部件地址
	private List<String> systemState;	//系统状态unitState;//部件状态//operaTab操作标志//runTab运行标志//operaTab操作标志
	private Map<Integer,String> stateTab;	//系统状态unitState;//部件状态//operaTab操作标志//runTab运行标志//operaTab操作标志
	private List<String> stateTabList;		//系统状态unitState;//部件状态//operaTab操作标志//runTab运行标志//operaTab操作标志
	private String unitMemos = "0";			//部件说明
	private String unitType = "0";			//部件类型
	private String operator = "0";			//操作员编号
	private int pushState = 0;				//是否推送。（1推送、0不推）
	private byte[] ackByte = null;					//回复
	
	public byte[] getAckByte() {
		return ackByte;
	}
	public void setAckByte(byte[] ackByte) {
		this.ackByte = ackByte;
	}
	public List<String> getStateTabList() {
		return stateTabList;
	}
	public void setStateTabList(List<String> stateTabList) {
		this.stateTabList = stateTabList;
	}
	public String getUnitAddress() {
		return unitAddress;
	}
	public void setUnitAddress(String unitAddress) {
		this.unitAddress = unitAddress;
	}
	public Map<Integer, String> getStateTab() {
		return stateTab;
	}
	public void setStateTab(Map<Integer, String> stateTab) {
		this.stateTab = stateTab;
	}
	public int getPushState() {
		return pushState;
	}
	public void setPushState(int pushState) {
		this.pushState = pushState;
	}
	public String getRepeaterMac() {
		return repeaterMac;
	}
	public void setRepeaterMac(String repeaterMac) {
		this.repeaterMac = repeaterMac;
	}
	public String getTabbingCmd() {
		return tabbingCmd;
	}
	public void setTabbingCmd(String tabbingCmd) {
		this.tabbingCmd = tabbingCmd;
	}
	public int getInfoNum() {
		return infoNum;
	}
	public void setInfoNum(int infoNum) {
		this.infoNum = infoNum;
	}
	public String getSystemTab() {
		return systemTab;
	}
	public void setSystemTab(String systemTab) {
		this.systemTab = systemTab;
	}
	public String getSystemAddress() {
		return systemAddress;
	}
	public void setSystemAddress(String systemAddress) {
		this.systemAddress = systemAddress;
	}
	public List<String> getSystemState() {
		return systemState;
	}
	public void setSystemState(List<String> systemState) {
		this.systemState = systemState;
	}
	public String getUnitMemos() {
		return unitMemos;
	}
	public void setUnitMemos(String unitMemos) {
		this.unitMemos = unitMemos;
	}
	public String getUnitType() {
		return unitType;
	}
	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}

}
