package com.cloudfire.entity;

public class BuildingInfoEntity {
	private String faultMac = "";//主机MAC
	private String bid = "0";//是 物联网建筑物id
	private String xzqy = "";//是 行政区域编号
	private String name ="";//是 建筑名称
	private String address = "";//  是 建筑地址
	private String linkman = "";  //否 联系人
	private String linkphone = "";//否 联系电话
	private String buildstate = "";//否 建筑情况
	private String buildtime = "";//否 竣工时间
	private String area = "0.00"; //是 建筑面积
	private String zdarea = "0.00";//  是 占地面积
	private String high = "0.00";// 是 建筑高度
	private String bzfloorarea = "0.00";// 否 标准层面积
	private int overfloor = 0;// 是 地上层数
	private int underfloor = 0 ;// 是 地下层数
	private String jzsort = "";// 是 建筑分类（涉及到建筑分类字典表）
	private String jzconfig = "";// 是 建筑结构（数据字典表dm_xfjd_jzjg）
	private String jzconfigother ="";// 否 建筑其他结构
	private String xfkzsplace ="";// 否 消防控制室位置
	private String nhgrade =""; //否 耐火等级（数据字典表dm_xfjd_jznhdj）
	private String gis_x_gd = "0";// 是 高德点位纬度
	private String gis_y_gd = "0";// 是 高德点位经度
	private String intime = "";// 是 录入时间
	private String chgtime = "";// 是 修改时间
	private char isxfkzs = 0;//是消防控制室情况有0 无1
	private String jdxx ="";// 否 街道
	private String jdxxNum = "";//是 街道编号
	private String street = "";//否 路
	private String street_number ="";// 否 号、弄
	private String street_remark ="";// 否 地址详细信息
	private String chgacc ="";// 是 修改人
	private String wydw ="";// 是 物业单位
	private String wydwplxr = "";// 是 物业单位联系人
	private String wlwtype = ""; //是 物联网操作来源
	private String jzxfsswbdw = "";// 否 建筑消防设施维保单位
	
	public String getFaultMac() {
		return faultMac;
	}
	public void setFaultMac(String faultMac) {
		this.faultMac = faultMac;
	}
	public String getBid() {
		return bid;
	}
	public void setBid(String bid) {
		this.bid = bid;
	}
	public String getXzqy() {
		return xzqy;
	}
	public void setXzqy(String xzqy) {
		this.xzqy = xzqy;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getLinkman() {
		return linkman;
	}
	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}
	public String getLinkphone() {
		return linkphone;
	}
	public void setLinkphone(String linkphone) {
		this.linkphone = linkphone;
	}
	public String getBuildstate() {
		return buildstate;
	}
	public void setBuildstate(String buildstate) {
		this.buildstate = buildstate;
	}
	public String getBuildtime() {
		return buildtime;
	}
	public void setBuildtime(String buildtime) {
		this.buildtime = buildtime;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getZdarea() {
		return zdarea;
	}
	public void setZdarea(String zdarea) {
		this.zdarea = zdarea;
	}
	public String getHigh() {
		return high;
	}
	public void setHigh(String high) {
		this.high = high;
	}
	public String getBzfloorarea() {
		return bzfloorarea;
	}
	public void setBzfloorarea(String bzfloorarea) {
		this.bzfloorarea = bzfloorarea;
	}
	public int getOverfloor() {
		return overfloor;
	}
	public void setOverfloor(int overfloor) {
		this.overfloor = overfloor;
	}
	public int getUnderfloor() {
		return underfloor;
	}
	public void setUnderfloor(int underfloor) {
		this.underfloor = underfloor;
	}
	public String getJzsort() {
		return jzsort;
	}
	public void setJzsort(String jzsort) {
		this.jzsort = jzsort;
	}
	public String getJzconfig() {
		return jzconfig;
	}
	public void setJzconfig(String jzconfig) {
		this.jzconfig = jzconfig;
	}
	public String getJzconfigother() {
		return jzconfigother;
	}
	public void setJzconfigother(String jzconfigother) {
		this.jzconfigother = jzconfigother;
	}
	public String getXfkzsplace() {
		return xfkzsplace;
	}
	public void setXfkzsplace(String xfkzsplace) {
		this.xfkzsplace = xfkzsplace;
	}
	public String getNhgrade() {
		return nhgrade;
	}
	public void setNhgrade(String nhgrade) {
		this.nhgrade = nhgrade;
	}
	public String getGis_x_gd() {
		return gis_x_gd;
	}
	public void setGis_x_gd(String gis_x_gd) {
		this.gis_x_gd = gis_x_gd;
	}
	public String getGis_y_gd() {
		return gis_y_gd;
	}
	public void setGis_y_gd(String gis_y_gd) {
		this.gis_y_gd = gis_y_gd;
	}
	public String getIntime() {
		return intime;
	}
	public void setIntime(String intime) {
		this.intime = intime;
	}
	public String getChgtime() {
		return chgtime;
	}
	public void setChgtime(String chgtime) {
		this.chgtime = chgtime;
	}
	public char getIsxfkzs() {
		return isxfkzs;
	}
	public void setIsxfkzs(char isxfkzs) {
		this.isxfkzs = isxfkzs;
	}
	public String getJdxx() {
		return jdxx;
	}
	public void setJdxx(String jdxx) {
		this.jdxx = jdxx;
	}
	public String getJdxxNum() {
		return jdxxNum;
	}
	public void setJdxxNum(String jdxxNum) {
		this.jdxxNum = jdxxNum;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getStreet_number() {
		return street_number;
	}
	public void setStreet_number(String street_number) {
		this.street_number = street_number;
	}
	public String getStreet_remark() {
		return street_remark;
	}
	public void setStreet_remark(String street_remark) {
		this.street_remark = street_remark;
	}
	public String getChgacc() {
		return chgacc;
	}
	public void setChgacc(String chgacc) {
		this.chgacc = chgacc;
	}
	public String getWydw() {
		return wydw;
	}
	public void setWydw(String wydw) {
		this.wydw = wydw;
	}
	public String getWydwplxr() {
		return wydwplxr;
	}
	public void setWydwplxr(String wydwplxr) {
		this.wydwplxr = wydwplxr;
	}
	public String getWlwtype() {
		return wlwtype;
	}
	public void setWlwtype(String wlwtype) {
		this.wlwtype = wlwtype;
	}
	public String getJzxfsswbdw() {
		return jzxfsswbdw;
	}
	public void setJzxfsswbdw(String jzxfsswbdw) {
		this.jzxfsswbdw = jzxfsswbdw;
	}
}


//bid int 是物联网建筑物id
//xzqy String 是行政区域编号
//name String 是建筑名称
//address String 是建筑地址
//linkman String 否联系人
//linkphone String 否联系电话
//buildstate String 否建筑情况
//buildtime String 否竣工时间
//area float 是建筑面积
//zdarea float 是占地面积
//high float 是建筑高度
//bzfloorarea float 否标准层面积
//overfloor int 是地上层数
//underfloor int 是地下层数
//jzsort String 是建筑分类（涉及到建筑分类字典表）
//jzconfig String 是建筑结构（数据字典表dm_xfjd_jzjg）
//jzconfigother String 否建筑其他结构
//xfkzsplace String 否消防控制室位置
//nhgrade String 否耐火等级（数据字典表dm_xfjd_jznhdj）
//gis_x_gd double 是高德点位纬度
//gis_y_gd double 是高德点位经度
//intime String 是录入时间
//chgtime String 是修改时间
//isxfkzs char 是消防控制室情况有0 无1
//jdxx String 否街道
//jdxxNum number 是街道编号
//street String 否路
//street_number String 否号、弄
//street_remark String 否地址详细信息
//chgacc String 是修改人
//wydw String 是物业单位
//wydwplxr String 是物业单位联系人
//wlwtype String 是物联网操作来源
//jzxfsswbdw String 否建筑消防设施维保单位
