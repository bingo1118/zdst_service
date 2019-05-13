package com.cloudfire.entity;

import java.io.Serializable;

public class PushAlarmMsgEntity implements Serializable,Cloneable{
	/**
     * address : 中国广东省广州市天河区黄埔大道西540号
     * alarmFamily : 1 报警值
     * alarmTime : 2016-12-13 14:42:07  报警时间
     * alarmType : 202   报警状态
     * areaId : 王之领地  区域
     * camera : {"cameraId":"3120987","cameraPwd":"123"}   
     * deviceType : 1  设备类型
     * ifDealAlarm : 0   
     * latitude : 24.131832   经度
     * longitude : 113.350209   纬度
     * mac : AFCC43CC     地址
     * name : 8    名称
     * placeAddress :	
     * placeType : 烧烤   商业类型
     * principal1 :	责任人一
     * principal1Phone :   责任人一联系方式
     * principal2 :
     * principal2Phone :
     */

    private String address="";
    private String alarmFamily="0";
    private String alarmTime="";
    private String alarmType;
    private String areaId="";
    private String pushIdAdress = "";
    
    public String getPushIdAdress() {
		return pushIdAdress;
	}

	public void setPushIdAdress(String pushIdAdress) {
		this.pushIdAdress = pushIdAdress;
	}

	/**
     * cameraId : 3120987
     * cameraPwd : 123
     */

    private CameraBean camera;
    private int deviceType;
    private int ifDealAlarm;
    private String latitude="";
    private String longitude="";
    private String mac="";
    private String name="";
    private String placeAddress="";
    private String placeType="";
    private String principal1="";
    private String principal1Phone="";
    private String principal2="";
    private String principal2Phone="";
    
    private String uploadpeople="";//确认报警上报人员账号

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAlarmFamily() {
        return alarmFamily;
    }

    public void setAlarmFamily(String alarmFamily) {
        this.alarmFamily = alarmFamily;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
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
    
    @Override  
    public Object clone() {  
        PushAlarmMsgEntity stu = null;  
        try{  
            stu = (PushAlarmMsgEntity)super.clone();  
        }catch(CloneNotSupportedException e) {  
            e.printStackTrace();  
        }  
        return stu;  
    }  

    public String getUploadpeople() {
		return uploadpeople;
	}

	public void setUploadpeople(String uploadpeople) {
		this.uploadpeople = uploadpeople;
	}

	public static class CameraBean implements Serializable{
        private String cameraId="";
        private String cameraPwd="";

        public String getCameraId() {
            return cameraId;
        }

        public void setCameraId(String cameraId) {
            this.cameraId = cameraId;
        }

        public String getCameraPwd() {
            return cameraPwd;
        }

        public void setCameraPwd(String cameraPwd) {
            this.cameraPwd = cameraPwd;
        }
    }
}
