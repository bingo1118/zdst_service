package com.cloudfire.entity;

import java.util.List;

public class SafetyContentListEntity {
	private String error="ªÒ»°≈‡—µƒ⁄»› ß∞‹";
    private int errorCode=2;
    private List<String> safetyItems;
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
	public List<String> getSafetyItems() {
		return safetyItems;
	}
	public void setSafetyItems(List<String> safetyItems) {
		this.safetyItems = safetyItems;
	}
}
