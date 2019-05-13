package com.cloudfire.entity;

public class PushAlarmToPCEntity {
	private PrinterEntity masterFault;
	
	private int deviceType = 221;
	
	public int getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}

	public PrinterEntity getMasterFault() {
		return masterFault;
	}

	public void setMasterFault(PrinterEntity masterFault) {
		this.masterFault = masterFault;
	}
	
	
}
