package com.cloudfire.entity;

import java.util.List;

public class AllRepeaterEntity {
	private String error="ªÒ»°—Ã∏– ß∞‹";
    private int errorCode=2;
    private List<RepeaterBean> repeater=null;
    
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
	public List<RepeaterBean> getRepeater() {
		return repeater;
	}
	public void setRepeater(List<RepeaterBean> repeater) {
		this.repeater = repeater;
	}
}
