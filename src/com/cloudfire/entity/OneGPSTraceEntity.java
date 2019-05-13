package com.cloudfire.entity;

import java.util.List;

public class OneGPSTraceEntity {
	private String error="ªÒ»° ß∞‹";
    private int errorCode=2;
    
    private List<GPSInfoBean> trace=null;
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
	public List<GPSInfoBean> getTrace() {
		return trace;
	}
	public void setTrace(List<GPSInfoBean> trace) {
		this.trace = trace;
	}

}
