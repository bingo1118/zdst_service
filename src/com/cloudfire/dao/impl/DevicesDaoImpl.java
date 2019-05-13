package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cloudfire.dao.DeviceDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.BQMacEntity;
import com.cloudfire.entity.CountValue;
import com.cloudfire.entity.Devices;
import com.cloudfire.entity.SearchDto;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.WorkingTime;
import com.cloudfire.entity.query.BqMacType;
import com.cloudfire.entity.query.DeviceNetState;
import com.cloudfire.until.Constant;
import com.cloudfire.until.Utils;

public class DevicesDaoImpl implements DeviceDao {

	@Override
	public List<Devices> getAllDevices() {
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("SELECT id,deviceName FROM devices   where 1 = 1 ");

		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,
				sqlstr.toString());
		ResultSet rs = null;
		List<Devices> dlist = null;
		SmokeBean sb;
		Devices de;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {

				if (dlist == null) {
					dlist = new ArrayList<Devices>();
				}
				de = new Devices();

				de.setId(rs.getInt(1));

				de.setDeviceName(rs.getString(2));

				dlist.add(de);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return dlist;
	}

	public Integer getNum(String strsql, String areaId) {
		int dateNum = 0;
		String sqlstr = "";
		if (Utils.isNullStr(areaId)) {
			if (strsql == "macNum") {
				sqlstr = "SELECT COUNT(mac) FROM smoke as s,areaidarea as a WHERE s.areaId=a.areaId   "; // 终端总数
				sqlstr +=
						"  s.deviceType " + " in (select id from devices) " + 
						" and s.areaId = "+ areaId;
			}
			if (strsql == "netStaterNum") {
				sqlstr = "SELECT COUNT(netState) FROM smoke as s,areaidarea as a WHERE s.areaId=a.areaId and netState = '1'"; // 正常状态数量
				sqlstr +=
						" and s.deviceType " + " in (select id from devices) " + 
						 " and s.areaId = " + areaId;
			}
			if (strsql == "ifDealNum") {
				sqlstr = "SELECT COUNT(ifAlarm) FROM smoke as s,areaidarea as a WHERE s.areaId=a.areaId and ifAlarm = '0'"; // 报警状态个数
				sqlstr += 
						" and s.deviceType " +  " in (select id from devices) " + 
						 " and s.areaId = " + areaId;
			}
			if (strsql == "noNetStater") {
				sqlstr = "SELECT COUNT(netState) FROM smoke as s,areaidarea as a WHERE s.areaId=a.areaId and netState <> 1"; // 故障个数
				sqlstr +=
						" and s.deviceType " + " in (select id from devices) " + 
						" and s.areaId = " + areaId;
			}
			if (strsql == "alarmTypeNum") {
				sqlstr = "SELECT COUNT(alarmType) FROM alarm al,smoke s,areaidarea a WHERE s.areaId=a.areaId and al.alarmType='202' AND al.smokeMac = s.mac"; // 火警状态数量
				sqlstr +=
						" and s.deviceType " + " in (select id from devices) " + 
						 " and s.areaId = " + areaId;
			}
			if (strsql == "alarmTruthNum") {
				sqlstr = "SELECT COUNT(alarmTruth) FROM alarm,smoke,areaidarea WHERE s.areaId=areaidarea.areaId and alarm.alarmTruth='1'  AND alarm.smokeMac = smoke.mac"; // 误报数量
				sqlstr += 
						" and smoke.deviceType " +  " in (select id from devices) " + 
						 " and s.areaId = " + areaId;
			}
		} else {
			if (strsql == "macNum") {
				sqlstr = "SELECT COUNT(mac) FROM smoke  ,areaidarea"; // 终端总数
				sqlstr += " where " +
						"smoke.deviceType " +  " in (select id from devices) " + 
						 "  and smoke.areaId>0 and smoke.areaId = areaidarea.areaId";
			}
			if (strsql == "netStaterNum") {
				sqlstr = "SELECT COUNT(netState) FROM smoke ,areaidarea WHERE smoke.areaId = areaidarea.areaId and netState = '1'"; // 正常状态数量
				sqlstr +=
						" and smoke.deviceType " + " in (select id from devices) " + 
						 " and smoke.areaId>0";
			}
			if (strsql == "ifDealNum") {
				sqlstr = "SELECT COUNT(ifAlarm) FROM smoke,areaidarea WHERE  smoke.areaId = areaidarea.areaId and  ifAlarm = '0'"; // 报警状态个数
				sqlstr += 
						" and smoke.deviceType " +  " in (select id from devices) " + 
						 " and smoke.areaId>0";
			}
			if (strsql == "noNetStater") {
				sqlstr = "SELECT COUNT(netState) FROM smoke,areaidarea WHERE smoke.areaId = areaidarea.areaId and  netState <> 1"; // 故障个数
				sqlstr += 
						" and smoke.deviceType " +  " in (select id from devices) " + 
						" and smoke.areaId>0 ";
			}
			if (strsql == "alarmTypeNum") {
				sqlstr = "SELECT COUNT(alarmType) FROM alarm a,smoke s WHERE a.alarmType='202' AND a.smokeMac = s.mac"; // 火警状态数量
				sqlstr +=
						" and s.deviceType " +  " in (select id from devices) " + 
						" and s.areaId>0";
			}
			if (strsql == "alarmTruthNum") {
				sqlstr = "SELECT COUNT(alarmTruth) FROM alarm,smoke WHERE alarm.alarmTruth='1'  AND alarm.smokeMac = smoke.mac"; // 误报数量
				sqlstr += 
						" and smoke.deviceType " +  " in (select id from devices) " + 
						" and smoke.areaId>0";
			}
		}

		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DBConnectionManager.prepare(conn, sqlstr);
			rs = ps.executeQuery();
			while (rs.next()) {
				dateNum = rs.getInt(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return dateNum;
	}

	public Integer getCountByMac(String strsql, int devType) {
		int dateNum = 0;
		String sqlstr = "";
		if (strsql == "macNum") {
			sqlstr = "SELECT COUNT(deviceType) FROM smoke WHERE netState = '1' AND deviceType = ?"; // 设备正常运行数量
																									// 2
		}
		if (strsql == "netStaterNum") {
			sqlstr = "SELECT COUNT(deviceType) FROM smoke WHERE netState <> 1 AND deviceType = ?"; // 正常状态数量&&设备故障数量4
		}
		if (strsql == "ifDealNum") {
			sqlstr = "SELECT COUNT(deviceType) FROM smoke s,alarm a WHERE s.mac = a.smokeMac AND s.deviceType = ? AND s.ifAlarm=1"; // 报警状态个数&&报警总数5
		}
		if (strsql == "noNetStater") {
			sqlstr = "SELECT COUNT(deviceType) FROM smoke WHERE netState = 0 AND deviceType	 = ?"; // 故障个数&&设备掉线数量
																									// 3
		}
		if (strsql == "alarmTypeNum") {
			sqlstr = "SELECT COUNT(alarmTruth) FROM smoke s,alarm a WHERE s.mac = a.smokeMac AND s.deviceType = ? AND s.ifAlarm=1 AND a.alarmTruth = 1"; // 设备报火警数量7
		}
		if (strsql == "alarmTruthNum") {
			sqlstr = "SELECT COUNT(deviceType) FROM smoke s,alarm a WHERE s.mac = a.smokeMac AND s.deviceType = ? AND s.ifAlarm=1 AND a.alarmType = 202"; // 设备误报数量6
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DBConnectionManager.prepare(conn, sqlstr);
			ps.setInt(1, devType);
			rs = ps.executeQuery();
			while (rs.next()) {
				dateNum = rs.getInt(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return dateNum;
	}

	public Integer getCountByMacAndUserId(String strsql, int devType,
			String userId) {
		int dateNum = 0;
		String sqlstr = "";
		if (strsql == "macNum") {
			sqlstr = "SELECT COUNT(deviceType) FROM smoke s,useridsmoke us WHERE netState = 1 AND s.mac = us.smokeMac AND deviceType = ? AND us.userId = ?"; // 设备正常运行数量
																																								// 2
		}
		if (strsql == "netStaterNum") {
			sqlstr = "SELECT COUNT(*) FROM smoke s,useridsmoke us WHERE netState <> 1 AND s.mac = us.smokeMac AND deviceType = ? AND us.userId = ?"; // 正常状态数量&&设备故障数量4
		}
		if (strsql == "ifDealNum") {
			sqlstr = "SELECT COUNT(deviceType) FROM smoke s,alarm a ,useridsmoke us WHERE s.mac = a.smokeMac AND us.smokeMac = s.mac AND s.ifAlarm = 1 AND s.deviceType = ? AND us.userId=?"; // 报警状态个数&&报警总数5
		}
		if (strsql == "noNetStater") {
			sqlstr = "SELECT COUNT(*) FROM smoke s, useridsmoke us WHERE netState = 0 AND s.mac = us.smokeMac AND deviceType = ? AND us.userId = ?"; // 故障个数&&设备掉线数量
																																						// 3
		}
		if (strsql == "alarmTypeNum") {
			sqlstr = "SELECT DISTINCT COUNT(*) FROM smoke s,alarm a ,useridsmoke us WHERE s.mac = a.smokeMac  AND s.ifAlarm=1 AND a.alarmTruth = 1 AND s.mac = us.smokeMac AND a.alarmType = '202'AND s.deviceType = ? AND us.userId = ?"; // 设备报火警数量7
		}
		if (strsql == "alarmTruthNum") {
			sqlstr = "SELECT  DISTINCT COUNT(alarmTruth)  FROM smoke s,alarm a,useridsmoke us WHERE s.mac = a.smokeMac AND us.smokeMac = s.mac AND s.ifAlarm = 1 AND a.alarmTruth = 1 AND s.deviceType = ? AND us.userId=?"; // 设备误报数量6
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DBConnectionManager.prepare(conn, sqlstr);
			ps.setInt(1, devType);
			ps.setString(2, userId);
			rs = ps.executeQuery();
			while (rs.next()) {
				dateNum = rs.getInt(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return dateNum;
	}

	public Integer getNumByPrivilege(String strsql, String currentId,
			String areaId) {
		int dateNum = 0;
		String sqlstr = "";
		String userId = currentId;
		if (Utils.isNullStr(userId)) {
			if (strsql == "macNum") {
				sqlstr = "SELECT COUNT(mac) FROM (SELECT s.mac,s.deviceType,u.userId,a.areaId FROM USER u,areaidarea a,useridareaid ua,smoke s,devices d WHERE s.deviceType = d.id and u.userId = ua.userId AND a.areaId = ua.areaId AND s.areaId = a.areaId AND u.userId=? GROUP BY mac) AS a WHERE 1=1 " ;
//						"and deviceType "+ Constant.sql; // 终端总数
			}
			if (strsql == "netStaterNum") {
				sqlstr = "SELECT COUNT(mac) FROM ( SELECT s.mac,s.deviceType,u.userId,a.areaId FROM USER u,areaidarea a,useridareaid ua,smoke s ,devices d WHERE s.deviceType = d.id and u.userId = ua.userId AND a.areaId = ua.areaId AND s.areaId = a.areaId AND u.userId=? AND s.netState = '1' GROUP BY mac) AS a WHERE 1 = 1  ";
//						+ Constant.sql; // 正常状态数量
			}
			if (strsql == "ifDealNum") {
				sqlstr = "SELECT COUNT(mac)  FROM ( SELECT s.mac,s.deviceType,u.userId,a.areaId  FROM USER u,areaidarea a,useridareaid ua,smoke s ,devices d WHERE s.deviceType = d.id and u.userId = ua.userId AND a.areaId = ua.areaId AND u.userId=? AND s.areaId = a.areaId AND s.ifAlarm = '0' GROUP BY mac) AS a WHERE 1 = 1  ";
//						+ Constant.sql; // 报警状态个数
			}
			if (strsql == "noNetStater") {
				sqlstr = "SELECT COUNT(mac) FROM (SELECT s.mac,s.deviceType,u.userId,a.areaId FROM USER u,areaidarea a,useridareaid ua,smoke s ,devices d WHERE s.deviceType = d.id and u.userId = ua.userId AND a.areaId = ua.areaId AND u.userId=? AND s.areaId = a.areaId AND s.netState <> '1' GROUP BY mac) AS a WHERE 1 = 1  ";
//						+ Constant.sql; // 故障个数
			}
			if (strsql == "alarmTypeNum") {
				sqlstr = "SELECT COUNT(smokeMac) FROM (SELECT * FROM alarm a,smoke s ,devices d WHERE s.deviceType = d.id and s.mac = a.smokeMac AND alarmType = '202' AND s.areaid IN(SELECT s.areaid FROM useridareaid u,smoke s WHERE u.areaid = s.areaid AND u.userid = ?) GROUP BY smokeMac) AS a WHERE 1 = 1  ";
//						+ Constant.sql; // 火警状态数量
			}
			if (strsql == "alarmTruthNum") {
				sqlstr = "SELECT COUNT(a.alarmType) FROM alarm a,s.deviceType,smoke s,USER u,useridsmoke us ,devices d WHERE s.deviceType = d.id and a.alarmTruth='1' and u.userid = ? AND a.smokeMac = s.mac AND u.userId = us.userId AND us.smokeMac=s.macand ";
//						+ Constant.sql; // 误报数量
			}
		} else {
			if (strsql == "macNum") {
				sqlstr = "SELECT COUNT(mac) FROM (SELECT s.mac,u.userId,a.areaId FROM USER u,areaidarea a,useridareaid ua,smoke s ,devices d WHERE s.deviceType = d.id and u.userId = ua.userId AND a.areaId = ua.areaId AND s.areaId = a.areaId GROUP BY mac) AS a WHERE 1=1 "; // 终端总数
			}
			if (strsql == "netStaterNum") {
				sqlstr = "SELECT COUNT(mac) FROM ( SELECT s.mac,u.userId,a.areaId FROM USER u,areaidarea a,useridareaid ua,smoke s ,devices d WHERE s.deviceType = d.id and u.userId = ua.userId AND a.areaId = ua.areaId AND s.areaId = a.areaId AND s.netState = '1' GROUP BY mac) AS a WHERE 1 = 1 "; // 正常状态数量
			}
			if (strsql == "ifDealNum") {
				sqlstr = "SELECT COUNT(mac)  FROM ( SELECT s.mac,u.userId,a.areaId  FROM USER u,areaidarea a,useridareaid ua,smoke s ,devices d WHERE s.deviceType = d.id and u.userId = ua.userId AND a.areaId = ua.areaId AND s.areaId = a.areaId AND s.ifAlarm = '0' GROUP BY mac) AS a WHERE 1 = 1 "; // 报警状态个数
			}
			if (strsql == "noNetStater") {
				sqlstr = "SELECT COUNT(mac) FROM (SELECT s.mac,u.userId,a.areaId FROM USER u,areaidarea a,useridareaid ua,smoke s ,devices d WHERE s.deviceType = d.id and u.userId = ua.userId AND a.areaId = ua.areaId AND s.areaId = a.areaId AND s.netState <> '1' GROUP BY mac) AS a WHERE 1 = 1 "; // 故障个数
			}
			if (strsql == "alarmTypeNum") {
				sqlstr = "SELECT COUNT(smokeMac) from (SELECT * from alarm a,smoke s ,devices d WHERE s.deviceType = d.id and s.mac = a.smokeMac and alarmType = '202'  GROUP BY smokeMac) as a where 1 = 1 "; // 火警状态数量
			}
			if (strsql == "alarmTruthNum") {
				sqlstr = "SELECT COUNT(a.alarmType) FROM alarm a,smoke s,USER u,useridsmoke us ,devices d WHERE s.deviceType = d.id and a.alarmTruth='1' AND a.smokeMac = s.mac AND u.userId = us.userId AND us.smokeMac=s.mac"; // 误报数量
			}
		}

		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		try {
			if (Utils.isNullStr(userId)) {
				ps.setString(1, userId);
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				dateNum = rs.getInt(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return dateNum;
	}

	@Override
	public List<CountValue> getCountNum(String areaId) {
		int macNum = 0; // 终端总数
		int netStaterNum = 0; // 正常状态数量
		int ifDealNum = 0; // 报警状态个数
		int noNetStater = 0; // 故障个数
		int alarmTypeNum = 0; // 火警状态数量
		int alarmTruthNum = 0; // 误报数量

		List<CountValue> inList = new ArrayList<CountValue>();
		CountValue cv = new CountValue();

		macNum = this.getNum("macNum", areaId);
		cv.setMacNum(macNum);
		netStaterNum = this.getNum("netStaterNum", areaId);
		cv.setNetStaterNum(netStaterNum);
		ifDealNum = this.getNum("ifDealNum", areaId);
		cv.setIfDealNum(ifDealNum);
		noNetStater = this.getNum("noNetStater", areaId);
		cv.setNoNetStater(noNetStater);
		alarmTypeNum = this.getNum("alarmTypeNum", areaId);
		cv.setAlarmTypeNum(alarmTypeNum);
		alarmTruthNum = this.getNum("alarmTruthNum", areaId);
		cv.setAlarmTruthNum(alarmTruthNum);
		inList.add(cv);

		return inList;
	}

	@Override
	public CountValue getCount(String areaId) {
		int macNum = 0; // 终端总数
		int netStaterNum = 0; // 正常状态数量
		int ifDealNum = 0; // 报警状态个数
		int noNetStater = 0; // 故障个数
		int alarmTypeNum = 0; // 火警状态数量
		int alarmTruthNum = 0; // 误报数量

		List<CountValue> inList = new ArrayList<CountValue>();
		CountValue cv = new CountValue();

		macNum = this.getNum("macNum", areaId);
		cv.setMacNum(macNum);
		netStaterNum = this.getNum("netStaterNum", areaId);
		cv.setNetStaterNum(netStaterNum);
		ifDealNum = this.getNum("ifDealNum", areaId);
		cv.setIfDealNum(ifDealNum);
		noNetStater = this.getNum("noNetStater", areaId);
		cv.setNoNetStater(noNetStater);
		alarmTypeNum = this.getNum("alarmTypeNum", areaId);
		cv.setAlarmTypeNum(alarmTypeNum);
		alarmTruthNum = this.getNum("alarmTruthNum", areaId);
		cv.setAlarmTruthNum(alarmTruthNum);
		inList.add(cv);

		return cv;
	}

	@Override
	public CountValue getCountByMac(int devType) {
		int macNum = 0; // 设备正常运行数量 2
		int netStaterNum = 0; // 设备故障数量4
		int ifDealNum = 0; // 报警总数5
		int noNetStater = 0; // 设备掉线数量 3
		int alarmTypeNum = 0; // 设备报火警数量7
		int alarmTruthNum = 0; // 设备误报数量6
		int otherNum = 0; // 其它数量 =报警总数-火警总数5-6

		List<CountValue> inList = new ArrayList<CountValue>();
		CountValue cv = new CountValue();

		macNum = this.getCountByMac("macNum", devType);
		cv.setMacNum(macNum);
		netStaterNum = this.getCountByMac("netStaterNum", devType);
		cv.setNetStaterNum(netStaterNum);
		ifDealNum = this.getCountByMac("ifDealNum", devType);
		cv.setIfDealNum(ifDealNum);
		noNetStater = this.getCountByMac("noNetStater", devType);
		cv.setNoNetStater(noNetStater);
		alarmTypeNum = this.getCountByMac("alarmTypeNum", devType);
		cv.setAlarmTypeNum(alarmTypeNum);
		alarmTruthNum = this.getCountByMac("alarmTruthNum", devType);
		cv.setAlarmTruthNum(alarmTruthNum);
		otherNum = cv.getIfDealNum() - cv.getAlarmTruthNum();
		cv.setOtherNum(otherNum);
		inList.add(cv);

		return cv;
	}

	public Integer getCountByMacSearch(String strsql, SearchDto dto) {
		int dateNum = 0;
		String sqlstr = "SELECT COUNT(deviceType) FROM smoke s,alarm a where 1=1 ";
		if (!StringUtils.isBlank(dto.getMacStatus())) {
			if (dto.getMacStatus().equals("2")) {
				sqlstr += " AND s.ifAlarm = 1";
			} else {
				sqlstr += " AND s.netState = " + dto.getMacStatus();
			}

		}
		if (!StringUtils.isBlank(dto.getFire1())) {
			sqlstr += " AND a.alarmTime >=" + dto.getFire1();
		}
		if (!StringUtils.isBlank(dto.getFire2())) {
			sqlstr += "AND a.alarmTime <=" + dto.getFire2();
		}
		if (!StringUtils.isBlank(dto.getFire1())
				&& !StringUtils.isBlank(dto.getFire2())) {
			sqlstr += " AND a.alarmTime >=" + dto.getFire1()
					+ " and a.alarmTime<=" + dto.getFire2();
		}
		if (strsql == "alarmTruthNum") {
			sqlstr += " AND a.alarmType = 202";
		}
		if (strsql == "macNum") {
			sqlstr += " AND s.netState = 1";
		}
		if (strsql == "netStaterNum") {
			sqlstr += " AND s.netState <> 1";
		}
		if (strsql == "ifDealNum") {
			sqlstr += " AND s.ifAlarm=1";
		}
		if (strsql == "noNetStater") {
			sqlstr += " AND s.netState = 0";
		}
		if (strsql == "alarmTypeNum") {
			sqlstr += " AND s.ifAlarm=1 AND a.alarmTruth = 1";
		}
		if (strsql == "alarmTruthNum") {
			sqlstr += " AND s.ifAlarm=1 AND a.alarmType = 202";
		}
		sqlstr += " AND s.deviceType = " + dto.getDeviceType();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DBConnectionManager.prepare(conn, sqlstr);
			rs = ps.executeQuery();
			while (rs.next()) {
				dateNum = rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return dateNum;
	}

	@Override
	public CountValue getCountByMacSearch(SearchDto dto) {
		int macNum = 0; // 设备正常运行数量 2
		int netStaterNum = 0; // 设备故障数量4
		int ifDealNum = 0; // 报警总数5
		int noNetStater = 0; // 设备掉线数量 3
		int alarmTypeNum = 0; // 设备报火警数量7
		int alarmTruthNum = 0; // 设备误报数量6
		int otherNum = 0; // 其它数量 =报警总数-火警总数5-6
		List<CountValue> inList = new ArrayList<CountValue>();
		CountValue cv = new CountValue();

		if (StringUtils.isBlank(dto.getMacStatus())) {
			macNum = this.getCountByMacSearch("macNum", dto);
			netStaterNum = this.getCountByMacSearch("netStaterNum", dto);
			ifDealNum = this.getCountByMacSearch("ifDealNum", dto);
			noNetStater = this.getCountByMacSearch("noNetStater", dto);
			alarmTypeNum = this.getCountByMacSearch("alarmTypeNum", dto);
			alarmTruthNum = this.getCountByMacSearch("alarmTruthNum", dto);
			otherNum = cv.getIfDealNum() - cv.getAlarmTruthNum();
		} else {
			SearchDto dto2 = new SearchDto();
			dto2.setCompanyName(dto.getCompanyName());
			dto2.setDeviceType(dto.getDeviceType());
			dto2.setFire1(dto.getFire1());
			dto2.setFire2(dto.getFire2());
			dto2.setFloor1(dto.getFloor1());
			dto2.setFloor2(dto.getFloor2());
			if (dto.getMacStatus().equals("1")) {
				macNum = this.getCountByMacSearch("", dto);// 设备正常运行数量 2
			} else if (dto.getMacStatus().equals("0")) {
				netStaterNum = this.getCountByMacSearch("", dto);// 设备故障数量4
			} else {
				ifDealNum = this.getCountByMacSearch("", dto); // 报警总数5
				alarmTruthNum = this.getCountByMacSearch("alarmTruthNum", dto2);
				;// 设备误报数量6
				otherNum = ifDealNum - alarmTruthNum;// 其它数量 =报警总数-火警总数5-6
			}
		}
		cv.setMacNum(macNum);
		cv.setNetStaterNum(netStaterNum);
		cv.setIfDealNum(ifDealNum);
		cv.setNoNetStater(noNetStater);
		cv.setAlarmTypeNum(alarmTypeNum);
		cv.setAlarmTruthNum(alarmTruthNum);
		cv.setOtherNum(otherNum);
		inList.add(cv);
		return cv;
	}

	@Override
	public CountValue getCountByPrivilege(String currentId, String areaId) {
		int macNum = 0; // 终端总数
		int netStaterNum = 0; // 正常状态数量
		int ifDealNum = 0; // 报警状态个数
		int noNetStater = 0; // 故障个数
		int alarmTypeNum = 0; // 火警状态数量
		int alarmTruthNum = 0; // 误报数量

		List<CountValue> inList = new ArrayList<CountValue>();
		CountValue cv = new CountValue();
		macNum = this.getNumByPrivilege("macNum", currentId, areaId);
		cv.setMacNum(macNum);
		netStaterNum = this
				.getNumByPrivilege("netStaterNum", currentId, areaId);
		cv.setNetStaterNum(netStaterNum);
		ifDealNum = this.getNumByPrivilege("ifDealNum", currentId, areaId);
		cv.setIfDealNum(ifDealNum);
		noNetStater = this.getNumByPrivilege("noNetStater", currentId, areaId);
		cv.setNoNetStater(noNetStater);
		// alarmTypeNum = this.getNumByPrivilege("alarmTypeNum",
		// currentId,areaId);
		cv.setAlarmTypeNum(alarmTypeNum);
		// alarmTruthNum = this.getNumByPrivilege("alarmTruthNum",
		// currentId,areaId);
		cv.setAlarmTruthNum(alarmTruthNum);
		inList.add(cv);
		return cv;
	}

	@Override
	public CountValue getCountByMacAndUserId(int devType, String userId) {
		int macNum = 0; // 设备正常运行数量 2
		int netStaterNum = 0; // 设备故障数量4
		int ifDealNum = 0; // 报警总数5
		int noNetStater = 0; // 设备掉线数量 3
		int alarmTypeNum = 0; // 设备报火警数量7
		int alarmTruthNum = 0; // 设备误报数量6
		int otherNum = 0; // 其它数量 =报警总数-火警总数5-6

		List<CountValue> inList = new ArrayList<CountValue>();
		CountValue cv = new CountValue();

		macNum = this.getCountByMacAndUserId("macNum", devType, userId);
		cv.setMacNum(macNum);
		netStaterNum = this.getCountByMacAndUserId("netStaterNum", devType,
				userId);
		cv.setNetStaterNum(netStaterNum);
		ifDealNum = this.getCountByMacAndUserId("ifDealNum", devType, userId);
		cv.setIfDealNum(ifDealNum);
		noNetStater = this.getCountByMacAndUserId("noNetStater", devType,
				userId);
		cv.setNoNetStater(noNetStater);
		alarmTypeNum = this.getCountByMacAndUserId("alarmTypeNum", devType,
				userId);
		cv.setAlarmTypeNum(alarmTypeNum);
		alarmTruthNum = this.getCountByMacAndUserId("alarmTruthNum", devType,
				userId);
		cv.setAlarmTruthNum(alarmTruthNum);
		otherNum = cv.getIfDealNum() - cv.getAlarmTruthNum();
		cv.setOtherNum(otherNum);
		inList.add(cv);

		return cv;
	}

	@Override
	public List<BQMacEntity> getBqMacEntity(String name, SearchDto dto,
			int pageNo, int pageSize) {
		String sqlstr = "select * from bqmac where 1=1 and deviceId=?";
		if (dto != null) {
			if (!StringUtils.isBlank(dto.getCompanyName())) {
				sqlstr += " and equipName like '%" + dto.getCompanyName()
						+ "%'";
			}
			if (!StringUtils.isBlank(dto.getFloor1())) {
				sqlstr += " and equipType ='" + dto.getFloor1() + "'";
			}
			if (!StringUtils.isBlank(dto.getMacStatus())) {
				sqlstr += " and status ='" + dto.getMacStatus() + "'";
			}
			if (!StringUtils.isBlank(dto.getFire1())) {
				sqlstr += " and createTime >='" + dto.getFire1() + "'";
			}
			if (!StringUtils.isBlank(dto.getFire2())) {
				sqlstr += " and createTime <='" + dto.getFire2() + "'";
			}
		}
		int start = (pageNo - 1) * pageSize;
		// sqlstr+=" limit ?,?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<BQMacEntity> list = new ArrayList<BQMacEntity>();
		try {
			ps = DBConnectionManager.prepare(conn, sqlstr);
			ps.setString(1, name);
			// ps.setInt(1, start);
			// ps.setInt(2, pageSize);
			rs = ps.executeQuery();
			while (rs.next()) {
				BQMacEntity bqMacEntity = new BQMacEntity();
				bqMacEntity.setNamed(rs.getString(2));
				bqMacEntity.setDevicetype(rs.getString(3));
				bqMacEntity.setDeviceId(rs.getInt(4) + "");
				bqMacEntity.setProjectName(rs.getString(5));
				bqMacEntity.setAddress(rs.getString(6));
				bqMacEntity.setStatus(rs.getInt(7));
				bqMacEntity.setCreateTime(rs.getString(8));
				list.add(bqMacEntity);
			}

		} catch (Exception e) {

		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return list;
	}

	@Override
	public List<BqMacType> getBqMacType() {
		String sqlstr = "select DISTINCT(equipType) from bqmac";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<BqMacType> list = new ArrayList();

		try {
			ps = DBConnectionManager.prepare(conn, sqlstr);
			rs = ps.executeQuery();
			while (rs.next()) {
				BqMacType bqMacType = new BqMacType();
				String equipType = rs.getString(1);
				bqMacType.setBqMacTypeId(equipType);
				bqMacType.setBqMacTypeIdName(equipType);
				list.add(bqMacType);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return list;
	}

	public List<DeviceNetState> getBqMacStatus(String deviceId) {// 查询所有的北秦的状态
		List<DeviceNetState> list = new ArrayList<>();
		String sql = "select DISTINCT status as status from bqmac where projectName =(select projectName from bqmac where ";
		Connection conn = DBConnectionManager.getConnection();
		if (StringUtils.isNotBlank(deviceId)) {
			sql += " deviceId=' " + deviceId + " ')";
		}
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {

			rs = ppst.executeQuery();
			while (rs.next()) {
				DeviceNetState deviceNetState = new DeviceNetState();
				int status = rs.getInt("status");
				switch (status) {
				case 0:
					deviceNetState.setNetState("0");
					deviceNetState.setNetStateName(Constant.Bq0);
					break;
				case 1:
					deviceNetState.setNetState("1");
					deviceNetState.setNetStateName(Constant.Bq1);
					break;
				default:
					break;
				}
				list.add(deviceNetState);
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return list;

	}

	@Override
	public int getBqCount(SearchDto dto) {
		// TODO Auto-generated method stub
		String mysql = "select count(*) from bqmac where 1=1 ";
		if (dto != null) {
			if (!StringUtils.isBlank(dto.getCompanyName())) {
				mysql += " and equipName like '%" + dto.getCompanyName() + "%'";
			}
			if (!StringUtils.isBlank(dto.getFloor1())) {
				mysql += " and equipType ='" + dto.getFloor1() + "'";
			}
			if (!StringUtils.isBlank(dto.getMacStatus())) {
				mysql += " and status ='" + dto.getMacStatus() + "'";
			}
			if (!StringUtils.isBlank(dto.getFire1())) {
				mysql += " and createTime >='" + dto.getFire1() + "'";
			}
			if (!StringUtils.isBlank(dto.getFire2())) {
				mysql += " and createTime <='" + dto.getFire2() + "'";
			}
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<BQMacEntity> list = new ArrayList<BQMacEntity>();
		int num = 0;
		try {
			ps = DBConnectionManager.prepare(conn, mysql);
			rs = ps.executeQuery();
			while (rs.next()) {
				num = rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return num;
	}

	@Override
	public List<BQMacEntity> getBqMacEntitiesByName(String name) {
		// TODO Auto-generated method stub
		String sql = "select * from bqMac b where b.projectName =?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<BQMacEntity> list = new ArrayList<BQMacEntity>();
		BQMacEntity bqMacEntity = null;
		List<BQMacEntity> list2 = new ArrayList<BQMacEntity>();
		try {
			ps = DBConnectionManager.prepare(conn, sql);
			ps.setString(1, name);
			rs = ps.executeQuery();
			while (rs.next()) {
				bqMacEntity = new BQMacEntity();
				bqMacEntity.setNamed(rs.getString(2));
				bqMacEntity.setDevicetype(rs.getString(3));
				bqMacEntity.setDeviceId(rs.getInt(4) + "");
				bqMacEntity.setProjectName(rs.getString(5));
				bqMacEntity.setAddress(rs.getString(6));
				bqMacEntity.setStatus(rs.getInt(7));
				bqMacEntity.setCreateTime(rs.getString(8));
				list.add(bqMacEntity);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return list;
	}

	@Override
	public int getDeviceTypeByMac(String mac) {
		String sqlstr = "SELECT deviceType from smoke where mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		int result = 0;
		ResultSet rs = null;
		try {
			ps.setString(1, mac);
			rs = ps.executeQuery();
			while(rs.next()){
				result = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	@Override
	public String getDeviceName(int deviceType) {
		String deviceName = "";
		String sql = "select deviceName from devices where id = " + deviceType;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			if (rs.next()) {
				deviceName = rs.getString("deviceName");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return deviceName;
	}

	@Override
	public List<Integer> getAllFire(List<String> areaIds) {
		List<Integer> fires = null;
		String sql = "select distinct id from devices d ,smoke s where id not in(5,10,15,18,19) and s.deviceType = d.id  ";
		int len = areaIds.size();
		if (len == 1)
			sql += " and areaId in (" + areaIds.get(0) +") "; 
		else {
			for (int i = 0; i < len; i++) {
				if (i == 0) {
					sql += " and areaId in (" + areaIds.get(0);
				} else if (i == len - 1) {
					sql += " ,"+areaIds.get(i) + ") ";
				} else {
					sql += " ,"+areaIds.get(i);
				}
			}
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			fires = new ArrayList<Integer>(); 
			while (rs.next()) {
				fires.add(rs.getInt("id"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return fires;
	}

	 //获取各类型的设备数量
	@Override
	public List<Devices> getDeviceNums(List<String> areaIds) {
		String sql = "select deviceType,deviceName,count(*) as num from smoke s,devices d where s.deviceType = d.id";
		if(areaIds == null)
			return null;
		else {
			int len = areaIds.size();
			if (len == 1)
				sql += " and areaId in (" + areaIds.get(0) +") "; 
			else {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						sql += " and areaId in (" + areaIds.get(0);
					} else if (i == len - 1) {
						sql += " ,"+areaIds.get(i) + ") ";
					} else {
						sql += " ,"+areaIds.get(i);
					}
				}
			}
		}
		sql +=" group by deviceName   ";
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<Devices> lstd = null;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				if (lstd == null) {
					lstd = new ArrayList<Devices>();
				}
				Devices dev = new Devices();
				
//				int devId = rs.getInt("deviceType");
				//烟感 1 烟感 21 loraWan烟感 31三江烟感 41NB烟感
				//水位 19水位 124DTU水位
				//水压 10水压	 42NB水压 125DTU水压
				//主机 9三江主机 119三江传输装置 126海湾主机 
				//20无线模块
				//燃气 2燃气  16NB燃气
			
				//屏蔽 17电梯 11红外 12门磁 31三江烟感
				if(rs.getInt("deviceType") ==  11 || rs.getInt("deviceType") ==  12 || rs.getInt("deviceType") ==  17 || rs.getInt("deviceType") ==  31)
					continue;
				
				dev.setId(rs.getInt("deviceType"));
				dev.setDeviceName(rs.getString("deviceName"));
				dev.setNum(rs.getInt("num"));
				lstd.add(dev);
			}
			
			//屏蔽电梯
//			if (lstd !=null) { //将
//			for (Devices devices : lstd) {
//				if (devices.getId() == )
//			}
			
			if (lstd != null) { //按照数量排序,倒序
				Collections.sort(lstd,new Comparator<Devices>(){
	
					@Override
					public int compare(Devices dev1, Devices dev2) {
						int i = dev2.getNum() - dev1.getNum();
						return i;
					}
					
				});
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return lstd;
	}

	//获取各类型的真实报警数量
	@Override
	public List<Devices> getAlarmNums(List<String> areaIds) {
	
		String sql = "select t.deviceType,deviceName,alarmNum from (select distinct deviceType,deviceName from smoke,devices where smoke.deviceType = devices.id "; 
		if(areaIds == null)
			return null;
		else {
			int len = areaIds.size();
			if (len == 1)
				sql += " and areaId in (" + areaIds.get(0) +") "; 
			else {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						sql += " and areaId in (" + areaIds.get(0);
					} else if (i == len - 1) {
						sql += " ,"+areaIds.get(i) + ") ";
					} else {
						sql += " ,"+areaIds.get(i);
					}
				}
			}
		}
		sql += " )t LEFT JOIN (select deviceType,count(*) as alarmNum from smoke s,alarm a where s.mac = a.smokeMac and a.alarmTruth = 4 " ;
		if(areaIds == null)
			return null; 
		else {
			int len = areaIds.size();
			if (len == 1)
				sql += " and areaId in (" + areaIds.get(0) +") "; 
			else {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						sql += " and areaId in (" + areaIds.get(0);
					} else if (i == len - 1) {
						sql += " ,"+areaIds.get(i) + ") ";
					} else {
						sql += " ,"+areaIds.get(i);
					}
				}
			}
		}
		sql += " group by deviceType) s on t.deviceType = s.deviceType";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<Devices> lstd = new ArrayList<Devices>();
		int alarmNum = 0;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				Devices dev = new Devices();
				
//				int devId = rs.getInt("deviceType");
				//烟感 1 烟感 21 loraWan烟感 31三江烟感 41NB烟感
				//水位 19水位 124DTU水位
				//水压 10水压	 42NB水压
				//主机 9三江主机 119三江传输装置 126海湾主机 20无线模块
				//燃气 2燃气  16NB燃气
				
				//屏蔽 17电梯 11红外 12门磁 31三江烟感
				if(rs.getInt("deviceType") ==  11 || rs.getInt("deviceType") ==  12 || rs.getInt("deviceType") ==  17 || rs.getInt("deviceType") ==  31)
					continue;
				dev.setId(rs.getInt("deviceType"));
				dev.setDeviceName(rs.getString("deviceName"));
				dev.setAlarmNum(rs.getInt("alarmNum"));
				alarmNum += rs.getInt("alarmNum");
				lstd.add(dev);
			}
			
			
			if (lstd != null) { //按照数量排序,倒序
				Collections.sort(lstd,new Comparator<Devices>(){
	
					@Override
					public int compare(Devices dev1, Devices dev2) {
						int i = dev2.getAlarmNum() - dev1.getAlarmNum();
						return i;
					}
					
				});
			}
			
			//将总数作为一个对象添加到list末尾
			Devices  total = new Devices();
			total.setId(0);
			total.setDeviceName("真实报警总数");
			total.setAlarmNum(alarmNum);
			lstd.add(total);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return lstd;
	}

	@Override
	public List<Devices> getStaticsByDeviceType(List<String> areaIds) {
		String sql = "select devicetype,deviceName,netstate,count(*)  from smoke s,devices d where d.id = s.devicetype ";
		if(areaIds == null)
			return null;
		else {
			int len = areaIds.size();
			if (len == 1)
				sql += " and areaId in (" + areaIds.get(0) +") "; 
			else {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						sql += " and areaId in (" + areaIds.get(0);
					} else if (i == len - 1) {
						sql += " ,"+areaIds.get(i) + ") ";
					} else {
						sql += " ,"+areaIds.get(i);
					}
				}
			}
		}
		sql += " group by deviceType,netstate";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<Devices> lstdev = null;
		Devices dev = null;
		try {
			rs = ps.executeQuery();
			int total = 0;
			while (rs.next()) {
				if (lstdev == null) {
					lstdev = new ArrayList<Devices>();
				}
				int devicetype = rs.getInt(1);
				String deviceName = rs.getString(2);
				int netstate = rs.getInt(3);
				int count = rs.getInt(4);
				if (dev == null || dev.getId() != devicetype){ //每一种设备类型
					if (dev != null) {
						dev.setNum(total);
						lstdev.add(dev);
					}
					dev = new Devices();
					dev.setId(devicetype);
					dev.setDeviceName(deviceName);
					if (netstate == 0) {
						dev.setOffNum(count);
					} else {
						dev.setOnNum(count);
					}
					total =count;
				} else {   //同一设备类型，由于同一设备类型有两条数据只可能是在线的统计数据，
					dev.setOnNum(count);
					total += count;
				}
			}
			if (dev != null) { //最后一个设备类型
				dev.setNum(total);
				lstdev.add(dev);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return lstdev;
	}

	@Override
	public List<WorkingTime> getWorkingTime(String userId) {
		String sql = "SELECT u.userid,u.named,u.workingTime,a.alarmType,t.alarmName,a.ifDealAlarm,a.alarmTime from user u,alarm a,alarmtype t where u.userid = a.smokeMac and t.alarmId = a.alarmType and u.superUserId = ?";
		List<WorkingTime> wlist = new ArrayList<WorkingTime>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, userId);
			rs = ps.executeQuery();
			while(rs.next()){
				WorkingTime work = new WorkingTime();
				work.setUserId(rs.getString("userid"));
				work.setNamed(rs.getString("named"));
				work.setWorktime(rs.getString("workingTime"));
				work.setAlarmType(rs.getString("alarmName"));
				int deal = rs.getInt("ifDealAlarm");
				if(deal==0){
					work.setDealalarm("未确认");
				}else{
					work.setDealalarm("已确认");
				}
				wlist.add(work);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return wlist;
	}
	@Override
	public void updateUserInfo(String working,String userId) {
		// TODO Auto-generated method stub
		String sql = "UPDATE user set workingTime = ? where userId = ?";
		List<WorkingTime> wlist = new ArrayList<WorkingTime>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, working);
			ps.setString(2, userId);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}
	
	@Override
	public Map<String,String> getUserWorking() {
		String sql = "SELECT userId,workingTime from user where superUserId!=''";
		Map<String,String> wlist = new HashMap<String,String>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				wlist.put(rs.getString(1), rs.getString(2));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return wlist;
	}

	@Override
	public WorkingTime selectParentIdByUserId(String id) {
		String sql = "SELECT superpUserId from user where userId = ?";
		//List<WorkingTime> wlist = new ArrayList<WorkingTime>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		WorkingTime work=null;
		try {
			ps.setString(1, id);
			rs = ps.executeQuery();
			while(rs.next()){
				String id2=rs.getString(1);
				work = new WorkingTime();
				work.setSuperUserId(rs.getString(1));
				/*wlist.add(work);*/
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return work;
		
	}

	@Override
	public void uploadImg(String imgPath, String mac) {
		String sql="update smoke set image = ? where mac=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		try {
			ps.setString(1, imgPath);
			ps.setString(2, mac);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}

	@Override
	public String getImgSrc(String mac) {
		String sql="select image from smoke where mac=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		String imgSrc = null;
		try {
			if (Utils.isNullStr(mac)) {
				ps.setString(1, mac);
			}
			rs = ps.executeQuery();
			
			while (rs.next()) {
				imgSrc = rs.getString(1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return imgSrc;
	}

	@Override
	public void uploadVideo(String videoPath, String mac) {
		String sql="update smoke set video = ? where mac=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		try {
			ps.setString(1, videoPath);
			ps.setString(2, mac);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}
	
	@Override
	public String getVideoSrc(String mac) {
		String sql="select video from smoke where mac=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		String videoSrc = null;
		try {
			if (Utils.isNullStr(mac)) {
				ps.setString(1, mac);
			}
			rs = ps.executeQuery();
			
			while (rs.next()) {
				videoSrc = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return videoSrc;
	}
	
}
