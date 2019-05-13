package com.cloudfire.entity;

/**
 * 
 * @author lzo
 *	���ʵ���
 */
public class ElectricEnergyEntity {
	private String imei = "";//IMEI(15)	
	private String imsi = "";//IMSI(15)	
	private String rssi = "";//RSSI(2)	
	private String BatteryPower;//��ص���(1)	
	private String alarmFimaly = "0";
	private int DevType = 4;//4	0x0004
	private int cmd = 0;//(1)0x30	
	private int shunt = 0;//(1)	����
	private int shuntRelevanceTime = 0;//����ʱ�䣺0 - �����; 0x1F �C ����; ��1-30�� - ��������ʱ
	private int shuntCuPer = 0;//Bit5: 1 - ��ѹ/��������;
	private int shuntTemp = 0;//Bit6: 1 - �¶�/©�����;
	private int shuntLink = 0;//Bit7: 1 �C ��������;
	private String ActivePowerA = "";//�й�����A��(2)	
	private String ActivePowerB = "";//(2)�й�����B��	
	private String ActivePowerC = "";//	�й�����C��
	private String ReactivePowerA = "";//(2)�޹�����A��	
	private String ReactivePowerB = "";//(2)�޹�����B��	 
	private String ReactivePowerC = "";//(2)�޹�����C��	
	private String ApparentPowerA = "";//(2)���ڹ���A��	
	private String ApparentPowerB = "";//(2)���ڹ���B��	
	private String ApparentPowerC = "";//(2)���ڹ���C��	
	private String PowerFactorA = "";//(2)��������A��	
	private String PowerFactorB = "";//(2)��������B��	
	private String PowerFactorC = "";//(2)��������C��	
	private String ActiveEnergyA = "";//(2)�й�����A��	
	private String ActiveEnergyB = "";//(2)�й�����B��	
	private String ActiveEnergyC = "";//(2)�й�����C��	
	private String ReactiveEnergyA = "";//(2)�޹�����A��	
	private String ReactiveEnergyB = "";//(2)�޹�����B��	
	private String ReactiveEnergyC = "";//(2)�޹�����C��	
	private String ApparentEnergyA = "";//(2)���ڵ���A��	
	private String ApparentEnergyB = "";//(2)���ڵ���B��	
	private String ApparentEnergyC = "";//(2)���ڵ���C��
	private String dataTime = "";	//	����ʱ��
	
	public String getAlarmFimaly() {
		return alarmFimaly;
	}
	public void setAlarmFimaly(String alarmFimaly) {
		this.alarmFimaly = alarmFimaly;
	}
	public int getShuntRelevanceTime() {
		return shuntRelevanceTime;
	}
	public void setShuntRelevanceTime(int shuntRelevanceTime) {
		if(shuntRelevanceTime<0){
			shuntRelevanceTime = 0;
		}
		this.shuntRelevanceTime = shuntRelevanceTime;
	}
	public int getShuntCuPer() {
		return shuntCuPer;
	}
	public void setShuntCuPer(int shuntCuPer) {
		this.shuntCuPer = shuntCuPer;
	}
	public int getShuntTemp() {
		return shuntTemp;
	}
	public void setShuntTemp(int shuntTemp) {
		if(shuntTemp<0){
			shuntTemp = 0;
		}
		this.shuntTemp = shuntTemp;
	}
	public int getShuntLink() {
		return shuntLink;
	}
	public void setShuntLink(int shuntLink) {
		this.shuntLink = shuntLink;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	public String getRssi() {
		return rssi;
	}
	public void setRssi(String rssi) {
		this.rssi = rssi;
	}
	public String getBatteryPower() {
		return BatteryPower;
	}
	public void setBatteryPower(String batteryPower) {
		BatteryPower = batteryPower;
	}
	public int getDevType() {
		return DevType;
	}
	public void setDevType(int devType) {
		DevType = devType;
	}
	public int getCmd() {
		return cmd;
	}
	public void setCmd(int cmd) {
		this.cmd = cmd;
	}
	public int getShunt() {
		return shunt;
	}
	public void setShunt(int shunt) {
		this.shunt = shunt;
	}
	public String getActivePowerA() {
		return ActivePowerA;
	}
	public void setActivePowerA(String activePowerA) {
		ActivePowerA = activePowerA;
	}
	public String getActivePowerB() {
		return ActivePowerB;
	}
	public void setActivePowerB(String activePowerB) {
		ActivePowerB = activePowerB;
	}
	public String getActivePowerC() {
		return ActivePowerC;
	}
	public void setActivePowerC(String activePowerC) {
		ActivePowerC = activePowerC;
	}
	public String getReactivePowerA() {
		return ReactivePowerA;
	}
	public void setReactivePowerA(String reactivePowerA) {
		ReactivePowerA = reactivePowerA;
	}
	public String getReactivePowerB() {
		return ReactivePowerB;
	}
	public void setReactivePowerB(String reactivePowerB) {
		ReactivePowerB = reactivePowerB;
	}
	public String getReactivePowerC() {
		return ReactivePowerC;
	}
	public void setReactivePowerC(String reactivePowerC) {
		ReactivePowerC = reactivePowerC;
	}
	public String getApparentPowerA() {
		return ApparentPowerA;
	}
	public void setApparentPowerA(String apparentPowerA) {
		ApparentPowerA = apparentPowerA;
	}
	public String getApparentPowerB() {
		return ApparentPowerB;
	}
	public void setApparentPowerB(String apparentPowerB) {
		ApparentPowerB = apparentPowerB;
	}
	public String getApparentPowerC() {
		return ApparentPowerC;
	}
	public void setApparentPowerC(String apparentPowerC) {
		ApparentPowerC = apparentPowerC;
	}
	public String getPowerFactorA() {
		return PowerFactorA;
	}
	public void setPowerFactorA(String powerFactorA) {
		PowerFactorA = powerFactorA;
	}
	public String getPowerFactorB() {
		return PowerFactorB;
	}
	public void setPowerFactorB(String powerFactorB) {
		PowerFactorB = powerFactorB;
	}
	public String getPowerFactorC() {
		return PowerFactorC;
	}
	public void setPowerFactorC(String powerFactorC) {
		PowerFactorC = powerFactorC;
	}
	public String getActiveEnergyA() {
		return ActiveEnergyA;
	}
	public void setActiveEnergyA(String activeEnergyA) {
		ActiveEnergyA = activeEnergyA;
	}
	public String getActiveEnergyB() {
		return ActiveEnergyB;
	}
	public void setActiveEnergyB(String activeEnergyB) {
		ActiveEnergyB = activeEnergyB;
	}
	public String getActiveEnergyC() {
		return ActiveEnergyC;
	}
	public void setActiveEnergyC(String activeEnergyC) {
		ActiveEnergyC = activeEnergyC;
	}
	public String getReactiveEnergyA() {
		return ReactiveEnergyA;
	}
	public void setReactiveEnergyA(String reactiveEnergyA) {
		ReactiveEnergyA = reactiveEnergyA;
	}
	public String getReactiveEnergyB() {
		return ReactiveEnergyB;
	}
	public void setReactiveEnergyB(String reactiveEnergyB) {
		ReactiveEnergyB = reactiveEnergyB;
	}
	public String getReactiveEnergyC() {
		return ReactiveEnergyC;
	}
	public void setReactiveEnergyC(String reactiveEnergyC) {
		ReactiveEnergyC = reactiveEnergyC;
	}
	public String getApparentEnergyA() {
		return ApparentEnergyA;
	}
	public void setApparentEnergyA(String apparentEnergyA) {
		ApparentEnergyA = apparentEnergyA;
	}
	public String getApparentEnergyB() {
		return ApparentEnergyB;
	}
	public void setApparentEnergyB(String apparentEnergyB) {
		ApparentEnergyB = apparentEnergyB;
	}
	public String getApparentEnergyC() {
		return ApparentEnergyC;
	}
	public void setApparentEnergyC(String apparentEnergyC) {
		ApparentEnergyC = apparentEnergyC;
	}
	public String getDataTime() {
		return dataTime;
	}
	public void setDataTime(String dataTime) {
		this.dataTime = dataTime;
	}
}
