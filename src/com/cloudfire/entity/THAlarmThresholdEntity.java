package com.cloudfire.entity;

import java.io.Serializable;

public class THAlarmThresholdEntity implements Serializable{

	private String error="";
    private int errorCode;
    private String value307;
    private String value308;
    private String value407;
    private String value408;
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
	public String getValue307() {
		return value307;
	}
	public void setValue307(String value307) {
		this.value307 = value307;
	}
	public String getValue308() {
		return value308;
	}
	public void setValue308(String value308) {
		this.value308 = value308;
	}
	public String getValue407() {
		return value407;
	}
	public void setValue407(String value407) {
		this.value407 = value407;
	}
	public String getValue408() {
		return value408;
	}
	public void setValue408(String value408) {
		this.value408 = value408;
	}
}
