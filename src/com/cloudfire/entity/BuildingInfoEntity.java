package com.cloudfire.entity;

public class BuildingInfoEntity {
	private String faultMac = "";//����MAC
	private String bid = "0";//�� ������������id
	private String xzqy = "";//�� ����������
	private String name ="";//�� ��������
	private String address = "";//  �� ������ַ
	private String linkman = "";  //�� ��ϵ��
	private String linkphone = "";//�� ��ϵ�绰
	private String buildstate = "";//�� �������
	private String buildtime = "";//�� ����ʱ��
	private String area = "0.00"; //�� �������
	private String zdarea = "0.00";//  �� ռ�����
	private String high = "0.00";// �� �����߶�
	private String bzfloorarea = "0.00";// �� ��׼�����
	private int overfloor = 0;// �� ���ϲ���
	private int underfloor = 0 ;// �� ���²���
	private String jzsort = "";// �� �������ࣨ�漰�����������ֵ��
	private String jzconfig = "";// �� �����ṹ�������ֵ��dm_xfjd_jzjg��
	private String jzconfigother ="";// �� ���������ṹ
	private String xfkzsplace ="";// �� ����������λ��
	private String nhgrade =""; //�� �ͻ�ȼ��������ֵ��dm_xfjd_jznhdj��
	private String gis_x_gd = "0";// �� �ߵµ�λγ��
	private String gis_y_gd = "0";// �� �ߵµ�λ����
	private String intime = "";// �� ¼��ʱ��
	private String chgtime = "";// �� �޸�ʱ��
	private char isxfkzs = 0;//�����������������0 ��1
	private String jdxx ="";// �� �ֵ�
	private String jdxxNum = "";//�� �ֵ����
	private String street = "";//�� ·
	private String street_number ="";// �� �š�Ū
	private String street_remark ="";// �� ��ַ��ϸ��Ϣ
	private String chgacc ="";// �� �޸���
	private String wydw ="";// �� ��ҵ��λ
	private String wydwplxr = "";// �� ��ҵ��λ��ϵ��
	private String wlwtype = ""; //�� ������������Դ
	private String jzxfsswbdw = "";// �� ����������ʩά����λ
	
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


//bid int ��������������id
//xzqy String ������������
//name String �ǽ�������
//address String �ǽ�����ַ
//linkman String ����ϵ��
//linkphone String ����ϵ�绰
//buildstate String �������
//buildtime String �񿢹�ʱ��
//area float �ǽ������
//zdarea float ��ռ�����
//high float �ǽ����߶�
//bzfloorarea float ���׼�����
//overfloor int �ǵ��ϲ���
//underfloor int �ǵ��²���
//jzsort String �ǽ������ࣨ�漰�����������ֵ��
//jzconfig String �ǽ����ṹ�������ֵ��dm_xfjd_jzjg��
//jzconfigother String ���������ṹ
//xfkzsplace String ������������λ��
//nhgrade String ���ͻ�ȼ��������ֵ��dm_xfjd_jznhdj��
//gis_x_gd double �Ǹߵµ�λγ��
//gis_y_gd double �Ǹߵµ�λ����
//intime String ��¼��ʱ��
//chgtime String ���޸�ʱ��
//isxfkzs char �����������������0 ��1
//jdxx String ��ֵ�
//jdxxNum number �ǽֵ����
//street String ��·
//street_number String ��š�Ū
//street_remark String ���ַ��ϸ��Ϣ
//chgacc String ���޸���
//wydw String ����ҵ��λ
//wydwplxr String ����ҵ��λ��ϵ��
//wlwtype String ��������������Դ
//jzxfsswbdw String ����������ʩά����λ
