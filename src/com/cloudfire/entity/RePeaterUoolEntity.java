package com.cloudfire.entity;

public class RePeaterUoolEntity {
	private String repeatMac="";	
	private String devMac="";
	
	private byte seqL;
	private byte seqH;
	
	private int Overvoltage = 0;	//过压阈值
	private int Undervoltage = 0;	//欠压阈值
	private int Overcurrent = 0;	//过流阈值
	private int Leakage = 0;	//漏电阈值
	
	private byte cmd2;
	private byte cmd;
	private byte cmd1;
	private int controlState = 0;//0操作成功，其他代表失败。
	
	public int getOvervoltage() {
		return Overvoltage;
	}
	public void setOvervoltage(int overvoltage) {
		Overvoltage = overvoltage;
	}
	public int getUndervoltage() {
		return Undervoltage;
	}
	public void setUndervoltage(int undervoltage) {
		Undervoltage = undervoltage;
	}
	public int getOvercurrent() {
		return Overcurrent;
	}
	public void setOvercurrent(int overcurrent) {
		Overcurrent = overcurrent;
	}
	public int getLeakage() {
		return Leakage;
	}
	public void setLeakage(int leakage) {
		Leakage = leakage;
	}
	public int getControlState() {
		return controlState;
	}
	public void setControlState(int controlState) {
		this.controlState = controlState;
	}
	public String getRepeatMac() {
		return repeatMac;
	}
	public void setRepeatMac(String repeatMac) {
		this.repeatMac = repeatMac;
	}
	public String getDevMac() {
		return devMac;
	}
	public void setDevMac(String devMac) {
		this.devMac = devMac;
	}
	public byte getSeqL() {
		return seqL;
	}
	public void setSeqL(byte seqL) {
		this.seqL = seqL;
	}
	public byte getSeqH() {
		return seqH;
	}
	public void setSeqH(byte seqH) {
		this.seqH = seqH;
	}
	public byte getCmd2() {
		return cmd2;
	}
	public void setCmd2(byte cmd2) {
		this.cmd2 = cmd2;
	}
	public byte getCmd() {
		return cmd;
	}
	public void setCmd(byte cmd) {
		this.cmd = cmd;
	}
	public byte getCmd1() {
		return cmd1;
	}
	public void setCmd1(byte cmd1) {
		this.cmd1 = cmd1;
	}
}
