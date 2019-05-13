package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.cloudfire.dao.DeviceDao;
import com.cloudfire.dao.MapControlDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.BuildingBean;
import com.cloudfire.entity.CountValue;
import com.cloudfire.entity.NFC_infoEntity;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.Utils;

public class MapControlDaoImpl implements MapControlDao {
	
	public List<SmokeBean> getAreaSmoke(String areaName, int areaId,
			String userId) {
		String sql = "select named,deviceName,netState,address,mac,longitude,latitude,"
				+ "principal1,principal1Phone,principal2,principal2Phone,ifalarm from smoke,devices d"
				+ " where deviceType =d.id and areaId=?  ";
//				"and smoke.deviceType " + Constant.sql;
 
		DeviceDao dd = new DevicesDaoImpl();
		CountValue cv = new CountValue();
		cv = dd.getCountByPrivilege(userId, null);
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		int ifint = 0;
		List<SmokeBean> lists = new ArrayList<SmokeBean>();
		try {
			ps.setInt(1, areaId);
			rs = ps.executeQuery();
			while (rs.next()) {
				ifint++;
				if (lists == null) {
					lists = new ArrayList<SmokeBean>();
				}
				SmokeBean mSmokeBean = new SmokeBean();
				mSmokeBean.setPlaceeAddress(rs.getString(2));
				mSmokeBean.setMac(rs.getString(5));
				mSmokeBean.setAddress(rs.getString(4));
				mSmokeBean.setName(rs.getString(1));
				int netState = rs.getInt(3);
				if (netState == 1) {
					mSmokeBean.setPlaceType("在线");
				} else {
					mSmokeBean.setPlaceType("离线");
				}
				mSmokeBean.setAreaName(areaName);
				mSmokeBean.setLatitude(rs.getString(7));
				mSmokeBean.setLongitude(rs.getString(6));
				mSmokeBean.setPrincipal1(rs.getString(8));
				mSmokeBean.setPrincipal1Phone(rs.getString(9));
				mSmokeBean.setPrincipal2(rs.getString(10));
				mSmokeBean.setPrincipal2Phone(rs.getString(11));
				mSmokeBean.setIfAlarm(rs.getInt("ifalarm"));
				mSmokeBean.setCv(cv);
				lists.add(mSmokeBean);
			}
			if (ifint == 0) {
				SmokeBean mSmokeBean = new SmokeBean();
				mSmokeBean.setCv(cv);
				lists.add(mSmokeBean);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return lists;
	}

	public List<SmokeBean> getAreaSmoke(int areaId) {
		// TODO Auto-generated method stub
		/*
		 * String
		 * sql="select named,deviceType,netState,address,mac,longitude,latitude,"
		 * +
		 * "principal1,principal1Phone,principal2,principal2Phone from smoke where areaId=?"
		 * ;
		 */
		String sql = "select ar.area , s.named,d.deviceName,s.netState,s.address,s.mac,s.longitude,s.latitude,"
				+ "s.principal1,s.principal1Phone,s.principal2,s.principal2Phone from smoke s,areaidarea ar,devices d where s.deviceType = d.id and s.areaid=ar.areaid and ar.areaId=?";
		DeviceDao dd = new DevicesDaoImpl();
		CountValue cv = new CountValue();
		cv = dd.getCount(areaId + "");
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<SmokeBean> lists = null;
		try {
			ps.setInt(1, areaId);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (lists == null) {
					lists = new ArrayList<SmokeBean>();
				}
				SmokeBean mSmokeBean = new SmokeBean();
				mSmokeBean.setPlaceeAddress(rs.getString(3));
				mSmokeBean.setMac(rs.getString(6));
				mSmokeBean.setAddress(rs.getString(5));
				mSmokeBean.setName(rs.getString(2));
				int netState = rs.getInt(4);
				if (netState == 1) {
					mSmokeBean.setPlaceType("在线");
				} else {
					mSmokeBean.setPlaceType("离线");
				}
				mSmokeBean.setAreaName(rs.getString(1));
				mSmokeBean.setLatitude(rs.getString(8));
				mSmokeBean.setLongitude(rs.getString(7));
				mSmokeBean.setPrincipal1(rs.getString(9));
				mSmokeBean.setPrincipal1Phone(rs.getString(10));
				mSmokeBean.setPrincipal2(rs.getString(11));
				mSmokeBean.setPrincipal2Phone(rs.getString(12));
				mSmokeBean.setCv(cv);
				lists.add(mSmokeBean);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return lists;
	}


	
	@Override
	public List<SmokeBean> getAreaSmokeLggData(String areaName, String comName,
			Integer[] idArea, String deviceId, String userId) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT s.named,d.deviceName,s.netState,s.address,s.mac,s.longitude,s.latitude,s.principal1,s.principal1Phone,s.principal2,s.principal2Phone,s.ifAlarm,a.area");
		sb.append(" FROM smoke as s,areaidarea as a, devices d");
		sb.append(" WHERE s.areaId=a.areaId and s.deviceType = d.id ");
//		sb.append(" and s.deviceType " + Constant.sql);
		if (Utils.isNullStr(deviceId) && deviceId != "0"
				&& !"0".equals(deviceId)) {
			sb.append(" AND s.deviceType = " + deviceId);
		}
		if (Utils.isNullStr(comName)) {
			sb.append(" AND s.named like '%" + comName + "%'");
		}
		if (comName == null || comName == "") {
			Integer areaId = idArea[0];
			sb.append(" AND s.areaId=" + areaId);

		} else {
			List<Integer> list11 = Arrays.asList(idArea);
			int size = list11.size();
			sb.append(" AND s.areaId in(");
			for (int i = 0; i < size; i++) {
				int a = list11.get(i);
				if (size - i == 1) {
					sb.append(a + ")");
				} else {
					sb.append(a + ",");
				}
			}

		}

		String sql = sb.toString();
		CountValue cv = new CountValue();
		int macNum = 0; // 终端总数&&设备正常运行数量 2
		int netStaterNum = 0; // 正常状态数量&&设备故障数量4
		int ifDealNum = 0; // 报警状态个数&&报警总数5
		int noNetStater = 0; // 故障个数&&设备掉线数量 3

		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<SmokeBean> lists = new ArrayList<SmokeBean>();
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				macNum++;
				if (lists == null) {
					lists = new ArrayList<SmokeBean>();
				}
				SmokeBean mSmokeBean = new SmokeBean();
				mSmokeBean.setPlaceeAddress(rs.getString(2));
				mSmokeBean.setMac(rs.getString(5));
				mSmokeBean.setAddress(rs.getString(4));
				mSmokeBean.setName(rs.getString(1));
				int netState = rs.getInt(3);
				if (netState == 1) {
					netStaterNum++;
					mSmokeBean.setPlaceType("在线");
				} else {
					noNetStater++;
					mSmokeBean.setPlaceType("离线");
				}
				// mSmokeBean.setAreaName(areaName);
				mSmokeBean.setLatitude(rs.getString(7));
				mSmokeBean.setLongitude(rs.getString(6));
				mSmokeBean.setPrincipal1(rs.getString(8));
				mSmokeBean.setPrincipal1Phone(rs.getString(9));
				mSmokeBean.setPrincipal2(rs.getString(10));
				mSmokeBean.setPrincipal2Phone(rs.getString(11));
				int ifalarmNum = rs.getInt("ifAlarm");
				mSmokeBean.setAreaName(rs.getString(13));
				if (ifalarmNum == 0) {
					ifDealNum++;
				}
				mSmokeBean.setIfAlarm(ifalarmNum);
				cv.setIfDealNum(ifDealNum);
				cv.setNetStaterNum(netStaterNum);
				cv.setNoNetStater(noNetStater);
				cv.setMacNum(macNum);
				mSmokeBean.setCv(cv);
				lists.add(mSmokeBean);
			}
			if (macNum == 0) {
				SmokeBean mSmokeBean = new SmokeBean();
				mSmokeBean.setCv(cv);
				lists.add(mSmokeBean);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return lists;
	}

	
	@Override
	public List<SmokeBean> getAreaSmokeLggNum(String areaName, String comName,
			List<Integer> areaId, String deviceId, String userId) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT s.named,d.deviceName,s.netState,s.address,s.mac,s.longitude,s.latitude,s.principal1,s.principal1Phone,s.principal2,s.principal2Phone,s.ifAlarm,a.area");
		sb.append(" FROM smoke as s,areaidarea as a,devices d");
		sb.append(" WHERE s.areaId=a.areaId and s.deviceType=d.id ");
//		sb.append(" and s.deviceType " + Constant.sql);
		if (Utils.isNullStr(deviceId) && deviceId != "0"
				&& !"0".equals(deviceId)) {
			sb.append(" AND s.deviceType = " + deviceId);
		}
		if (Utils.isNullStr(comName)) {
			sb.append(" AND s.named like '%" + comName + "%'");
		}
		sb.append(" AND s.areaId in(");
		int size = areaId.size();
		for (int i = 0; i < size; i++) {
			int a = areaId.get(i);
			if (size - i == 1) {
				sb.append(a + ")");
			} else {
				sb.append(a + ",");
			}

		}
		String sql = sb.toString();
		CountValue cv = new CountValue();
		int macNum = 0; // 终端总数&&设备正常运行数量 2
		int netStaterNum = 0; // 正常状态数量&&设备故障数量4
		int ifDealNum = 0; // 报警状态个数&&报警总数5
		int noNetStater = 0; // 故障个数&&设备掉线数量 3

		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<SmokeBean> lists = new ArrayList<SmokeBean>();
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				macNum++;
				if (lists == null) {
					lists = new ArrayList<SmokeBean>();
				}
				SmokeBean mSmokeBean = new SmokeBean();
				mSmokeBean.setPlaceeAddress(rs.getString(2));
				mSmokeBean.setMac(rs.getString(5));
				mSmokeBean.setAddress(rs.getString(4));
				mSmokeBean.setName(rs.getString(1));
				int netState = rs.getInt(3);
				if (netState == 1) {
					netStaterNum++;
					mSmokeBean.setPlaceType("在线");
				} else {
					noNetStater++;
					mSmokeBean.setPlaceType("离线");
				}
				// mSmokeBean.setAreaName(areaName);
				mSmokeBean.setLatitude(rs.getString(7));
				mSmokeBean.setLongitude(rs.getString(6));
				mSmokeBean.setPrincipal1(rs.getString(8));
				mSmokeBean.setPrincipal1Phone(rs.getString(9));
				mSmokeBean.setPrincipal2(rs.getString(10));
				mSmokeBean.setPrincipal2Phone(rs.getString(11));
				int ifalarmNum = rs.getInt("ifAlarm");
				mSmokeBean.setAreaName(rs.getString(13));
				if (ifalarmNum == 0) {
					ifDealNum++;
				}
				mSmokeBean.setIfAlarm(ifalarmNum);
				cv.setIfDealNum(ifDealNum);
				cv.setNetStaterNum(netStaterNum);
				cv.setNoNetStater(noNetStater);
				cv.setMacNum(macNum);
				mSmokeBean.setCv(cv);
				lists.add(mSmokeBean);
			}
			if (macNum == 0) {
				SmokeBean mSmokeBean = new SmokeBean();
				mSmokeBean.setCv(cv);
				lists.add(mSmokeBean);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return lists;
	}

	@Override
	public List<SmokeBean> getAreaSmokeLggNum(String areaName, String comName,
			String[] areaId, String deviceId, String userId) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT s.named,d.deviceName,s.netState,s.address,s.mac,s.longitude,s.latitude,s.principal1,s.principal1Phone,s.principal2,s.principal2Phone,s.ifAlarm,a.area,s.deviceType");
		sb.append(" FROM smoke as s,areaidarea as a,devices d");
		sb.append(" WHERE s.areaId=a.areaId and s.deviceType=d.id ");
//		sb.append(" and s.deviceType " + Constant.sql);
		if (Utils.isNullStr(deviceId) && deviceId != "0"
				&& !"0".equals(deviceId)) {
			sb.append(" AND s.deviceType = " + deviceId);
		}
		if (Utils.isNullStr(comName)) {
			sb.append(" AND s.named like '%" + comName + "%'");
		}
		
		sb.append(" AND s.areaId in(");
		int size = areaId.length;
		for (int i = 0; i < size - 1; i++) {
			sb.append(areaId[i]);
			sb.append(",");
		}
		sb.append(areaId[size - 1]);
		sb.append(")");
		
		String sql = sb.toString();
		
		CountValue cv = null;
		int macNum = 0; // 终端总数&&设备正常运行数量 2
		int netStaterNum = 0; // 正常状态数量&&设备故障数量4
		int ifDealNum = 0; // 报警状态个数&&报警总数5
		int noNetStater = 0; // 故障个数&&设备掉线数量 3

		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<SmokeBean> lists = new ArrayList<SmokeBean>();
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				macNum++;
				SmokeBean mSmokeBean = new SmokeBean();
				mSmokeBean.setDeviceType(rs.getInt("deviceType"));
				mSmokeBean.setPlaceeAddress(rs.getString(2));
				mSmokeBean.setMac(rs.getString(5));
				mSmokeBean.setAddress(rs.getString(4));
				mSmokeBean.setName(rs.getString(1));
				int netState = rs.getInt(3);
				if (netState == 1) {
					netStaterNum++;
					mSmokeBean.setPlaceType("在线");
				} else {
					noNetStater++;
					mSmokeBean.setPlaceType("离线");
				}
				// mSmokeBean.setAreaName(areaName);
				mSmokeBean.setLatitude(rs.getString(7));
				mSmokeBean.setLongitude(rs.getString(6));
				mSmokeBean.setPrincipal1(rs.getString(8));
				mSmokeBean.setPrincipal1Phone(rs.getString(9));
				mSmokeBean.setPrincipal2(rs.getString(10));
				mSmokeBean.setPrincipal2Phone(rs.getString(11));
				int ifalarmNum = rs.getInt("ifAlarm");
				mSmokeBean.setAreaName(rs.getString(13));
				if (ifalarmNum == 0) {
					ifDealNum++;
				}
				mSmokeBean.setIfAlarm(ifalarmNum);
				lists.add(mSmokeBean);
			}
			//确认lists是否有元素
			if (lists.size() == 0){
				lists.add(new SmokeBean());
			}
			//将统计结果放到集合的第一个元素里
			cv = new CountValue();
			cv.setIfDealNum(ifDealNum);
			cv.setNetStaterNum(netStaterNum);
			cv.setNoNetStater(noNetStater);
			cv.setMacNum(macNum);
			lists.get(0).setCv(cv);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return lists;
	}


	@Override
	public List<SmokeBean> getAreaSmoke(String areaName, String comName,
			String areaId, String deviceId, String userId) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT s.named,d.deviceName,s.netState,s.address,s.mac,s.longitude,s.latitude,s.principal1,s.principal1Phone,s.principal2,s.principal2Phone,s.ifAlarm");
		sb.append(" FROM smoke s,devices d ");
		sb.append(" WHERE 1 = 1 and s.deviceType = d.id ");
//		sb.append(" and s.deviceType " + Constant.sql);
		if (Utils.isNullStr(deviceId) && deviceId != "0"
				&& !"0".equals(deviceId)) {
			sb.append(" AND s.deviceType = " + deviceId);
		}
		if (Utils.isNullStr(comName)) {
			sb.append(" AND s.named like '%" + comName + "%'");
		}
		sb.append(" AND s.areaId = " + areaId);
		String sql = sb.toString();
		CountValue cv = new CountValue();
		int macNum = 0; // 终端总数&&设备正常运行数量 2
		int netStaterNum = 0; // 正常状态数量&&设备故障数量4
		int ifDealNum = 0; // 报警状态个数&&报警总数5
		int noNetStater = 0; // 故障个数&&设备掉线数量 3

		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<SmokeBean> lists = new ArrayList<SmokeBean>();
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				macNum++;
				if (lists == null) {
					lists = new ArrayList<SmokeBean>();
				}
				SmokeBean mSmokeBean = new SmokeBean();
				mSmokeBean.setPlaceeAddress(rs.getString(2));
				mSmokeBean.setMac(rs.getString(5));
				mSmokeBean.setAddress(rs.getString(4));
				mSmokeBean.setName(rs.getString(1));
				int netState = rs.getInt(3);
				if (netState == 1) {
					netStaterNum++;
					mSmokeBean.setPlaceType("在线");
				} else {
					noNetStater++;
					mSmokeBean.setPlaceType("离线");
				}
				mSmokeBean.setAreaName(areaName);
				mSmokeBean.setLatitude(rs.getString(7));
				mSmokeBean.setLongitude(rs.getString(6));
				mSmokeBean.setPrincipal1(rs.getString(8));
				mSmokeBean.setPrincipal1Phone(rs.getString(9));
				mSmokeBean.setPrincipal2(rs.getString(10));
				mSmokeBean.setPrincipal2Phone(rs.getString(11));
				int ifalarmNum = rs.getInt("ifAlarm");
				if (ifalarmNum == 0) {
					ifDealNum++;
				}
				mSmokeBean.setIfAlarm(ifalarmNum);
				cv.setIfDealNum(ifDealNum);
				cv.setNetStaterNum(netStaterNum);
				cv.setNoNetStater(noNetStater);
				cv.setMacNum(macNum);
				mSmokeBean.setCv(cv);
				lists.add(mSmokeBean);
			}
			if (macNum == 0) {
				SmokeBean mSmokeBean = new SmokeBean();
				mSmokeBean.setCv(cv);
				lists.add(mSmokeBean);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return lists;
	}


	@Override
	public List<SmokeBean> getAreaSmoke(String comName, int areaId,
			int deviceId) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT s.named,d.deviceName,s.netState,s.address,s.mac,s.longitude,s.latitude,s.principal1,s.principal1Phone,s.principal2,s.principal2Phone,d.id");
		sb.append(" FROM smoke s,devices d");
		sb.append(" WHERE s.areaId=?");
		sb.append(" AND d.id = s.deviceType");
		sb.append(" AND s.deviceType = ?");
		String sql = sb.toString();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<SmokeBean> lists = null;
		try {
			ps.setInt(1, areaId);
			ps.setInt(2, deviceId);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (lists == null) {
					lists = new ArrayList<SmokeBean>();
				}
				SmokeBean mSmokeBean = new SmokeBean();
				mSmokeBean.setPlaceeAddress(rs.getString(2));
				mSmokeBean.setMac(rs.getString(5));
				mSmokeBean.setAddress(rs.getString(4));
				mSmokeBean.setName(rs.getString(1));
				int netState = rs.getInt(3);
				if (netState == 1) {
					mSmokeBean.setPlaceType("在线");
				} else {
					mSmokeBean.setPlaceType("离线");
				}
				mSmokeBean.setAreaName("comName");
				mSmokeBean.setLatitude(rs.getString(7));
				mSmokeBean.setLongitude(rs.getString(6));
				mSmokeBean.setPrincipal1(rs.getString(8));
				mSmokeBean.setPrincipal1Phone(rs.getString(9));
				mSmokeBean.setPrincipal2(rs.getString(10));
				mSmokeBean.setPrincipal2Phone(rs.getString(11));
				lists.add(mSmokeBean);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return lists;
	}

	
	public List<SmokeBean> getAreaSmokeLgNum(String areaName,
			List<?> areaId, String userId, String privilege) {

		if (areaId == null) {
			return null;
		} else {
			StringBuffer sb = new StringBuffer();
			sb = sb.append("select s.named,d.deviceName,s.netState,s.address,s.mac,s.longitude,s.latitude,s.principal1,s.principal1Phone,s.principal2,s.principal2Phone,s.ifalarm,a.area,s.deviceType from smoke as s,areaidarea as a,devices d  where s.deviceType=d.id and  s.areaId=a.areaId and s.areaId in(");
			int size = areaId.size();
			for (int i = 0; i < size; i++) {
//				int a = areaId.get(i);
				if (size - i == 1) {
					sb.append(areaId.get(i) + ")");
				} else {
					sb.append(areaId.get(i) + ",");
				}

			}
//			sb.append("and s.deviceType " + Constant.sql);
			String sql = sb.toString();
			/*
			 * String sql =
			 * "select named,deviceType,netState,address,mac,longitude,latitude,"
			 * +
			 * "principal1,principal1Phone,principal2,principal2Phone,ifalarm from smoke"
			 * + " where areaId=?  and smoke.deviceType " + Constant.sql;
			 */
			DeviceDao dd = new DevicesDaoImpl();
			CountValue cv = new CountValue();
			if (privilege == "4" || "4".equals(privilege)) {
				cv = dd.getCount(null);
			} else {
				cv = dd.getCountByPrivilege(userId, null);
			}
			Connection conn = DBConnectionManager.getConnection();
			PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
			ResultSet rs = null;
			int ifint = 0;
			List<SmokeBean> lists = new ArrayList<SmokeBean>();
			try {
				rs = ps.executeQuery();
				while (rs.next()) {
					ifint++;
					if (lists == null) {
						lists = new ArrayList<SmokeBean>();
					}
					SmokeBean mSmokeBean = new SmokeBean();
					mSmokeBean.setDeviceType(rs.getInt("deviceType"));
					mSmokeBean.setPlaceeAddress(rs.getString(2));
					mSmokeBean.setMac(rs.getString(5));
					mSmokeBean.setAddress(rs.getString(4));
					mSmokeBean.setName(rs.getString(1));
					int netState = rs.getInt(3);
					if (netState == 1) {
						mSmokeBean.setPlaceType("在线");
					} else {
						mSmokeBean.setPlaceType("离线");
					}
					// mSmokeBean.setAreaName(areaName);
					mSmokeBean.setLatitude(rs.getString(7));
					mSmokeBean.setLongitude(rs.getString(6));
					mSmokeBean.setPrincipal1(rs.getString(8));
					mSmokeBean.setPrincipal1Phone(rs.getString(9));
					mSmokeBean.setPrincipal2(rs.getString(10));
					mSmokeBean.setPrincipal2Phone(rs.getString(11));
					mSmokeBean.setIfAlarm(rs.getInt("ifalarm"));
					mSmokeBean.setAreaName(rs.getString(13));
					mSmokeBean.setCv(cv);
					lists.add(mSmokeBean);
				}
				if (ifint == 0) {
					SmokeBean mSmokeBean = new SmokeBean();
					mSmokeBean.setCv(cv);
					lists.add(mSmokeBean);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				DBConnectionManager.close(rs);
				DBConnectionManager.close(ps);
				DBConnectionManager.close(conn);
			}
			return lists;
		}

	}

	public List<SmokeBean> getAreaSmokeLgData(String areaName, Integer areaId,
			String userId, String privilege) {

		if (areaId == null) {
			return null;
		} else {
			StringBuffer sb = new StringBuffer();
			sb = sb.append("select s.named,d.deviceName,s.netState,s.address,s.mac,s.longitude,s.latitude,s.principal1,s.principal1Phone,s.principal2,s.principal2Phone,s.ifalarm,a.area from smoke as s,areaidarea as a,devices d  where deviceType = d.id and s.areaId=a.areaId and s.areaId="
					+ areaId);

//			sb.append(" and s.deviceType " + Constant.sql);
			String sql = sb.toString();
			/*
			 * String sql =
			 * "select named,deviceType,netState,address,mac,longitude,latitude,"
			 * +
			 * "principal1,principal1Phone,principal2,principal2Phone,ifalarm from smoke"
			 * + " where areaId=?  and smoke.deviceType " + Constant.sql;
			 */
			DeviceDao dd = new DevicesDaoImpl();
			CountValue cv = new CountValue();
			if (privilege == "4" || "4".equals(privilege)) {
				cv = dd.getCount(null);
			} else {
				cv = dd.getCountByPrivilege(userId, null);
			}
			Connection conn = DBConnectionManager.getConnection();
			PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
			ResultSet rs = null;
			int ifint = 0;
			List<SmokeBean> lists = new ArrayList<SmokeBean>();
			try {
				rs = ps.executeQuery();
				while (rs.next()) {
					ifint++;
					if (lists == null) {
						lists = new ArrayList<SmokeBean>();
					}
					SmokeBean mSmokeBean = new SmokeBean();
					mSmokeBean.setPlaceeAddress(rs.getString(2));
					mSmokeBean.setMac(rs.getString(5));
					mSmokeBean.setAddress(rs.getString(4));
					mSmokeBean.setName(rs.getString(1));
					int netState = rs.getInt(3);
					if (netState == 1) {
						mSmokeBean.setPlaceType("在线");
					} else {
						mSmokeBean.setPlaceType("离线");
					}
					// mSmokeBean.setAreaName(areaName);
					mSmokeBean.setLatitude(rs.getString(7));
					mSmokeBean.setLongitude(rs.getString(6));
					mSmokeBean.setPrincipal1(rs.getString(8));
					mSmokeBean.setPrincipal1Phone(rs.getString(9));
					mSmokeBean.setPrincipal2(rs.getString(10));
					mSmokeBean.setPrincipal2Phone(rs.getString(11));
					mSmokeBean.setIfAlarm(rs.getInt("ifalarm"));
					mSmokeBean.setAreaName(rs.getString(13));
					mSmokeBean.setCv(cv);
					lists.add(mSmokeBean);
				}
				if (ifint == 0) {
					SmokeBean mSmokeBean = new SmokeBean();
					mSmokeBean.setCv(cv);
					lists.add(mSmokeBean);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				DBConnectionManager.close(rs);
				DBConnectionManager.close(ps);
				DBConnectionManager.close(conn);
			}
			return lists;
		}

	}

	@Override
	public List<SmokeBean> getAreaSmokeByLongitu(String areaName,
			String longitude, String latitude, String smokeMac, String userId) {
		String sql = "SELECT named,deviceName,netState,address,mac,longitude,latitude,principal1,principal1Phone,principal2,principal2Phone,a.area as area,ifAlarm FROM smoke ,areaidarea a,devices d WHERE deviceType = d.id and smoke.areaId = a.areaId and longitude=? AND latitude = ? AND mac = ? ";
//		sql += " and smoke.deviceType " + Constant.sql;

		DeviceDao dd = new DevicesDaoImpl();
		CountValue cv = new CountValue();
		cv = dd.getCountByPrivilege(userId, null);
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<SmokeBean> lists = null;
		try {
			ps.setString(1, longitude);
			ps.setString(2, latitude);
			ps.setString(3, smokeMac);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (lists == null) {
					lists = new ArrayList<SmokeBean>();
				}
				SmokeBean mSmokeBean = new SmokeBean();
				mSmokeBean.setIfAlarm(rs.getInt("ifAlarm"));
				mSmokeBean.setPlaceeAddress(rs.getString(2));
				mSmokeBean.setMac(rs.getString(5));
				mSmokeBean.setAddress(rs.getString(4));
				mSmokeBean.setName(rs.getString(1));
				int netState = rs.getInt(3);
				if (netState == 1) {
					mSmokeBean.setPlaceType("在线");
				} else {
					mSmokeBean.setPlaceType("离线");
				}
				// mSmokeBean.setAreaName(areaName);
				mSmokeBean.setLatitude(rs.getString(7));
				mSmokeBean.setLongitude(rs.getString(6));
				mSmokeBean.setPrincipal1(rs.getString(8));
				mSmokeBean.setPrincipal1Phone(rs.getString(9));
				mSmokeBean.setPrincipal2(rs.getString(10));
				mSmokeBean.setPrincipal2Phone(rs.getString(11));
				mSmokeBean.setAreaName(rs.getString("area"));
				mSmokeBean.setCv(cv);
				lists.add(mSmokeBean);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return lists;
	}

	@Override
	public List<NFC_infoEntity> getAreaNfc(String areaName, String areaId,
			NFC_infoEntity nfc) {
		List<NFC_infoEntity> nfcList = new ArrayList<NFC_infoEntity>();
		String sqlstr = "SELECT * FROM (SELECT r.uuid,r.longitude,r.latitude,r.endTime FROM nfcrecord r,nfcinfo i WHERE r.uuid = i.uid AND i.areaid = ? AND r.endTime > ? AND r.endTime < ? ORDER BY r.endTime DESC) AS a GROUP BY uuid";
		String getTime = GetTime.ConvertTimeByLong();
		String endTime_1 = getTime.substring(0, 8) + "01 00:00:01";
		String endTime_2 = getTime.substring(0, 8) + "31 23:59:59";
		if (nfc.getEndTime_1() != null && nfc.getEndTime_1() != "") {
			endTime_1 = nfc.getEndTime_1();
			endTime_2 = nfc.getEndTime_2();
		}
		CountValue cv = new CountValue();
		// int
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		try {
			ps.setString(1, areaId);
			ps.setString(2, endTime_1);
			ps.setString(3, endTime_2);
			rs = ps.executeQuery();
			while (rs.next()) {
				NFC_infoEntity nfcinfo = new NFC_infoEntity();
				nfcinfo.setUid(rs.getString("uuid"));
				nfcinfo.setLongitude(rs.getString("longitude"));
				nfcinfo.setLatitude(rs.getString("latitude"));
				nfcList.add(nfcinfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return nfcList;
	}

	@Override
	public List<BuildingBean> getBuildingStatics(String deviceId,String comName, String[] areaIds) {
		List<BuildingBean> lstBuilding = new ArrayList<BuildingBean>();
	
		//第一个为所有的统计数量
		BuildingBean bb = new BuildingBean();
		CountValue cv = new CountValue();
		int macNum = 0; //设备总数
		int netStaterNum = 0; // 在线数
		int ifDealNum = 0; // 报警数
		int noNetStater = 0; //离线数
		cv.setMacNum(macNum);
		cv.setNetStaterNum(netStaterNum);
		cv.setIfDealNum(ifDealNum);
		cv.setNoNetStater(noNetStater);
		bb.setBuildingName("全部");
		bb.setCv(cv);
		lstBuilding.add(bb);
		
		StringBuffer sb = new StringBuffer();
		sb.append("select floors,longitude,latitude,netState,ifAlarm from smoke where 1 = 1 ");
		if(StringUtils.isNotBlank(deviceId)){
			sb.append(" and deviceType = "+ deviceId);
		}
		if(StringUtils.isNotBlank(comName)){
			sb.append(" and named like '"+ comName +"'");
		}
		sb.append(" and areaId in ( ");
		int len = areaIds.length;
		for(int i=0;i<len-1;i++){
			sb.append(areaIds[i]+",");
		}
		sb.append(areaIds[len - 1]+") ");
		
		sb.append(" ORDER BY floors,netState,ifAlarm ");
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sb.toString());
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			int i = 0;
			while(rs.next()){
				macNum++;
				String buildingName = rs.getString("floors");
				if (!lstBuilding.get(i).getBuildingName().equals(buildingName)){
					BuildingBean bbu = new BuildingBean();
					bbu.setBuildingName(buildingName);
					bbu.setLongtitude(rs.getString("longitude"));
					bbu.setLatitude(rs.getString("latitude"));
					CountValue cvu =new CountValue();
					cvu.setMacNum(1);
					if (rs.getInt("netState")==0){
						noNetStater++;
						cvu.setNetStaterNum(0);
						cvu.setNoNetStater(1);
					} else {
						netStaterNum++;
						cvu.setNetStaterNum(1);
						cvu.setNoNetStater(0);
					}
					if (rs.getInt("ifAlarm") ==0){
						ifDealNum++;
						cvu.setIfDealNum(0);
					}
					bbu.setCv(cvu);
					i++;
					lstBuilding.add(bbu);
				} else {
					lstBuilding.get(i).getCv().setMacNum(lstBuilding.get(i).getCv().getMacNum()+1);
					if (rs.getInt("netState")==0){
						noNetStater++;
						lstBuilding.get(i).getCv().setNoNetStater(lstBuilding.get(i).getCv().getNoNetStater()+1);
					} else {
						netStaterNum++;
						lstBuilding.get(i).getCv().setNetStaterNum(lstBuilding.get(i).getCv().getNetStaterNum()+1);
					}
					if (rs.getInt("ifAlarm") ==0){
						ifDealNum++;
						lstBuilding.get(i).getCv().setIfDealNum(lstBuilding.get(i).getCv().getIfDealNum()+1);
					}
				}
				
			}
			lstBuilding.get(0).getCv().setMacNum(macNum);
			lstBuilding.get(0).getCv().setNoNetStater(noNetStater);
			lstBuilding.get(0).getCv().setNetStaterNum(netStaterNum);
			lstBuilding.get(0).getCv().setIfDealNum(ifDealNum);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return lstBuilding;
	}

}
