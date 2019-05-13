package com.cloudfire.entity.query;

import com.cloudfire.until.BaseQuery;

public class GpsQuery extends BaseQuery {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String gasMac ;
	
	private String startTime;
	
	private String endTime;
	
	private String status;
	
	private String status7020;

	public String getGasMac() {
		return gasMac;
	}

	public void setGasMac(String gasMac) {
		this.gasMac = gasMac;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus7020() {
		return status7020;
	}

	public void setStatus7020(String status7020) {
		this.status7020 = status7020;
	}

}
