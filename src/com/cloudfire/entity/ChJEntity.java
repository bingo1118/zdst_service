package com.cloudfire.entity;

/**
 * �ϼѵ���ʵ����
 * @author hr
 *
 */
public class ChJEntity {  
	private int type;// �������߱��� 1��2
	private int threshold; //�豸���  //0x0A 10A; 0x14 20A;0x20 30A 
	private int alarmType;//�������� //0xCB ������0xC2  ����������0xC3  ��·������0xC4  ���ȱ�����
	private String data7="";//����
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getThreshold() {
		return threshold;
	}
	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}
	public int getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(int alarmType) {
		this.alarmType = alarmType;
	}
	public String getData7() {
		return data7;
	}
	public void setData7(String data7) {
		this.data7 = data7;
	}

	
}
