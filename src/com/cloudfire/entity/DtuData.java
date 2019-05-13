package com.cloudfire.entity;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.cloudfire.until.JavaByteArrToHex;

public class DtuData {
	private String dtuId;
	private String dtuName;
	private  int msgLens;  //���ݳ���
	private int msgType; //��������
	private int heartbeatRate; //DTU������������� s
	private String time; //ʱ��  yy MM dd hh mm ss
	private int interval; //�ɼ�ʱ���� s 1-256s
	private int leftEnergy; //ʣ����� 00-99
	private int strong; //�ź�ǿ��  0x31-0x35 �ź�ǿ�ȵ��� 
	private List<DtuDataGroup> lstdg;
	public String getDtuId() {
		return dtuId;
	}
	public void setDtuId(String dtuId) {
		this.dtuId = dtuId;
	}
	public String getDtuName() {
		return dtuName;
	}
	public void setDtuName(String dtuName) {
		this.dtuName = dtuName;
	}
	public int getMsgLens() {
		return msgLens;
	}
	public void setMsgLens(int msgLens) {
		this.msgLens = msgLens;
	}
	public int getMsgType() {
		return msgType;
	}
	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}
	public int getHeartbeatRate() {
		return heartbeatRate;
	}
	public void setHeartbeatRate(int heartbeatRate) {
		this.heartbeatRate = heartbeatRate;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}

	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
	public int getLeftEnergy() {
		return leftEnergy;
	}
	public void setLeftEnergy(int leftEnergy) {
		this.leftEnergy = leftEnergy;
	}
	public int getStrong() {
		return strong;
	}
	public void setStrong(int strong) {
		this.strong = strong;
	}
	public List<DtuDataGroup> getLstdg() {
		return lstdg;
	}
	public void setLstdg(List<DtuDataGroup> lstdg) {
		this.lstdg = lstdg;
	}
	
}
