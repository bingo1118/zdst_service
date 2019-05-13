package com.cloudfire.entity;

public class HttpRsultUser {
	private String error="";
    private int errorCode;
    private String retime;
    private boolean result = false;
    
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
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
	public String getRetime() {
		return retime;
	}
	public void setRetime(String retime) {
		this.retime = retime;
	}
    
}
