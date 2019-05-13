package com.cloudfire.entity;

import java.util.List;

public class AlarmSummaryEntity {
  private String error="";
  private int  errorCode;
  private List<Integer>  alarmCountList;
public List<Integer> getAlarmCountList() {
	return alarmCountList;
}
public void setAlarmCountList(List<Integer> alarmCountList) {
	this.alarmCountList = alarmCountList;
}
public String getError() {
	return error;
}
public void setError(String error) {
	this.error = error;
}
public int getErrorCode() {
	return errorCode;
}
public void setErrorCode(int errorCode) {
	this.errorCode = errorCode;
}

}
