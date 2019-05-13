package com.cloudfire.entity.meter;

public class MeterSettingHttpEntity {
	private String error="";
	private int errorCode;
	private MeterInfoEntity setting;
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
	public MeterInfoEntity getSetting() {
		return setting;
	}
	public void setSetting(MeterInfoEntity setting) {
		this.setting = setting;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
}
