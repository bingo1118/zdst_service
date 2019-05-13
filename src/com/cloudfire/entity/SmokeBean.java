package com.cloudfire.entity;

import java.util.List;

public class SmokeBean {
	private String address="";
    private String areaName="";
    private int areaId;
    private int parentId;
    private int row;
    private byte[] addressbyte;
    private byte[] placeaddressbyte;
    private byte[] smokeNamed;
    private String lowVoltage;
    
    public String getLowVoltage() {
		return lowVoltage;
	}

	public void setLowVoltage(String lowVoltage) {
		this.lowVoltage = lowVoltage;
	}

	public byte[] getSmokeNamed() {
		return smokeNamed;
	}

	public void setSmokeNamed(byte[] smokeNamed) {
		this.smokeNamed = smokeNamed;
	}

	public byte[] getAddressbyte() {
		return addressbyte;
	}

	public void setAddressbyte(byte[] addressbyte) {
		this.addressbyte = addressbyte;
	}

	public byte[] getPlaceaddressbyte() {
		return placeaddressbyte;
	}

	public void setPlaceaddressbyte(byte[] placeaddressbyte) {
		this.placeaddressbyte = placeaddressbyte;
	}

	/**
     * areaName :
     * cameraAddress :
     * cameraId :
     * cameraName :
     * cameraPwd :
     * latitude :
     * longitude :
     * placeType :
     * principal1 :
     * principal1Phone :
     * principal2 :
     * principal2Phone :
     */

    private CameraBean camera;
    private int deviceType;
    private String deviceTypeName;
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
    private int ifAlarm;
    private CountValue cv;
    private int electrState;
    private String heartime;
    private String rssivalue;
    
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

	public String getHeartime() {
		return heartime;
	}

	public void setHeartime(String heartime) {
		this.heartime = heartime;
	}

	public CountValue getCv() {
		return cv;
	}

	public void setCv(CountValue cv) {
		this.cv = cv;
	}

	public int getIfAlarm() {
		return ifAlarm;
	}

	public void setIfAlarm(int ifAlarm) {
		this.ifAlarm = ifAlarm;
	}

	/**
     * add lzo by time with 2017-4-27
     * @return fenye
     */
    private int currentPage; // 当前页, 默认显示第一页
	private int totalCount;      // 总记录数
	private int totalPage;       // 总页数 
	private int count;	//统计区域类的声光数量
	private List<String> list;	//存储声光的mac地址
	
	public void setList(List<String> list) {
		this.list = list;
	}
	
	public List<String> getList() {
		return list;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	public int getCount() {
		return count;
	}
	
	public void setRow(int row) {
		this.row = row;
	}
	
    public int getRow() {
		return row;
	}
	
    public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getCharacterId() {
		return characterId;
	}

	public void setCharacterId(int characterId) {
		this.characterId = characterId;
	}

	public String getAddUserId() {
		return addUserId;
	}

	public void setAddUserId(String addUserId) {
		this.addUserId = addUserId;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public int getAreaId() {
		return areaId;
	}

	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}

	public String getCharacterName() {
		return characterName;
	}

	public void setCharacterName(String characterName) {
		this.characterName = characterName;
	}

	public String getPlaceTypeName() {
		return placeTypeName;
	}

	public void setPlaceTypeName(String placeTypeName) {
		this.placeTypeName = placeTypeName;
	}

	public String getPlaceTypeId() {
		return placeTypeId;
	}

	public void setPlaceTypeId(String placeTypeId) {
		this.placeTypeId = placeTypeId;
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

    public CameraBean getCamera() {
        return camera;
    }

    public void setCamera(CameraBean camera) {
        this.camera = camera;
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

	public SmokeBean() {
		super();
	}

	public SmokeBean(String address, String areaName, int areaId,
			CameraBean camera, int deviceType, int ifDealAlarm,
			String latitude, String longitude, String mac, String name,
			int netState, String placeType, String placeeAddress,
			String principal1, String principal1Phone, String principal2,
			String principal2Phone, String repeater, String placeTypeId,
			String placeTypeName, String characterName, String addTime,
			String addUserId, int characterId) {
		super();
		this.address = address;
		this.areaName = areaName;
		this.areaId = areaId;
		this.camera = camera;
		this.deviceType = deviceType;
		this.ifDealAlarm = ifDealAlarm;
		this.latitude = latitude;
		this.longitude = longitude;
		this.mac = mac;
		this.name = name;
		this.netState = netState;
		this.placeType = placeType;
		this.placeeAddress = placeeAddress;
		this.principal1 = principal1;
		this.principal1Phone = principal1Phone;
		this.principal2 = principal2;
		this.principal2Phone = principal2Phone;
		this.repeater = repeater;
		this.placeTypeId = placeTypeId;
		this.placeTypeName = placeTypeName;
		this.characterName = characterName;
		this.addTime = addTime;
		this.addUserId = addUserId;
		this.characterId = characterId;
	}

	public int getElectrState() {
		return electrState;
	}

	public void setElectrState(int electrState) {
		this.electrState = electrState;
	}
    
   private String alarmName;

	public String getAlarmName() {
		return alarmName;
	}
	
	public void setAlarmName(String alarmName) {
		this.alarmName = alarmName;
	}
   
	
	private int interval1; //上报时间间隔
	private int interval2; //采集时间间隔
	private String highGage; //高阈值
	private String lowGage; //低阈值
	private int offTime; //离线时间
	private String adjusted;//修正值
	
	public String getAdjusted() {
		return adjusted;
	}

	public void setAdjusted(String adjusted) {
		this.adjusted = adjusted;
	}

	public int getOffTime() {
		return offTime;
	}

	public void setOffTime(int offTime) {
		this.offTime = offTime;
	}

	public String getHighGage() {
		return highGage;
	}

	public void setHighGage(String highGage) {
		this.highGage = highGage;
	}

	public String getLowGage() {
		return lowGage;
	}

	public void setLowGage(String lowGage) {
		this.lowGage = lowGage;
	}

	public int getInterval1() {
		return interval1;
	}

	public void setInterval1(int interval1) {
		this.interval1 = interval1;
	}

	public int getInterval2() {
		return interval2;
	}

	public void setInterval2(int interval2) {
		this.interval2 = interval2;
	}
	
	private String highTemp;  //高温
	private String lowTemp;  //低温
	private String highHumi;  //高湿度
	private String lowHumi;  //低湿度

	public String getHighTemp() {
		return highTemp;
	}

	public void setHighTemp(String string) {
		this.highTemp = string;
	}

	public String getLowTemp() {
		return lowTemp;
	}

	public void setLowTemp(String lowTemp) {
		this.lowTemp = lowTemp;
	}

	public String getHighHumi() {
		return highHumi;
	}

	public void setHighHumi(String highHumi) {
		this.highHumi = highHumi;
	}

	public String getLowHumi() {
		return lowHumi;
	}

	public void setLowHumi(String lowHumi) {
		this.lowHumi = lowHumi;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public String getDeviceTypeName() {
		return deviceTypeName;
	}

	public void setDeviceTypeName(String deviceTypeName) {
		this.deviceTypeName = deviceTypeName;
	}
	
}
