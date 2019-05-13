package com.cloudfire.entity;

import java.util.List;

public class HttpRsult {
	private String error="";
    private int errorCode;
    private List<ElectricHistoryEntity> eleList;
	public List<ElectricHistoryEntity> getEleList() {
		return eleList;
	}
	public void setEleList(List<ElectricHistoryEntity> eleList) {
		this.eleList = eleList;
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
	@Override
	public String toString() {
		return "HttpRsult [error=" + error + ", errorCode=" + errorCode + ", eleList=" + eleList + "]";
	}
	
}
