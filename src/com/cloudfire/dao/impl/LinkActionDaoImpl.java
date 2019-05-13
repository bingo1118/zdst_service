package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cloudfire.dao.LinkActionDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.LinkAction;
import com.cloudfire.until.Utils;

public class LinkActionDaoImpl implements LinkActionDao {

	@Override
	public LinkAction getTypeByMac(String mac) {
		String sql = "SELECT s.mac, s.deviceType, d.deviceName FROM smoke s, devices d WHERE mac = ? and s.deviceType = d.id";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		LinkAction linkAction = null;
		try {
			if (Utils.isNullStr(mac)) {
				ps.setString(1, mac);
			}
			rs = ps.executeQuery();

			while (rs.next()) {
				linkAction = new LinkAction();
				linkAction.setAlarmMac(mac);
				linkAction.setDeviceType1(rs.getInt(2));
				linkAction.setAlarmMacType(rs.getString(3));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return linkAction;
	}

	@Override
	public int addLinkAction(LinkAction linkAction) {
		String sql = "insert into linkaction(alarmMac,deviceType1,alarmMacType,responseMac,deviceType2,responseMacType,alarmType,action,userid,time) VALUES(?,?,?,?,?,?,?,?,?,?);";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		int result = 0;
		try {
			ps.setString(1, linkAction.getAlarmMac());
			ps.setInt(2, linkAction.getDeviceType1());
			ps.setString(3, linkAction.getAlarmMacType());
			ps.setString(4, linkAction.getResponseMac());
			ps.setInt(5, linkAction.getDeviceType2());
			ps.setString(6, linkAction.getResponseMacType());
			ps.setString(7, linkAction.getAlarmType());
			ps.setString(8, linkAction.getAction());
			ps.setString(9, linkAction.getUserid());
			ps.setString(10, linkAction.getTime());
			result = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	@Override
	public int countLinkAction(LinkAction query) {
		int totalcount = 0;
		String sql = "select count(linkid) from linkaction";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				totalcount = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return totalcount;
	}

	@Override
	public List<LinkAction> linkActionList(LinkAction query) {
		String limit = " limit " + query.getStartRow() + "," + query.getPageSize();
		String sql = "select linkid,alarmMac,deviceType1,alarmMacType,responseMac,deviceType2,responseMacType,alarmType,action,time,userid from linkaction group by time desc";
		sql += limit;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<LinkAction> list = new ArrayList<LinkAction>();
		try {
			int row = query.getStartRow();
			rs = ps.executeQuery();
			while (rs.next()) {
				LinkAction linkAction = new LinkAction();
				linkAction.setRow(++row);
				linkAction.setId(rs.getInt(1));
				linkAction.setAlarmMac(rs.getString(2));
				linkAction.setDeviceType1(rs.getInt(3));
				linkAction.setAlarmMacType(rs.getString(4));
				linkAction.setResponseMac(rs.getString(5));
				linkAction.setDeviceType2(rs.getInt(6));
				linkAction.setResponseMacType(rs.getString(7));
				linkAction.setAlarmType(rs.getString(8));
				linkAction.setAction(rs.getString(9));
				linkAction.setTime(rs.getString(10));
				linkAction.setUserid(rs.getString(11));
				list.add(linkAction);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return list;
	}

	@Override
	public int removeById(int id) {
		String sql = "delete from linkaction where linkid = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		int rs = 0;
		try {
			ps.setInt(1, id);
			rs = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return rs;
	}

	@Override
	public int checkMac(String alarmMac, String responseMac) {
		int totalcount = 0;
		String sql = "select count(linkid) as totalCount from linkaction where alarmMac=? and responseMac=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, alarmMac);
			ps.setString(2, responseMac);
			rs = ps.executeQuery();
			while (rs.next()) {
				totalcount = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return totalcount;
	}

	@Override
	public List<String> getLoraElectircMacByAlarmMac(String alarmMac) {
		String sql = "select responseMac from linkaction where alarmMac = ?  and deviceType2=52";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<String> resMacList = new ArrayList<String>();
		try {
			if (Utils.isNullStr(alarmMac)) {
				ps.setString(1, alarmMac);
			}
			rs = ps.executeQuery();

			while (rs.next()) {
				String resMac = rs.getString(1);
				resMacList.add(resMac);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return resMacList;
	}

	@Override
	public List<String> getNBElectircMacByAlarmMac(String alarmMac) {
		String sql = "select responseMac from linkaction where alarmMac = ?  and deviceType2=75";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<String> resMacList = new ArrayList<String>();
		try {
			if (Utils.isNullStr(alarmMac)) {
				ps.setString(1, alarmMac);
			}
			rs = ps.executeQuery();

			while (rs.next()) {
				String resMac = rs.getString(1);
				resMacList.add(resMac);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return resMacList;
	}

	@Override
	public List<String> getNB7020MacByAlarmMac(String alarmMac) {
		String sql = "select responseMac from linkaction where alarmMac = ?  and deviceType2=73";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<String> resMacList = new ArrayList<String>();
		try {
			if (Utils.isNullStr(alarmMac)) {
				ps.setString(1, alarmMac);
			}
			rs = ps.executeQuery();

			while (rs.next()) {
				String resMac = rs.getString(1);
				resMacList.add(resMac);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return resMacList;
	}

	@Override
	public String getUseridByMac(String alarmMac, String electricMac) {
		String sql = "SELECT userid FROM linkaction WHERE alarmMac = ? and responseMac = ?";
		String userid = "";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, alarmMac);
			ps.setString(2, electricMac);
			rs = ps.executeQuery();
			while (rs.next()) {
				userid = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return userid;
	}

	@Override
	public String getImeiByNJ(String mac) {
		String sql = "select imei from imei_device where imei = ?";
		String imei = "";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, mac);
			rs = ps.executeQuery();
			while (rs.next()) {
				imei = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return imei;
	}

	@Override
	public String getAreaIdByMac(String mac) {
		String sql = "select areaId from smoke where mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		String areaId = "";
		ResultSet rs = null;
		try {
			ps.setString(1, mac);
			rs = ps.executeQuery();
			while (rs.next()) {
				areaId = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return areaId;
	}

}
