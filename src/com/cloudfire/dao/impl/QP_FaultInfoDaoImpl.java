package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.lang3.StringUtils;

import com.cloudfire.dao.QP_FaultInfoDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.QP_FaultInfos;

public class QP_FaultInfoDaoImpl implements QP_FaultInfoDao {

	@Override
	public QP_FaultInfos getFaultInfos(String deviceId) {
		String sqlstr = "SELECT deviceId,bid,xzqy,name,address,linkman,linkphone,buildstate,buildtime,area,zdarea,high,bzfloorarea,overfloor,underfloor,jzsort,jzconfig,jzconfigother,xfkzsplace,nhgrade,gis_x_gd,gis_y_gd,intime,chgtime,isxfkzs,jdxx,jdxxNum,street,street_number,street_remark,chgacc,wydw,wydwplxr,wlwtype,jzxfsswbdw from qp_faultinfos where deviceId='"+deviceId+"'";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		QP_FaultInfos qpf = new QP_FaultInfos();
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				qpf.setJzxfsswbdw(rs.getString("jzxfsswbdw"));
				qpf.setWlwtype(rs.getString("wlwtype"));
				qpf.setWydwplxr(rs.getString("wydwplxr"));
				qpf.setWydw(rs.getString("wydw"));
				qpf.setChgacc(rs.getString("chgacc"));
				qpf.setStreet_remark(rs.getString("street_remark"));
				qpf.setStreet_number(rs.getString("street_number"));
				qpf.setStreet(rs.getString("street"));
				qpf.setJdxxNum(rs.getString("jdxxNum"));
				qpf.setJdxx(rs.getString("jdxx"));
				qpf.setIsxfkzs(rs.getInt("isxfkzs"));
				qpf.setChgtime(rs.getString("chgtime"));
				qpf.setIntime(rs.getString("intime"));
				qpf.setGis_y_gd(rs.getString("gis_y_gd"));
				qpf.setGis_x_gd(rs.getString("gis_x_gd"));
				qpf.setNhgrade(rs.getString("nhgrade"));
				qpf.setXfkzsplace(rs.getString("xfkzsplace"));
				qpf.setJzconfigother(rs.getString("jzconfigother"));
				qpf.setJzconfig(rs.getString("jzconfig"));
				qpf.setJzsort(rs.getString("jzsort"));
				qpf.setUnderfloor(rs.getString("underfloor"));
				qpf.setOverfloor(rs.getString("overfloor"));
				qpf.setBzfloorarea(rs.getString("bzfloorarea"));
				qpf.setHigh(rs.getString("high"));
				qpf.setZdarea(rs.getString("zdarea"));
				qpf.setArea(rs.getString("area"));
				qpf.setBuildtime(rs.getString("buildtime"));
				qpf.setBuildstate(rs.getString("buildstate"));
				qpf.setLinkphone(rs.getString("linkphone"));
				qpf.setLinkman(rs.getString("linkman"));
				qpf.setAddress(rs.getString("address"));
				qpf.setName(rs.getString("name"));
				qpf.setXzqy(rs.getString("xzqy"));
				qpf.setBid(rs.getString("bid"));
			}
			qpf.setDeviceId(deviceId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return qpf;
	}

	@Override
	public boolean addFaultInfos(QP_FaultInfos qpf) {
		boolean result = false;
		String sql = "REPLACE qp_faultinfos (deviceId,bid,xzqy,name,address,linkman,linkphone,buildstate,buildtime,area,zdarea,high,bzfloorarea,overfloor,underfloor,jzsort,jzconfig,jzconfigother,xfkzsplace,nhgrade,gis_x_gd,gis_y_gd,intime,chgtime,isxfkzs,jdxx,jdxxNum,street,street_number,street_remark,chgacc,wydw,wydwplxr,wlwtype,jzxfsswbdw) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(35, qpf.getJzxfsswbdw());
			ps.setString(34, qpf.getWlwtype());
			ps.setString(33, qpf.getWydwplxr());
			ps.setString(32, qpf.getWydw());
			ps.setString(31, qpf.getChgacc());
			ps.setString(30, qpf.getStreet_remark());
			ps.setString(29, qpf.getStreet_number());
			ps.setString(28, qpf.getStreet());
			ps.setString(27, qpf.getJdxxNum());
			ps.setString(26, qpf.getJdxx());
			ps.setInt(25, qpf.getIsxfkzs());
			ps.setString(24, qpf.getChgtime());
			ps.setString(23, qpf.getIntime());
			ps.setString(22, qpf.getGis_y_gd());
			ps.setString(21, qpf.getGis_x_gd());
			ps.setString(20, qpf.getNhgrade());
			ps.setString(19, qpf.getXfkzsplace());
			ps.setString(18, qpf.getJzconfigother());
			ps.setString(17, qpf.getJzconfig());
			ps.setString(16, qpf.getJzsort());
			ps.setString(15, qpf.getUnderfloor());
			ps.setString(14, qpf.getOverfloor());
			ps.setString(13, qpf.getBzfloorarea());
			ps.setString(12, qpf.getHigh());
			ps.setString(11, qpf.getZdarea());
			ps.setString(10, qpf.getArea());
			ps.setString(9, qpf.getBuildtime());
			ps.setString(8, qpf.getBuildstate());
			ps.setString(7, qpf.getLinkphone());
			ps.setString(6, qpf.getLinkman());
			ps.setString(5, qpf.getAddress());
			ps.setString(4, qpf.getName());
			ps.setString(3, qpf.getXzqy());
			ps.setString(2, qpf.getBid());
			ps.setString(1, qpf.getDeviceId());
			int rs = ps.executeUpdate();
			if(rs>0){
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	@Override
	public boolean updateFaultInfo(QP_FaultInfos qpf) {
		boolean result = false;
		StringBuffer sqls = new StringBuffer();
		sqls.append("UPDATE qp_faultinfos SET ");
		if(StringUtils.isNotBlank(qpf.getBid())){
			sqls.append(" bid = '"+qpf.getBid()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getXzqy())){
			sqls.append(" xzqy = '"+qpf.getXzqy()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getName())){
			sqls.append(" name = '"+qpf.getName()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getAddress())){
			sqls.append(" address = '"+qpf.getAddress()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getLinkman())){
			sqls.append(" linkman = '"+qpf.getLinkman()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getLinkphone())){
			sqls.append(" linkphone = '"+qpf.getLinkphone()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getBuildstate())){
			sqls.append(" buildstate = '"+qpf.getBuildstate()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getBuildtime())){
			sqls.append(" buildtime = '"+qpf.getBuildtime()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getArea())){
			sqls.append(" area = '"+qpf.getArea()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getZdarea())){
			sqls.append(" zdarea = '"+qpf.getZdarea()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getHigh())){
			sqls.append(" high = '"+qpf.getHigh()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getBzfloorarea())){
			sqls.append(" bzfloorarea = '"+qpf.getBzfloorarea()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getOverfloor())){
			sqls.append(" overfloor = '"+qpf.getOverfloor()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getUnderfloor())){
			sqls.append(" underfloor = '"+qpf.getUnderfloor()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getJzsort())){
			sqls.append(" jzsort = '"+qpf.getJzsort()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getJzconfig())){
			sqls.append(" jzconfig = '"+qpf.getJzconfig()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getJzconfigother())){
			sqls.append(" jzconfigother = '"+qpf.getJzconfigother()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getXfkzsplace())){
			sqls.append(" xfkzsplace = '"+qpf.getXfkzsplace()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getNhgrade())){
			sqls.append(" nhgrade = '"+qpf.getNhgrade()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getGis_x_gd())){
			sqls.append(" gis_x_gd = '"+qpf.getGis_x_gd()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getGis_y_gd())){
			sqls.append(" gis_y_gd = '"+qpf.getGis_y_gd()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getIntime())){
			sqls.append(" intime = '"+qpf.getIntime()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getChgtime())){
			sqls.append(" chgtime = '"+qpf.getChgtime()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getJdxx())){
			sqls.append(" jdxx = '"+qpf.getJdxx()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getJdxxNum())){
			sqls.append(" jdxxNum = '"+qpf.getJdxxNum()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getStreet())){
			sqls.append(" street = '"+qpf.getStreet()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getStreet_number())){
			sqls.append(" street_number = '"+qpf.getStreet_number()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getStreet_remark())){
			sqls.append(" street_remark = '"+qpf.getStreet_remark()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getChgacc())){
			sqls.append(" chgacc = '"+qpf.getChgacc()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getWydw())){
			sqls.append(" wydw = '"+qpf.getWydw()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getWydwplxr())){
			sqls.append(" wydwplxr = '"+qpf.getWydwplxr()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getWlwtype())){
			sqls.append(" wlwtype = '"+qpf.getWlwtype()+"',");
		}
		if(StringUtils.isNotBlank(qpf.getJzxfsswbdw())){
			sqls.append(" jzxfsswbdw = '"+qpf.getJzxfsswbdw()+"',");
		}
		
		sqls.append(" isxfkzs = '"+qpf.getIsxfkzs()+"'");
		sqls.append(" where deviceId = "+qpf.getDeviceId());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqls.toString());
		try {
			int res = ps.executeUpdate();
			if(res>0){
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

}
