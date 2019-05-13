package com.cloudfire.entity;

public class WorkingTime {
	
	private String userId;
	private String named;
	private String worktime;
	private String alarmType;
	private String alarmName;
	private String dealalarm;
	private String superUserId;
	//u.userid,u.named,u.workingTime,a.alarmType,t.alarmName
	
	public String getUserId() {
		return userId;
	}
	public String getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}
	public String getAlarmName() {
		return alarmName;
	}
	public void setAlarmName(String alarmName) {
		this.alarmName = alarmName;
	}
	public String getDealalarm() {
		return dealalarm;
	}
	public void setDealalarm(String dealalarm) {
		this.dealalarm = dealalarm;
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
	public String getWorktime() {
		return worktime;
	}
	public void setWorktime(String worktime) {
		this.worktime = worktime;
	}
	public String getSuperUserId() {
		return superUserId;
	}
	public void setSuperUserId(String superUserId) {
		this.superUserId = superUserId;
	}
	
}
