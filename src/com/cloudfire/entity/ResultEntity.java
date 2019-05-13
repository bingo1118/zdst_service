package com.cloudfire.entity;

public class ResultEntity {
	/**
     * error : 修改摄像机密码成功
     * errorCode : 0
     */

    private String error="";
    private int errorCode;

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
}
