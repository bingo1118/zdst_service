package com.cloudfire.entity;

public class UserEntity {
	private String userId;
	private String named;
	private int privilege;
	private int ifTryUser;
	private String endTime;
	private int privId;
	private int istxt;
	private String pwd;
	private String salt;
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	public int getIstxt() {
		return istxt;
	}
	public void setIstxt(int istxt) {
		this.istxt = istxt;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getNamed() {
		return named;
	}
	public void setNamed(String named) {
		this.named = named;
	}
	public int getPrivilege() {
		return privilege;
	}
	public void setPrivilege(int privilege) {
		this.privilege = privilege;
	}
	public int getIfTryUser() {
		return ifTryUser;
	}
	public void setIfTryUser(int ifTryUser) {
		this.ifTryUser = ifTryUser;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public int getPrivId() {
		return privId;
	}
	public void setPrivId(int privId) {
		this.privId = privId;
	}
	
}
