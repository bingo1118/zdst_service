package com.cloudfire.entity;

/**
 * @author lzo
 *	有线装置设备信息
 */
public class FaultInfoEntity {
	
	private String repeaterMac; //有线终端mac （中继器编号）
	private String faultCode;	//终端下的设备号 （设备编号）
	private String faultDevDesc;//设备信息  (物业公司)
	private String faultRoominfo; //楼栋信息 （楼栋楼层）
	private String faultType;	//警报信息 （设备状态）
	private String faultInfo;	//操作信息  （信息备注）
	private String faultTime;	//时间 （设备上报时间）
	private String areaName;
	private String address;
	private String principal1;  //负责人
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
	private int alarmNume;
	private int id;				//ID编号
	private String dealUser;	//处理人
	private int row;
	private String hostType;
	private String netState;
	private String rssivalue;
	
	private int ifAlarm;
	private String alarmName;
	
	public int getIfAlarm() {
		return ifAlarm;
	}

	public void setIfAlarm(int ifAlarm) {
		this.ifAlarm = ifAlarm;
	}

	public String getAlarmName() {
		return alarmName;
	}

	public void setAlarmName(String alarmName) {
		this.alarmName = alarmName;
	}

	public String getRssivalue() {
		return rssivalue;
	}

	public void setRssivalue(String rssivalue) {
		if(rssivalue==null){
			this.rssivalue = "0";
		}else{
			this.rssivalue = rssivalue;
		}
	}

	public String getNetState() {
		return netState;
	}

	public void setNetState(String netState) {
		this.netState = netState;
	}

	public String getHostType() {
		return hostType;
	}

	public void setHostType(String hostType) {
		this.hostType = hostType;
	}
	private String dealText;
	private String dealTime;
	
	public String getDealText() {
		return dealText;
	}

	public void setDealText(String dealText) {
		this.dealText = dealText;
	}

	public String getDealTime() {
		return dealTime;
	}

	public void setDealTime(String dealTime) {
		this.dealTime = dealTime;
	}

	public int getAlarmNume() {
		return alarmNume;
	}

	public void setAlarmNume(int alarmNume) {
		this.alarmNume = alarmNume;
	}

	public String getPrincipal1() {
		return principal1;
	}

	public void setPrincipal1(String principal1) {
		this.principal1 = principal1;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public void setRow(int row) {
		this.row = row;
	}
	
	public int getRow() {
		return row;
	}
	
	public String getFaultRoominfo() {
		return faultRoominfo;
	}
	public void setFaultRoominfo(String faultRoominfo) {
		this.faultRoominfo = faultRoominfo;
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
	
	
	

}
