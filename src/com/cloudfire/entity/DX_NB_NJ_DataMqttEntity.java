package com.cloudfire.entity;

public class DX_NB_NJ_DataMqttEntity {
	
	private String imei;//IMEI对应MAC
	private String imsi;//imsi卡号
	private String address;//地址
	private String time;//添加设备时间
	private String named;//名称
	private String principal1;//联系人
	private String principal1Phone;//联系人电话
	private String dataType;//数据类型（stDevBattery:电池量、stDevSignal：信号强度、HeartBeat：燃气心跳、Alarm：燃气报警、stDevCMD：烟感）	
	private String dataValue;//对应数据类型的数据值（
		/*烟感做特殊处理：0x00 在位	智件在位，工作正常(同时作为心跳使用)
		0x01 告警	智件告警
		0x02 低电	智件电池低电压
		0x08 对码	智件对码
		0x0D 测试	智件测试
		0x11 报警恢复	智件报警恢复
		0x12 低压恢复	智件电池低电压恢复
		0x13 防拆	智件防拆报警
		0x14 防拆恢复	智件防拆恢复
		0x06 撤防(消音)	撤除报警(消音)
		）*/
	private String deviceId;//设备唯一地址
	
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getNamed() {
		return named;
	}
	public void setNamed(String named) {
		this.named = named;
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
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getDataValue() {
		return dataValue;
	}
	public void setDataValue(String dataValue) {
		this.dataValue = dataValue;
	}
}
