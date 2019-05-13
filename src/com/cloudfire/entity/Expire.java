package com.cloudfire.entity;

public class Expire {
	private String innerIp;
	private String outerIp;
	private String registerCode;
	private String expireTime;
	private String cur_innerIp;
	private String cur_outerIp;
	private String validTime; //失效时间点
	private int state; //注册码状态标志位 0 未过期 1过期前一个月 2已过期
	private int servers;  //注册码使用的服务器个数
	public int getServers() {
		return servers;
	}
	public void setServers(int servers) {
		this.servers = servers;
	}
	public String getInnerIp() {
		return innerIp;
	}
	public void setInnerIp(String innerIp) {
		this.innerIp = innerIp;
	}
	public String getOuterIp() {
		return outerIp;
	}
	public void setOuterIp(String outerIp) {
		this.outerIp = outerIp;
	}
	public String getRegisterCode() {
		return registerCode;
	}
	public void setRegisterCode(String registerCode) {
		this.registerCode = registerCode;
	}
	public String getExpireTime() {
		return expireTime;
	}
	public void setExpireTime(String expireTime) {
		this.expireTime = expireTime;
	}
	public String getCur_innerIp() {
		return cur_innerIp;
	}
	public void setCur_innerIp(String cur_innerIp) {
		this.cur_innerIp = cur_innerIp;
	}
	public String getCur_outerIp() {
		return cur_outerIp;
	}
	public void setCur_outerIp(String cur_outerIp) {
		this.cur_outerIp = cur_outerIp;
	}
	public String getValidTime() {
		return validTime;
	}
	public void setValidTime(String validTime) {
		this.validTime = validTime;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	
	
}
