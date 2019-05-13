package com.cloudfire.entity.query;

import com.cloudfire.until.BaseQuery;

public class FaultInfoEntityQuery extends BaseQuery {

	private static final long serialVersionUID = -2784528120911445838L;
	
	private String repeaterMac; //有线终端mac （中继器编号）
	private String faultCode;	//终端下的设备号 （设备编号）
	private String faultDevDesc;//设备信息  (物业公司)
	private String faultRoominfo; //楼栋信息 （楼栋楼层）
	private String faultType;	//警报信息 （设备状态）
	private String faultInfo;	//操作信息  （信息备注）
	private String faultTime;	//时间 （设备上报时间）
	private int hostNOsmall;
	private int masterTypesmall;
	private int slaveTypesmall;
	private int makeTypesmall;
	private int equipmentTypesmall;
	private int zoneNOtiny;
	private int buildingNOtiny;
	private int floorNOtiny;
	private int roomNOtiny;
	private int faultUnitNOtiny;
	private int alarmTypesmall;
	private int id;				//ID编号
	private String dealUser;	//处理人
	private String J_xl_1;
	private String J_xl_2;
	private String type;
	private String status;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getJ_xl_1() {
		return J_xl_1;
	}
	public void setJ_xl_1(String j_xl_1) {
		J_xl_1 = j_xl_1;
	}
	public String getJ_xl_2() {
		return J_xl_2;
	}
	public void setJ_xl_2(String j_xl_2) {
		J_xl_2 = j_xl_2;
	}
	public String getRepeaterMac() {
		return repeaterMac;
	}
	public void setRepeaterMac(String repeaterMac) {
		this.repeaterMac = repeaterMac;
	}
	public String getFaultCode() {
		return faultCode;
	}
	public void setFaultCode(String faultCode) {
		this.faultCode = faultCode;
	}
	public String getFaultDevDesc() {
		return faultDevDesc;
	}
	public void setFaultDevDesc(String faultDevDesc) {
		this.faultDevDesc = faultDevDesc;
	}
	public String getFaultRoominfo() {
		return faultRoominfo;
	}
	public void setFaultRoominfo(String faultRoominfo) {
		this.faultRoominfo = faultRoominfo;
	}
	public String getFaultType() {
		return faultType;
	}
	public void setFaultType(String faultType) {
		this.faultType = faultType;
	}
	public String getFaultInfo() {
		return faultInfo;
	}
	public void setFaultInfo(String faultInfo) {
		this.faultInfo = faultInfo;
	}
	public String getFaultTime() {
		return faultTime;
	}
	public void setFaultTime(String faultTime) {
		this.faultTime = faultTime;
	}
	public int getHostNOsmall() {
		return hostNOsmall;
	}
	public void setHostNOsmall(int hostNOsmall) {
		this.hostNOsmall = hostNOsmall;
	}
	public int getMasterTypesmall() {
		return masterTypesmall;
	}
	public void setMasterTypesmall(int masterTypesmall) {
		this.masterTypesmall = masterTypesmall;
	}
	public int getSlaveTypesmall() {
		return slaveTypesmall;
	}
	public void setSlaveTypesmall(int slaveTypesmall) {
		this.slaveTypesmall = slaveTypesmall;
	}
	public int getMakeTypesmall() {
		return makeTypesmall;
	}
	public void setMakeTypesmall(int makeTypesmall) {
		this.makeTypesmall = makeTypesmall;
	}
	public int getEquipmentTypesmall() {
		return equipmentTypesmall;
	}
	public void setEquipmentTypesmall(int equipmentTypesmall) {
		this.equipmentTypesmall = equipmentTypesmall;
	}
	public int getZoneNOtiny() {
		return zoneNOtiny;
	}
	public void setZoneNOtiny(int zoneNOtiny) {
		this.zoneNOtiny = zoneNOtiny;
	}
	public int getBuildingNOtiny() {
		return buildingNOtiny;
	}
	public void setBuildingNOtiny(int buildingNOtiny) {
		this.buildingNOtiny = buildingNOtiny;
	}
	public int getFloorNOtiny() {
		return floorNOtiny;
	}
	public void setFloorNOtiny(int floorNOtiny) {
		this.floorNOtiny = floorNOtiny;
	}
	public int getRoomNOtiny() {
		return roomNOtiny;
	}
	public void setRoomNOtiny(int roomNOtiny) {
		this.roomNOtiny = roomNOtiny;
	}
	public int getFaultUnitNOtiny() {
		return faultUnitNOtiny;
	}
	public void setFaultUnitNOtiny(int faultUnitNOtiny) {
		this.faultUnitNOtiny = faultUnitNOtiny;
	}
	public int getAlarmTypesmall() {
		return alarmTypesmall;
	}
	public void setAlarmTypesmall(int alarmTypesmall) {
		this.alarmTypesmall = alarmTypesmall;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDealUser() {
		return dealUser;
	}
	public void setDealUser(String dealUser) {
		this.dealUser = dealUser;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
