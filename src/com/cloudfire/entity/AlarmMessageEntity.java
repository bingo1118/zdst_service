package com.cloudfire.entity;

public class AlarmMessageEntity {
	  /**
     * address : …Ó€⁄ – ∫Ï€Í¥Â12∆Ã
     * alarmFamily : 1
     * alarmTime : 2016-12-27 12:47:47
     * alarmTruth : 0
     * alarmType : 202
     * areaName : ƒœ∑Ô≈…≥ˆÀ˘
     * dealDetail : 
     * dealPeople : 
     * dealTime : 
     * deviceType : 1
     * ifDealAlarm : 0
     * latitude : 22.730546
     * longitude : 113.9363
     * mac : 7E2F1730
     * name : π‚√˜÷£‘£…≠…’¿∞µÍ
     * placeType : ≤Õ“˚µÍ
     * placeeAddress : 
     * principal1 : ÷£‘£…≠
     * principal1Phone : 13433361604
     * principal2 : 
     * principal2Phone : 
     */

    private String address="";
    private int alarmFamily;
    private String alarmTime="";
    private int alarmTruth;
    private int alarmType;
    private String areaName="";
    private String dealDetail="";
    private String dealPeople="";
    private String dealTime="";
    private String deviceType="";
    private int ifDealAlarm;
    private String latitude="";
    private String longitude="";
    private String mac="";
    private String name="";
    private String placeType="";
    private String placeeAddress="";
    private String principal1="";
    private String principal1Phone="";
    private String principal2="";
    private String principal2Phone="";
    private String alarmFamilys;
    private String image_path="";
    private String video_path="";
    private String id;
    
    
    
    public String getImage_path() {
		return image_path;
	}

	public void setImage_path(String image_path) {
		this.image_path = image_path;
	}

	public String getVideo_path() {
		return video_path;
	}

	public void setVideo_path(String video_path) {
		this.video_path = video_path;
	}

	public String getAlarmFamilys() {
		return alarmFamilys;
	}

	public void setAlarmFamilys(String alarmFamilys) {
		this.alarmFamilys = alarmFamilys;
	}

	public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAlarmFamily() {
        return alarmFamily;
    }

    public void setAlarmFamily(int alarmFamily) {
        this.alarmFamily = alarmFamily;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public int getAlarmTruth() {
        return alarmTruth;
    }

    public void setAlarmTruth(int alarmTruth) {
        this.alarmTruth = alarmTruth;
    }

    public int getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(int alarmType) {
        this.alarmType = alarmType;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getDealDetail() {
        return dealDetail;
    }

    public void setDealDetail(String dealDetail) {
        this.dealDetail = dealDetail;
    }

    public String getDealPeople() {
        return dealPeople;
    }

    public void setDealPeople(String dealPeople) {
        this.dealPeople = dealPeople;
    }

    public String getDealTime() {
        return dealTime;
    }

    public void setDealTime(String dealTime) {
        this.dealTime = dealTime;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
