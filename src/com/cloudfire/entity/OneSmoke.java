package com.cloudfire.entity;

public class OneSmoke {
	 private String address="";
	    private int areaId;
	    private String placeTypeId;//@@9.26
	    private String areaName="";
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

	    private CameraBean camera=null;
	    private int deviceType;
	    private int ifDealAlarm;
	    private String latitude="";
	    private String longitude="";
	    private String name="";
	    private int netState;
	    private String placeAddress="";
	    private String placeType="";
	    private String principal1="";
	    private String principal1Phone="";
	    private String principal2="";
	    private String principal2Phone="";
	    private String repeater="";
	    private String deviceTypeName="";

	    public String getAddress() {
	        return address;
	    }

	    public void setAddress(String address) {
	        this.address = address;
	    }

	    public int getAreaId() {
	        return areaId;
	    }

	    public void setAreaId(int areaId) {
	        this.areaId = areaId;
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

	    public String getPlaceAddress() {
	        return placeAddress;
	    }

	    public void setPlaceAddress(String placeAddress) {
	        this.placeAddress = placeAddress;
	    }

	    public String getPlaceType() {
	        return placeType;
	    }

	    public void setPlaceType(String placeType) {
	        this.placeType = placeType;
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

		public String getDeviceTypeName() {
			return deviceTypeName;
		}

		public void setDeviceTypeName(String deviceTypeName) {
			this.deviceTypeName = deviceTypeName;
		}
}
