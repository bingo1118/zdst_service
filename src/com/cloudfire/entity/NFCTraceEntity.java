package com.cloudfire.entity;

import java.util.List;

public class NFCTraceEntity {
	private String error="ªÒ»° ß∞‹";
    private int errorCode=2;
    private List<NFCInfoEntity> nfcTraceList=null;
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
	public List<NFCInfoEntity> getNfcTraceList() {
		return nfcTraceList;
	}
	public void setNfcTraceList(List<NFCInfoEntity> nfcTraceList) {
		this.nfcTraceList = nfcTraceList;
	}

}
