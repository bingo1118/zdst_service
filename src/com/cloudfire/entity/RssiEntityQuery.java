package com.cloudfire.entity;

import com.cloudfire.until.BaseQuery;

public class RssiEntityQuery extends BaseQuery {

	private static final long serialVersionUID = -4830825053207194754L;
	
	private String deviceMac = "";
	private String rssivalue = "";
	private String rssitime = "";
	
	
	public String getDeviceMac() {
		return deviceMac;
	}
	public void setDeviceMac(String deviceMac) {
		this.deviceMac = deviceMac;
	}
	public String getRssivalue() {
		return rssivalue;
	}
	public void setRssivalue(String rssivalue) {
		this.rssivalue = rssivalue;
	}
	public String getRssitime() {
		return rssitime;
	}
	public void setRssitime(String rssitime) {
		this.rssitime = rssitime;
	}
}
