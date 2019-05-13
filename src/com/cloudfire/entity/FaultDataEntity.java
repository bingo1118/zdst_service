package com.cloudfire.entity;

import java.util.List;
import java.util.Map;

/**
 * @author lzo
 *	����װ���豸��Ϣ
 */
public class FaultDataEntity {
	
	private String repeaterMac; //���ߴ����ն�mac
	private String tabbingCmd = "0";	//���ͱ�־
	private int infoNum = 0;		//��Ϣ��Ŀ
	private String systemTab = "0";	//ϵͳ��־
	private String systemAddress = "0";	//ϵͳ��ַ
	private String unitAddress = "0";	//������ַ
	private List<String> systemState;	//ϵͳ״̬unitState;//����״̬//operaTab������־//runTab���б�־//operaTab������־
	private Map<Integer,String> stateTab;	//ϵͳ״̬unitState;//����״̬//operaTab������־//runTab���б�־//operaTab������־
	private List<String> stateTabList;		//ϵͳ״̬unitState;//����״̬//operaTab������־//runTab���б�־//operaTab������־
	private String unitMemos = "0";			//����˵��
	private String unitType = "0";			//��������
	private String operator = "0";			//����Ա���
	private int pushState = 0;				//�Ƿ����͡���1���͡�0���ƣ�
	private byte[] ackByte = null;					//�ظ�
	
	public byte[] getAckByte() {
		return ackByte;
	}
	public void setAckByte(byte[] ackByte) {
		this.ackByte = ackByte;
	}
	public List<String> getStateTabList() {
		return stateTabList;
	}
	public void setStateTabList(List<String> stateTabList) {
		this.stateTabList = stateTabList;
	}
	public String getUnitAddress() {
		return unitAddress;
	}
	public void setUnitAddress(String unitAddress) {
		this.unitAddress = unitAddress;
	}
	public Map<Integer, String> getStateTab() {
		return stateTab;
	}
	public void setStateTab(Map<Integer, String> stateTab) {
		this.stateTab = stateTab;
	}
	public int getPushState() {
		return pushState;
	}
	public void setPushState(int pushState) {
		this.pushState = pushState;
	}
	public String getRepeaterMac() {
		return repeaterMac;
	}
	public void setRepeaterMac(String repeaterMac) {
		this.repeaterMac = repeaterMac;
	}
	public String getTabbingCmd() {
		return tabbingCmd;
	}
	public void setTabbingCmd(String tabbingCmd) {
		this.tabbingCmd = tabbingCmd;
	}
	public int getInfoNum() {
		return infoNum;
	}
	public void setInfoNum(int infoNum) {
		this.infoNum = infoNum;
	}
	public String getSystemTab() {
		return systemTab;
	}
	public void setSystemTab(String systemTab) {
		this.systemTab = systemTab;
	}
	public String getSystemAddress() {
		return systemAddress;
	}
	public void setSystemAddress(String systemAddress) {
		this.systemAddress = systemAddress;
	}
	public List<String> getSystemState() {
		return systemState;
	}
	public void setSystemState(List<String> systemState) {
		this.systemState = systemState;
	}
	public String getUnitMemos() {
		return unitMemos;
	}
	public void setUnitMemos(String unitMemos) {
		this.unitMemos = unitMemos;
	}
	public String getUnitType() {
		return unitType;
	}
	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}

}
