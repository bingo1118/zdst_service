package com.cloudfire.entity;

import com.cloudfire.until.BaseQuery;

/**
 * ��ѯ������¼����������
 * @author hr
 *
 */
public class OperationQuery extends BaseQuery{
	private String operator;  //������
	private String object;  //��������
	private String opt;  //��������
	private String startTime; //��ʼʱ��
	private String endTime;  //����ʱ��
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
