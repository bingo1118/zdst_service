package com.cloudfire.entity;

public class SmokeSummaryEntity {
	  /**
     * allSmokeNumber : 181
     * error : ³É¹¦
     * errorCode : 0
     * lossSmokeNumber : 3
     * onlineSmokeNumber : 178
     */

    private int allSmokeNumber;
    private String error="";
    private int errorCode;
    private int lossSmokeNumber;
    private int onlineSmokeNumber;
    private int lowVoltageNumber;

    public int getAllSmokeNumber() {
        return allSmokeNumber;
    }

    public void setAllSmokeNumber(int allSmokeNumber) {
        this.allSmokeNumber = allSmokeNumber;
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

    public int getLossSmokeNumber() {
        return lossSmokeNumber;
    }

    public void setLossSmokeNumber(int lossSmokeNumber) {
        this.lossSmokeNumber = lossSmokeNumber;
    }

    public int getOnlineSmokeNumber() {
        return onlineSmokeNumber;
    }

    public void setOnlineSmokeNumber(int onlineSmokeNumber) {
        this.onlineSmokeNumber = onlineSmokeNumber;
    }

	public int getLowVoltageNumber() {
		return lowVoltageNumber;
	}

	public void setLowVoltageNumber(int lowVoltageNumber) {
		this.lowVoltageNumber = lowVoltageNumber;
	}
}
