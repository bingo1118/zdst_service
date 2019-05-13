package com.cloudfire.entity;

import java.util.List;

public class AllCheakItemEntity {
	
	private String error="获取设备失败";
    private int errorCode=2;

    private List<NFCCheakItem> items=null;

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

	public List<NFCCheakItem> getItems() {
		return items;
	}

	public void setItems(List<NFCCheakItem> items) {
		this.items = items;
	}

}
