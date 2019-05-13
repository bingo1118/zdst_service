package com.cloudfire.entity;

import com.cloudfire.until.BaseQuery;

/**
 * 查询操作记录的条件对象
 * @author hr
 *
 */
public class OperationQuery extends BaseQuery{
	private String operator;  //操作者
	private String object;  //操作对象
	private String opt;  //操作类型
	private String startTime; //开始时间
	private String endTime;  //结束时间
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}
	public String getOpt() {
		return opt;
	}
	public void setOpt(String opt) {
		this.opt = opt;
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
	
	
}
