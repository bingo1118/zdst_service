package com.cloudfire.entity;

import java.util.List;

public class AllCameraEntity {
	private String error="";
	private int errorCode;
	private List<AllCamera> camera=null;
	 
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
	public List<AllCamera> getCamera() {
		return camera;
	}
	public void setCamera(List<AllCamera> camera) {
		this.camera = camera;
	}
	 
}
