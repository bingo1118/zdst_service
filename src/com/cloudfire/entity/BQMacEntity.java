package com.cloudfire.entity;

import com.cloudfire.until.BaseQuery;

public class BQMacEntity extends BaseQuery{

	private static final long serialVersionUID = 1L;
	private String deviceId;		//�豸Id
	private String named;		//�豸����
	private String devicetype;	//�豸��������
	private String projectName;	//������Ŀ����
	private String address;	//��װλ��
	private int status;//״̬
	private String createTime;//��װʱ��
	private String statusStr;
	
	private String begintime;
	private String endtime;
	
	
	
	
	
	public String getBegintime() {
		return begintime;
	}
	public void setBegintime(String begintime) {
		this.begintime = begintime;
	}
	public String getEndtime() {
		return endtime;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
	public String getStatusStr() {
		return statusStr;
	}
	public void setStatusStr(String statusStr) {
		this.statusStr = statusStr;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getNamed() {
		return named;
	}
	public void setNamed(String named) {
		this.named = named;
	}
	public String getDevicetype() {
		return devicetype;
	}
	public void setDevicetype(String devicetype) {
		this.devicetype = devicetype;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public BQMacEntity(String deviceId, String named, String devicetype,
			String projectName, String address) {
		super();
		this.deviceId = deviceId;
		this.named = named;
		this.devicetype = devicetype;
		this.projectName = projectName;
		this.address = address;
	}
	public BQMacEntity() {
		super();
	}
	@Override
	public String toString() {
		return "BQMacEntity [deviceId=" + deviceId + ", named=" + named
				+ ", devicetype=" + devicetype + ", projectName=" + projectName
				+ ", address=" + address + "]";
	}

	
	
	
	
	
	

}
