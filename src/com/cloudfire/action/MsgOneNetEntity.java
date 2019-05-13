package com.cloudfire.action;

public class MsgOneNetEntity {
	private String at;
	private String type;
	private String dev_id;
	private String imei;
	
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	//数据消息
	private String ds_id;
	private String value;

	//设备在线消息
	private String status;
	private String  login_type;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getLogin_type() {
		return login_type;
	}
	public void setLogin_type(String login_type) {
		this.login_type = login_type;
	}
	public String getAt() {
		return at;
	}
	public void setAt(String at) {
		this.at = at;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDs_id() {
		return ds_id;
	}
	public void setDs_id(String ds_id) {
		this.ds_id = ds_id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDev_id() {
		return dev_id;
	}
	public void setDev_id(String dev_id) {
		this.dev_id = dev_id;
	}
}
