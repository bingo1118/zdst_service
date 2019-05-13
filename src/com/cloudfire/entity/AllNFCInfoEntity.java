package com.cloudfire.entity;

import java.util.List;
import java.util.Map;

public class AllNFCInfoEntity {
    
    private String error="获取设备失败";
    private int errorCode=2;

    private List<NFCInfoEntity> nfcList=null;

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

	public List<NFCInfoEntity> getNfcList() {
		return nfcList;
	}

	public void setNfcList(List<NFCInfoEntity> nfcList) {
		this.nfcList = nfcList;
	}


}
