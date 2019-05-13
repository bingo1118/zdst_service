package com.cloudfire.entity;

public class CameraBean {
	/**
     * areaName : 南凤派出所辖区
     * cameraAddress : 中国广东省广州市天河区黄埔大道西548号
     * cameraId : 3121638
     * cameraName : 陪伴随意
     * cameraPwd : 123
     * latitude : 23.131682
     * longitude : 113.350275
     * mac1 : 1
     * mac2 : 1
     * mac3 : 1
     * mac4 : 1
     * mac5 : 1
     * placeType :
     * principal1 :
     * principal1Phone :
     * principal2 :
     * principal2Phone :
     */
    private String areaName="";
    private String cameraAddress="";
    private String cameraId="";
    private String cameraName="";
    private String cameraPwd="";
    private String latitude="";
    private String longitude="";
    private String placeType="";
    private String principal1="";
    private String principal1Phone="";
   
	private String principal2="";
    private String principal2Phone="";
    private int position;
    private int size;
    private String videoPosition;
    private String videoSize;
    private String mac1;
    private String mac2;
    private String mac3;
    private String mac4;
    private String mac5;
    
    public String getMac1() {
		return mac1;
	}

	public void setMac1(String mac1) {
		this.mac1 = mac1;
	}

	public String getMac2() {
		return mac2;
	}

	public void setMac2(String mac2) {
		this.mac2 = mac2;
	}

	public String getMac3() {
		return mac3;
	}

	public void setMac3(String mac3) {
		this.mac3 = mac3;
	}

	public String getMac4() {
		return mac4;
	}

	public void setMac4(String mac4) {
		this.mac4 = mac4;
	}

	public String getMac5() {
		return mac5;
	}

	public void setMac5(String mac5) {
		this.mac5 = mac5;
	}

	public String getVideoPosition() {
		return videoPosition;
	}

	public void setVideoPosition(String videoPosition) {
		this.videoPosition = videoPosition;
	}

	public String getVideoSize() {
		return videoSize;
	}

	public void setVideoSize(String videoSize) {
		this.videoSize = videoSize;
	}

	public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getCameraAddress() {
        return cameraAddress;
    }

    public void setCameraAddress(String cameraAddress) {
        this.cameraAddress = cameraAddress;
    }

    public String getCameraId() {
        return cameraId;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public String getCameraPwd() {
        return cameraPwd;
    }

    public void setCameraPwd(String cameraPwd) {
        this.cameraPwd = cameraPwd;
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
    
    public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}


}
