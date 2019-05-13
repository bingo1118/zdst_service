package com.cloudfire.entity;

import java.util.List;

public class DvrInfo {
	private String error="";
    private int errorCode;
    private List<DvrInfoEntity> dvrInfo;
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
	public List<DvrInfoEntity> getDvrInfo() {
		return dvrInfo;
	}
	public void setDvrInfo(List<DvrInfoEntity> dvrInfo) {
		this.dvrInfo = dvrInfo;
	}
    
}
