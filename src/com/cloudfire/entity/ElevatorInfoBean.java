package com.cloudfire.entity;

public class ElevatorInfoBean {
	private String id="";	
	private byte overGround;
	private short storeyNumber;
	private byte status;
	private int door;
	private byte people;		
	private byte alarm;
	private byte storeyStuckAlarm;
	private byte besiegeAlarm;
	private byte upperLimit;
	private byte lowerLimit;
	private byte runOpen;
	private byte overSpeed;
	private byte netState;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public byte getOverGround() {
		return overGround;
	}
	public void setOverGround(byte overGround) {
		this.overGround = overGround;
	}
	public short getStoreyNumber() {
		return storeyNumber;
	}
	public void setStoreyNumber(short storeyNumber) {
		this.storeyNumber = storeyNumber;
	}
	public byte getStatus() {
		return status;
	}
	public void setStatus(byte status) {
		this.status = status;
	}
	public int getDoor() {
		return door;
	}
	public void setDoor(int door) {
		this.door = door;
	}
	public byte getPeople() {
		return people;
	}
	public void setPeople(byte people) {
		this.people = people;
	}
	public byte getAlarm() {
		return alarm;
	}
	public void setAlarm(byte alarm) {
		this.alarm = alarm;
	}
	public byte getStoreyStuckAlarm() {
		return storeyStuckAlarm;
	}
	public void setStoreyStuckAlarm(byte storeyStuckAlarm) {
		this.storeyStuckAlarm = storeyStuckAlarm;
	}
	public byte getBesiegeAlarm() {
		return besiegeAlarm;
	}
	public void setBesiegeAlarm(byte besiegeAlarm) {
		this.besiegeAlarm = besiegeAlarm;
	}
	public byte getUpperLimit() {
		return upperLimit;
	}
	public void setUpperLimit(byte upperLimit) {
		this.upperLimit = upperLimit;
	}
	public byte getLowerLimit() {
		return lowerLimit;
	}
	public void setLowerLimit(byte lowerLimit) {
		this.lowerLimit = lowerLimit;
	}
	public byte getRunOpen() {
		return runOpen;
	}
	public void setRunOpen(byte runOpen) {
		this.runOpen = runOpen;
	}
	public byte getOverSpeed() {
		return overSpeed;
	}
	public void setOverSpeed(byte overSpeed) {
		this.overSpeed = overSpeed;
	}
	public byte getNetState() {
		return netState;
	}
	public void setNetState(byte netState) {
		this.netState = netState;
	}
}
