package com.cloudfire.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AllElectricInfoEntity<T> {
	 /**
     * error : 获取烟感成功）
     * errorCode : 0
     */
    private String error="";
    private int errorCode;
    @JsonProperty("Electric")
    private List<T> electric=null;

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

	public List<T> getElectric() {
		return electric;
	}

	public void setElectric(List<T> electric) {
		this.electric = electric;
	}

}
