package com.cloudfire.entity.query;

import java.util.List;

import com.cloudfire.until.BaseQuery;

public class MainIndexEntityQuery extends BaseQuery {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2283067882928643392L;
	
	private String address="";
    private String areaName="";
    private int areaId;
    private int row;
    private int deviceType;
    private int ifDealAlarm;
    private String latitude="";
    private String longitude="";
    private String mac="";
    private String name="";				
    private int netState;
    private String placeType="";
    private String placeeAddress="";
    private String principal1="";
    private String principal1Phone="";
    private String principal2="";
    private String principal2Phone="";
    private String repeater="";
    private String placeTypeId="";
    private String placeTypeName="";
    private String characterName="";
    private String addTime="";
    private String addUserId="";
    private int characterId;
    private int privilege;
    private String userId;
    private String deviceName="";
    private int parentId = 0;
    
    private String floors = "";
    private String storeys = "";
    private String positions = "";
    private String J_xl_1="";
    private String J_xl_2="";
    private List<String> areaIds;
    
    public List<String> getAreaIds() {
		return areaIds;
	}

	public void setAreaIds(List<String> areaIds) {
		this.areaIds = areaIds;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
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

	public String getFloors() {
		return floors;
	}

	public void setFloors(String floors) {
		this.floors = floors;
	}

	public String getStoreys() {
		return storeys;
	}

	public void setStoreys(String storeys) {
		this.storeys = storeys;
	}

	public String getPositions() {
		return positions;
	}

	public void setPositions(String positions) {
		this.positions = positions;
	}

	public String getDeviceName() {
		return deviceName;
	}
    
    public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
    
    public void setUserId(String userId) {
		this.userId = userId;
	}
    
    public String getUserId() {
		return userId;
	}
    
    public void setPrivilege(int privilege) {
		this.privilege = privilege;
	}
    
    public int getPrivilege() {
		return privilege;
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
	public int getAreaId() {
		return areaId;
	}
	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}
	public int getIfDealAlarm() {
		return ifDealAlarm;
	}
	public void setIfDealAlarm(int ifDealAlarm) {
		this.ifDealAlarm = ifDealAlarm;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getNetState() {
		return netState;
	}
	public void setNetState(int netState) {
		this.netState = netState;
	}
	public String getPlaceType() {
		return placeType;
	}
	public void setPlaceType(String placeType) {
		this.placeType = placeType;
	}
	public String getPlaceeAddress() {
		return placeeAddress;
	}
	public void setPlaceeAddress(String placeeAddress) {
		this.placeeAddress = placeeAddress;
	}
	public String getPrincipal1() {
		return principal1;
	}
	public void setPrincipal1(String principal1) {
		this.principal1 = principal1;
	}
	public String getPrincipal1Phone() {
		return principal1Phone;
	}
	public void setPrincipal1Phone(String principal1Phone) {
		this.principal1Phone = principal1Phone;
	}
	public String getPrincipal2() {
		return principal2;
	}
	public void setPrincipal2(String principal2) {
		this.principal2 = principal2;
	}
	public String getPrincipal2Phone() {
		return principal2Phone;
	}
	public void setPrincipal2Phone(String principal2Phone) {
		this.principal2Phone = principal2Phone;
	}
	public String getRepeater() {
		return repeater;
	}
	public void setRepeater(String repeater) {
		this.repeater = repeater;
	}
	public String getPlaceTypeId() {
		return placeTypeId;
	}
	public void setPlaceTypeId(String placeTypeId) {
		this.placeTypeId = placeTypeId;
	}
	public String getPlaceTypeName() {
		return placeTypeName;
	}
	public void setPlaceTypeName(String placeTypeName) {
		this.placeTypeName = placeTypeName;
	}
	public String getCharacterName() {
		return characterName;
	}
	public void setCharacterName(String characterName) {
		this.characterName = characterName;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public String getAddUserId() {
		return addUserId;
	}
	public void setAddUserId(String addUserId) {
		this.addUserId = addUserId;
	}
	public int getCharacterId() {
		return characterId;
	}
	public void setCharacterId(int characterId) {
		this.characterId = characterId;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
