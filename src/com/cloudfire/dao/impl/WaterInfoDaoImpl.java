/**
 * 下午7:19:20
 */
package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cloudfire.dao.PlaceTypeDao;
import com.cloudfire.dao.WaterInfoDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.SmartControlEntity;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.Water;
import com.cloudfire.entity.WaterBean;
import com.cloudfire.entity.WaterEntity;
import com.cloudfire.entity.query.WaterQuery;
import com.cloudfire.push.MyThread;
import com.cloudfire.push.WebThread;
import com.cloudfire.until.Constant;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.IfStopAlarm;
import com.cloudfire.until.Utils;

/**
 * @author cheng 2017-3-21 下午7:19:20
 */
public class WaterInfoDaoImpl implements WaterInfoDao {
	private PlaceTypeDao mPlaceTypeDao;

	public boolean addWaterInfo(String waterRepeater, String waterMac, int status, String value) {
		boolean flag = false;
		String sqlString = "insert into waterinfo (waterMac,repeaterMac,status,value,time,remark) value(?,?,?,?,?,?)";
		Connection connection = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(connection, sqlString);
		try {
			ppst.setString(1, waterMac);
			ppst.setString(2, waterRepeater);
			ppst.setInt(3, status);
			ppst.setString(4, value);
			String mytimeString = GetTime.ConvertTimeByLong();
			ppst.setString(5, mytimeString);
			ppst.setString(6, "无");
			int i = ppst.executeUpdate();
			if (i > 0) {
				flag = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(connection);
		}
		return flag;
	}
	
	
	public boolean addHengxingFalanWaterInfo(String waterRepeater, String waterMac, int status, String value,long time) {
		boolean flag = false;
		//判断数据不存在才会存储数据
		String sqlString = "insert into waterinfo (waterMac,repeaterMac,status,value,time,remark) SELECT ?,?,?,?,?,? FROM DUAL where not EXISTS (SELECT waterMac from waterinfo WHERE waterMac=? and time=? ) ";
		Connection connection = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(connection, sqlString);
		try {
			ppst.setString(1, waterMac);
			ppst.setString(2, waterRepeater);
			ppst.setInt(3, status);
			ppst.setString(4, value);
			String mytimeString = GetTime.getTimeByLong(time);
			ppst.setString(5, mytimeString);
			ppst.setString(6, "无");
			ppst.setString(7, waterMac);
			ppst.setString(8, waterMac);
			int i = ppst.executeUpdate();
			if (i > 0) {
				flag = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(connection);
		}
		return flag;
	}

	public boolean addWaterInfo(String waterRepeater, String waterMac, int status, String value, String time) {
		boolean flag = false;
		String sqlString = "insert into waterinfo (waterMac,repeaterMac,status,value,time,remark) value(?,?,?,?,?,?)";
		Connection connection = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(connection, sqlString);
		try {
			ppst.setString(1, waterMac);
			ppst.setString(2, waterRepeater);
			ppst.setInt(3, status);
			ppst.setString(4, value);
			ppst.setString(5, time);
			ppst.setString(6, "无");
			int i = ppst.executeUpdate();
			if (i > 0) {
				flag = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(connection);
		}
		return flag;
	}

	// 获得水压设备的信息
	public List<WaterQuery> getAllWaterInfo(List<String> areaList, WaterQuery query) {
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		int len = areaList.size();
		String limit = " limit " + query.getStartRow() + "," + query.getPageSize();
		Map<String, String> map = mPlaceTypeDao.getShopTypeById();
//		String sql = "select first.*,second.* from (";
		
		String sql = "";
		sql += "select  s.named,s.mac,s.address,s.netState,s.repeater,a.area,s.placeTypeId,s.time,s.deviceType,s.electrState,d.deviceName,s.rssivalue,s.ifAlarm "
				+ "from smoke s,areaidarea a,devices d " + "where 1=1 and d.id = s.deviceType and s.areaId = a.areaId";
		if (query != null) {
			if (StringUtils.isNotBlank(query.getDeviceType())) {
				switch (query.getDeviceType()) {
				case "10":
				case "70":
				case "125":
					sql += " and s.deviceType " + Constant.waterInfosSql;
					break;
				case "19":
				case "69":
				case "124":
					sql += " and s.deviceType " + Constant.waterLevelSql;
					break;
				default:
					sql += " and s.deviceType =" + query.getDeviceType();
					break;
				}
				// sql += " and s.deviceType "+Constant.waterInfosSql;
			}
			if(StringUtils.isNotBlank(query.getType())){
				sql += " and d.devId = " + query.getType();
			}
			if (StringUtils.isNotBlank(query.getMac())) {
				sql += " and s.mac like '%" + query.getMac() + "%'";
			}
			if (StringUtils.isNotBlank(query.getNetState())) {
				sql += " and s.netState ='" + Integer.parseInt(query.getNetState()) + "'";
			}
			if (StringUtils.isNotBlank(query.getAreaName())) {
				sql += " and s.areaId = '" + Integer.parseInt(query.getAreaName()) + "'";
			}
			if (StringUtils.isNotBlank(query.getPlaceType())) {
				sql += " and s.placeTypeId = '" + Integer.parseInt(query.getPlaceType()) + "'";
			}
			if (len == 0) {
				return null;
			}
			if (len == 1) {
				sql += " and s.areaId in ('" + areaList.get(0) + "') order by s.time desc" + limit;
			}
			if (len > 1) {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						sql += " and s.areaId in ('" + areaList.get(i) + "', ";
					} else if (i == (len - 1)) {
						sql += " " + areaList.get(i) + ") order by s.time desc " + limit;
					} else {
						sql += " " + areaList.get(i) + ",";
					}
				}
			}
		}

//		sql += ") first left join (select * from "
//				+ "(select smokeMac,a.alarmType,al.alarmName from alarm a,alarmType al "
//				+ "where a.alarmType = al.alarmId ";
//		if (query != null) {
//			if (!StringUtils.isBlank(query.getDeviceType())) {
//				sql += "and smokeMac in  (select mac from smoke where deviceType =" + query.getDeviceType() + ")";
//			}
//		}
//		sql += " order by alarmTime desc) ala group by smokeMac) second " + " on first.mac = second.smokeMac";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<WaterQuery> lists = new ArrayList<>();

		try {
			rs = ps.executeQuery();
			int row = query.getStartRow();
			while (rs.next()) {

				WaterQuery entity = new WaterQuery();
				entity.setRow(++row);
				entity.setAddress(rs.getString(3));
				entity.setAreaName(rs.getString(6));
				entity.setAddWaterTime(rs.getString(8));
				String placeTypeId = rs.getString(7) + "";
				if (StringUtils.isNumeric(placeTypeId)) {
					entity.setPlaceType(map.get(placeTypeId));
				}
				if (rs.getInt(4) == 0) {
					entity.setNetState(Constant.NETSTATE0);
				} else {
					entity.setNetState(Constant.NETSTATE1);
				}
				entity.setDeviceType(rs.getString("deviceName"));

				entity.setName(rs.getString(1));
				entity.setRepeater(rs.getString(5));
				entity.setMac(rs.getString(2));
				if (rs.getInt("electrState") == 1) {
					entity.setElectrState(Constant.ON);
				} else if (rs.getInt("electrState") == 2) {
					entity.setElectrState(Constant.OFF);
				} else {
					entity.setElectrState(Constant.OFF);
				}
				entity.setRssivalue(rs.getString("rssivalue"));
				int ifAlarm = rs.getInt("ifAlarm");
				entity.setIfAlarm(ifAlarm);
				if (ifAlarm == 0) { // 报警状态
//					entity.setAlarmType(rs.getInt("alarmType"));
					entity.setAlarmName("报警");
				}
				lists.add(entity);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return lists;
	}

	// 获得水压设备的信息
	public int getAllWaterInfoCount(List<String> areaList, WaterQuery query) {
		int totalcount = 0;
		int len = areaList.size();
		String sql = "select  s.named,s.mac,s.address,s.netState,s.repeater,a.area,s.placeTypeId,s.time,s.deviceType from smoke s,areaidarea a,devices d "
				+ "where 1=1 and s.areaId = a.areaId and s.deviceType = d.id  ";
		if (query != null) {
			if (StringUtils.isNotBlank(query.getDeviceType())) {
				switch (query.getDeviceType()) {
				case "10":
				case "70":
				case "125":
					sql += " and s.deviceType " + Constant.waterInfosSql;
					break;
				case "19":
				case "69":
				case "124":
					sql += " and s.deviceType " + Constant.waterLevelSql;
					break;
				default:
					sql += " and s.deviceType = " + query.getDeviceType();
					break;
				}

			}
			if(StringUtils.isNotBlank(query.getType())){
				sql += " and d.devId = " + query.getType();
			}
			if (StringUtils.isNotBlank(query.getMac())) {
				sql += " and s.mac like '%" + query.getMac() + "%'";
			}
			if (StringUtils.isNotBlank(query.getNetState())) {
				sql += " and s.netState ='" + Integer.parseInt(query.getNetState()) + "'";
			}
			if (StringUtils.isNotBlank(query.getAreaName())) {
				sql += " and s.areaId = '" + Integer.parseInt(query.getAreaName()) + "'";
			}
			if (StringUtils.isNotBlank(query.getPlaceType())) {
				sql += " and s.placeTypeId = '" + Integer.parseInt(query.getPlaceType()) + "'";
			}
			if (len == 0) {
				return (Integer) null;
			}
			if (len == 1) {
				sql += " and s.areaId in ('" + areaList.get(0) + "') order by s.time desc";
			}
			if (len > 1) {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						sql += " and s.areaId in ('" + areaList.get(i) + "', ";
					} else if (i == (len - 1)) {
						sql += " " + areaList.get(i) + ") order by s.time desc ";
					} else {
						sql += " " + areaList.get(i) + ",";
					}
				}
			}
		}

		Connection conn = DBConnectionManager.getConnection();
		String basicSQL = " select count(*) as totalcount from ( " + sql + " ) aa";
		PreparedStatement ps = DBConnectionManager.prepare(conn, basicSQL);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				totalcount = rs.getInt("totalcount");
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

	// 温湿度设备信息
	@Override
	public List<WaterQuery> getAllThInfo(List<String> areaList, WaterQuery query) {
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		int len = areaList.size();
		String limit = " limit " + query.getStartRow() + "," + query.getPageSize();
		Map<String, String> map = mPlaceTypeDao.getShopTypeById();
		String sql = "";
		sql += "select  s.named,s.mac,s.address,s.netState,s.repeater,a.area,s.placeTypeId,s.time,s.deviceType,s.electrState,d.deviceName,s.rssivalue,s.ifAlarm ,a.isTrue "
				+ "from smoke s,areaidarea a,devices d " + "where 1=1 and d.id = s.deviceType and s.areaId = a.areaId";
		if (query != null) {
			if(StringUtils.isNotBlank(query.getType())){
				sql += " and d.devid = "+query.getType();
			}
			if (StringUtils.isNotBlank(query.getDeviceType())) {
				sql += " and s.deviceType = "+query.getDeviceType();
			}
			if (StringUtils.isNotBlank(query.getMac())) {
				sql += " and s.mac like '%" + query.getMac() + "%'";
			}
			if (StringUtils.isNotBlank(query.getNetState())) {
				sql += " and s.netState ='" + Integer.parseInt(query.getNetState()) + "'";
			}
			if (StringUtils.isNotBlank(query.getAreaName())) {
				sql += " and s.areaId = '" + Integer.parseInt(query.getAreaName()) + "'";
			}
			if (StringUtils.isNotBlank(query.getPlaceType())) {
				sql += " and s.placeTypeId = '" + Integer.parseInt(query.getPlaceType()) + "'";
			}
			if (len == 0) {
				return null;
			}
			if (len == 1) {
				sql += " and s.areaId in ('" + areaList.get(0) + "') order by s.time desc" + limit;
			}
			if (len > 1) {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						sql += " and s.areaId in ('" + areaList.get(i) + "', ";
					} else if (i == (len - 1)) {
						sql += " " + areaList.get(i) + ") order by s.time desc " + limit;
					} else {
						sql += " " + areaList.get(i) + ",";
					}
				}
			}
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<WaterQuery> lists = new ArrayList<>();

		try {
			rs = ps.executeQuery();
			int row = query.getStartRow();
			while (rs.next()) {

				WaterQuery entity = new WaterQuery();
				entity.setRow(++row);
				entity.setAddress(rs.getString(3));
				entity.setAreaName(rs.getString(6));
				entity.setAddWaterTime(rs.getString(8));
				String placeTypeId = rs.getString(7) + "";
				if (StringUtils.isNumeric(placeTypeId)) {
					entity.setPlaceType(map.get(placeTypeId));
				}
				if (rs.getInt(4) == 0) {
					entity.setNetState(Constant.NETSTATE0);
				} else {
					entity.setNetState(Constant.NETSTATE1);
				}
				entity.setDeviceType(rs.getString("deviceName"));

				entity.setName(rs.getString(1));
				entity.setRepeater(rs.getString(5));
				entity.setMac(rs.getString(2));
				entity.setIsTrue(rs.getInt("isTrue"));
				if (rs.getInt("electrState") == 1) {
					entity.setElectrState(Constant.ON);
				} else if (rs.getInt("electrState") == 2) {
					entity.setElectrState(Constant.OFF);
				} else {
					entity.setElectrState(Constant.OFF);
				}
				entity.setRssivalue(rs.getString("rssivalue"));
				int ifAlarm = rs.getInt("ifAlarm");
				entity.setIfAlarm(ifAlarm);
				if (ifAlarm == 0) { // 报警状态
					entity.setAlarmName("报警");
				}
				lists.add(entity);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return lists;
	}

	@Override
	public int getAllThInfoCount(List<String> areaList, WaterQuery query) {
		int totalcount = 0;
		int len = areaList.size();
		String sql = "select  s.named,s.mac,s.address,s.netState,s.repeater,a.area,s.placeTypeId,s.time,s.deviceType from smoke s,areaidarea a,devices d "
				+ "where 1=1 and s.areaId = a.areaId and d.id = s.deviceType  ";
		if (query != null) {
			if (StringUtils.isNotBlank(query.getType())){
				sql += " and d.devId = "+query.getType();
			}
			if (StringUtils.isNotBlank(query.getDeviceType())) {
				sql += " and s.deviceType = "+query.getDeviceType();
			}
			if (StringUtils.isNotBlank(query.getMac())) {
				sql += " and s.mac like '%" + query.getMac() + "%'";
			}
			if (StringUtils.isNotBlank(query.getNetState())) {
				sql += " and s.netState ='" + Integer.parseInt(query.getNetState()) + "'";
			}
			if (StringUtils.isNotBlank(query.getAreaName())) {
				sql += " and s.areaId = '" + Integer.parseInt(query.getAreaName()) + "'";
			}
			if (StringUtils.isNotBlank(query.getPlaceType())) {
				sql += " and s.placeTypeId = '" + Integer.parseInt(query.getPlaceType()) + "'";
			}
			if (len == 0) {
				return (Integer) null;
			}
			if (len == 1) {
				sql += " and s.areaId in ('" + areaList.get(0) + "') order by s.time desc";
			}
			if (len > 1) {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						sql += " and s.areaId in ('" + areaList.get(i) + "', ";
					} else if (i == (len - 1)) {
						sql += " " + areaList.get(i) + ") order by s.time desc ";
					} else {
						sql += " " + areaList.get(i) + ",";
					}
				}
			}
		}

		Connection conn = DBConnectionManager.getConnection();
		String basicSQL = " select count(*) as totalcount from ( " + sql + " ) aa";
		PreparedStatement ps = DBConnectionManager.prepare(conn, basicSQL);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				totalcount = rs.getInt("totalcount");
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
	public List<WaterQuery> getAllWaterRecord(WaterQuery query) {
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("SELECT waterMac,repeaterMac,status,value,time,remark from waterinfo where waterMac = ? ");
		if (query.getStartTime() != null && query.getStartTime().length() > 0) {
			sqlstr.append(" AND time >'" + query.getStartTime() + " 00:00:01'");
		}
		if (query.getEndTime() != null && query.getEndTime().length() > 0) {
			sqlstr.append(" AND time <'" + query.getEndTime() + " 23:59:59'");
		}
		if (query.getDevictTypeName() != null && query.getDevictTypeName().length() > 0) {
			if (query.getDevictTypeName().contains(",")) {
				String[] status = query.getDevictTypeName().split(",");
				sqlstr.append("and ( status = " + status[0] + " or status = " + status[1] + ") ");
			} else {
				sqlstr.append("and status = " + query.getDevictTypeName());
			}
		}
		sqlstr.append(" ORDER BY time DESC ");
		sqlstr.append(" limit " + query.getStartRow() + "," + query.getPageSize());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr.toString());
		ResultSet rs = null;
		List<WaterQuery> waterRecord = new ArrayList<WaterQuery>();
		try {
			ps.setString(1, query.getMac());
			rs = ps.executeQuery();
			int row = query.getStartRow();
			while (rs.next()) {
				WaterQuery wq = new WaterQuery();
				row++;
				wq.setRow(row);
				wq.setMac(rs.getString(1));
				wq.setRepeater(rs.getString(2));
				int status = rs.getInt(3);
				switch (status) {
				case 0:
				case 219: // 正常
					wq.setDevictTypeName("正常");
					break;
				case 2:
				case 218:// 高水压
					wq.setDevictTypeName("高水压");
					break;
				case 1:
				case 209:// D1,低水压
					wq.setDevictTypeName("低水压");
					break;
				case 220: // dc 低电压
					wq.setDevictTypeName("低电压");
					break;
				case 217: // d9升高
					wq.setDevictTypeName("水压升高");
					break;
				case 210: // d2降低
					wq.setDevictTypeName("水压降低");
					break;
				case 3: // 故障
					wq.setDevictTypeName("故障");
					break;
				case 207: // 水位过低
					wq.setDevictTypeName("水位过低");
					break;
				case 208: // 水位过高
					wq.setDevictTypeName("水位过高");
					break;
				}
				wq.setValue(rs.getString(4));
				wq.setAlarmEndTime(rs.getString(5));
				String remark = rs.getString(6);
				if (remark == null || "无".equals(remark) || "".equals(remark)) // 默认单位为无的
					remark = "KPa";
				wq.setRemark(remark);
				waterRecord.add(wq);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return waterRecord;
	}
	
	@Override
	public List<WaterQuery> getAllWaterRecordList(WaterQuery query) {
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("SELECT waterMac,repeaterMac,status,value,time,remark from waterinfo where waterMac = ? ");
		if (query.getStartTime() != null && query.getStartTime().length() > 0) {
			sqlstr.append(" AND time >'" + query.getStartTime() + " 00:00:01'");
		}
		if (query.getEndTime() != null && query.getEndTime().length() > 0) {
			sqlstr.append(" AND time <'" + query.getEndTime() + " 23:59:59'");
		}
		if (query.getDevictTypeName() != null && query.getDevictTypeName().length() > 0) {
			if (query.getDevictTypeName().contains(",")) {
				String[] status = query.getDevictTypeName().split(",");
				sqlstr.append("and ( status = " + status[0] + " or status = " + status[1] + ") ");
			} else {
				sqlstr.append("and status = " + query.getDevictTypeName());
			}
		}
		sqlstr.append(" ORDER BY time DESC ");
		sqlstr.append(" limit " + query.getStartRow() + "," + query.getPageSize());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr.toString());
		ResultSet rs = null;
		List<WaterQuery> waterRecord = new ArrayList<WaterQuery>();
		try {
			ps.setString(1, query.getMac());
			rs = ps.executeQuery();
			int row = query.getStartRow();
			while (rs.next()) {
				WaterQuery wq = new WaterQuery();
				row++;
				wq.setRow(row);
				wq.setMac(rs.getString(1));
				wq.setRepeater(rs.getString(2));
				int status = rs.getInt(3);
				switch (status) {
				case 0:
				case 219: // 正常
					wq.setDevictTypeName("正常");
					break;
				case 2:
				case 218:// 高水压
					wq.setDevictTypeName("高水位");
					break;
				case 1:
				case 209:// D1,低水压
					wq.setDevictTypeName("低水位");
					break;
				case 220: // dc 低电压
					wq.setDevictTypeName("低电量");
					break;
				case 217: // d9升高
					wq.setDevictTypeName("水位升高");
					break;
				case 210: // d2降低
					wq.setDevictTypeName("水位降低");
					break;
				case 3: // 故障
					wq.setDevictTypeName("故障");
					break;
				case 207: // 水位过低
					wq.setDevictTypeName("水位过低");
					break;
				case 208: // 水位过高
					wq.setDevictTypeName("水位过高");
					break;
				}
				wq.setValue(rs.getString(4));
				wq.setAlarmEndTime(rs.getString(5));
				String remark = rs.getString(6);
				if (remark == null || "无".equals(remark) || "".equals(remark)) // 默认单位为无的
					remark = "M";
				wq.setRemark(remark);
				waterRecord.add(wq);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return waterRecord;
	}

	@Override
	public List<WaterQuery> NBWaterDetailList(WaterQuery query) {
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("SELECT waterMac,repeaterMac,status,value,time,remark from waterinfo where waterMac = ? ");
		if (query.getStartTime() != null && query.getStartTime().length() > 0) {
			sqlstr.append(" AND time >'" + query.getStartTime() + " 00:00:01'");
		}
		if (query.getEndTime() != null && query.getEndTime().length() > 0) {
			sqlstr.append(" AND time <'" + query.getEndTime() + " 23:59:59'");
		}
		if (query.getDevictTypeName() != null && query.getDevictTypeName().length() > 0) {
			if (query.getDevictTypeName().contains(",")) {
				String[] status = query.getDevictTypeName().split(",");
				sqlstr.append("and ( status = " + status[0] + " or status = " + status[1] + ") ");
			} else {
				sqlstr.append("and status = " + query.getDevictTypeName());
			}
		}
		sqlstr.append(" ORDER BY time DESC ");
		sqlstr.append(" limit " + query.getStartRow() + "," + query.getPageSize());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr.toString());
		ResultSet rs = null;
		List<WaterQuery> waterRecord = new ArrayList<WaterQuery>();
		try {
			ps.setString(1, query.getMac());
			rs = ps.executeQuery();
			int row = query.getStartRow();
			while (rs.next()) {
				WaterQuery wq = new WaterQuery();
				row++;
				wq.setRow(row);
				wq.setMac(rs.getString(1));
				wq.setRepeater(rs.getString(2));
				wq.setAlarmEndTime(rs.getString(5));
				String remark = rs.getString(6);

				int type = Integer.valueOf(query.getDeviceType());
				if (type == 42) {// 水压
					int status = rs.getInt(3);
					switch (status) {
					case 0:
					case 219: // 正常
						wq.setDevictTypeName("正常");
						break;
					case 2:
					case 218:// 高水压
						wq.setDevictTypeName("高水压");
						break;
					case 1:
					case 209:// D1,低水压
						wq.setDevictTypeName("低水压");
						break;
					case 220: // dc 低电压
						wq.setDevictTypeName("低电压");
						break;
					}
					wq.setValue(rs.getString(4));
					if (remark == null || "无".equals(remark) || "".equals(remark)) {
					} // 默认单位为无的
					remark = "KPa";
					// wq.setRemark(remark);
				} else if (type == 46) {// 水位
					int status = rs.getInt(3);
					switch (status) {
					case 0:
					case 219: // 正常
						wq.setDevictTypeName("正常");
						break;
					case 2:
					case 218:// 高水压
						wq.setDevictTypeName("高水位");
						break;
					case 1:
					case 209:// D1,低水压
						wq.setDevictTypeName("低水位");
						break;
					case 220: // dc 低电压
						wq.setDevictTypeName("低电量");
						break;
					}
					wq.setValue(rs.getString(4));
					if (remark == null || "无".equals(remark) || "".equals(remark)) {// 默认单位为无的
						remark = "M";
					}
				}
				wq.setRemark(remark);
				waterRecord.add(wq);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return waterRecord;
	}

	@Override
	public int getAllWaterRecordCount(WaterQuery query) {
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("SELECT count(id) from waterinfo where waterMac = ?");
		if (query.getStartTime() != null && query.getStartTime().length() > 0) {
			sqlstr.append(" AND time >'" + query.getStartTime() + " 00:00:01'");
		}
		if (query.getEndTime() != null && query.getEndTime().length() > 0) {
			sqlstr.append(" AND time <'" + query.getEndTime() + " 23:59:59'");
		}
		if (query.getDevictTypeName() != null && query.getDevictTypeName().length() > 0) {
			if (query.getDevictTypeName().contains(",")) {
				String[] status = query.getDevictTypeName().split(",");
				sqlstr.append(" and ( status = " + status[0] + " or status = " + status[1] + ") ");
			} else {
				sqlstr.append(" and status = " + query.getDevictTypeName());
			}
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr.toString());
		ResultSet rs = null;
		int count = 0;
		try {
			ps.setString(1, query.getMac());
			rs = ps.executeQuery();
			while (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return count;
	}

	@Override
	public List<WaterQuery> getAllThRecord(WaterQuery query) {
		String limit = " limit " + query.getStartRow() + "," + query.getPageSize();
		String sqlstr = "SELECT s.mac,s.repeater,th.time,th.temperature,th.humidity from th_info th,smoke s where s.mac = th.mac and s.mac = ?";
		if (query.getStartTime() != null && query.getStartTime().length() > 0) {
			sqlstr += " AND th.time >'" + query.getStartTime() + " 00:00:01' ";
		}
		if (query.getEndTime() != null && query.getEndTime().length() > 0) {
			sqlstr += " AND th.time <'" + query.getEndTime() + " 23:59:59'";
		}
		sqlstr += " ORDER BY th.time DESC";
		
		sqlstr = sqlstr + limit;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		List<WaterQuery> waterRecord = new ArrayList<WaterQuery>();
		try {
			ps.setString(1, query.getMac());
			rs = ps.executeQuery();
			int row = query.getStartRow();
			while (rs.next()) {
				WaterQuery wq = new WaterQuery();
				row++;
				wq.setRow(row);
				wq.setMac(rs.getString(1));
				wq.setRepeater(rs.getString(2));
				wq.setAlarmEndTime(rs.getString(3));
				wq.setTemperature(rs.getFloat("temperature"));
				wq.setHumidity(rs.getFloat("humidity"));
				waterRecord.add(wq);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return waterRecord;
	}

	@Override
	public int getAllThRecordCount(WaterQuery query) {
		String sqlstr = "SELECT count(mac) from th_info where mac = ?";
		if (query.getStartTime() != null && query.getStartTime().length() > 0) {
			sqlstr += " AND time >'" + query.getStartTime() + " 00:00:01' ";
		}
		if (query.getEndTime() != null && query.getEndTime().length() > 0) {
			sqlstr += " AND time <'" + query.getEndTime() + " 23:59:59'";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		int count = 0;
		try {
			ps.setString(1, query.getMac());
			rs = ps.executeQuery();
			while (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return count;
	}

	@Override
	public int getWaterLeve(String waterMac, String values) {
		int result = 0;
		float f_values = Float.parseFloat(values);
		float f_low = Float.parseFloat(getWaterLow(waterMac));
		float f_high = Float.parseFloat(getWaterHigh(waterMac));
		if (IfStopAlarm.map.containsKey(waterMac)) { // ifStopAlarm.map 停止报警设备表
			if ((System.currentTimeMillis() - IfStopAlarm.map.get(waterMac)) < 60 * 60 * 1000) { // 停止报警时间超过10分钟可以重新报警
				return result;
			} else {
				IfStopAlarm.map.remove(waterMac);
			}
		} // @@2018.01.08
		if (f_low != 0 && (f_values < f_low)) {
			result = 207;
		} else if (f_high != 0 && (f_values > f_high)) {
			result = 208;
		}
		return result;
	}

	@Override
	public int getWaterGage2(String waterMac, String values) {
		int result = 0;
		float f_values = Float.parseFloat(values);
		float f_low = Float.parseFloat(getWaterLow(waterMac));
		float f_high = Float.parseFloat(getWaterHigh(waterMac));
		if (IfStopAlarm.map.containsKey(waterMac)) { // ifStopAlarm.map 停止报警设备表
			if ((System.currentTimeMillis() - IfStopAlarm.map.get(waterMac)) < 60 * 60 * 1000) { // 停止报警时间超过10分钟可以重新报警
				return result;
			} else {
				IfStopAlarm.map.remove(waterMac);
			}
		} // @@2018.01.08
		if (f_low != 0 && (f_values < f_low)) {
			result = 209;
		} else if (f_high != 0 && (f_values > f_high)) {
			result = 218;
		}
		return result;
	}

	@Override
	public int getWaterGage(String waterMac, String values) {
		int result = 0;
		float f_values = Float.parseFloat(values);
		float f_low = Float.parseFloat(getWaterLow2(waterMac));
		float f_high = Float.parseFloat(getWaterHigh2(waterMac));
		if (IfStopAlarm.map.containsKey(waterMac)) { // ifStopAlarm.map 停止报警设备表
			if ((System.currentTimeMillis() - IfStopAlarm.map.get(waterMac)) < 60 * 60 * 1000) { // 停止报警时间超过10分钟可以重新报警
				return result;
			} else {
				IfStopAlarm.map.remove(waterMac);
			}
		} // @@2018.01.08
		if (f_low != 0 && (f_values < f_low)) {
			result = 209;
		} else if (f_high != 0 && (f_values > f_high)) {
			result = 218;
		}
		return result;
	}

	// 低水位阈值
	public String getWaterLow(String waterMac) {
		String result = "0";
		String sqlstr = "SELECT alarmthreshold1 from alarmthreshold where smokeMac = ? and alarmFamily = 207";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		try {
			ps.setString(1, waterMac);
			rs = ps.executeQuery();
			while (rs.next()) {
				result = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	// 高水位阈值
	public String getWaterHigh(String waterMac) {
		String result = "0";
		String sqlstr = "SELECT alarmthreshold1 from alarmthreshold where smokeMac = ? and alarmFamily = 208";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		try {
			ps.setString(1, waterMac);
			rs = ps.executeQuery();
			while (rs.next()) {
				result = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	// 低水压阈值
	public String getWaterLow2(String waterMac) {
		String result = "0";
		String sqlstr = "SELECT alarmthreshold1 from alarmthreshold where smokeMac = ? and alarmFamily = 209";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		try {
			ps.setString(1, waterMac);
			rs = ps.executeQuery();
			while (rs.next()) {
				result = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	// 高水压阈值
	public String getWaterHigh2(String waterMac) {
		String result = "0";
		String sqlstr = "SELECT alarmthreshold1 from alarmthreshold where smokeMac = ? and alarmFamily = 218";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		try {
			ps.setString(1, waterMac);
			rs = ps.executeQuery();
			while (rs.next()) {
				result = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	// 高温度阈值
	public String getHighTemperature(String waterMac) {
		String result = "0";
		String sqlstr = "SELECT alarmthreshold1 from alarmthreshold where smokeMac = ? and alarmFamily = 308";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		try {
			ps.setString(1, waterMac);
			rs = ps.executeQuery();
			while (rs.next()) {
				result = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	// 低温度阈值
	public String getlowTemperature(String waterMac) {
		String result = "0";
		String sqlstr = "SELECT alarmthreshold1 from alarmthreshold where smokeMac = ? and alarmFamily = 307";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		try {
			ps.setString(1, waterMac);
			rs = ps.executeQuery();
			while (rs.next()) {
				result = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	// 高湿度阈值
	public String getHighHumidity(String waterMac) {
		String result = "0";
		String sqlstr = "SELECT alarmthreshold1 from alarmthreshold where smokeMac = ? and alarmFamily = 408";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		try {
			ps.setString(1, waterMac);
			rs = ps.executeQuery();
			while (rs.next()) {
				result = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	// 低湿度阈值
	public String getlowHumidity(String waterMac) {
		String result = "0";
		String sqlstr = "SELECT alarmthreshold1 from alarmthreshold where smokeMac = ? and alarmFamily = 407";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		try {
			ps.setString(1, waterMac);
			rs = ps.executeQuery();
			while (rs.next()) {
				result = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	// 水设备阈值
	public List<Float> getWaterThreshold(String waterMac) {
		List<Float> lstThreshold = null;
		String sqlstr = "SELECT alarmthreshold1 from alarmthreshold where smokeMac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		try {
			ps.setString(1, waterMac);
			rs = ps.executeQuery();
			while (rs.next()) {
				lstThreshold = new ArrayList<Float>();
				lstThreshold.add(Float.parseFloat(rs.getString(1)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return lstThreshold;
	}

	@Override
	public boolean updateWaterLeve(Water water) {
		boolean result = false;
		String sql = "insert into waterinfo(repeaterMac,status,value,time,waterMac)VALUES(?,?,?,?,?)";
		if ((19 != water.getType())&&(46 !=water.getType())) { // lora水位对所有水位数据都存储，其他水位只存相对上一次的数据不同的才存储。
			if (ifExitWaterMac(water.getWaterMac(), water.getValue())) {
				return result;
			}
		}
		// if(Float.parseFloat(water.getValue())<0.0001) return false;
		// /*water.setValue("0.00");*/
		String datetime = GetTime.ConvertTimeByLong();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, water.getRepeaterMac());
			ps.setInt(2, water.getAlarmType());
			ps.setString(3, water.getValue());
			ps.setString(4, datetime);
			ps.setString(5, water.getWaterMac());
			if (ps.executeUpdate() > 0) {
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

	// 判断是否存在
	public boolean ifExitWaterMac(String waterMac, String waterLeve) {
		boolean result = false;
		String sql = "SELECT waterMac,value from waterinfo where waterMac = ? ORDER BY id desc LIMIT 1";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, waterMac);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (Float.parseFloat(waterLeve) == Float.parseFloat(rs.getString(2))) {
					result = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	// 获取水系统的统计分析数据
	@Override
	public List<WaterBean> getWaterStatistic(String startTime, String endTime, String areaId) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int minute = Integer.parseInt(startTime.substring(14, 16));
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		try {
			start.setTime(sdf.parse(startTime));
			end.setTime(sdf.parse(endTime));
			if (minute < 15) {
				start.add(Calendar.HOUR, -1);
				String st = sdf.format(start.getTime());
				st = st.substring(0, 14) + "45:00";
				start.setTime(sdf.parse(st));
			} else if (minute < 45) {
				String st = sdf.format(start.getTime());
				st = st.substring(0, 14) + "15:00";
				start.setTime(sdf.parse(st));
			} else {
				String st = sdf.format(start.getTime());
				st = st.substring(0, 14) + "45:00";
				start.setTime(sdf.parse(st));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<WaterBean> lstWater = new ArrayList<WaterBean>();
		while (start.getTime().before(end.getTime()) && start.getTime().before(Calendar.getInstance().getTime())) {
			startTime = sdf.format(start.getTime());
			start.add(Calendar.MINUTE, 30);
			endTime = sdf.format(start.getTime());
			String sql = "select mac,named,address,deviceType,value from "
					+ "(select mac,named,address,deviceType  from smoke where deviceType in (10,15,18,19,42,124,125) ";
			if (!"0".equals(areaId))
				sql += "and areaid =" + areaId;
			sql += ") temp1 " + "left join " + "(select waterMac,value from " + "(select waterMac,value from waterinfo "
					+ "where   time >= '" + startTime + "' and time < '" + endTime + "' "
					+ "ORDER BY time desc ) temp2 " + "group by waterMac) temp3 " + "on temp1.mac = temp3.waterMac";

			ps = DBConnectionManager.prepare(conn, sql);
			try {
				rs = ps.executeQuery();
				while (rs.next()) {
					WaterBean wb = new WaterBean();
					wb.setWaterMac(rs.getString("mac"));
					wb.setNamed(rs.getString("named"));
					wb.setAddress(rs.getString("address"));
					switch (rs.getInt("deviceType")) {
					case 10:
					case 42:
					case 125:
						wb.setUnit("Kpa");
						break;
					case 19:
					case 124:
						wb.setUnit("m");
						break;
					default:
						wb.setUnit("未知");
						break;
					}
					// wb.setDeviceType(rs.getInt("deviceType"));
					if (rs.getString("value") == null || "".equals(rs.getString("value"))) // 若没有数据则设置为-1
						wb.setValue("无");
					else
						wb.setValue(rs.getDouble("value") + "");
					wb.setTime(startTime);
					lstWater.add(wb);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		DBConnectionManager.close(rs);
		DBConnectionManager.close(ps);
		DBConnectionManager.close(conn);

		return lstWater;
	}

	public List<WaterBean> getWaterStatistic2(String startTime, String endTime, String areaId) {
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<WaterBean> lstWater = new ArrayList<WaterBean>();
		String sql = "select mac,named,address,deviceType,value,w.time from smoke s,waterinfo w where s.mac = w.waterMac and s.deviceType in (10,15,18,19,42,124,125) "
				+ "and w.time >= '" + startTime + "' and w.time <= '" + endTime + "' ";
		if (StringUtils.isNotEmpty(areaId)) {
			sql += " and areaId in (" + areaId + ") ";
		}
		sql += "ORDER BY mac,w.time desc";

		ps = DBConnectionManager.prepare(conn, sql);
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				WaterBean wb = new WaterBean();
				wb.setWaterMac(rs.getString("mac"));
				wb.setNamed(rs.getString("named"));
				wb.setAddress(rs.getString("address"));
				switch (rs.getInt("deviceType")) {
				case 10:
				case 42:
				case 125:
					wb.setUnit("Kpa");
					break;
				case 19:
				case 124:
					wb.setUnit("m");
					break;
				default:
					wb.setUnit("未知");
					break;
				}
				// wb.setDeviceType(rs.getInt("deviceType"));
				if (rs.getString("value") == null || "".equals(rs.getString("value"))) // 若没有数据则设置为-1
					wb.setValue("无");
				else
					wb.setValue(rs.getDouble("value") + "");
				wb.setTime(rs.getString("time"));
				lstWater.add(wb);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return lstWater;
	}

	@Override
	public int addWaterWaveValue(String repeaterMac, String waterMac, int Hvalue, int Lvalue) {
		int result = 0;
		String sql = "";
		if (ifExitWaterMac(waterMac)) {
			sql = "UPDATE alarmthreshold set alarmthreshold1=?,alarmthreshold2=?,alarmTime=?,repeaterMac=? where smokeMac = ?";
		} else {
			sql = "INSERT into alarmthreshold(alarmthreshold1,alarmthreshold2,alarmTime,repeaterMac,smokeMac) VALUES(?,?,?,?,?)";
		}
		String updaTime = GetTime.ConvertTimeByLong();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setInt(1, Hvalue);
			ps.setInt(2, Lvalue);
			ps.setString(3, updaTime);
			ps.setString(4, repeaterMac);
			ps.setString(5, waterMac);
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	public boolean ifExitWaterMac(String waterMac) {
		boolean result = false;
		String sql = "SELECT smokeMac from alarmthreshold where smokeMac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, waterMac);
			rs = ps.executeQuery();
			while (rs.next()) {
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	public void addWaters(List<Water> lstWater) {
		String sql = "insert into waterinfo (waterMac,repeaterMac,status,value,time) value(?,?,?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			conn.setAutoCommit(false);
			for (Water water : lstWater) {
				ps.setString(1, water.getWaterMac());
				ps.setString(2, water.getRepeaterMac());
				ps.setInt(3, water.getAlarmType());
				ps.setString(4, water.getValue());
				ps.setString(5, water.getWaterTime());
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
		} catch (SQLException e) {
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

	}

	@Override
	public String getWaterValue(String time, String mac) {
		String sql = "select value from waterinfo where mac = ? and time < ? order by id desc limit 1";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		String value = "-1";
		try {
			ps.setString(1, mac);
			ps.setString(2, time);
			rs = ps.executeQuery();
			while (rs.next()) {
				value = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return value;
	}

	/**
	 * 获取固定时间间隔的离散时间点的水系统设备的值,暂定时间间隔为1小时。各时间点为整点
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@Override
	public List<Water> getWaterRecords(String startTime, String endTime, String waterMac) {
		List<Water> realRecord = new ArrayList<Water>();
		String sql = "select * from waterinfo where waterMac = ? and time > ? and time < ? order by time";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;

		try {
			ps.setString(1, waterMac);
			ps.setString(2, startTime);
			ps.setString(3, endTime);

			rs = ps.executeQuery();
			while (rs.next()) {
				if (realRecord == null) {
					realRecord = new ArrayList<Water>();
				}
				Water water = new Water();
				water.setWaterMac(rs.getString("waterMac"));
				water.setValue(rs.getString("value"));
				water.setAlarmType(rs.getInt("status"));
				water.setWaterTime(rs.getString("time"));

				realRecord.add(water);
			}

		} catch (SQLException e) {
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return realRecord;
	}

	@Override
	public List<WaterEntity> getWatersByAreaid(String areaid) {
		List<WaterEntity> waters = null;
		String sql = "select mac,named from smoke where areaid =? and deviceType " + Constant.waterInfosSql;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, areaid);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (waters == null)
					waters = new ArrayList<WaterEntity>();
				WaterEntity water = new WaterEntity();
				water.setWaterMac(rs.getString("mac"));
				water.setWaterName(rs.getString("named"));
				waters.add(water);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return waters;
	}
	
	@Override
	public List<WaterEntity> getWaterLevelsByAreaid(String areaid) {
		List<WaterEntity> waters = null;
		String sql = "select mac,named from smoke where areaid =? and deviceType " + Constant.waterLevelSql;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, areaid);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (waters == null)
					waters = new ArrayList<WaterEntity>();
				WaterEntity water = new WaterEntity();
				water.setWaterMac(rs.getString("mac"));
				water.setWaterName(rs.getString("named"));
				waters.add(water);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return waters;
	}

	@Override
	public WaterEntity getWaterEntity(String waterMac) {
		WaterEntity waterEntity = new WaterEntity();
		String sql = "select mac,named from smoke where mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, waterMac);
			rs = ps.executeQuery();
			while (rs.next()) {
				waterEntity.setWaterMac(rs.getString("mac"));
				waterEntity.setWaterName(rs.getString("named"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return waterEntity;
	}

	@Override
	public String getGage(String waterMac, int alarmFamily) {
		String result = null;
		String sqlstr = "SELECT alarmthreshold1 from alarmthreshold where smokeMac = ? and alarmFamily = "+alarmFamily;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		try {
			ps.setString(1, waterMac);
			rs = ps.executeQuery();
			while(rs.next()){
				result = rs.getString(1);
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
	public Water getWaterByMac(String waterMac) {
		String sql = "select repeater from smoke where mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		Water water = null;
		try {
			ps.setString(1, waterMac);
			rs = ps.executeQuery();
			while (rs.next()) {
				water = new Water();
				/*water.setWaterMac(waterMac);
				water.setStart_Time(rs.getString("startime"));
				water.setEnd_Time(rs.getString("endtime"));
				water.setHeartTime(rs.getString("heartime"));*/
				/*water.setInterval(rs.getInt("interval1")*60*1000); //数据库中默认为10min,操作的时候要转化为ms
				water.setAdjusted(rs.getString("adjusted"));*/
				water.setRepeaterMac(rs.getString("repeater"));
				/*water.setWavevalue(rs.getInt("waveValue"));*/
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return water;
	}

	@Override
	public int updateGage(String waterValue, String waterMac, int alarmFamily) {
		String sql = "replace into alarmthreshold(alarmthreshold1,smokeMac,alarmFamily) values("+waterValue+",'"+waterMac+"',"+alarmFamily+")";
		/*switch(alarmFamily){
		case  308://更新高温阈值 
			sql = "replace into alarmthreshold(alarmthreshold1,smokeMac,alarmFamily) values("+waterValue+",'"+waterMac+"',"+alarmFamily+")";
			break;
		case  307: //更新低温阈值
			sql = "replace into alarmthreshold(alarmthreshold2,smokeMac,alarmFamily) values("+waterValue+",'"+waterMac+"',"+alarmFamily+")";
			break;
		case  408://高湿度
			sql = "replace into alarmthreshold(alarmthreshold3,smokeMac,alarmFamily) values("+waterValue+",'"+waterMac+"',"+alarmFamily+")";
			break;
		case  407://低湿度
			sql = "replace into alarmthreshold(alarmthreshold4,smokeMac,alarmFamily) values("+waterValue+",'"+waterMac+"',"+alarmFamily+")";
			break;
		}*/
				
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		int n = 0;
		try {
			n = ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return n;
	}

	// 给设备类型为10的lora水压添加低阈值100
	// public static void main(String[] args) {
	// String sqlstr = "SELECT mac from smoke where deviceType = 10";
	// Connection conn = DBConnectionManager.getConnection();
	// PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
	// ResultSet rs = null;
	// List<String> macs = new ArrayList<String>();
	// try {
	// rs = ps.executeQuery();
	// while(rs.next()){
	// macs.add(rs.getString(1));
	// }
	//
	// sqlstr = "insert into
	// alarmthreshold(smokeMac,alarmthreshold1,alarmFamily) values (?,100,207)";
	// ps = DBConnectionManager.prepare(conn, sqlstr);
	// for (String mac : macs) {
	// ps.setString(1, mac);
	// ps.executeUpdate();
	// }
	//
	// } catch (SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }finally {
	// DBConnectionManager.close(rs);
	// DBConnectionManager.close(ps);
	// DBConnectionManager.close(conn);
	// }
	// }
}
