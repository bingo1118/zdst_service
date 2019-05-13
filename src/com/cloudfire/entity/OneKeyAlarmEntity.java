package com.cloudfire.entity;

public class OneKeyAlarmEntity {
	  /**
     * address : 中国广东省广州市天河区黄埔大道西532号
     * alarmFamily : 2
     * alarmSerialNumber : 2017-01-04 09:21:29:814--13622215085
     * alarmTime : 2017-01-04 09:21:29
     * alarmType : 3
     * areaName : 天河区
     * callerId : 13622215085
     * callerName : 1
     * deviceType : 6
     * info : 7777777
     * latitude : 23.131814
     * longitude : 113.35014
     * smoke : 29391730
     */

    private String address="";
    private int alarmFamily;
    private String alarmSerialNumber="";
    private String alarmTime="";
    private int alarmType;
    private String areaName="";
    private String callerId="";
    private String callerName="";
    private int deviceType;
    private String info="";
    private String latitude="";
    private String longitude="";
    private String smoke="";

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

    public String getAlarmSerialNumber() {
        return alarmSerialNumber;
    }

    public void setAlarmSerialNumber(String alarmSerialNumber) {
        this.alarmSerialNumber = alarmSerialNumber;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
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

    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
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

    public String getSmoke() {
        return smoke;
    }

    public void setSmoke(String smoke) {
        this.smoke = smoke;
    }
}
