package com.cloudfire.entity;

import java.util.List;

public class PlaceTypeEntity {
	private String error="��ȡ��������ʧ��";
    private int errorCode=2;
    private List<ShopTypeEntity> placeType;
    
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
	public List<ShopTypeEntity> getPlaceType() {
		return placeType;
	}
	public void setPlaceType(List<ShopTypeEntity> placeType) {
		this.placeType = placeType;
	}
    
    
}
