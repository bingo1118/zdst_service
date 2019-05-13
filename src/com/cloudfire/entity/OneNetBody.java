package com.cloudfire.entity;

import java.util.Map;

public class OneNetBody {
	private String title;  //设备名，必填
	private String desc;  //设备描述，可选
	private String[] tags;  //eg. ["china","mobile"],设备标签，可选，一个或多个
	private String protocol; //eg."LWM2M", 接入协议，必填
	private Map<String,Float> location; //eg. {"lon": 106, "lat": 29, "ele": 370},  设备位置，经纬，高度，可选
//	private boolean private;
	private Map<String,String> auth_info; //NBIOT设备{imei:imsi}(len<=17:len<=16) 必填
	private boolean obsv; //true|false, /是否订阅设备资源，默认为true
	private Map<String,String> other; //eg. {"version": "1.0.0",  "manu": "china mobile"}   其他信息（可选，JSON格式，可自定义）

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String[] getTags() {
		return tags;
	}
	public void setTags(String[] tags) {
		this.tags = tags;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public Map<String, Float> getLocation() {
		return location;
	}
	public void setLocation(Map<String, Float> location) {
		this.location = location;
	}
	public Map<String, String> getAuth_info() {
		return auth_info;
	}
	public void setAuth_info(Map<String, String> auth_info) {
		this.auth_info = auth_info;
	}
	public boolean isObsv() {
		return obsv;
	}
	public void setObsv(boolean obsv) {
		this.obsv = obsv;
	}
	public Map<String, String> getOther() {
		return other;
	}
	public void setOther(Map<String, String> other) {
		this.other = other;
	}
	
	
//	"title":
//		"mydevice", //设备名,
//		"desc": "some description", //设备描述（可选）
//		"tags":["china","mobile"], //设备标签（可选，可为一个或多个）
//		"protocol":"LWM2M", //接入协议
//		"location": {"lon":106, "lat": 29, "ele": 370}, //设备位置{"纬度", "精度", "高度"}（可选）
//		"private": true | false, //设备私密性（可选，默认为ture）
//		"auth_info":{"xxxxxxxxxxxx":"xxxxxxxxxxxxxx"}, //NBIOT设备：{"imei码"："imsi码"}，imei（不超过17位）和imsi（不超过16位）都由数字或者字母组成
//		"obsv":true|false,,//是否订阅设备资源，默认为true
//		"other":{"version": "1.0.0", "manu": "china mobile"},//其他信息（可选，JSON格式，可自定义）
	
	
}
