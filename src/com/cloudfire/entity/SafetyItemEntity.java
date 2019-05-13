package com.cloudfire.entity;

import java.util.List;

public class SafetyItemEntity {
	private String error="ªÒ»°≈‡—µƒ⁄»› ß∞‹";
    private int errorCode=2;
    private List<SafetyItem> safetyItems;
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
	public List<SafetyItem> getSafetyItems() {
		return safetyItems;
	}
	public void setSafetyItems(List<SafetyItem> safetyItems) {
		this.safetyItems = safetyItems;
	}
}
