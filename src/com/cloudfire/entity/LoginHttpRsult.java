package com.cloudfire.entity;

public class LoginHttpRsult {
	private String error="";
    private int errorCode;
    private String userId="";
    private String privId;
    private int loginState;
    
	public int getLoginState() {
		return loginState;
	}
	public void setLoginState(int loginState) {
		this.loginState = loginState;
	}
	public String getPrivId() {
		return privId;
	}
	public void setPrivId(String privId) {
		this.privId = privId;
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
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}
