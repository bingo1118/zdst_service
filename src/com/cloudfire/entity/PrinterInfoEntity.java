package com.cloudfire.entity;

import java.util.List;

public class PrinterInfoEntity {
	private String error="��ȡ�豸ʧ��";
	private int errorCode=1;
	private List<PrinterInfo> equipment;
	private List<FaultInfoEntity> faultment;
	
	
	public List<FaultInfoEntity> getFaultment() {
		return faultment;
	}
	public void setFaultment(List<FaultInfoEntity> faultment) {
		this.faultment = faultment;
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
	public List<PrinterInfo> getEquipment() {
		return equipment;
	}
	public void setEquipment(List<PrinterInfo> equipment) {
		this.equipment = equipment;
	}
	
	
}
