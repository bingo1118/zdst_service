package com.cloudfire.entity;

import java.util.HashMap;
import java.util.Map;

public class GpsEntityBean {
	
	private String devMac;				//GPSID
	private String longitude;			//经度
	private String latitude;			//纬度
	private String speeds;				//航速
	private String heading;				//航向
	private String basestation; 		//'基站',
	private int positions;				//'1已定位，0未定位',
	private String dataTime;			// '获取数据时间',
	private Map<String,String> gpsMap;	//ID-对应-IP
	private String ipstr;				//IP地址
	private byte[] dataM;				//回复ACK
	private int gpsState = 0;			//状态
	private String named;				//名称
	private String memos;				//备注
	
	public String getNamed() {
		return named;
	}
	public void setNamed(String named) {
		this.named = named;
	}
	public String getMemos() {
		return memos;
	}
	public void setMemos(String memos) {
		this.memos = memos;
	}
	public byte[] getDataM() {
		return dataM;
	}
	public void setDataM(byte[] dataM) {
		this.dataM = dataM;
	}
	private static GpsEntityBean gpsBean = null;
	public int getGpsState() {
		return gpsState;
	}
	public void setGpsState(int gpsState) {
		this.gpsState = gpsState;
	}
	public static GpsEntityBean newInstance(){
		if(gpsBean == null){
			gpsBean = new GpsEntityBean();
		}
		return gpsBean;
	}
	public String getDevMac() {
		return devMac;
	}
	public void setDevMac(String devMac) {
		this.devMac = devMac;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getSpeeds() {
		return speeds;
	}
	public void setSpeeds(String speeds) {
		this.speeds = speeds;
	}
	public String getHeading() {
		return heading;
	}
	public void setHeading(String heading) {
		this.heading = heading;
	}
	public String getBasestation() {
		return basestation;
	}
	public void setBasestation(String basestation) {
		this.basestation = basestation;
	}
	public int getPositions() {
		return positions;
	}
	public void setPositions(int positions) {
		this.positions = positions;
	}
	public String getDataTime() {
		return dataTime;
	}
	public void setDataTime(String dataTime) {
		this.dataTime = dataTime;
	}
	public Map<String, String> getGpsMap() {
		if(gpsMap==null){
			gpsMap = new HashMap<String,String>();
		}
		return gpsMap;
	}
	public void setGpsMap(Map<String, String> gpsMap) {
		this.gpsMap = gpsMap;
	}
	public String getIpstr() {
		return ipstr;
	}
	public void setIpstr(String ipstr) {
		this.ipstr = ipstr;
	}
}
