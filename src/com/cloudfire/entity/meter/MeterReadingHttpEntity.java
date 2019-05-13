package com.cloudfire.entity.meter;

import java.util.List;

public class MeterReadingHttpEntity {
	private int errorCode;
	private String error;
	private List<MeterReadingEntity> readingList;
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public List<MeterReadingEntity> getReadingList() {
		return readingList;
	}
	public void setReadingList(List<MeterReadingEntity> readingList) {
		this.readingList = readingList;
	}
	
}
