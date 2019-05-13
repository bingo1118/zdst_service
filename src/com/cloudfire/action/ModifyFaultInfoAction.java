package com.cloudfire.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.cloudfire.dao.QP_FaultInfoDao;
import com.cloudfire.dao.impl.QP_FaultInfoDaoImpl;
import com.cloudfire.entity.QP_FaultInfos;
import com.opensymphony.xwork2.ActionSupport;

public class ModifyFaultInfoAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{

	private static final long serialVersionUID = -4581514309685063419L;
	private HttpServletRequest request;
	private HttpServletResponse response;

	private String deviceId;		//varchar(50)��ƽ����װ�ñ��
	private String bid;			//varchar(50)������������id
	private String xzqy;			//varchar(20)����������
	private String name;			//varchar(50)��������
	private String address;		//varchar(50)������ַ
	private String linkman;		//varchar(20)��ϵ��
	private String linkphone;		//varchar(20)��ϵ�绰
	private String buildstate;		//varchar(50)�������
	private String buildtime;		//varchar(20)����ʱ��
	private String area;			//varchar(10)�������
	private String zdarea;			//varchar(10)ռ�����
	private String high;			//varchar(10)�����߶�
	private String bzfloorarea;	//varchar(10)��׼�����
	private String overfloor;		//varchar(5)���ϲ���
	private String underfloor;		//varchar(5)���²���
	private String jzsort;			//varchar(10)��������
	private String jzconfig;		//varchar(20)jzconfig
	private String jzconfigother;	//varchar(20)���������ṹ
	private String xfkzsplace;		//varchar(10)����������λ��
	private String nhgrade;		//varchar(10)�ͻ�ȼ�
	private String gis_x_gd;		//varchar(20)�ߵµ�λγ��
	private String gis_y_gd;		//varchar(20)�ߵµ�λ����
	private String intime;			//varchar(20)¼��ʱ��
	private String chgtime;		//varchar(20)�޸�ʱ��
	private int isxfkzs = 0;			//char(1)���������������0 ��1
	private String jdxx;			//varchar(50)�ֵ�
	private String jdxxNum;		//varchar(20)�ֵ����
	private String street;			//varchar(20)·
	private String street_number;	//varchar(20)�š�Ū
	private String street_remark;	//varchar(50)��ַ��ϸ��Ϣ
	private String chgacc;			//varchar(20)�޸���
	private String wydw;			//varchar(40)��ҵ��λ
	private String wydwplxr;		//varchar(20)��ҵ��λ��ϵ��
	private String wlwtype;		//varchar(20)������������Դ
	private String jzxfsswbdw;		//varchar(40)����������ʩά����λ
	 
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
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
	public String getOverfloor() {
		return overfloor;
	}
	public void setOverfloor(String overfloor) {
		this.overfloor = overfloor;
	}
	public String getUnderfloor() {
		return underfloor;
	}
	public void setUnderfloor(String underfloor) {
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
	public int getIsxfkzs() {
		return isxfkzs;
	}
	public void setIsxfkzs(int isxfkzs) {
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
//	private static Logger logger = Logger.getLogger(Buildinginfo.class);
//	private static final long serialVersionUid = 1302377908285976972L; 
	
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public void ModifyFaultInfo(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		
		String path = "";
		String messageCode = "����ʧ��"; 
		String deviceIds = this.request.getParameter("deviceId");
		QP_FaultInfos fault = new QP_FaultInfos();
		fault.setDeviceId(deviceId);
		fault.setBid(bid);
		fault.setXzqy(xzqy);
		fault.setName(name);
		fault.setAddress(address);
		fault.setLinkman(linkman);
		fault.setLinkphone(linkphone);
		fault.setBuildstate(buildstate);
		fault.setBuildtime(buildtime);
		fault.setArea(area);
		fault.setZdarea(zdarea);
		fault.setHigh(high);
		fault.setBzfloorarea(bzfloorarea);
		fault.setOverfloor(overfloor);
		fault.setUnderfloor(underfloor);
		fault.setJzsort(jzsort);
		fault.setJzconfig(jzconfig);
		fault.setJzconfigother(jzconfigother);
		fault.setXfkzsplace(xfkzsplace);
		fault.setNhgrade(nhgrade);
		fault.setGis_x_gd(gis_x_gd);
		fault.setGis_y_gd(gis_y_gd);
		fault.setIntime(intime);
		fault.setChgtime(chgtime);
		fault.setXfkzsplace(xfkzsplace);
		fault.setJdxx(jdxx);
		fault.setJdxxNum(jdxxNum);
		fault.setStreet(street);
		fault.setStreet_number(street_number);
		fault.setStreet_remark(street_remark);
		fault.setChgacc(chgacc);
		fault.setWydw(wydw);
		fault.setWydwplxr(wydwplxr);
		fault.setWlwtype(wlwtype);
		fault.setJzxfsswbdw(jzxfsswbdw);
		
		try {
			QP_FaultInfoDao qdao = new QP_FaultInfoDaoImpl(); 
			boolean result = qdao.addFaultInfos(fault);
			if(result) {
				messageCode = "�����ɹ�";
//				return SUCCESS;
			}else{
//				return ERROR;
			}
			
//			path = "/WEB-INF/page/builds/buildingInfos.jsp";
//			this.response.sendRedirect(path);
			this.request.getSession().setAttribute("messageCode", messageCode);
			this.request.getRequestDispatcher("buildinfo.do?repeaterMac="+deviceId).forward(this.request, this.response);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}
}