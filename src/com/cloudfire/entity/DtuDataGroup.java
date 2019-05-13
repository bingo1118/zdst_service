package com.cloudfire.entity;

/*
 * DTU包里面的数据组
 * 
 */
public class DtuDataGroup {
	private int state; //状态
	private int unit;  //单位
	private float value; //数值
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getUnit() {
		return unit;
	}
	public void setUnit(int unit) {
		this.unit = unit;
	}
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}
	
}
