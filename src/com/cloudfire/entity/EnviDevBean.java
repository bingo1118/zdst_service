package com.cloudfire.entity;

import java.util.List;

public class EnviDevBean implements Comparable{
	private String address="";
    private String areaName="";
    private int areaId;
    private int row;
    private byte[] addressbyte;
    private byte[] placeaddressbyte;
    
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

    private EnvironmentEntity enviInfo;
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
    private int ifAlarm;
    private CountValue cv;
    
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

	public EnviDevBean() {
		super();
	}

	public EnviDevBean(String address, String areaName, int areaId,
			EnvironmentEntity camera, int deviceType, int ifDealAlarm,
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
		this.enviInfo = camera;
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

	public EnvironmentEntity getEnviInfo() {
		return enviInfo;
	}

	public void setEnviInfo(EnvironmentEntity enviInfo) {
		this.enviInfo = enviInfo;
	}

	@Override
	public int compareTo(Object o) {
			EnviDevBean sdto = (EnviDevBean)o;
	       int otherAge = sdto.getEnviInfo().getPriority();
	       int my=this.enviInfo.getPriority();
	       if(my<=otherAge){
	    	   return -1;
	       }else{
	    	   return 1;
	       }
	}
}
