package com.cloudfire.entity;

public class Operation {
	private String operator;  //操作者
	private String object;  //操作对象
	private String time; //操作时间
	private int state;  //操作结果 0待定 1成功 2失败
	private String result; //操作结果  
	private int optype; //操作类型 0报警处理 1创建账号 2高阈值 3低阈值 4采集时间间隔 5采集时间间隔 6离线时间
	private String typeName; //操作类型名字
	private String content; //操作内容  
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
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
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getOptype() {
		return optype;
	}
	public void setOptype(int optype) {
		this.optype = optype;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

}
