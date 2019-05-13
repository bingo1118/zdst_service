package com.cloudfire.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OneChuangAnEntity {

	private String error="";
    private int errorCode;
    /**
     * electricTime : 2017-01-09 17:24:51
     * electricType : 6
     * electricValue : [{"ElectricThreshold":"242","id":1,"value":"0.00"},{"ElectricThreshold":"","id":2,"value":"0.00"},{"ElectricThreshold":"","id":3,"value":"0.00"}]
     */
    @JsonProperty("Electric")
    private List<ChuangAnBean> electric;
    
	public List<ChuangAnBean> getElectric() {
		return electric;
	}
	public void setElectric(List<ChuangAnBean> electric) {
		this.electric = electric;
	}
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
}
