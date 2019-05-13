package com.cloudfire.entity;

/**
 * 
 * @author lzo
 *	功率电能
 */
public class ElectricEnergyEntity {
	private String imei = "";//IMEI(15)	
	private String imsi = "";//IMSI(15)	
	private String rssi = "";//RSSI(2)	
	private String BatteryPower;//电池电量(1)	
	private String alarmFimaly = "0";
	private int DevType = 4;//4	0x0004
	private int cmd = 0;//(1)0x30	
	private int shunt = 0;//(1)	分励
	private int shuntRelevanceTime = 0;//分励时间：0 - 无输出; 0x1F – 常开; ’1-30’ - 分励保持时
	private int shuntCuPer = 0;//Bit5: 1 - 电压/电流分励;
	private int shuntTemp = 0;//Bit6: 1 - 温度/漏电分励;
	private int shuntLink = 0;//Bit7: 1 – 联动分励;
	private String ActivePowerA = "";//有功功率A相(2)	
	private String ActivePowerB = "";//(2)有功功率B相	
	private String ActivePowerC = "";//	有功功率C相
	private String ReactivePowerA = "";//(2)无功功率A相	
	private String ReactivePowerB = "";//(2)无功功率B相	 
	private String ReactivePowerC = "";//(2)无功功率C相	
	private String ApparentPowerA = "";//(2)视在功率A相	
	private String ApparentPowerB = "";//(2)视在功率B相	
	private String ApparentPowerC = "";//(2)视在功率C相	
	private String PowerFactorA = "";//(2)功率因素A相	
	private String PowerFactorB = "";//(2)功率因素B相	
	private String PowerFactorC = "";//(2)功率因素C相	
	private String ActiveEnergyA = "";//(2)有功电能A相	
	private String ActiveEnergyB = "";//(2)有功电能B相	
	private String ActiveEnergyC = "";//(2)有功电能C相	
	private String ReactiveEnergyA = "";//(2)无功电能A相	
	private String ReactiveEnergyB = "";//(2)无功电能B相	
	private String ReactiveEnergyC = "";//(2)无功电能C相	
	private String ApparentEnergyA = "";//(2)视在电能A相	
	private String ApparentEnergyB = "";//(2)视在电能B相	
	private String ApparentEnergyC = "";//(2)视在电能C相
	private String dataTime = "";	//	心跳时间
	
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
