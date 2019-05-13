package com.cloudfire.entity.query;

import java.math.BigDecimal;

import com.cloudfire.until.BaseQuery;

public class DevicesFee extends BaseQuery {

	private static final long serialVersionUID = -6903417978053998593L;
	
	private int id;

	public String deviceName;
	
	public String devSystemName;
	
	private BigDecimal money;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDevSystemName() {
		return devSystemName;
	}

	public void setDevSystemName(String devSystemName) {
		this.devSystemName = devSystemName;
	}

	public BigDecimal getMoney() {
		return money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}
	
}
