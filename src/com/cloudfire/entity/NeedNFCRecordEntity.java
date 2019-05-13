package com.cloudfire.entity;

import java.util.List;

public class NeedNFCRecordEntity {
	
	private String error="获取设备失败";
    private int errorCode=2;

    private List<OneKindDevRecord> nfcList=null;

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

	public List<OneKindDevRecord> getNfcList() {
		return nfcList;
	}

	public void setNfcList(List<OneKindDevRecord> nfcList) {
		this.nfcList = nfcList;
	}

}

