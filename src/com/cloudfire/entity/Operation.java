package com.cloudfire.entity;

public class Operation {
	private String operator;  //������
	private String object;  //��������
	private String time; //����ʱ��
	private int state;  //������� 0���� 1�ɹ� 2ʧ��
	private String result; //�������  
	private int optype; //�������� 0�������� 1�����˺� 2����ֵ 3����ֵ 4�ɼ�ʱ���� 5�ɼ�ʱ���� 6����ʱ��
	private String typeName; //������������
	private String content; //��������  
	
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
