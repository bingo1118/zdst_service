package com.cloudfire.entity;

import java.util.List;

public class AckToSoundAndDevice {
	private List<SmokeBean> smokeList;
	private List<AckDeviceBean> ackList;
	
	public List<SmokeBean> getSmokeList() {
		return smokeList;
	}
	public void setSmokeList(List<SmokeBean> smokeList) {
		this.smokeList = smokeList;
	}
	public List<AckDeviceBean> getAckList() {
		return ackList;
	}
	public void setAckList(List<AckDeviceBean> ackList) {
		this.ackList = ackList;
	}
}
