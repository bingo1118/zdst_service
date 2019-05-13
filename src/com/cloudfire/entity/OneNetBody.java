package com.cloudfire.entity;

import java.util.Map;

public class OneNetBody {
	private String title;  //�豸��������
	private String desc;  //�豸��������ѡ
	private String[] tags;  //eg. ["china","mobile"],�豸��ǩ����ѡ��һ������
	private String protocol; //eg."LWM2M", ����Э�飬����
	private Map<String,Float> location; //eg. {"lon": 106, "lat": 29, "ele": 370},  �豸λ�ã���γ���߶ȣ���ѡ
//	private boolean private;
	private Map<String,String> auth_info; //NBIOT�豸{imei:imsi}(len<=17:len<=16) ����
	private boolean obsv; //true|false, /�Ƿ����豸��Դ��Ĭ��Ϊtrue
	private Map<String,String> other; //eg. {"version": "1.0.0",  "manu": "china mobile"}   ������Ϣ����ѡ��JSON��ʽ�����Զ��壩

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
//		"mydevice", //�豸��,
//		"desc": "some description", //�豸��������ѡ��
//		"tags":["china","mobile"], //�豸��ǩ����ѡ����Ϊһ��������
//		"protocol":"LWM2M", //����Э��
//		"location": {"lon":106, "lat": 29, "ele": 370}, //�豸λ��{"γ��", "����", "�߶�"}����ѡ��
//		"private": true | false, //�豸˽���ԣ���ѡ��Ĭ��Ϊture��
//		"auth_info":{"xxxxxxxxxxxx":"xxxxxxxxxxxxxx"}, //NBIOT�豸��{"imei��"��"imsi��"}��imei��������17λ����imsi��������16λ���������ֻ�����ĸ���
//		"obsv":true|false,,//�Ƿ����豸��Դ��Ĭ��Ϊtrue
//		"other":{"version": "1.0.0", "manu": "china mobile"},//������Ϣ����ѡ��JSON��ʽ�����Զ��壩
	
	
}
