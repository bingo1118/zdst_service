package com.cloudfire.entity;

public class BQEletricAVGData {
	//private int Month;
	private String type;//��������
	private String avg1;//����1��ƽ��ֵ
	private String avg2;//����2��ƽ��ֵ
	private String avg3;//����3��ƽ��ֵ
	private String avg4;//����4��ƽ��ֵ
	private String time;//����ʱ��
	
	
	
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAvg1() {
		return avg1;
	}
	public void setAvg1(String avg1) {
		this.avg1 = avg1;
	}
	public String getAvg2() {
		return avg2;
	}
	public void setAvg2(String avg2) {
		this.avg2 = avg2;
	}
	public String getAvg3() {
		return avg3;
	}
	public void setAvg3(String avg3) {
		this.avg3 = avg3;
	}
	public String getAvg4() {
		return avg4;
	}
	public void setAvg4(String avg4) {
		this.avg4 = avg4;
	}
	
	public BQEletricAVGData(String type, String avg1, String avg2, String avg3,
			String avg4, String time) {
		super();
		this.type = type;
		this.avg1 = avg1;
		this.avg2 = avg2;
		this.avg3 = avg3;
		this.avg4 = avg4;
		this.time = time;
	}
	public BQEletricAVGData() {
		super();
	}
	
	

}
