package com.cloudfire.dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cloudfire.dao.AllCameraDao;
import com.cloudfire.dao.PlaceTypeDao;
import com.cloudfire.dao.SmartControlDao;
import com.cloudfire.dao.WaterInfoDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.AreaAndRepeater;
import com.cloudfire.entity.CameraBean;
import com.cloudfire.entity.DealOkAlarmEntity;
import com.cloudfire.entity.DeviceCostEntity;
import com.cloudfire.entity.ShopTypeEntity;
import com.cloudfire.entity.SmartControlEntity;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.SmokeElectricInfoEntity;
import com.cloudfire.entity.StaticsAnalysEntity;
import com.cloudfire.entity.Water;
import com.cloudfire.entity.WaterEntity;
import com.cloudfire.entity.query.DeviceNetState;
import com.cloudfire.entity.query.DeviceType;
import com.cloudfire.entity.query.SearchElectricInfo;
import com.cloudfire.entity.query.SmokeQuery;
import com.cloudfire.until.Constant;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.Utils;

public class SmartControlDaoImpl implements SmartControlDao {
	private PlaceTypeDao mPlaceTypeDao;
	private AllCameraDao mAllCameraDao;

	public List<SmartControlEntity> getAllSmokeInfo(List<String> areaList,
			SmokeQuery query) {
		// TODO Auto-generated method stub
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		mAllCameraDao = new AllCameraDaoImpl();
		int len = areaList.size();
		String limit = " limit " + query.getStartRow() + ","+ query.getPageSize();
		Map<String, String> map = mPlaceTypeDao.getShopTypeById();
//		String sql = "select first.*,second.* from (";
		String sql = "";
		sql += "select  s.named,s.mac,s.address,s.netState,s.repeater,a.area,s.placeTypeId,s.time,s.deviceType,s.cameraChannel,d.deviceName,s.rssivalue,s.ifAlarm " +
				"from smoke s,areaidarea a,devices d "
				+ "where 1=1 and s.areaId = a.areaId and d.id = s.deviceType ";
//		sql += " and s.deviceType " + Constant.sql;
		if (query != null) {
			if (!StringUtils.isBlank(query.getDeviceType())) {
				sql += " and s.deviceType=" + query.getDeviceType();
			}
			if (!StringUtils.isBlank(query.getMac())) {
				sql += " and s.mac like '%" + query.getMac() + "%'";
			}
			if (!StringUtils.isBlank(query.getNetState())) {
				sql += " and s.netState ='" + query.getNetState() + "'";
			}
			if (!StringUtils.isBlank(query.getAreaName())) {
				sql += " and s.areaId = '"
						+ Integer.parseInt(query.getAreaName()) + "'";
			}
			if (!StringUtils.isBlank(query.getPlaceType())) {
				sql += " and s.placeTypeId = '"
						+ Integer.parseInt(query.getPlaceType()) + "'";
			}
			if (len == 0) {
				return null;
			}
			if (len == 1) {
				sql += " and s.areaId in ('" + areaList.get(0)
						+ "') order by s.time desc" + limit;
			}
			if (len > 1) {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						sql += " and s.areaId in ('" + areaList.get(i) + "', ";
					} else if (i == (len - 1)) {
						sql += " " + areaList.get(i)
								+ ") order by s.time desc " + limit;
					} else {
						sql += " " + areaList.get(i) + ",";
					}
				}
			}
		}
		
//		sql += ") first left join (select * from " +
//				"(select smokeMac,a.alarmType,al.alarmName from alarm a,alarmType al " +
//				"where a.alarmType = al.alarmId order by alarmTime desc) ala group by smokeMac) second " +
//				" on first.mac = second.smokeMac";
		
		Connection conn = DBConnectionManager.getConnection();
		System.out.println("数据："+sql);
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<SmartControlEntity> lists = new ArrayList<>();

		try {
			rs = ps.executeQuery();
			int row = query.getStartRow();
			while (rs.next()) {

				SmartControlEntity mSmartControlEntity = new SmartControlEntity();
				mSmartControlEntity.setRow(++row);
				mSmartControlEntity.setAddress(rs.getString(3));
				mSmartControlEntity.setCompany(rs.getString(6));
				mSmartControlEntity.setStateTime(rs.getString(8));
				mSmartControlEntity.setDeviceTypeId(rs.getInt("deviceType"));

				CameraBean cameraBean = mAllCameraDao.getCameraBySmokeMac(rs.getString(2));
				mSmartControlEntity.setCamera(cameraBean);
				if (cameraBean == null) {
					mSmartControlEntity.setCameraChannel(0);
				} else {
					mSmartControlEntity.setCameraChannel(1);
				}
				// mSmartControlEntity.setCameraChannel(rs.getInt(10));

				String placeTypeId = rs.getString(7) + "";
				if (StringUtils.isNumeric(placeTypeId)) {
					mSmartControlEntity.setPlaceTypeName(map.get(placeTypeId));
				}
				if (rs.getInt(4) == 0) {
					mSmartControlEntity.setDevState(Constant.NETSTATE0);
				} else {
					mSmartControlEntity.setDevState(Constant.NETSTATE1);
				}
				mSmartControlEntity.setDevType(rs.getString("deviceName"));
				mSmartControlEntity.setEnterprise(rs.getString(1));
				mSmartControlEntity.setRepeaterMac(rs.getString(5));
				mSmartControlEntity.setSmokeMac(rs.getString(2));
				mSmartControlEntity.setRssivalue(rs.getString("rssivalue"));
				int ifAlarm = rs.getInt("ifAlarm");
				mSmartControlEntity.setIfAlarm(ifAlarm);
				if(ifAlarm == 0){ //报警状态
//					mSmartControlEntity.setAlarmType(rs.getInt("alarmType"));
//					mSmartControlEntity.setAlarmName(rs.getString("alarmName"));
					mSmartControlEntity.setAlarmName("报警");
				}
				lists.add(mSmartControlEntity);
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
	
	
	public List<DeviceCostEntity> getAllSmokesInfo(List<String> areaList,
			SmokeQuery query) {
		// TODO Auto-generated method stub
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		mAllCameraDao = new AllCameraDaoImpl();
		int len = areaList.size();
		String limit = " limit " + query.getStartRow() + ","
				+ query.getPageSize();
		Map<String, String> map = mPlaceTypeDao.getShopTypeById();
		String sql = "select first.*,second.* from (";
		sql += "select  s.named,s.mac,s.address,s.netState,s.repeater,a.area,s.placeTypeId,s.time,s.deviceType,s.cameraChannel,d.deviceName,s.rssivalue,s.ifAlarm ,dc.cost " +
				"from smoke s,areaidarea a,devices d,deviceCost dc "
				+ " where 1=1 and s.areaId = a.areaId and d.id = s.deviceType and dc.smokeCostMac=s.mac ";
//		sql += " and s.deviceType " + Constant.sql;
		if (query != null) {
			if (!StringUtils.isBlank(query.getDeviceType())) {
				sql += " and s.deviceType=" + query.getDeviceType();
			}
			if (!StringUtils.isBlank(query.getMac())) {
				sql += " and s.mac like '%" + query.getMac() + "%'";
			}
			if (!StringUtils.isBlank(query.getNetState())) {
				sql += " and s.netState ='" + query.getNetState() + "'";
			}
			if (!StringUtils.isBlank(query.getAreaName())) {
				sql += " and s.areaId = '"
						+ Integer.parseInt(query.getAreaName()) + "'";
			}
			if (!StringUtils.isBlank(query.getPlaceType())) {
				sql += " and s.placeTypeId = '"
						+ Integer.parseInt(query.getPlaceType()) + "'";
			}
			if (len == 0) {
				return null;
			}
			if (len == 1) {
				sql += " and s.areaId in ('" + areaList.get(0)
						+ "') order by s.time desc" + limit;
			}
			if (len > 1) {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						sql += " and s.areaId in ('" + areaList.get(i) + "', ";
					} else if (i == (len - 1)) {
						sql += " " + areaList.get(i)
								+ ") order by s.time desc " + limit;
					} else {
						sql += " " + areaList.get(i) + ",";
					}
				}
			}
		}
		
		sql += ") first left join (select * from " +
				"(select smokeMac,a.alarmType,al.alarmName from alarm a,alarmType al " +
				"where a.alarmType = al.alarmId order by a.id desc) ala group by smokeMac) second " +
				" on first.mac = second.smokeMac";
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<DeviceCostEntity> lists = new ArrayList<>();

		try {
			rs = ps.executeQuery();
			int row = query.getStartRow();
			while (rs.next()) {

				DeviceCostEntity mSmartControlEntity = new DeviceCostEntity();
				mSmartControlEntity.setRow(++row);
				mSmartControlEntity.setAddress(rs.getString(3));
				mSmartControlEntity.setCompany(rs.getString(6));
				mSmartControlEntity.setStateTime(rs.getString(8));

				CameraBean cameraBean = mAllCameraDao.getCameraBySmokeMac(rs
						.getString(2));
				mSmartControlEntity.setCamera(cameraBean);
				if (cameraBean == null) {
					mSmartControlEntity.setCameraChannel(0);
				} else {
					mSmartControlEntity.setCameraChannel(1);
				}
				// mSmartControlEntity.setCameraChannel(rs.getInt(10));

				String placeTypeId = rs.getString(7) + "";
				if (StringUtils.isNumeric(placeTypeId)) {
					mSmartControlEntity.setPlaceTypeName(map.get(placeTypeId));
				}
				if (rs.getInt(4) == 0) {
					mSmartControlEntity.setDevState(Constant.NETSTATE0);
				} else {
					mSmartControlEntity.setDevState(Constant.NETSTATE1);
				}
				mSmartControlEntity.setDevType(rs.getString("deviceName"));
				mSmartControlEntity.setEnterprise(rs.getString(1));
				mSmartControlEntity.setRepeaterMac(rs.getString(5));
				mSmartControlEntity.setSmokeMac(rs.getString(2));
				mSmartControlEntity.setRssivalue(rs.getString("rssivalue"));
				int ifAlarm = rs.getInt("ifAlarm");
				mSmartControlEntity.setIfAlarm(ifAlarm);
				if(ifAlarm == 0){ //报警状态
					mSmartControlEntity.setAlarmType(rs.getInt("alarmType"));
					mSmartControlEntity.setAlarmName(rs.getString("alarmName"));
				}
				Double cost=rs.getDouble("cost");
				mSmartControlEntity.setCost(cost);
				lists.add(mSmartControlEntity);
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

	public List<SmartControlEntity> getAllDeviceInfo(List<String> areaList) {
		// TODO Auto-generated method stub
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		int len = areaList.size();
		Map<String, String> map = mPlaceTypeDao.getShopTypeById();
		String sql = "select  s.named,s.mac,s.address,s.netState,s.repeater,a.area,s.placeTypeId,s.time,s.deviceType from smoke s,areaidarea a "
				+ "where 1=1 and s.areaId = a.areaId  ";
//		sql += " and s.deviceType " + Constant.sql;
		if (len == 1) {
			sql += " and s.areaId in ('" + areaList.get(0)
					+ "') order by s.time desc";
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

		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<SmartControlEntity> lists = new ArrayList<>();

		try {
			rs = ps.executeQuery();
			while (rs.next()) {

				SmartControlEntity mSmartControlEntity = new SmartControlEntity();
				mSmartControlEntity.setAddress(rs.getString(3));
				mSmartControlEntity.setCompany(rs.getString(6));
				mSmartControlEntity.setStateTime(rs.getString(8));
				String placeTypeId = rs.getString(7) + "";
				if (StringUtils.isNumeric(placeTypeId)) {
					mSmartControlEntity.setPlaceTypeName(map.get(placeTypeId));
				}
				if (rs.getInt(4) == 0) {
					mSmartControlEntity.setDevState(Constant.NETSTATE0);
				} else {
					mSmartControlEntity.setDevState(Constant.NETSTATE1);
				}
				int typeInt = rs.getInt("deviceType");
				switch (typeInt) {
				case 1:
					mSmartControlEntity.setDevType(Constant.DEVICETYPE1);
					break;
				case 2:
					mSmartControlEntity.setDevType(Constant.DEVICETYPE2);
					break;
				case 5:
					mSmartControlEntity.setDevType(Constant.DEVICETYPE5);
					break;
				case 7:
					mSmartControlEntity.setDevType(Constant.DEVICETYPE7);
					break;
				case 8:
					mSmartControlEntity.setDevType(Constant.DEVICETYPE8);
					break;
				case 10:
					mSmartControlEntity.setDevType(Constant.DEVICETYPE10);
					break;
				case 9:
					mSmartControlEntity.setDevType(Constant.DEVICETYPE9);
					break;
				case 18:
					mSmartControlEntity.setDevType(Constant.DEVICETYPE18);
					break;
				case 16:
					mSmartControlEntity.setDevType(Constant.DEVICETYPE16);
					break;
				case 126:
					mSmartControlEntity.setDevType(Constant.DEVICETYPE126);
					break;
				}
				mSmartControlEntity.setEnterprise(rs.getString(1));
				mSmartControlEntity.setRepeaterMac(rs.getString(5));
				mSmartControlEntity.setSmokeMac(rs.getString(2));
				lists.add(mSmartControlEntity);
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

	public int getAllSmokeInfoCount(List<String> areaList, SmokeQuery query) {
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		int len = areaList.size();
		int totalcount = 0;
		//Map<String, String> map = mPlaceTypeDao.getShopTypeById();
		String sql = "select  s.named,s.mac,s.address,s.netState,s.repeater,a.area,s.placeTypeId,s.time,s.deviceType,d.deviceName from smoke s,areaidarea a,devices d  "
				+ "where 1=1 and s.areaId = a.areaId and d.id = s.deviceType ";
//		sql += " and s.deviceType " + Constant.sql;
		if (query != null) {
			if (!StringUtils.isBlank(query.getDeviceType())) {
				sql += " and s.deviceType=" + query.getDeviceType();
			}
			if (!StringUtils.isBlank(query.getMac())) {
				sql += " and s.mac like '%" + query.getMac() + "%'";
			}
			if (!StringUtils.isBlank(query.getNetState())) {
				sql += " and s.netState ='" + query.getNetState() + "'";
			}
			if (!StringUtils.isBlank(query.getAreaName())) {
				sql += " and s.areaId = '"
						+ Integer.parseInt(query.getAreaName()) + "'";
			}
			if (!StringUtils.isBlank(query.getPlaceType())) {
				sql += " and s.placeTypeId = '"
						+ Integer.parseInt(query.getPlaceType()) + "'";
			}

			if (len == 0) {
				return (Integer) null;
			}
			if (len == 1) {
				sql += " and s.areaId in ('" + areaList.get(0)
						+ "') order by s.time desc";
			}
			if (len > 1) {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						sql += " and s.areaId in ('" + areaList.get(i) + "', ";
					} else if (i == (len - 1)) {
						sql += " " + areaList.get(i)
								+ ") order by s.time desc ";
					} else {
						sql += " " + areaList.get(i) + ",";
					}
				}
			}
		}
		String mysql = "select count(*) as totalcount from ( " + sql + " ) aa";
		Connection conn = DBConnectionManager.getConnection();
		System.out.println("数量："+mysql);
		PreparedStatement ps = DBConnectionManager.prepare(conn, mysql);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				totalcount = rs.getInt("totalcount");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return totalcount;
	}
	
	
	public int getAllSmokesInfoCount(List<String> areaList, SmokeQuery query) {
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		int len = areaList.size();
		int totalcount = 0;
		//Map<String, String> map = mPlaceTypeDao.getShopTypeById();
		String sql = "select  s.named,s.mac,s.address,s.netState,s.repeater,a.area,s.placeTypeId,s.time,s.deviceType,dc.smokeCostMac from smoke s,areaidarea a ,deviceCost dc "
				+ " where 1=1 and s.areaId = a.areaId and dc.smokeCostMac=s.mac ";
//		sql += " and s.deviceType " + Constant.sql;
		if (query != null) {
			if (!StringUtils.isBlank(query.getDeviceType())) {
				sql += " and s.deviceType=" + query.getDeviceType();
			}
			if (!StringUtils.isBlank(query.getMac())) {
				sql += " and s.mac like '%" + query.getMac() + "%'";
			}
			if (!StringUtils.isBlank(query.getNetState())) {
				sql += " and s.netState ='" + query.getNetState() + "'";
			}
			if (!StringUtils.isBlank(query.getAreaName())) {
				sql += " and s.areaId = '"
						+ Integer.parseInt(query.getAreaName()) + "'";
			}
			if (!StringUtils.isBlank(query.getPlaceType())) {
				sql += " and s.placeTypeId = '"
						+ Integer.parseInt(query.getPlaceType()) + "'";
			}

			if (len == 0) {
				return (Integer) null;
			}
			if (len == 1) {
				sql += " and s.areaId in ('" + areaList.get(0)
						+ "') order by s.time desc";
			}
			if (len > 1) {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						sql += " and s.areaId in ('" + areaList.get(i) + "', ";
					} else if (i == (len - 1)) {
						sql += " " + areaList.get(i)
								+ ") order by s.time desc ";
					} else {
						sql += " " + areaList.get(i) + ",";
					}
				}
			}
		}
		String mysql = "select count(*) as totalcount from ( " + sql + " ) aa";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, mysql);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				totalcount = rs.getInt("totalcount");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return totalcount;
	}

	public List<SmartControlEntity> smokeInfo(List<String> areaList,
			SmokeQuery query) {
		// TODO Auto-generated method stub
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		mAllCameraDao = new AllCameraDaoImpl();
		int len = areaList.size();
		String limit = " limit " + query.getStartRow() + ","
				+ query.getPageSize();
		Map<String, String> map = mPlaceTypeDao.getShopTypeById();
//		String sql = "select first.*,second.* from (";
		String sql = "";
		sql += "select  s.named,s.mac,s.address,s.netState,s.repeater,a.area,s.placeTypeId,s.time ,s.cameraChannel,d.deviceName,s.rssivalue,s.ifAlarm,d.devId " +
				"from smoke s,areaidarea a,devices d"
				+ " where 1=1 and s.deviceType=d.id and s.areaId = a.areaId ";
		if (query != null) {
			if (StringUtils.isNotBlank(query.getDeviceType())) {
				sql += "and s.deviceType=" + query.getDeviceType();
			}
			if (StringUtils.isNotBlank(query.getMac())) {
				sql += " and s.mac like '%" + query.getMac() + "%'";
			}
			if (StringUtils.isNotBlank(query.getNetState())) {
				sql += " and s.netState ='" + query.getNetState() + "'";
			}
			if (StringUtils.isNotBlank(query.getAreaName())) {
				sql += " and s.areaId = '"
						+ Integer.parseInt(query.getAreaName()) + "'";
			}
			if (StringUtils.isNotBlank(query.getPlaceType())) {
				sql += " and s.placeTypeId = '"
						+ Integer.parseInt(query.getPlaceType()) + "'";
			}
			if (StringUtils.isNotBlank(query.getType())) {
				sql += " and d.devId = '"+ query.getType() + "'";
			}

			if (len == 0) {
				return null;
			}
			if (len == 1) {
				sql += " and s.areaId in ('" + areaList.get(0)
						+ "') order by s.time desc" + limit;
			}
			if (len > 1) {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						sql += " and s.areaId in ('" + areaList.get(i) + "', ";
					} else if (i == (len - 1)) {
						sql += " " + areaList.get(i)
								+ ") order by s.time desc " + limit;
					} else {
						sql += " " + areaList.get(i) + ",";
					}
				}
			}
		}

//		sql += ") first left join (select * from " +
//				"(select smokeMac,a.alarmType,al.alarmName from alarm a,alarmType al " +
//				"where a.alarmType = al.alarmId " ;
//		if (query != null) {
//			if (!StringUtils.isBlank(query.getDeviceType())) {
//				sql += "and smokeMac in  (select mac from smoke where deviceType =" + query.getDeviceType()+")";
//			}
//		}
//		sql+= " order by alarmTime desc) ala group by smokeMac) second " +
//				" on first.mac = second.smokeMac";
		
		Connection conn = DBConnectionManager.getConnection();
		System.out.println("sql:"+sql);
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<SmartControlEntity> lists = new ArrayList<>();

		try {
			rs = ps.executeQuery();
			int row = query.getStartRow();
			while (rs.next()) {

				SmartControlEntity mSmartControlEntity = new SmartControlEntity();
				mSmartControlEntity.setRow(++row);
				mSmartControlEntity.setAddress(rs.getString(3));
				mSmartControlEntity.setCompany(rs.getString(6));
				mSmartControlEntity.setStateTime(rs.getString(8));
				CameraBean cameraBean = mAllCameraDao.getCameraBySmokeMac(rs
						.getString(2));
				mSmartControlEntity.setCamera(cameraBean);
				// mSmartControlEntity.setCameraChannel(rs.getInt(9));
				if (cameraBean == null) {
					mSmartControlEntity.setCameraChannel(0);
				} else {
					mSmartControlEntity.setCameraChannel(1);
				}
				String placeTypeId = rs.getString(7) + "";
				if (StringUtils.isNumeric(placeTypeId)) {
					mSmartControlEntity.setPlaceTypeName(map.get(placeTypeId));
				}
				if (rs.getInt(4) == 0) {
					mSmartControlEntity.setDevState(Constant.NETSTATE0);
				} else {
					mSmartControlEntity.setDevState(Constant.NETSTATE1);
				}
				mSmartControlEntity.setDevType(rs.getString("deviceName"));
				mSmartControlEntity.setEnterprise(rs.getString(1));
				mSmartControlEntity.setRepeaterMac(rs.getString(5));
				mSmartControlEntity.setSmokeMac(rs.getString(2));
				mSmartControlEntity.setRssivalue(rs.getString("rssivalue"));
				int ifAlarm = rs.getInt("ifAlarm");
				mSmartControlEntity.setIfAlarm(ifAlarm);
				if(ifAlarm == 0){ //报警状态
//					mSmartControlEntity.setAlarmType(rs.getInt("alarmType"));
					mSmartControlEntity.setAlarmName("报警");
				}
				lists.add(mSmartControlEntity);
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
	
	public List<SmartControlEntity> NBsmokeInfo(List<String> areaList, SmokeQuery query) {
		// TODO Auto-generated method stub
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		mAllCameraDao = new AllCameraDaoImpl();
		int len = areaList.size();
		String limit = " limit " + query.getStartRow() + ","
				+ query.getPageSize();
		Map<String, String> map = mPlaceTypeDao.getShopTypeById();
		String sql = "";
		sql += "select  s.named,s.mac,s.address,s.netState,s.repeater,a.area,s.placeTypeId,s.time ,s.cameraChannel,d.deviceName,s.rssivalue,s.ifAlarm " +
				"from smoke s,areaidarea a,devices d"
				+ " where 1=1 and s.deviceType=d.id and s.areaId = a.areaId  ";
		if (query != null) {
			if (StringUtils.isNotBlank(query.getDeviceType())) {
				switch(query.getDeviceType()){
					case "45":
						sql += "and s.deviceType in (45)";
						break;
					default:
						sql += "and s.deviceType in (72,73)" ;
						break;
				}
				
			} 
			if(StringUtils.isNotBlank(query.getType())){
				sql += " and d.devId = "+query.getType();
			}

			if (!StringUtils.isBlank(query.getMac())) {
				sql += " and s.mac like '%" + query.getMac() + "%'";
			}
			if (!StringUtils.isBlank(query.getNetState())) {
				sql += " and s.netState ='" + query.getNetState() + "'";
			}
			if (!StringUtils.isBlank(query.getAreaName())) {
				sql += " and s.areaId = '"
						+ Integer.parseInt(query.getAreaName()) + "'";
			}
			if (!StringUtils.isBlank(query.getPlaceType())) {
				sql += " and s.placeTypeId = '"
						+ Integer.parseInt(query.getPlaceType()) + "'";
			}

			if (len == 0) {
				return null;
			}
			if (len == 1) {
				sql += " and s.areaId in ('" + areaList.get(0)
						+ "') order by s.time desc" + limit;
			}
			if (len > 1) {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						sql += " and s.areaId in ('" + areaList.get(i) + "', ";
					} else if (i == (len - 1)) {
						sql += " " + areaList.get(i)
								+ ") order by s.time desc " + limit;
					} else {
						sql += " " + areaList.get(i) + ",";
					}
				}
			}
		}

		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<SmartControlEntity> lists = new ArrayList<>();

		try {
			rs = ps.executeQuery();
			int row = query.getStartRow();
			while (rs.next()) {

				SmartControlEntity mSmartControlEntity = new SmartControlEntity();
				mSmartControlEntity.setRow(++row);
				mSmartControlEntity.setAddress(rs.getString(3));
				mSmartControlEntity.setCompany(rs.getString(6));
				mSmartControlEntity.setStateTime(rs.getString(8));
				CameraBean cameraBean = mAllCameraDao.getCameraBySmokeMac(rs
						.getString(2));
				mSmartControlEntity.setCamera(cameraBean);
				// mSmartControlEntity.setCameraChannel(rs.getInt(9));
				if (cameraBean == null) {
					mSmartControlEntity.setCameraChannel(0);
				} else {
					mSmartControlEntity.setCameraChannel(1);
				}
				String placeTypeId = rs.getString(7) + "";
				if (StringUtils.isNumeric(placeTypeId)) {
					mSmartControlEntity.setPlaceTypeName(map.get(placeTypeId));
				}
				if (rs.getInt(4) == 0) {
					mSmartControlEntity.setDevState(Constant.NETSTATE0);
				} else {
					mSmartControlEntity.setDevState(Constant.NETSTATE1);
				}
				mSmartControlEntity.setDevType(rs.getString("deviceName"));
				mSmartControlEntity.setEnterprise(rs.getString(1));
				mSmartControlEntity.setRepeaterMac(rs.getString(5));
				mSmartControlEntity.setSmokeMac(rs.getString(2));
				mSmartControlEntity.setRssivalue(rs.getString("rssivalue"));
				int ifAlarm = rs.getInt("ifAlarm");
				mSmartControlEntity.setIfAlarm(ifAlarm);
				if(ifAlarm == 0){ //报警状态
					mSmartControlEntity.setAlarmName("报警");
				}
				lists.add(mSmartControlEntity);
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

	public int getSmokeInfoCount(List<String> areaList, SmokeQuery query) {
		int count = 0;
		int len = areaList.size();
		String sql = "select s.named,s.mac,s.address,s.netState,s.repeater,a.area,s.placeTypeId,s.time from smoke s,areaidarea a,devices d "
				+ " where 1=1 and s.deviceType=d.id and s.areaId = a.areaId  ";
		if (query != null) {
			if (StringUtils.isNotBlank(query.getDeviceType())) {
				sql += " and s.deviceType=" + query.getDeviceType();
			}
			if (StringUtils.isNotBlank(query.getMac())) {
				sql += " and s.mac like '%" + query.getMac() + "%'";
			}
			if (StringUtils.isNotBlank(query.getNetState())) {
				sql += " and s.netState ='" + query.getNetState() + "'";
			}
			if (StringUtils.isNotBlank(query.getAreaName())) {
				sql += " and s.areaId = '"
						+ Integer.parseInt(query.getAreaName()) + "'";
			}
			if (StringUtils.isNotBlank(query.getPlaceType())) {
				sql += " and s.placeTypeId = '"
						+ Integer.parseInt(query.getPlaceType()) + "'";
			}
			if (StringUtils.isNotBlank(query.getType())) {
				sql += " and d.devId = '"+ query.getType() + "'";
			}
			if (len == 0) {
				return (Integer) null;
			}
			if (len == 1) {
				sql += " and s.areaId in ('" + areaList.get(0) + "') ";
			}
			if (len > 1) {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						sql += " and s.areaId in ('" + areaList.get(i) + "', ";
					} else if (i == (len - 1)) {
						sql += " " + areaList.get(i) + ") ";
					} else {
						sql += " " + areaList.get(i) + ",";
					}
				}
			}
		}
		Connection conn = DBConnectionManager.getConnection();
		String mysql = "select count(*) as totalcount from ( " + sql + " ) aa";
		PreparedStatement ps = DBConnectionManager.prepare(conn, mysql);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				count = rs.getInt("totalcount");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return count;
	}

	/** 根据type和其他条件查询 */
	public List<SearchElectricInfo> searchDeviceByTypeQuery(
			SearchElectricInfo info, List<String> areaList) {
		String limit = " limit " + info.getStartRow() + ","
				+ info.getPageSize();
		List<SearchElectricInfo> list = new ArrayList<>();
		// 需要用到区域查询
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		Map<String, String> map = mPlaceTypeDao.getShopTypeById();
		// 地点的查询
		StringBuffer strSql = new StringBuffer();
		int len = areaList.size();
		/** 主查询 */
		String sql = "select distinct s.time,s.address,s.mac,s.named,s.netState"
				+ ",s.placeTypeId,s.placeAddress,s.repeater,s.ifAlarm,a.area,s.deviceType,alarm.alarmTime from smoke s,alarm,areaidarea a "
				+ " where 1=1 and s.areaId=a.areaId ";
		if (info != null) {
			if (!StringUtils.isBlank(info.getType())) {
				sql += " and s.deviceType= '"
						+ Integer.parseInt(info.getType()) + "'";
			}
			if (!StringUtils.isBlank(info.getMac())) {
				sql += " and s.mac = '" + info.getMac() + "'";
			}
			if (!StringUtils.isBlank(info.getNetState())) {
				sql += " and s.netState ='" + info.getNetState() + "'";
			}
			if (!StringUtils.isBlank(info.getAreaName())) {
				sql += " and s.areaId = '"
						+ Integer.parseInt(info.getAreaName()) + "'";
			}
			if (!StringUtils.isBlank(info.getPlaceType())) {
				sql += " and s.placeTypeId = '"
						+ Integer.parseInt(info.getPlaceType()) + "'";
			}
			if (!StringUtils.isBlank(info.getAlarmBeginTime())) {
				sql += " and alarm.alarmTime >= '" + info.getAlarmBeginTime()
						+ "'";
			}
			if (!StringUtils.isBlank(info.getAlarmEndTime())) {
				sql += " and alarm.alarmTime <= '" + info.getAlarmEndTime()
						+ "'";
			}
			if (len == 0) {
				return null;
			}
			if (len == 1) {
				sql += " and s.areaId in ('" + areaList.get(0)
						+ "') order by s.time desc " + limit;
			}
			if (len > 1) {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						sql += " and s.areaId in ('" + areaList.get(i) + "', ";
					} else if (i == (len - 1)) {
						sql += " " + areaList.get(i)
								+ ") order by s.time desc " + limit;
					} else {
						sql += " " + areaList.get(i) + ",";
					}
				}
			}

		}
		Connection conn = DBConnectionManager.getConnection();
		String allSql = new String(sql + strSql.toString());
		PreparedStatement ppst = DBConnectionManager.prepare(conn, allSql);
		ResultSet rs = null;

		try {
			rs = ppst.executeQuery();
			while (rs.next()) {
				SearchElectricInfo deviceInfo = new SearchElectricInfo();
				deviceInfo.setAddSmokeTime(rs.getString(12));
				deviceInfo.setAddress(rs.getString(2));
				deviceInfo.setMac(rs.getString(3));
				deviceInfo.setName(rs.getString(4));
				String netState = rs.getString(5);
				deviceInfo
						.setNetState(Integer.parseInt(netState) == 0 ? Constant.NETSTATE0
								: Constant.NETSTATE1);
				String placeType = rs.getString(6) + "";
				if (StringUtils.isNumeric(placeType)) {
					deviceInfo.setPlaceType(map.get(placeType));
				}

				deviceInfo.setDeviceType(Integer.parseInt(info.getType()));
				deviceInfo.setPlaceeAddress(rs.getString(7));
				deviceInfo.setRepeater(rs.getString(8));
				String ifDealAlarm = rs.getString(9);
				deviceInfo.setIfDealAlarm(ifDealAlarm.equals("1") ? "已处理"
						: "未处理");
				deviceInfo.setAreaName(rs.getString(10));
				int deviceType = rs.getInt(11);
				switch (deviceType) {
				case 1:
					deviceInfo.setDevictTypeName(Constant.DEVICETYPE1);
					break;
				case 2:
					deviceInfo.setDevictTypeName(Constant.DEVICETYPE2);
					break;
				case 5:
					deviceInfo.setDevictTypeName(Constant.DEVICETYPE5);
					break;
				case 7:
					deviceInfo.setDevictTypeName(Constant.DEVICETYPE7);
					break;
				case 8:
					deviceInfo.setDevictTypeName(Constant.DEVICETYPE8);
					break;
				case 9:
					deviceInfo.setDevictTypeName(Constant.DEVICETYPE9);
					break;
				case 10:
					deviceInfo.setDevictTypeName(Constant.DEVICETYPE10);
					break;
				case 11:
					deviceInfo.setDevictTypeName(Constant.DEVICETYPE11);
					break;
				case 12:
					deviceInfo.setDevictTypeName(Constant.DEVICETYPE12);
					break;
				case 13:
					deviceInfo.setDevictTypeName(Constant.DEVICETYPE13);
					break;
				case 16:
					deviceInfo.setDevictTypeName(Constant.DEVICETYPE16);
					break;
				case 18:
					deviceInfo.setDevictTypeName(Constant.DEVICETYPE18);
					break;	
				case 126:
					deviceInfo.setDevictTypeName(Constant.DEVICETYPE126);
					break;
				}
				

				list.add(deviceInfo);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	/** 根据type和其他条件查询 */
	public int searchCountDeviceByTypeQuery(SearchElectricInfo info,
			List<String> areaList) {
		// 地点的查询
		StringBuffer strSql = new StringBuffer();
		int len = areaList.size();
		/** 主查询 */
		String sql = "select count(*) as totalcount "
				+ " from smoke s,alarm,areaidarea a "
				+ " where 1=1 and s.areaId=a.areaId ";
		if (info != null) {
			if (!StringUtils.isBlank(info.getType())) {
				sql += " and s.deviceType= '"
						+ Integer.parseInt(info.getType()) + "'";
			}
			if (!StringUtils.isBlank(info.getMac())) {
				sql += " and s.mac = '" + info.getMac() + "'";
			}
			if (!StringUtils.isBlank(info.getNetState())) {
				sql += " and s.netState ='"
						+ Integer.parseInt(info.getNetState()) + "'";
			}
			if (!StringUtils.isBlank(info.getAreaName())) {
				sql += " and s.areaId = '"
						+ Integer.parseInt(info.getAreaName()) + "'";
			}
			if (!StringUtils.isBlank(info.getPlaceType())) {
				sql += " and s.placeTypeId = '"
						+ Integer.parseInt(info.getPlaceType()) + "'";
			}
			if (!StringUtils.isBlank(info.getAlarmBeginTime())) {
				sql += " and alarm.alarmTime >= '" + info.getAlarmBeginTime()
						+ "'";
			}
			if (!StringUtils.isBlank(info.getAlarmEndTime())) {
				sql += " and alarm.alarmTime <= '" + info.getAlarmEndTime()
						+ "'";
			}
			if (len == 0) {
				return (Integer) null;
			}
			if (len == 1) {
				sql += " and s.areaId in ('" + areaList.get(0) + "') ";
			}
			if (len > 1) {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						sql += " and s.areaId in ('" + areaList.get(i) + "', ";
					} else if (i == (len - 1)) {
						sql += " " + areaList.get(i) + ") ";
					} else {
						sql += " " + areaList.get(i) + ",";
					}
				}
			}

		}
		int totalcount = 0;
		Connection conn = DBConnectionManager.getConnection();
		String allSql = new String(sql + strSql.toString());
		PreparedStatement ppst = DBConnectionManager.prepare(conn, allSql);
		ResultSet rs = null;
		try {
			rs = ppst.executeQuery();
			while (rs.next()) {
				totalcount = rs.getInt("totalcount");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}

		return totalcount;
	}

	/** 获得placeType的类型 */
	public List<ShopTypeEntity> getPlaceType(String type, List<String> areaList) {
		List<ShopTypeEntity> list = new ArrayList<>();
		/** 区域的查询 */
		/** 区域的查询 */
		StringBuffer strSql = new StringBuffer();
		int len = areaList.size();
		if (len == 0) {
			return null;
		}
		if (len == 1) {
			strSql.append(" and s.areaId in (?) order by s.placeTypeId asc");
		} else {
			for (int i = 0; i < len; i++) {
				if (i == 0) {
					strSql.append(" and s.areaId in (?, ");
				} else if (i == (len - 1)) {
					strSql.append(" ?) order by s.placeTypeId asc ");
				} else {
					strSql.append(" ?, ");
				}
			}
		}

		String basicSQL = "select DISTINCT s.placeTypeId,p.placeName from smoke s,placetypeidplacename p,areaidarea a WHERE 1=1 and s.deviceType=? and s.areaId=a.areaId and s.placeTypeId = p.placeTypeId";
		int deviceType = Integer.parseInt(type);
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, basicSQL
				+ strSql.toString());
		ResultSet rs = null;
		try {
			ppst.setInt(1, deviceType);
			for (int i = 1; i <= len; i++) {
				ppst.setInt(i + 1, Integer.parseInt(areaList.get(i - 1)));
			}
			rs = ppst.executeQuery();
			while (rs.next()) {
				ShopTypeEntity entity = new ShopTypeEntity();
				entity.setPlaceTypeId(rs.getString(1));
				entity.setPlaceTypeName(rs.getString(2));
				list.add(entity);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return list;
	}
	
	/** 获得placeType的类型 */
	public List<ShopTypeEntity> getPlaceType(String type, String devType, List<String> aids) {
		if(aids == null || aids.size() == 0){
			return null;
		}
		List<ShopTypeEntity> list = new ArrayList<>();
		/** 区域的查询 */
		StringBuffer sql = new StringBuffer();
		sql.append("select DISTINCT s.placeTypeId,p.placeName from smoke s,placetypeidplacename p,areaidarea a,devices d");
		sql.append(" WHERE 1=1 and d.id = s.deviceType and s.areaId=a.areaId and s.placeTypeId = p.placeTypeId ");
		if(StringUtils.isNotBlank(devType)){
			sql.append(" and d.devId = " + devType);
		}
		if(StringUtils.isNotBlank(type)){
			sql.append(" and d.id = " + type);
		}
		
		sql.append(" and s.areaId in (");
		
		for(String aid : aids){
			sql.append(aid+",");
		}
		sql.append("-1) order by s.placeTypeId asc");

		
		Connection conn = DBConnectionManager.getConnection();
		System.out.println("sql:"+sql.toString());
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sql.toString());
		ResultSet rs = null;
		try {
			rs = ppst.executeQuery();
			while (rs.next()) {
				ShopTypeEntity entity = new ShopTypeEntity();
				entity.setPlaceTypeId(rs.getString(1));
				entity.setPlaceTypeName(rs.getString(2));
				list.add(entity);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return list;
	}
	
	public List<ShopTypeEntity> getPlaceType() {
		List<ShopTypeEntity> list = new ArrayList<>();
		String basicSQL = "select  placeTypeId,placeName from placetypeidplacename order by placeName";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, basicSQL);
		ResultSet rs = null;
		try {
			rs = ppst.executeQuery();
			while (rs.next()) {
				ShopTypeEntity entity = new ShopTypeEntity();
				entity.setPlaceTypeId(rs.getString(1));
				entity.setPlaceTypeName(rs.getString(2));
				list.add(entity);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return list;
	}

	/** 获得placeType的类型 */
	public List<ShopTypeEntity> getPlaceTypeNotType(List<String> areaList) {
		List<ShopTypeEntity> list = new ArrayList<>();
		/** 区域的查询 */
		StringBuffer strSql = new StringBuffer();
		int len = areaList.size();
		if (len == 0) {
			return null;
		}
		if (len == 1) {
			strSql.append(" and s.areaId in (?)");
		} else {
			for (int i = 0; i < len; i++) {
				if (i == 0) {
					strSql.append(" and s.areaId in (?, ");
				} else if (i == (len - 1)) {
					strSql.append(" ?) ");
				} else {
					strSql.append(" ?, ");
				}
			}
		}

		String basicSQL = "select DISTINCT s.placeTypeId,p.placeName from smoke s,placetypeidplacename p,areaidarea a WHERE 1=1" +
			" and s.areaId=a.areaId and s.placeTypeId = p.placeTypeId";
		Connection conn = DBConnectionManager.getConnection();
		basicSQL = basicSQL+strSql.toString() + " order by p.placeName";
		PreparedStatement ppst = DBConnectionManager.prepare(conn, basicSQL);
		ResultSet rs = null;
		try {
			for (int i = 1; i <= len; i++) {
				ppst.setInt(i, Integer.parseInt(areaList.get(i - 1)));
			}
			rs = ppst.executeQuery();
			while (rs.next()) {
				ShopTypeEntity entity = new ShopTypeEntity();
				entity.setPlaceTypeId(rs.getString(1));
				entity.setPlaceTypeName(rs.getString(2));
				list.add(entity);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return list;
	}
	
	/** 获得区域 */
	public List<AreaAndRepeater> getAreaAndRepeater(List<String> aids) {
		if(aids == null || aids.size() == 0){
			return null;
		}
		List<AreaAndRepeater> list = new ArrayList<>();
		StringBuffer sql=new StringBuffer();
		sql.append( "select DISTINCT a.areaId,a.area from areaidarea a ");
		sql.append(" where a.areaId in (");
		for(String aid : aids){
			sql.append(aid + ",");
		}
		sql.append("-1) order by  a.area");
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sql.toString());
		ResultSet rs = null;
		try {
			rs = ppst.executeQuery();
			while (rs.next()) {
				AreaAndRepeater areaAndRepeater = new AreaAndRepeater();
				areaAndRepeater.setAreaId(rs.getInt(1));
				areaAndRepeater.setAreaName(rs.getString(2));
				list.add(areaAndRepeater);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return list;
	}

	/** 获得区域 */
	public List<AreaAndRepeater> getAreaAndRepeater(String type,
			List<String> areaList) {
		List<AreaAndRepeater> list = new ArrayList<>();
//		String basicSQL = "select DISTINCT s.areaId,a.area from smoke s,areaidarea a where s.deviceType=? and s.areaId =? and s.areaId=a.areaId ";
		int deviceType = Integer.parseInt(type);
		StringBuffer sql=new StringBuffer();
		sql.append( "select DISTINCT s.areaId,a.area from smoke s,areaidarea a where s.deviceType=? ");
		if(areaList.size()!=0){
			sql.append(" and s.areaId in (");
			for(int i=0;i<areaList.size();i++){
				if(i==areaList.size()-1){
					sql.append(Integer.parseInt(areaList.get(i)));
				}else if(i !=areaList.size()-1){
					sql.append(Integer.parseInt(areaList.get(i))+",");
				}
			}
			sql.append(")");
		}
		sql.append(" and s.areaId=a.areaId order by  a.area");
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sql.toString());
		ResultSet rs = null;
		try {
			ppst.setInt(1, deviceType);
//			for (String areId : areaList) {
//				int areIdInt = Integer.parseInt(areId);
//				ppst.setInt(2, areIdInt);
			rs = ppst.executeQuery();
			while (rs.next()) {
				AreaAndRepeater areaAndRepeater = new AreaAndRepeater();
				areaAndRepeater
						.setAreaId(Integer.parseInt(rs.getString(1)));
				areaAndRepeater.setAreaName(rs.getString(2));
				list.add(areaAndRepeater);
			}
//			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return list;
	}
	
	/** 获得区域 */
	public List<AreaAndRepeater> getAreaAndRepeater(String type,String devType,List<String> areaList) {
		List<AreaAndRepeater> list = new ArrayList<>();
		int deviceType = Integer.parseInt(type);
		StringBuffer sql=new StringBuffer();
		sql.append( "select DISTINCT s.areaId,a.area from smoke s,areaidarea a where s.deviceType=? ");
		if(areaList.size()!=0){
			sql.append(" and s.areaId in (");
			for(int i=0;i<areaList.size();i++){
				if(i==areaList.size()-1){
					sql.append(Integer.parseInt(areaList.get(i)));
				}else if(i !=areaList.size()-1){
					sql.append(Integer.parseInt(areaList.get(i))+",");
				}
			}
			sql.append(")");
		}
		sql.append(" and s.areaId=a.areaId order by  a.area");
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sql.toString());
		ResultSet rs = null;
		try {
			rs = ppst.executeQuery();
			while (rs.next()) {
				AreaAndRepeater areaAndRepeater = new AreaAndRepeater();
				areaAndRepeater
						.setAreaId(Integer.parseInt(rs.getString(1)));
				areaAndRepeater.setAreaName(rs.getString(2));
				list.add(areaAndRepeater);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return list;
	}

	
	//edit by yfs @2017/11/15 让地区根据地区名按字典排序
	/** 获得区域，不带设备类型 */  
	public List<AreaAndRepeater> getAreaAndRepeaterNoType(List<String> areaList) {
		if (areaList == null) {
			return null;
		} else {
			List<AreaAndRepeater> list = new ArrayList<>();
			StringBuffer sql=new StringBuffer();
			sql.append( "select DISTINCT s.areaId,a.area from smoke s,areaidarea a where 1=1");
			if(areaList.size()!=0){
				sql.append(" and s.areaId in (");
				for(int i=0;i<areaList.size();i++){
					if(i==areaList.size()-1){
						sql.append(Integer.parseInt(areaList.get(i)));
					}else if(i !=areaList.size()-1){
						sql.append(Integer.parseInt(areaList.get(i))+",");
					}
				}
				sql.append(")");
			}
			
			
			sql.append(" and s.areaId=a.areaId order by  a.area");
			Connection conn = DBConnectionManager.getConnection();
			PreparedStatement ppst = DBConnectionManager
					.prepare(conn, sql.toString());
			ResultSet rs = null;
			try {
					rs = ppst.executeQuery();
					while (rs.next()) {
						AreaAndRepeater areaAndRepeater = new AreaAndRepeater();
						areaAndRepeater.setAreaId(Integer.parseInt(rs
								.getString(1)));
						areaAndRepeater.setAreaName(rs.getString(2));
						list.add(areaAndRepeater);
					}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				DBConnectionManager.close(rs);
				DBConnectionManager.close(ppst);
				DBConnectionManager.close(conn);
			}
			return list;
		}

	}

	/** 获得区域，不带设备类型 */
	public List<AreaAndRepeater> getAreaAndRepeaterNoType() {
		List<AreaAndRepeater> list = new ArrayList<>();
		String basicSQL = "select DISTINCT s.areaId,a.area from smoke s,areaidarea a where  s.areaId=a.areaId";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, basicSQL);
		ResultSet rs = null;
		try {

			rs = ppst.executeQuery();
			while (rs.next()) {
				AreaAndRepeater areaAndRepeater = new AreaAndRepeater();
				areaAndRepeater.setAreaId(Integer.parseInt(rs.getString(1)));
				areaAndRepeater.setAreaName(rs.getString(2));
				list.add(areaAndRepeater);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return list;
	}

	@Override
	public List<AreaAndRepeater> getAreaByUser(String userId, String privilege) {
		String loginSql = null;
		int priv = Integer.parseInt(privilege);
		switch (priv) {
		case 4:
			loginSql = "select areaId, area from areaidarea group by areaId";
			break;
		case 3:
		case 2:
		case 5:
		case 6:
		case 7:
		case 51:
		case 61:
			loginSql = "select aa.areaId, aa.area from useridareaid ua, areaidarea aa where ua.areaId = aa.areaId and  ua.userId = ? group by aa.areaId";
			break;
		case 1:
			break;
		case 10:
			loginSql = "select areaId, area from areaidarea limit 1 ";
			break;
		default:
			loginSql = "select aa.areaId, aa.area from useridareaid ua, areaidarea aa where ua.areaId = aa.areaId and  ua.userId = ? group by aa.areaId";
			break;
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, loginSql);
		ResultSet rs = null;
		List<AreaAndRepeater> list = new ArrayList<>();
		try {
			if ((priv != 4)&&(priv !=10)) {
				ps.setString(1, userId);
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				AreaAndRepeater area = new AreaAndRepeater();
				area.setAreaId(Integer.parseInt(rs.getString(1)));
				area.setAreaName(rs.getString(2));
				list.add(area)  ;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return list;
	}
	
	/** 获得已处理的报警的设备类型 */
	public List<DealOkAlarmEntity> getDeviceTypeAndName(List<String> areaId) {
		if (areaId == null) {
			return null;
		} else {
			List<DealOkAlarmEntity> list = new ArrayList<>();

			String baseSQL = "select deviceType,deviceName from ( select DISTINCT deviceType  from alarm ,smoke where alarm.smokeMac = smoke.mac and alarm.ifDealAlarm=1  ";
//			baseSQL += " and smoke.deviceType " + Constant.sql;
			StringBuffer strSql = new StringBuffer();
			int len = areaId.size();
			if (len == 0) {
				return null;
			}
			if (len == 1) {
				strSql.append(" and smoke.areaId in (?)  ");
			} else {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						strSql.append(" and smoke.areaId in (?, ");
					} else if (i == (len - 1)) {
						strSql.append(" ?)");
					} else {
						strSql.append(" ?, ");
					}
				}
			}
			
			strSql.append(") dt  left join devices  on deviceType = id order by deviceName");
			
			String allSQL = new String(baseSQL + strSql);
			Connection conn = DBConnectionManager.getConnection();
			PreparedStatement ppst = DBConnectionManager.prepare(conn,
					allSQL);
			ResultSet rs = null;
			try {
				for (int i = 1; i <= len; i++) {
					ppst.setInt(i, Integer.parseInt(areaId.get(i - 1)));
				}
				rs = ppst.executeQuery();
				while (rs.next()) {
					DealOkAlarmEntity entity = new DealOkAlarmEntity();
					int deviceType = rs.getInt(1);
					String devName=rs.getString(2);
					entity.setDeviceType(deviceType+"");
					entity.setDevName(devName);
					if(entity.getDeviceType()!=null){
					list.add(entity);
				}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {

				DBConnectionManager.close(rs);
				DBConnectionManager.close(ppst);
				DBConnectionManager.close(conn);
			}
			return list;
		}
		
	}

	/** 获得所有的设备类型 */
	public List<DeviceType> getAllDeviceTypeAndName(List<String> areaId) {
		List<DeviceType> list = new ArrayList<>();
		StringBuffer sb = new StringBuffer();
		sb.append("select  deviceType,deviceName from smoke s,devices d where 1=1  ");
		sb.append(" and s.areaId in ("+Utils.list2String(areaId)+")");
		sb.append(" and s.deviceType = d.id ");
		sb.append(" GROUP BY deviceName ");

		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sb.toString());
		ResultSet rs = null;
		try {
			rs = ppst.executeQuery();
			while (rs.next()) {
				DeviceType entity = new DeviceType();
				entity.setDevName(rs.getString(2));
				entity.setDeviceType(rs.getInt(1)+"");
				list.add(entity);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return list;
	}

	// by liao zhenwei
	public List<DeviceType> getDeviceTypeSummary(List<String> areaId) {
		List<DeviceType> list = new ArrayList<>();

		String baseSQL = "select DISTINCT deviceType from smoke where  ";
		StringBuffer strSql = new StringBuffer();
		int len = areaId.size();

		if (len == 0) {
			return null;
		}
		if (len == 1) {
			strSql.append("  smoke.areaId in (?) ORDER BY smoke.deviceType ");
		} else {
			for (int i = 0; i < len; i++) {
				if (i == 0) {
					strSql.append("  smoke.areaId in (?, ");
				} else if (i == (len - 1)) {
					strSql.append(" ?) ORDER BY smoke.deviceType");
				} else {
					strSql.append(" ?, ");
				}
			}
		}

		String allSQL = new String(baseSQL + strSql);
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, allSQL);
		ResultSet rs = null;
		try {
			for (int i = 1; i <= len; i++) {
				ppst.setInt(i, Integer.parseInt(areaId.get(i - 1)));
			}
			rs = ppst.executeQuery();
			while (rs.next()) {
				DeviceType entity = new DeviceType();
				int deviceType = rs.getInt(1);
				switch (deviceType) {
				case 1:
					entity.setDevName(Constant.DEVICETYPE1);
					entity.setDeviceType("1");
					break;
				case 2:
					entity.setDevName(Constant.DEVICETYPE2);
					entity.setDeviceType("2");
					break;

				case 5:
					entity.setDevName(Constant.DEVICETYPE5);
					entity.setDeviceType("5");
					break;
				case 7:
					entity.setDevName(Constant.DEVICETYPE7);
					entity.setDeviceType("7");
					break;

				case 8:
					entity.setDevName(Constant.DEVICETYPE8);
					entity.setDeviceType("8");
					break;
				case 9:
					entity.setDevName(Constant.DEVICETYPE9);
					entity.setDeviceType("9");
					break;
				case 10:
					entity.setDevName(Constant.DEVICETYPE10);
					entity.setDeviceType("10");
					break;
				case 11:
					entity.setDevName(Constant.DEVICETYPE11);
					entity.setDeviceType("11");
					break;
				case 12:
					entity.setDevName(Constant.DEVICETYPE12);
					entity.setDeviceType("12");
					break;
				case 13:
					entity.setDevName(Constant.DEVICETYPE13);
					entity.setDeviceType("13");
					break;
				case 16:
					entity.setDevName(Constant.DEVICETYPE16);
					entity.setDeviceType("16");
					break;
				case 18:
					entity.setDevName(Constant.DEVICETYPE18);
					entity.setDeviceType("18");
					break;
				case 126:
					entity.setDevName(Constant.DEVICETYPE126);
					entity.setDeviceType("126");
					break;
				default:
					entity.setDevName("其他类型设备");
					entity.setDeviceType(String.valueOf(deviceType));
					break;
				}
				list.add(entity);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return list;
	}



	/** 获得区域 */
	public List<AreaAndRepeater> getAreaAndRepeaterAndSmokeNumber(
			List<String> areaList, String type) {
		List<AreaAndRepeater> list = new ArrayList<>();
		String basicSQL = "select DISTINCT s.areaId,a.area,count(mac) from smoke s,areaidarea a where s.deviceType=? and s.areaId =? and s.areaId=a.areaId GROUP BY s.areaId";
		int deviceType = Integer.parseInt(type);

		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, basicSQL);
		ResultSet rs = null;
		try {
			ppst.setInt(1, deviceType);
			for (String areId : areaList) {
				int lossSmoke = getLossSmoke(areId, type);
				int onLineSmoke = getOnlineSmoke(areId, type);
				int areIdInt = Integer.parseInt(areId);
				ppst.setInt(2, areIdInt);
				rs = ppst.executeQuery();
				while (rs.next()) {
					AreaAndRepeater areaAndRepeater = new AreaAndRepeater();
					areaAndRepeater
							.setAreaId(Integer.parseInt(rs.getString(1)));
					areaAndRepeater.setAreaName(rs.getString(2));
					areaAndRepeater.setSmokeNumber(rs.getInt(3));
					areaAndRepeater.setLossSmokeNumber(lossSmoke);
					areaAndRepeater.setOnLineSmokeNumber(onLineSmoke);
					list.add(areaAndRepeater);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return list;
	}

	public List<AreaAndRepeater> getAreaAndRepeaterAndSmoke(
			List<String> areaList) {
		List<AreaAndRepeater> list = new ArrayList<>();
		String basicSQL = "select DISTINCT s.areaId,a.area,count(mac) from smoke s,areaidarea a where  s.areaId =? and s.areaId=a.areaId ";
//		basicSQL += " and s.deviceType " + Constant.sql;
		basicSQL += " GROUP BY s.areaId ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, basicSQL);
		ResultSet rs = null;
		try {
			for (String areId : areaList) {
				int lossSmoke = getLossSmoke(areId);
				int onLineSmoke = getOnlineSmoke(areId);
				int areIdInt = Integer.parseInt(areId);
				ppst.setInt(1, areIdInt);
				rs = ppst.executeQuery();
				while (rs.next()) {
					AreaAndRepeater areaAndRepeater = new AreaAndRepeater();
					areaAndRepeater
							.setAreaId(Integer.parseInt(rs.getString(1)));
					areaAndRepeater.setAreaName(rs.getString(2));
					areaAndRepeater.setSmokeNumber(rs.getInt(3));
					areaAndRepeater.setLossSmokeNumber(lossSmoke);
					areaAndRepeater.setOnLineSmokeNumber(onLineSmoke);
					list.add(areaAndRepeater);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return list;
	}
	
	public List<AreaAndRepeater> getAreaAndSmokeInfo(List<String> areaList) {
		List<AreaAndRepeater> list = new ArrayList<>();
		StringBuffer sql = new StringBuffer();
		sql.append("select DISTINCT s.areaId,a.area,s.netState,count(mac) as number from smoke s,areaidarea a, devices d where s.areaId=a.areaId and d.id = s.deviceType ");
		if(areaList == null){
			return null;
		}else{
			sql.append(" and a.areaId in(0");
			for(String areaid :areaList){
				sql.append(","+areaid);
			}
			sql.append(")");
		}
				
		sql.append(" GROUP BY s.areaId,s.netState");
		int onLineSmokeNumbers = 0;
		int lossSmokeNumbers = 0;
		int lossSmoke = 0;
		int onLineSmoke = 0;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sql.toString());
		ResultSet rs = null;
		try {
			int areaid = 0;
			rs = ppst.executeQuery();
			AreaAndRepeater areaAndRepeater = null;
			while (rs.next()) {
				if(areaid != (rs.getInt("areaId"))){
					if(areaAndRepeater!=null){
						list.add(areaAndRepeater);
					}
					areaAndRepeater = new AreaAndRepeater();
					lossSmoke = 0;
					onLineSmoke = 0;
				}
				if(rs.getInt("netState")==1){
					onLineSmoke = rs.getInt("number");
					onLineSmokeNumbers = onLineSmokeNumbers + onLineSmoke;
				}else {
					lossSmoke = rs.getInt("number");
					lossSmokeNumbers = lossSmokeNumbers + lossSmoke;
				}
				areaid = rs.getInt("areaId");
				areaAndRepeater.setAreaId(areaid);
				areaAndRepeater.setAreaName(rs.getString(2));
				areaAndRepeater.setSmokeNumber(lossSmoke+onLineSmoke);
				areaAndRepeater.setLossSmokeNumber(lossSmoke);
				areaAndRepeater.setOnLineSmokeNumber(onLineSmoke);
				if(rs.isLast()){
					list.add(areaAndRepeater);
				}
			}
			areaAndRepeater = new AreaAndRepeater();
			areaAndRepeater.setLossSmokeNumbers(lossSmokeNumbers);
			areaAndRepeater.setOnLineSmokeNumbers(onLineSmokeNumbers);
			areaAndRepeater.setSmokeNumbers(lossSmokeNumbers+onLineSmokeNumbers);
			list.add(areaAndRepeater);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return list;
	}

	/**
	 * @param areId
	 * @param type
	 * @return
	 */
	private int getLossSmoke(String areaId, String type) {
		String basicSQL = "select count(*) as total from smoke s where s.netState=0 and s.deviceType=? and s.areaId =? ";
//		basicSQL += " and s.deviceType " + Constant.sql;
		int deviceType = Integer.parseInt(type);
		int lossSmoke = 0;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, basicSQL);
		ResultSet rs = null;
		try {
			ppst.setInt(1, deviceType);

			int areIdInt = Integer.parseInt(areaId);
			ppst.setInt(2, areIdInt);
			rs = ppst.executeQuery();
			while (rs.next()) {
				lossSmoke = rs.getInt("total");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}

		return lossSmoke;
	}

	private int getLossSmoke(String areaId) {
		String basicSQL = "select count(*) as total from smoke s where s.netState=0 and s.areaId =? ";
//		basicSQL += " and s.deviceType " + Constant.sql;
		int lossSmoke = 0;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, basicSQL);
		ResultSet rs = null;
		try {
			int areIdInt = Integer.parseInt(areaId);
			ppst.setInt(1, areIdInt);
			rs = ppst.executeQuery();
			while (rs.next()) {
				lossSmoke = rs.getInt("total");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}

		return lossSmoke;
	}

	/**
	 * @param areaList
	 * @param type
	 * @return
	 */
	private int getOnlineSmoke(String areaId, String type) {

		String basicSQL = "select count(*) as total from smoke s where s.netState=1 and s.deviceType=? and s.areaId =? ";
		int deviceType = Integer.parseInt(type);
		int onLineSmoke = 0;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, basicSQL);
		ResultSet rs = null;
		try {
			ppst.setInt(1, deviceType);

			int areIdInt = Integer.parseInt(areaId);
			ppst.setInt(2, areIdInt);
			rs = ppst.executeQuery();
			while (rs.next()) {
				onLineSmoke = rs.getInt("total");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}

		return onLineSmoke;
	}

	private int getOnlineSmoke(String areaId) {

		String basicSQL = "select count(*) as total from smoke s where s.netState=1 and s.areaId =? ";
//		basicSQL += " and s.deviceType " + Constant.sql;
		int onLineSmoke = 0;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, basicSQL);
		ResultSet rs = null;
		try {

			int areIdInt = Integer.parseInt(areaId);
			ppst.setInt(1, areIdInt);
			rs = ppst.executeQuery();
			while (rs.next()) {
				onLineSmoke = rs.getInt("total");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}

		return onLineSmoke;
	}

	public List<DeviceNetState> getDeviceNetState(List<String> areaId) {
		String baseSQL = "select DISTINCT netState from smoke where 1=1";
		StringBuffer strSql = new StringBuffer();
		int len = 0;
		if(areaId!=null){
			len=areaId.size();
		}
		if (len == 0) {
			strSql.append("ORDER BY smoke.netState desc");
		}
		if (len == 1) {
			strSql.append("  and smoke.areaId in (?) order by smoke.netState desc  ");
		} else {
			for (int i = 0; i < len; i++) {
				if (i == 0) {
					strSql.append(" and smoke.areaId in (?, ");
				} else if (i == (len - 1)) {
					strSql.append(" ?) ORDER BY smoke.netState desc");
				} else {
					strSql.append(" ?, ");
				}
			}
		}
		List<DeviceNetState> list = new ArrayList<>();
		String allSQL = new String(baseSQL + strSql);
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, allSQL);
		ResultSet rs = null;

		try {
			for (int i = 1; i <= len; i++) {
				ppst.setInt(i, Integer.parseInt(areaId.get(i - 1)));
			}
			rs = ppst.executeQuery();
			while (rs.next()) {
				DeviceNetState entity = new DeviceNetState();
				int netState = rs.getInt(1);
				switch (netState) {
				case 1:
					entity.setNetState(String.valueOf(netState));
					entity.setNetStateName(Constant.NETSTATE1);
					break;
				case 0:
					entity.setNetState(String.valueOf(netState));
					entity.setNetStateName(Constant.NETSTATE0);
					break;
				}
				list.add(entity);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return list;

	}

	public List<DeviceNetState> getDeviceNetState() {
		String baseSQL = "select DISTINCT netState from smoke  ";
		List<DeviceNetState> list = new ArrayList<>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, baseSQL);
		ResultSet rs = null;

		try {
			rs = ppst.executeQuery();
			while (rs.next()) {
				DeviceNetState entity = new DeviceNetState();
				int netState = rs.getInt(1);
				switch (netState) {
				case 1:
					entity.setNetState(String.valueOf(netState));
					entity.setNetStateName(Constant.NETSTATE1);
					break;
				case 0:
					entity.setNetState(String.valueOf(netState));
					entity.setNetStateName(Constant.NETSTATE0);
					break;
				}
				list.add(entity);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return list;

	}

	public StaticsAnalysEntity getAllElectricinfoLg(List<String> areaList,
			List<String> areaIdstr, String StartDate, String EndDate) {

		String sql = "SELECT named,mac,address,areaId,netState,ifAlarm FROM smoke WHERE deviceType in (5,52)";
		if (areaList != null&&areaList.size()>0) {
			sql = sql + " AND areaId in(";
			for (String areaId : areaList) {
				sql = sql + areaId + ",";
			}
			sql = sql + "0) ";
		}
		if (areaIdstr != null&&areaIdstr.size()>0) {
			sql = sql + " AND areaId in(";
			for (String areaId : areaIdstr) {
				sql = sql + areaId + ",";
			}
			sql = sql + "0) ";
		} else {
			sql += " and areaId  = 0 ";
		}
//		sql = sql + " group by mac";
		int sum = 0; // 总共设备数量
		int netStateOnLine = 0; // 在线数量
		int netStateIsLoss = 0; // 离线数量
		int ifAlarm = 0; // 报警状态数量
		
		int alarmSum = 0; // 报警总数次数
		int overvoltage = 0; // 过压报警 43
		int undervoltage = 0; // 欠压报警 44
		int overcurrent = 0; // 过流报警 45
		int leakage = 0; // 漏电流报警 46
		int temperature = 0; // 温度报警数量 47
		int brokedown=0;             //故障36
		 int close=0;  //合闸48
		
		 //统计电气设备的在线数，离线数，报警状态数
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sql);
		StaticsAnalysEntity sae = new StaticsAnalysEntity();
		ResultSet rs = null;
		List<SmokeBean> lstSmoke = new ArrayList<SmokeBean>();  //用户管理的所有设备
		try {
			rs = ppst.executeQuery();
			SmokeBean sb = null;
			while (rs.next()) {
				sb = new SmokeBean();
				sb.setMac(rs.getString("mac"));
				sb.setName(rs.getString("named"));
				sb.setNetState(rs.getInt("netState"));
				sb.setIfAlarm(rs.getInt("ifAlarm"));
				lstSmoke.add(sb);
				
				sum++;
				if (rs.getInt("ifAlarm") == 0) {
					ifAlarm++;
				}
				if (rs.getInt("netState") == 1) {
					netStateOnLine++;
				} else {
					netStateIsLoss++;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		
		List<SmokeElectricInfoEntity> smokeList = getAllElectricinfo(areaList, areaIdstr, StartDate, EndDate,lstSmoke);  
		for(SmokeElectricInfoEntity seie : smokeList) {
			overvoltage += seie.getOvervoltage(); // 过压报警 43
			undervoltage += seie.getUndervoltage(); // 欠压报警 44
			overcurrent += seie.getOvercurrent(); // 过流报警 45
			leakage += seie.getLeakage(); // 漏电流报警 46
			temperature += seie.getTemperature(); // 温度报警数量 47
			brokedown+=seie.getBrokedown();             //故障36
			close+=seie.getClose();  //合闸48
		}
		
		sae.setSum(sum);
		sae.setNetStateOnLine(netStateOnLine);
		sae.setIfAlarm(ifAlarm);
		sae.setNetStateIsLoss(netStateIsLoss);
		
		sae.setOvervoltage(overvoltage);
		sae.setClose(close);
		sae.setBrokedown(brokedown);
		sae.setOvercurrent(overcurrent);
		sae.setUndervoltage(undervoltage);
		sae.setLeakage(leakage);
		sae.setTemperature(temperature);
		sae.setSeeList(smokeList);
		alarmSum = overvoltage + undervoltage + overcurrent + leakage
				+ temperature+brokedown+close;
		sae.setAlarmSum(alarmSum);
		return sae;
	}

	public List<SmokeElectricInfoEntity> getAllElectricinfo(List<String> areaList,
			List<String> areaIdstr, String StartDate, String EndDate,List<SmokeBean> lstSmoke) {
		List<SmokeElectricInfoEntity> smokeList = new ArrayList<SmokeElectricInfoEntity>();
		String sql = "select mac,named,alarmType,s.netState,ifAlarm,count(*) as stat from alarm a,smoke s where s.mac = a.smokeMac and devicetype in (5,52) ";
		if (areaList != null&&areaList.size()>0) {
			sql = sql + " AND areaId in(";
			for (String areaId : areaList) {
				sql = sql + areaId + ",";
			}
			sql = sql + "0) ";
		}
		if (areaIdstr != null&&areaIdstr.size()>0) {
			sql = sql + " AND areaId in(";
			for (String areaId : areaIdstr) {
				sql = sql + areaId + ",";
			}
			sql = sql + "0) ";
		} else {
			sql += " and areaid = 0";
		}
		if (Utils.isNullStr(StartDate)) {
			sql = sql + " AND alarmTime > '" + StartDate + "'";
		}
		if (Utils.isNullStr(EndDate)) {
			sql = sql + " AND alarmTime< '"+ EndDate + "'";
		}
		
		sql +=" group by mac,alarmType"; 
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			rs = ppst.executeQuery();
			SmokeElectricInfoEntity seie   = null;
			while(rs.next()){
				if (seie == null) {
					seie = new SmokeElectricInfoEntity();
					seie.setSmokeMac(rs.getString("mac"));
					seie.setNamed(rs.getString("named"));
					seie.setNetState(rs.getInt("netState"));
					seie.setIfAlarm(rs.getInt("ifAlarm"));
				} else if(!seie.getSmokeMac().equals(rs.getString("mac"))){
					smokeList.add(seie);
					seie = new SmokeElectricInfoEntity();
					seie.setSmokeMac(rs.getString("mac"));
					seie.setNamed(rs.getString("named"));
					seie.setNetState(rs.getInt("netState"));
					seie.setIfAlarm(rs.getInt("ifAlarm"));
				}
				
				Iterator<SmokeBean> iterator = lstSmoke.iterator();
				while(iterator.hasNext()) {
					if(seie.getSmokeMac().equals(iterator.next().getMac())){
						iterator.remove();
						break;
					}
				}
				
				int at = rs.getInt("alarmType");
				int stat = rs.getInt("stat");
				switch (at){
				case 43:
				case  143:
					seie.setOvervoltage(stat);
					break;
				case 44:
				case  144:
					seie.setUndervoltage(stat);
					break;
				case 45:
				case  145:
					seie.setOvercurrent(stat);
					break;
				case 46:
				case  146:
					seie.setLeakage(stat);
					break;
				case 47:
				case  147:
					seie.setTemperature(stat);
					break;
				case 48:
				case  148:
					seie.setClose(stat);
					break;
				case 36:
				case  136:
					seie.setBrokedown(stat);
					break;
				}
				
			}
			if (seie != null)  //最后一个也添加进去。
				smokeList.add(seie);
			
			Iterator<SmokeBean> iterator = lstSmoke.iterator();
			while(iterator.hasNext()) {  //把不存在于alarm表内的设备添加到smokeList
				SmokeBean next = iterator.next();
				SmokeElectricInfoEntity sei =new SmokeElectricInfoEntity();
				sei.setSmokeMac(next.getMac());
				sei.setNamed(next.getName());
				sei.setNetState(next.getNetState());
				sei.setIfAlarm(next.getIfAlarm());
				smokeList.add(sei);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		
		return smokeList;
	}
	
	public StaticsAnalysEntity getAllElectricinfo(List<String> areaList,
			String areaIdstr, String StartDate, String EndDate) {

		String sql = "SELECT named,mac,address,areaId,netState,ifAlarm FROM smoke WHERE deviceType = 5";
		if (areaList != null) {
			sql = sql + " AND areaId in(";
			for (String areaId : areaList) {
				sql = sql + areaId + ",";
			}
			sql = sql + "0) ";
		}
		if (Utils.isNullStr(areaIdstr)) {
			sql = sql + " and areaId = " + areaIdstr;
		}
		sql = sql + " group by mac";
		int sum = 0; // 总共设备数量
		int netStateOnLine = 0; // 在线数量
		int netStateIsLoss = 0; // 离线数量
		int ifAlarm = 0; // 报警状态数量
		int alarmSum = 0; // 报警总数次数
		int overvoltage = 0; // 过压报警 43
		int undervoltage = 0; // 欠压报警 44
		int overcurrent = 0; // 过流报警 45
		int leakage = 0; // 漏电流报警 46
		int temperature = 0; // 温度报警数量 47
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sql);
		List<SmokeElectricInfoEntity> smokeList = new ArrayList<SmokeElectricInfoEntity>();
		StaticsAnalysEntity sae = new StaticsAnalysEntity();
		ResultSet rs = null;
		try {
			rs = ppst.executeQuery();
			while (rs.next()) {
				SmokeElectricInfoEntity see = new SmokeElectricInfoEntity();
				see.setSmokeMac(rs.getString("mac"));
				see.setIfAlarm(rs.getInt("ifAlarm"));
				see.setNetState(rs.getInt("netState"));
				see.setNamed(rs.getString("named"));
				sum++;
				if (rs.getInt("ifAlarm") == 0) {
					ifAlarm++;
				}
				if (rs.getInt("netState") == 1) {
					netStateOnLine++;
				} else {
					netStateIsLoss++;
				}
				List<SmokeElectricInfoEntity> elist = getElectricinfo(
						rs.getString("mac"), areaIdstr, StartDate, EndDate);
				for (SmokeElectricInfoEntity seie : elist) {
					int family = seie.getAlarmFamily();
					if (family == 45) {
						overcurrent = overcurrent + seie.getOvercurrent();
						see.setOvercurrent(seie.getOvercurrent());
					} else if (family == 46) {
						leakage = leakage + seie.getLeakage();
						see.setLeakage(seie.getLeakage());
					} else if (family == 47) {
						temperature = temperature + seie.getTemperature();
						see.setTemperature(seie.getTemperature());
					} else if (family == 44) {
						undervoltage = undervoltage + seie.getUndervoltage();
						see.setUndervoltage(seie.getUndervoltage());
					} else if (family == 43) {
						overvoltage = overvoltage + seie.getOvervoltage();
						see.setOvervoltage(seie.getOvervoltage());
					}
				}
				smokeList.add(see);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		alarmSum = overvoltage + undervoltage + overcurrent + leakage
				+ temperature;
		sae.setSum(sum);
		sae.setNetStateOnLine(netStateOnLine);
		sae.setAlarmSum(alarmSum);
		sae.setNetStateIsLoss(netStateIsLoss);
		sae.setOvervoltage(overvoltage);
		sae.setOvercurrent(overcurrent);
		sae.setUndervoltage(undervoltage);
		sae.setLeakage(leakage);
		sae.setTemperature(temperature);
		sae.setSeeList(smokeList);
		sae.setIfAlarm(ifAlarm);
		return sae;
	}

	public List<SmokeElectricInfoEntity> getElectricinfo(String smokeMac,
			String areaIdstr, String StartDate, String EndDate) {
		List<SmokeElectricInfoEntity> smokeList = new ArrayList<SmokeElectricInfoEntity>();
		String sql = "SELECT a.smokeMac,a.alarmType,a.alarmTime,a.alarmFamily,a.alarmTruth,COUNT(a.alarmType) AS alarmSum,s.netState,s.ifAlarm   "
				+ " FROM alarm a,smoke s WHERE smokeMac =  '" + smokeMac + "'";
		if (Utils.isNullStr(areaIdstr)) {
			sql = sql + " AND areaid = '"+ areaIdstr+"'";
		}
		if (Utils.isNullStr(StartDate)) {
			sql = sql + " AND alarmTime > '" + StartDate + "'";
		}
		if (Utils.isNullStr(EndDate)) {
			sql = sql + " AND alarmTime< '"+ EndDate + "'";
		}

		sql = sql + " AND s.mac = a.smokeMac GROUP BY alarmType";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sql);
		int testone = 0;
		ResultSet rs = null;
		SmokeElectricInfoEntity see = null;
		try {
			rs = ppst.executeQuery();
			while (rs.next()) {
				testone++;
				see = new SmokeElectricInfoEntity();
				see.setAlarmFamily(rs.getInt("alarmType"));
				int family = rs.getInt("alarmType");
				if (family == 45||family==145) {
					see.setOvercurrent(rs.getInt("alarmSum"));
				} else if (family == 46||family==146) {
					see.setLeakage(rs.getInt("alarmSum"));
				} else if (family == 47||family==147) {
					see.setTemperature(rs.getInt("alarmSum"));
				} else if (family == 44||family==144) {
					see.setUndervoltage(rs.getInt("alarmSum"));
				} else if (family == 43||family==143) {
					see.setOvervoltage(rs.getInt("alarmSum"));
				}else if (family == 36||family ==136) {
					see.setBrokedown(rs.getInt("alarmSum"));
				}else if (family == 48||family ==148) {
					see.setClose(rs.getInt("alarmSum"));
				}
				smokeList.add(see);
			}
			if (testone == 0) {
				see = new SmokeElectricInfoEntity();
				see.setOvercurrent(0);
				see.setLeakage(0);
				see.setTemperature(0);
				see.setUndervoltage(0);
				see.setOvervoltage(0);
				smokeList.add(see);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}

		return smokeList;
	}

	@Override
	public List<Water> getWaterInfo(String waterMac,String start_Time,String end_Time) {
		StringBuilder sqlstr = new StringBuilder();
		sqlstr.append("SELECT waterMac,status,value,time from waterinfo where 1 = 1");
		WaterInfoDao wDao = new WaterInfoDaoImpl();
		float L_value = Float.parseFloat(wDao.getWaterLow(waterMac));
		float H_value = Float.parseFloat(wDao.getWaterHigh(waterMac));
		if(StringUtils.isNotEmpty(waterMac)){
			sqlstr.append(" and waterMac = ?");
		}
		if(StringUtils.isNotEmpty(start_Time)){
			start_Time = start_Time+" 00:00:01";
			sqlstr.append(" and time > ?");
		}
		if(StringUtils.isNotEmpty(end_Time)){
			end_Time = end_Time + "23:59:59";
			sqlstr.append(" and time < ?");
		}
		sqlstr.append(" ORDER BY time ");
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr.toString());
		ResultSet rs = null;
		List<Water> waterList = new ArrayList<Water>();
		try {
			if(StringUtils.isNotEmpty(waterMac)){
				ps.setString(1, waterMac);
				if(StringUtils.isNotEmpty(start_Time)){
					ps.setString(2, start_Time);
					if(StringUtils.isNotEmpty(end_Time)){
						ps.setString(3, end_Time);
					}
				}else if(StringUtils.isNotEmpty(end_Time)){
					ps.setString(2, end_Time);
				}
			}else if(StringUtils.isNotEmpty(start_Time)){
				ps.setString(1, start_Time);
				if(StringUtils.isNotEmpty(end_Time)){
					ps.setString(2, end_Time);
				}
			}else if(StringUtils.isNotEmpty(end_Time)){
				ps.setString(1, end_Time);
			}
			rs = ps.executeQuery();
			while(rs.next()){
				Water wt = new Water();
				wt.setWaterMac(rs.getString(1));
				wt.setAlarmType(rs.getInt(2));
				wt.setValue(rs.getString(3));
				wt.setWaterTime(rs.getString(4));
				wt.setL_value(L_value);
				wt.setH_value(H_value);
				waterList.add(wt);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return waterList;
	}
	
	@Override
	public List<WaterEntity> getWaterInfoByAreaId(String areaid,String smokeMac,String start_Time,String end_Time) {
		// TODO Auto-generated method stub
		StringBuilder sqlstr = new StringBuilder();
		sqlstr.append(" SELECT named,mac,areaId from smoke where deviceType in (10,19)");
		if(StringUtils.isNotEmpty(areaid)){
			sqlstr.append(" and areaId = ?");
		}
		if(StringUtils.isNotEmpty(smokeMac)){
			sqlstr.append(" and mac = ?");
		}
		String waterMac;
		List<Water> waterList;
		List<WaterEntity> waterEntityList = new ArrayList<WaterEntity>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr.toString());
		ResultSet rs = null;
		try {
			if(StringUtils.isNotEmpty(areaid)){
				ps.setString(1, areaid);
				if(StringUtils.isNotEmpty(smokeMac)){
					ps.setString(2, smokeMac);
				}
			}else if(StringUtils.isNotEmpty(smokeMac)){
				ps.setString(1,smokeMac);
			}
			rs = ps.executeQuery();
			while(rs.next()){
				WaterEntity we = new WaterEntity();
				waterMac = rs.getString(2);
				we.setWaterName(rs.getString(1));
				we.setWaterMac(waterMac);
				waterList = getWaterInfo(waterMac,start_Time,end_Time);
				we.setWaterList(waterList);
				waterEntityList.add(we);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return waterEntityList;
	}
	
	@Override
	public List<WaterEntity> getWaterInfoByAreaId(String areaid,String smokeMac,String start_Time,String end_Time,int deviceType) {
		// TODO Auto-generated method stub
		StringBuilder sqlstr = new StringBuilder();
		sqlstr.append(" SELECT named,mac,areaId from smoke where deviceType  = ?");
		if(StringUtils.isNotEmpty(areaid)){
			sqlstr.append(" and areaId = ?");
		}
		if(StringUtils.isNotEmpty(smokeMac)){
			sqlstr.append(" and mac = ?");
		}
		String waterMac;
		List<Water> waterList;
		List<WaterEntity> waterEntityList = new ArrayList<WaterEntity>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr.toString());
		ResultSet rs = null;
		try {
			ps.setInt(1, deviceType);
			if(StringUtils.isNotEmpty(areaid)){
				ps.setString(2, areaid);
				if(StringUtils.isNotEmpty(smokeMac)){
					ps.setString(3, smokeMac);
				}
			}else if(StringUtils.isNotEmpty(smokeMac)){
				ps.setString(2,smokeMac);
			}
			rs = ps.executeQuery();
			while(rs.next()){
				WaterEntity we = new WaterEntity();
				waterMac = rs.getString(2);
				we.setWaterName(rs.getString(1));
				we.setWaterMac(waterMac);
				waterList = getWaterInfo(waterMac,start_Time,end_Time);
				we.setWaterList(waterList);
				waterEntityList.add(we);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return waterEntityList;
	}

	//根据设备类型id获取设备类型名字
	@Override
	public String getDeviceNameByType(String deviceType) {
		String deviceName = "";
		String sql = "select deviceName from devices where id = " + deviceType;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			if (rs.next()) {
				deviceName = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return deviceName;
	}
	
	//10水压 42NB水压 125DTU水压
	@Override
	public List<WaterEntity> getWaterInfoAll(String areaid,String smokeMac,String start_Time,String end_Time) {
		// TODO Auto-generated method stub
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * from (");
		sql.append(" SELECT a.named,a.mac,a.areaId,b.waterMac,b.status,b.value,b.time from (");
		sql.append(" SELECT named,mac,areaId from smoke where deviceType in(10,42,125,70)");
		sql.append(" ) a LEFT JOIN (");
		sql.append(" SELECT waterMac,status,value,time from waterinfo");
		sql.append(") b on b.waterMac = a.mac ");
		sql.append(") as c where 1 = 1 ");
		
		if(StringUtils.isNotEmpty(areaid)){
			if(areaid.indexOf(",") == -1)
				sql.append(" and areaId = "+areaid);
			else
				sql.append(" and areaId in ("+areaid+")");
		}
		if(StringUtils.isNotEmpty(smokeMac)){
			sql.append(" and mac = '"+smokeMac+"'");
		}
		if(StringUtils.isNotBlank(start_Time)){
			sql.append(" and time > '"+start_Time+"'");
		}
		if(StringUtils.isNotBlank(end_Time)){
			sql.append(" and time < '"+end_Time+"'");
		}
		
		sql.append(" GROUP BY mac,time ORDER BY mac ,time asc ");
		
		
		String waterMac="";
		List<Water> waterList = null;
		List<WaterEntity> waterEntityList = new ArrayList<WaterEntity>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql.toString());
		ResultSet rs = null;
		WaterEntity we = null;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				if(!rs.getString("mac").equals(waterMac)){
					if(!rs.isFirst()){
						we.setWaterList(waterList);
						waterEntityList.add(we);
					}
					we = new WaterEntity();
					waterList = new ArrayList<Water>();
				}
				waterMac = rs.getString("mac");
				Water wt = new Water();
				wt.setWaterMac(waterMac);
				wt.setAlarmType(rs.getInt("status"));
				wt.setValue(rs.getString("value"));
				wt.setWaterTime(rs.getString("time"));
				waterList.add(wt);
				we.setWaterName(rs.getString("named"));
				we.setWaterMac(waterMac);
				if(rs.isLast()){
					we.setWaterList(waterList);
					waterEntityList.add(we);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return waterEntityList;
	}
	
	//19水位 124DTU水位
	@Override
	public List<WaterEntity> getWaterLeveInfoAll(String areaid,String smokeMac,String start_Time,String end_Time) {
		// TODO Auto-generated method stub
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * from (");
		sql.append(" SELECT a.named,a.mac,a.areaId,b.waterMac,b.status,b.value,b.time from (");
		sql.append(" SELECT named,mac,areaId from smoke where deviceType in(19,124,69)");
		sql.append(" ) a LEFT JOIN (");
		sql.append(" SELECT waterMac,status,value,time from waterinfo");
		sql.append(") b on b.waterMac = a.mac ");
		sql.append(") as c where 1 = 1 ");
		
		if(StringUtils.isNotEmpty(areaid)){
			if(areaid.indexOf(",") == -1)
				sql.append(" and areaId = "+areaid);
			else
				sql.append(" and areaId in ("+areaid+")");
		}
		if(StringUtils.isNotEmpty(smokeMac)){
			sql.append(" and mac = '"+smokeMac+"'");
		}
		if(StringUtils.isNotBlank(start_Time)){
			sql.append(" and time > '"+start_Time+"'");
		}
		if(StringUtils.isNotBlank(end_Time)){
			sql.append(" and time < '"+end_Time+"'");
		}
		
		sql.append(" GROUP BY 	mac,time ORDER BY mac,time asc");
		
		
		String waterMac="";
		List<Water> waterList = null;
		List<WaterEntity> waterEntityList = new ArrayList<WaterEntity>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql.toString());
		ResultSet rs = null;
		WaterEntity we = null;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				if(!rs.getString("mac").equals(waterMac)){
					if(!rs.isFirst()){
						we.setWaterList(waterList);
						waterEntityList.add(we);
					}
					we = new WaterEntity();
					waterList = new ArrayList<Water>();
				}
				waterMac = rs.getString("mac");
				Water wt = new Water();
				wt.setWaterMac(waterMac);
				wt.setAlarmType(rs.getInt("status"));
				wt.setValue(rs.getString("value"));
				wt.setWaterTime(rs.getString("time"));
				waterList.add(wt);
				we.setWaterName(rs.getString("named"));
				we.setWaterMac(waterMac);
				if(rs.isLast()){
					we.setWaterList(waterList);
					waterEntityList.add(we);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return waterEntityList;
	}

	@Override
	public WaterEntity getWaterOfToday(String waterMac) {
		String sql = "select mac,named,w.time,w.value from waterinfo w,smoke s where w.waterMac = s.mac and  watermac = ? and w.time >? and w.time < ? order by w.time desc";
		long time = System.currentTimeMillis();
		String endTime = GetTime.getTimeByLong(time);
		String startTime = endTime.substring(0,10) + " 00:00:00";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		WaterEntity we = null;
		try {
			ps.setString(1, waterMac);
			ps.setString(2, startTime);
			ps.setString(3, endTime);
			rs = ps.executeQuery();
			while(rs.next()){
				if (we == null) {
					we = new WaterEntity();
					we.setWaterMac(waterMac);
					we.setWaterName(rs.getString("named"));
					List<Water> lstwater = new ArrayList<Water>();
					we.setWaterList(lstwater);
				}
				Water water = new Water();
				water.setWaterTime(rs.getString("time"));
				water.setValue(rs.getString("value"));
				we.getWaterList().add(water);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return we;
	}

	@Override
	public WaterEntity getWaterLeveOfToday(String waterMac) {
		String sql = "select mac,named,w.time,w.value from waterinfo w,smoke s where w.waterMac = s.mac and  watermac = ? and w.time >? and w.time < ?";
		long time = System.currentTimeMillis();
		String endTime = GetTime.getTimeByLong(time);
		String startTime = endTime.substring(0,10) + " 00:00:00";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		WaterEntity we = null;
		try {
			ps.setString(1, waterMac);
			ps.setString(2, startTime);
			ps.setString(3, endTime);
			rs = ps.executeQuery();
			while(rs.next()){
				if (we == null) {
					we = new WaterEntity();
					we.setWaterMac(waterMac);
					we.setWaterName(rs.getString("named"));
					List<Water> lstwater = new ArrayList<Water>();
					we.setWaterList(lstwater);
				}
				Water water = new Water();
				water.setWaterTime(rs.getString("time"));
				water.setValue(rs.getString("value"));
				we.getWaterList().add(water);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return we;
	}

	@Override
	public WaterEntity getAlarmRecord(String mac) {
		String sql = "select s.mac,s.named,a.alarmType,a.alarmTime,at.alarmName from alarm a,smoke s,alarmType at where a.smokeMac = s.mac and a.alarmType = at.alarmId and s.mac = ? and a.alarmTime > ? and a.alarmTime < ? order by a.id desc";
		long time = System.currentTimeMillis();
		String endTime = GetTime.getTimeByLong(time);
		String startTime = GetTime.getTimeByLong(time - 7 * 24 * 60 * 60 * 1000);
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		WaterEntity we = null;
		try {
			ps.setString(1, mac);
			ps.setString(2, startTime);
			ps.setString(3, endTime);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (we == null) {
					we = new WaterEntity();
					we.setWaterMac(mac);
					we.setWaterName(rs.getString("named"));
					List<Water> lstwater = new ArrayList<Water>();
					we.setWaterList(lstwater);
				}
				Water water = new Water();
				water.setWaterTime(rs.getString("alarmTime"));
				water.setValue(rs.getString("alarmName"));
				we.getWaterList().add(water);
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return we;
	}
	
	public int getNBSmokeInfoCount(List<String> areaList, SmokeQuery query) {
		int count = 0;
		int len = areaList.size();
		//and deviceType in(22,41,45,55,56,57,58,59,61,72,73)
		String sql = "select s.named,s.mac,s.address,s.netState,s.repeater,a.area,s.placeTypeId,s.time ,d.deviceName from smoke s,areaidarea a ,devices d "
				+ "where 1=1 and s.areaId = a.areaId  and d.id=s.deviceType";
		if (query != null) {
			if (StringUtils.isNotBlank(query.getDeviceType())) {
				if (query.getDeviceType().equals("45")) {
					sql += " and s.deviceType in "+"(45)";
				} else {
					sql += " and s.deviceType in "+"(72,73)";
				}
			}
			if(StringUtils.isNotBlank(query.getType())){
				sql += " and d.devId= '" + query.getType() + "'";
			}

			if (StringUtils.isNotBlank(query.getMac())) {
				sql += " and s.mac like '%" + query.getMac() + "%'";
			}
			if (StringUtils.isNotBlank(query.getNetState())) {
				sql += " and s.netState ='" + query.getNetState() + "'";
			}
			if (StringUtils.isNotBlank(query.getAreaName())) {
				sql += " and s.areaId = '"
						+ Integer.parseInt(query.getAreaName()) + "'";
			}
			if (StringUtils.isNotBlank(query.getPlaceType())) {
				sql += " and s.placeTypeId = '"
						+ Integer.parseInt(query.getPlaceType()) + "'";
			}
			if (len == 0) {
				return (Integer) null;
			}
			if (len == 1) {
				sql += " and s.areaId in ('" + areaList.get(0) + "') ";
			}
			if (len > 1) {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						sql += " and s.areaId in ('" + areaList.get(i) + "', ";
					} else if (i == (len - 1)) {
						sql += " " + areaList.get(i) + ") ";
					} else {
						sql += " " + areaList.get(i) + ",";
					}
				}
			}
		}
		Connection conn = DBConnectionManager.getConnection();
		String mysql = "select count(*) as totalcount from ( " + sql + " ) aa";
		PreparedStatement ps = DBConnectionManager.prepare(conn, mysql);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				count = rs.getInt("totalcount");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return count;
	}


	@Override
	public List<DeviceCostEntity> getSmokeInfoByDeviceType(List<String> areaList, SmokeQuery query) {
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		mAllCameraDao = new AllCameraDaoImpl();
		int len = areaList.size();
		String limit = " limit " + query.getStartRow() + ","
				+ query.getPageSize();
		Map<String, String> map = mPlaceTypeDao.getShopTypeById();
		String sql = "select first.*,second.* from (";
		sql += "select  s.named,s.mac,s.address,s.netState,s.repeater,a.area,s.placeTypeId,s.time,s.deviceType,s.cameraChannel,d.deviceName,s.rssivalue,s.ifAlarm ,dc.cost " +
				"from smoke s,areaidarea a,devices d,deviceCost dc "
				+ " where 1=1 and s.areaId = a.areaId and d.id = s.deviceType and dc.smokeCostMac=s.mac and dc.cost=0";
//		sql += " and s.deviceType " + Constant.sql;
		if (query != null) {
			if (!StringUtils.isBlank(query.getDeviceType())) {
				sql += " and s.deviceType=" + query.getDeviceType();
			}
			if (!StringUtils.isBlank(query.getMac())) {
				sql += " and s.mac like '%" + query.getMac() + "%'";
			}
			if (!StringUtils.isBlank(query.getNetState())) {
				sql += " and s.netState ='" + query.getNetState() + "'";
			}
			if (!StringUtils.isBlank(query.getAreaName())) {
				sql += " and s.areaId = '"
						+ Integer.parseInt(query.getAreaName()) + "'";
			}
			if (!StringUtils.isBlank(query.getPlaceType())) {
				sql += " and s.placeTypeId = '"
						+ Integer.parseInt(query.getPlaceType()) + "'";
			}
			if (len == 0) {
				return null;
			}
			if (len == 1) {
				sql += " and s.areaId in ('" + areaList.get(0)
						+ "') order by s.time desc" + limit;
			}
			if (len > 1) {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						sql += " and s.areaId in ('" + areaList.get(i) + "', ";
					} else if (i == (len - 1)) {
						sql += " " + areaList.get(i)
								+ ") order by s.time desc " + limit;
					} else {
						sql += " " + areaList.get(i) + ",";
					}
				}
			}
		}
		
		sql += ") first left join (select * from " +
				"(select smokeMac,a.alarmType,al.alarmName from alarm a,alarmType al " +
				"where a.alarmType = al.alarmId order by a.id desc) ala group by smokeMac) second " +
				" on first.mac = second.smokeMac";
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<DeviceCostEntity> lists = new ArrayList<>();

		try {
			rs = ps.executeQuery();
			int row = query.getStartRow();
			while (rs.next()) {

				DeviceCostEntity mSmartControlEntity = new DeviceCostEntity();
				mSmartControlEntity.setRow(++row);
				mSmartControlEntity.setAddress(rs.getString(3));
				mSmartControlEntity.setCompany(rs.getString(6));
				mSmartControlEntity.setStateTime(rs.getString(8));

				CameraBean cameraBean = mAllCameraDao.getCameraBySmokeMac(rs
						.getString(2));
				mSmartControlEntity.setCamera(cameraBean);
				if (cameraBean == null) {
					mSmartControlEntity.setCameraChannel(0);
				} else {
					mSmartControlEntity.setCameraChannel(1);
				}
				// mSmartControlEntity.setCameraChannel(rs.getInt(10));

				String placeTypeId = rs.getString(7) + "";
				if (StringUtils.isNumeric(placeTypeId)) {
					mSmartControlEntity.setPlaceTypeName(map.get(placeTypeId));
				}
				if (rs.getInt(4) == 0) {
					mSmartControlEntity.setDevState(Constant.NETSTATE0);
				} else {
					mSmartControlEntity.setDevState(Constant.NETSTATE1);
				}
				mSmartControlEntity.setDevType(rs.getString("deviceName"));
				mSmartControlEntity.setEnterprise(rs.getString(1));
				mSmartControlEntity.setRepeaterMac(rs.getString(5));
				mSmartControlEntity.setSmokeMac(rs.getString(2));
				mSmartControlEntity.setRssivalue(rs.getString("rssivalue"));
				int ifAlarm = rs.getInt("ifAlarm");
				mSmartControlEntity.setIfAlarm(ifAlarm);
				if(ifAlarm == 0){ //报警状态
					mSmartControlEntity.setAlarmType(rs.getInt("alarmType"));
					mSmartControlEntity.setAlarmName(rs.getString("alarmName"));
				}
				Double cost=rs.getDouble("cost");
				mSmartControlEntity.setCost(cost);
				lists.add(mSmartControlEntity);
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
	public int getSmokeInfoCountByDeviceType(List<String> areaList, SmokeQuery query) {
			mPlaceTypeDao = new PlaceTypeDaoImpl();
			int len = areaList.size();
			int totalcount = 0;
			//Map<String, String> map = mPlaceTypeDao.getShopTypeById();
			String sql = "select  s.named,s.mac,s.address,s.netState,s.repeater,a.area,s.placeTypeId,s.time,s.deviceType,dc.smokeCostMac from smoke s,areaidarea a ,deviceCost dc "
					+ " where 1=1 and s.areaId = a.areaId and dc.smokeCostMac=s.mac and dc.cost=0";
//			sql += " and s.deviceType " + Constant.sql;
			if (query != null) {
				if (!StringUtils.isBlank(query.getDeviceType())) {
					sql += " and s.deviceType=" + query.getDeviceType();
				}
				if (!StringUtils.isBlank(query.getMac())) {
					sql += " and s.mac like '%" + query.getMac() + "%'";
				}
				if (!StringUtils.isBlank(query.getNetState())) {
					sql += " and s.netState ='" + query.getNetState() + "'";
				}
				if (!StringUtils.isBlank(query.getAreaName())) {
					sql += " and s.areaId = '"
							+ Integer.parseInt(query.getAreaName()) + "'";
				}
				if (!StringUtils.isBlank(query.getPlaceType())) {
					sql += " and s.placeTypeId = '"
							+ Integer.parseInt(query.getPlaceType()) + "'";
				}

				if (len == 0) {
					return (Integer) null;
				}
				if (len == 1) {
					sql += " and s.areaId in ('" + areaList.get(0)
							+ "') order by s.time desc";
				}
				if (len > 1) {
					for (int i = 0; i < len; i++) {
						if (i == 0) {
							sql += " and s.areaId in ('" + areaList.get(i) + "', ";
						} else if (i == (len - 1)) {
							sql += " " + areaList.get(i)
									+ ") order by s.time desc ";
						} else {
							sql += " " + areaList.get(i) + ",";
						}
					}
				}
			}
			String mysql = "select count(*) as totalcount from ( " + sql + " ) aa";
			Connection conn = DBConnectionManager.getConnection();
			PreparedStatement ps = DBConnectionManager.prepare(conn, mysql);
			ResultSet rs = null;
			try {
				rs = ps.executeQuery();
				while (rs.next()) {
					totalcount = rs.getInt("totalcount");
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				DBConnectionManager.close(rs);
				DBConnectionManager.close(ps);
				DBConnectionManager.close(conn);
			}
			return totalcount;
		}


	@Override
	public List<DeviceCostEntity> getSmokeInfoByDeviceType2(List<String> areaList, SmokeQuery query) {
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		mAllCameraDao = new AllCameraDaoImpl();
		int len = areaList.size();
		String limit = " limit " + query.getStartRow() + ","
				+ query.getPageSize();
		Map<String, String> map = mPlaceTypeDao.getShopTypeById();
		String sql = "select first.*,second.* from (";
		sql += "select  s.named,s.mac,s.address,s.netState,s.repeater,a.area,s.placeTypeId,s.time,s.deviceType,s.cameraChannel,d.deviceName,s.rssivalue,s.ifAlarm ,dc.cost " +
				"from smoke s,areaidarea a,devices d,deviceCost dc "
				+ " where 1=1 and s.areaId = a.areaId and d.id = s.deviceType and dc.smokeCostMac=s.mac and dc.cost>0";
//		sql += " and s.deviceType " + Constant.sql;
		if (query != null) {
			if (!StringUtils.isBlank(query.getDeviceType())) {
				sql += " and s.deviceType=" + query.getDeviceType();
			}
			if (!StringUtils.isBlank(query.getMac())) {
				sql += " and s.mac like '%" + query.getMac() + "%'";
			}
			if (!StringUtils.isBlank(query.getNetState())) {
				sql += " and s.netState ='" + query.getNetState() + "'";
			}
			if (!StringUtils.isBlank(query.getAreaName())) {
				sql += " and s.areaId = '"
						+ Integer.parseInt(query.getAreaName()) + "'";
			}
			if (!StringUtils.isBlank(query.getPlaceType())) {
				sql += " and s.placeTypeId = '"
						+ Integer.parseInt(query.getPlaceType()) + "'";
			}
			if (len == 0) {
				return null;
			}
			if (len == 1) {
				sql += " and s.areaId in ('" + areaList.get(0)
						+ "') order by s.time desc" + limit;
			}
			if (len > 1) {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						sql += " and s.areaId in ('" + areaList.get(i) + "', ";
					} else if (i == (len - 1)) {
						sql += " " + areaList.get(i)
								+ ") order by s.time desc " + limit;
					} else {
						sql += " " + areaList.get(i) + ",";
					}
				}
			}
		}
		
		sql += ") first left join (select * from " +
				"(select smokeMac,a.alarmType,al.alarmName from alarm a,alarmType al " +
				"where a.alarmType = al.alarmId order by a.id desc) ala group by smokeMac) second " +
				" on first.mac = second.smokeMac";
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<DeviceCostEntity> lists = new ArrayList<>();

		try {
			rs = ps.executeQuery();
			int row = query.getStartRow();
			while (rs.next()) {

				DeviceCostEntity mSmartControlEntity = new DeviceCostEntity();
				mSmartControlEntity.setRow(++row);
				mSmartControlEntity.setAddress(rs.getString(3));
				mSmartControlEntity.setCompany(rs.getString(6));
				mSmartControlEntity.setStateTime(rs.getString(8));

				CameraBean cameraBean = mAllCameraDao.getCameraBySmokeMac(rs
						.getString(2));
				mSmartControlEntity.setCamera(cameraBean);
				if (cameraBean == null) {
					mSmartControlEntity.setCameraChannel(0);
				} else {
					mSmartControlEntity.setCameraChannel(1);
				}
				// mSmartControlEntity.setCameraChannel(rs.getInt(10));

				String placeTypeId = rs.getString(7) + "";
				if (StringUtils.isNumeric(placeTypeId)) {
					mSmartControlEntity.setPlaceTypeName(map.get(placeTypeId));
				}
				if (rs.getInt(4) == 0) {
					mSmartControlEntity.setDevState(Constant.NETSTATE0);
				} else {
					mSmartControlEntity.setDevState(Constant.NETSTATE1);
				}
				mSmartControlEntity.setDevType(rs.getString("deviceName"));
				mSmartControlEntity.setEnterprise(rs.getString(1));
				mSmartControlEntity.setRepeaterMac(rs.getString(5));
				mSmartControlEntity.setSmokeMac(rs.getString(2));
				mSmartControlEntity.setRssivalue(rs.getString("rssivalue"));
				int ifAlarm = rs.getInt("ifAlarm");
				mSmartControlEntity.setIfAlarm(ifAlarm);
				if(ifAlarm == 0){ //报警状态
					mSmartControlEntity.setAlarmType(rs.getInt("alarmType"));
					mSmartControlEntity.setAlarmName(rs.getString("alarmName"));
				}
				Double cost=rs.getDouble("cost");
				mSmartControlEntity.setCost(cost);
				lists.add(mSmartControlEntity);
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
	public int getSmokeInfoCountByDeviceType2(List<String> areaList, SmokeQuery query) {
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		int len = areaList.size();
		int totalcount = 0;
		//Map<String, String> map = mPlaceTypeDao.getShopTypeById();
		String sql = "select  s.named,s.mac,s.address,s.netState,s.repeater,a.area,s.placeTypeId,s.time,s.deviceType,dc.smokeCostMac from smoke s,areaidarea a ,deviceCost dc "
				+ " where 1=1 and s.areaId = a.areaId and dc.smokeCostMac=s.mac and dc.cost>0";
//		sql += " and s.deviceType " + Constant.sql;
		if (query != null) {
			if (!StringUtils.isBlank(query.getDeviceType())) {
				sql += " and s.deviceType=" + query.getDeviceType();
			}
			if (!StringUtils.isBlank(query.getMac())) {
				sql += " and s.mac like '%" + query.getMac() + "%'";
			}
			if (!StringUtils.isBlank(query.getNetState())) {
				sql += " and s.netState ='" + query.getNetState() + "'";
			}
			if (!StringUtils.isBlank(query.getAreaName())) {
				sql += " and s.areaId = '"
						+ Integer.parseInt(query.getAreaName()) + "'";
			}
			if (!StringUtils.isBlank(query.getPlaceType())) {
				sql += " and s.placeTypeId = '"
						+ Integer.parseInt(query.getPlaceType()) + "'";
			}

			if (len == 0) {
				return (Integer) null;
			}
			if (len == 1) {
				sql += " and s.areaId in ('" + areaList.get(0)
						+ "') order by s.time desc";
			}
			if (len > 1) {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						sql += " and s.areaId in ('" + areaList.get(i) + "', ";
					} else if (i == (len - 1)) {
						sql += " " + areaList.get(i)
								+ ") order by s.time desc ";
					} else {
						sql += " " + areaList.get(i) + ",";
					}
				}
			}
		}
		String mysql = "select count(*) as totalcount from ( " + sql + " ) aa";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, mysql);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				totalcount = rs.getInt("totalcount");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return totalcount;
	}

	/**
	 * 查找出设备费用正常的设备mac和cost
	 */
	@Override
	public List<DeviceCostEntity> getAllSmoke() {
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		mAllCameraDao = new AllCameraDaoImpl();
		
		String sql = "select first.*,second.* from (";
		sql += "select  s.mac,dc.cost " +
				"from smoke s,areaidarea a,devices d,deviceCost dc "
				+ " where 1=1 and s.areaId = a.areaId and d.id = s.deviceType and dc.smokeCostMac=s.mac and dc.cost>0 and s.netState='1'";
//		sql += " and s.deviceType " + Constant.sql;
		
		sql += ") first left join (select * from " +
				"(select smokeMac,a.alarmType,al.alarmName from alarm a,alarmType al " +
				"where a.alarmType = al.alarmId order by a.id desc) ala group by smokeMac) second " +
				" on first.mac = second.smokeMac";
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<DeviceCostEntity> lists = new ArrayList<>();

		try {
			rs = ps.executeQuery();
			while (rs.next()) {

				DeviceCostEntity mSmartControlEntity = new DeviceCostEntity();
				mSmartControlEntity.setRepeaterMac(rs.getString(1));
				Double cost=rs.getDouble("cost");
				mSmartControlEntity.setCost(cost);
				lists.add(mSmartControlEntity);
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
	public int updateCostByMac(String mac, Double cost) {
		String sql = "update devicecost set cost=? where smokeCostMac=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		int rs=0;
		try {
			ps.setString(2, mac);
			ps.setDouble(1, cost);
			rs = ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return rs;
	}


	@Override
	public DeviceCostEntity selectSmokeCost(String mac) {
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		mAllCameraDao = new AllCameraDaoImpl();
		Map<String, String> map = mPlaceTypeDao.getShopTypeById();
		String sql = "select  s.named,s.mac,s.address,s.netState,s.repeater,a.area,s.placeTypeId,s.time,s.deviceType,s.cameraChannel,d.deviceName,s.rssivalue,s.ifAlarm ,s.free " +
				"from smoke s,areaidarea a,devices d "
				+ " where 1=1 and s.areaId = a.areaId and d.id = s.deviceType and s.mac=?";
		
		/*sql += ") first left join (select * from " +
				"(select smokeMac,a.alarmType,al.alarmName from alarm a,alarmType al " +
				"where a.alarmType = al.alarmId order by alarmTime desc) ala group by smokeMac) second " +
				" on first.mac = second.smokeMac";*/
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		DeviceCostEntity mSmartControlEntity = new DeviceCostEntity();
		ResultSet rs = null;
		try {
			ps.setString(1, mac);
			rs = ps.executeQuery();
			while (rs.next()) {
				mSmartControlEntity.setAddress(rs.getString(3));
				mSmartControlEntity.setCompany(rs.getString(6));
				mSmartControlEntity.setStateTime(rs.getString(8));
				CameraBean cameraBean = mAllCameraDao.getCameraBySmokeMac(rs
						.getString(2));
				mSmartControlEntity.setCamera(cameraBean);
				if (cameraBean == null) {
					mSmartControlEntity.setCameraChannel(0);
				} else {
					mSmartControlEntity.setCameraChannel(1);
				}

				String placeTypeId = rs.getString(7) + "";
				if (StringUtils.isNumeric(placeTypeId)) {
					mSmartControlEntity.setPlaceTypeName(map.get(placeTypeId));
				}
				if (rs.getInt(4) == 0) {
					mSmartControlEntity.setDevState(Constant.NETSTATE0);
				} else {
					mSmartControlEntity.setDevState(Constant.NETSTATE1);
				}
				mSmartControlEntity.setDevType(rs.getString("deviceName"));
				mSmartControlEntity.setEnterprise(rs.getString(1));
				mSmartControlEntity.setRepeaterMac(rs.getString(5));
				mSmartControlEntity.setSmokeMac(rs.getString(2));
				mSmartControlEntity.setRssivalue(rs.getString("rssivalue"));
				int ifAlarm = rs.getInt("ifAlarm");
				mSmartControlEntity.setIfAlarm(ifAlarm);
				if(ifAlarm == 0){ //报警状态
					mSmartControlEntity.setAlarmType(rs.getInt("alarmType"));
					mSmartControlEntity.setAlarmName(rs.getString("alarmName"));
				}
				Double cost = rs.getDouble("free");
				mSmartControlEntity.setCost(cost);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return mSmartControlEntity;
	}
	
	public int getSmokeListCount(List<String> areaList, SmokeQuery query) {
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		int len = areaList.size();
		int totalcount = 0;
		//Map<String, String> map = mPlaceTypeDao.getShopTypeById();
		String sql = "select  s.named,s.mac,s.address,s.netState,s.repeater,a.area,s.placeTypeId,s.time,s.deviceType,s.freeTime, d.money, s.stopTime from smoke s,areaidarea a,devices d  "
				+ "where 1=1 and s.areaId = a.areaId and d.id = s.deviceType  ";
//		sql += " and s.deviceType " + Constant.sql;
		if (query != null) {
			if (!StringUtils.isBlank(query.getDeviceType())) {
				sql += " and s.deviceType=" + query.getDeviceType();
			}
			if (!StringUtils.isBlank(query.getMac())) {
				sql += " and s.mac like '%" + query.getMac() + "%'";
			}
			if (!StringUtils.isBlank(query.getNetState())) {
				sql += " and s.netState ='" + query.getNetState() + "'";
			}
			if (!StringUtils.isBlank(query.getAreaName())) {
				sql += " and s.areaId = '"
						+ Integer.parseInt(query.getAreaName()) + "'";
			}
			if (!StringUtils.isBlank(query.getPlaceType())) {
				sql += " and s.placeTypeId = '"
						+ Integer.parseInt(query.getPlaceType()) + "'";
			}

			if (len == 0) {
				return (Integer) null;
			}
			if (len == 1) {
				sql += " and s.areaId in ('" + areaList.get(0)
						+ "') order by s.time desc";
			}
			if (len > 1) {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						sql += " and s.areaId in ('" + areaList.get(i) + "', ";
					} else if (i == (len - 1)) {
						sql += " " + areaList.get(i)
								+ ") order by s.time desc ";
					} else {
						sql += " " + areaList.get(i) + ",";
					}
				}
			}
		}
		String mysql = "select count(*) as totalcount from ( " + sql + " ) aa";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, mysql);
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
	
	public List<SmartControlEntity> getSmokeList(List<String> areaList,
			SmokeQuery query) {
		// TODO Auto-generated method stub
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		mAllCameraDao = new AllCameraDaoImpl();
		int len = areaList.size();
		String limit = " limit " + query.getStartRow() + ","
				+ query.getPageSize();
		Map<String, String> map = mPlaceTypeDao.getShopTypeById();
		String sql = "select first.*,second.* from (";
		sql += "select  s.named,s.mac,s.address,s.netState,s.repeater,a.area,s.placeTypeId,s.time,s.deviceType,s.cameraChannel,d.deviceName,s.rssivalue,s.ifAlarm, s.freeTime, d.money, s.stopTime " +
				"from smoke s,areaidarea a,devices d "
				+ "where 1=1 and s.areaId = a.areaId and d.id = s.deviceType ";
//		sql += " and s.deviceType " + Constant.sql;
		if (query != null) {
			if (!StringUtils.isBlank(query.getDeviceType())) {
				sql += " and s.deviceType=" + query.getDeviceType();
			}
			if (!StringUtils.isBlank(query.getMac())) {
				sql += " and s.mac like '%" + query.getMac() + "%'";
			}
			if (!StringUtils.isBlank(query.getNetState())) {
				sql += " and s.netState ='" + query.getNetState() + "'";
			}
			if (!StringUtils.isBlank(query.getAreaName())) {
				sql += " and s.areaId = '"
						+ Integer.parseInt(query.getAreaName()) + "'";
			}
			if (!StringUtils.isBlank(query.getPlaceType())) {
				sql += " and s.placeTypeId = '"
						+ Integer.parseInt(query.getPlaceType()) + "'";
			}
			if (len == 0) {
				return null;
			}
			if (len == 1) {
				sql += " and s.areaId in ('" + areaList.get(0)
						+ "') order by s.time desc" + limit;
			}
			if (len > 1) {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						sql += " and s.areaId in ('" + areaList.get(i) + "', ";
					} else if (i == (len - 1)) {
						sql += " " + areaList.get(i)
								+ ") order by s.time desc " + limit;
					} else {
						sql += " " + areaList.get(i) + ",";
					}
				}
			}
		}
		
		sql += ") first left join (select * from " +
				"(select smokeMac,a.alarmType,al.alarmName from alarm a,alarmType al " +
				"where a.alarmType = al.alarmId order by a.id desc) ala group by smokeMac) second " +
				" on first.mac = second.smokeMac";
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<SmartControlEntity> lists = new ArrayList<>();

		try {
			rs = ps.executeQuery();
			int row = query.getStartRow();
			while (rs.next()) {

				SmartControlEntity mSmartControlEntity = new SmartControlEntity();
				mSmartControlEntity.setRow(++row);
				mSmartControlEntity.setAddress(rs.getString(3));
				mSmartControlEntity.setCompany(rs.getString(6));
				mSmartControlEntity.setStateTime(rs.getString(8));

				CameraBean cameraBean = mAllCameraDao.getCameraBySmokeMac(rs
						.getString(2));
				mSmartControlEntity.setCamera(cameraBean);
				if (cameraBean == null) {
					mSmartControlEntity.setCameraChannel(0);
				} else {
					mSmartControlEntity.setCameraChannel(1);
				}
				// mSmartControlEntity.setCameraChannel(rs.getInt(10));

				String placeTypeId = rs.getString(7) + "";
				if (StringUtils.isNumeric(placeTypeId)) {
					mSmartControlEntity.setPlaceTypeName(map.get(placeTypeId));
				}
				if (rs.getInt(4) == 0) {
					mSmartControlEntity.setDevState(Constant.NETSTATE0);
				} else {
					mSmartControlEntity.setDevState(Constant.NETSTATE1);
				}
				mSmartControlEntity.setDevType(rs.getString("deviceName"));
				mSmartControlEntity.setEnterprise(rs.getString(1));
				mSmartControlEntity.setRepeaterMac(rs.getString(5));
				mSmartControlEntity.setSmokeMac(rs.getString(2));
				mSmartControlEntity.setRssivalue(rs.getString("rssivalue"));
				int ifAlarm = rs.getInt("ifAlarm");
				mSmartControlEntity.setIfAlarm(ifAlarm);
				if(ifAlarm == 0){ //报警状态
					mSmartControlEntity.setAlarmType(rs.getInt("alarmType"));
					mSmartControlEntity.setAlarmName(rs.getString("alarmName"));
				}
				mSmartControlEntity.setFreeTime(rs.getString("freeTime"));
				
				if(rs.getBigDecimal("money") == null || rs.getBigDecimal("money").equals("")) {
					mSmartControlEntity.setFree(BigDecimal.valueOf(10.00));
				}else{
					mSmartControlEntity.setFree(rs.getBigDecimal("money"));
				}
				mSmartControlEntity.setStopTime(rs.getString("stopTime"));
				lists.add(mSmartControlEntity);
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
	public int countNBWater(List<String> areaList, SmokeQuery query) {
		int len = areaList.size();
		int totalcount = 0;
		String sql = "select  s.named,s.mac,s.address,s.netState,s.repeater,a.area,s.placeTypeId,s.time ,s.cameraChannel,d.deviceName,s.rssivalue,s.ifAlarm,s.deviceType "
						+" from smoke s, areaidarea a, devices d where 1=1 and s.deviceType=d.id and s.areaId = a.areaId and deviceType in(46,42)";  
		if (query != null) {
			if(StringUtils.isNotBlank(query.getType())){
				sql += " and d.devId = "+query.getType();
			}
			if (!StringUtils.isBlank(query.getNetState())) {
				sql += " and s.netState =" + Integer.valueOf(query.getNetState());
			}
			if (!StringUtils.isBlank(query.getMac())) {
				sql += " and s.mac like '%" + query.getMac() + "%'";
			}
			if (!StringUtils.isBlank(query.getAreaName())) {
				sql += " and s.areaId = '" + Integer.parseInt(query.getAreaName()) + "'";
			}
			if (!StringUtils.isBlank(query.getPlaceType())) {
				sql += " and s.placeTypeId = '"
						+ Integer.parseInt(query.getPlaceType()) + "'";
			}
			if (len == 0) {
				return (Integer) null;
			}
			if (len == 1) {
				sql += " and s.areaId in ('" + areaList.get(0)
						+ "')";
			}
			if (len > 1) {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						sql += " and s.areaId in ('" + areaList.get(i) + "', ";
					} else if (i == (len - 1)) {
						sql += " " + areaList.get(i)
								+ ")";
					} else {
						sql += " " + areaList.get(i) + ",";
					}
				}
			}
		}
		sql+=" order by s.time desc";
		
		String mysql = "select count(*) as totalcount from ( " + sql + " ) aa";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, mysql);
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
	public List<SmartControlEntity> NBWaterList(List<String> areaList, SmokeQuery query) {
		String limit = " limit " + query.getStartRow() + ","
				+ query.getPageSize();
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		Map<String, String> map = mPlaceTypeDao.getShopTypeById();
		String sql = "select  s.named,s.mac,s.address,s.netState,s.repeater,a.area,s.placeTypeId,s.time ,s.cameraChannel,d.deviceName,s.rssivalue,s.ifAlarm,s.deviceType "
				+" from smoke s, areaidarea a, devices d where 1=1 and s.deviceType=d.id and s.areaId = a.areaId and deviceType in(46,42)";  
		int len = areaList.size();
		if (query != null) {
			if(StringUtils.isNotBlank(query.getType())){
				sql += " and d.devId = " + query.getType();
			}
			if (!StringUtils.isBlank(query.getNetState())) {
				sql += " and s.netState = " + Integer.valueOf(query.getNetState());
			}
			if (!StringUtils.isBlank(query.getMac())) {
				sql += " and s.mac like '%" + query.getMac() + "%'";
			}
			if (!StringUtils.isBlank(query.getAreaName())) {
				sql += " and s.areaId = '"
						+ Integer.parseInt(query.getAreaName()) + "'";
			}
			if (!StringUtils.isBlank(query.getPlaceType())) {
				sql += " and s.placeTypeId = '"
						+ Integer.parseInt(query.getPlaceType()) + "'";
			}
			if (len == 0) {
				return null;
			}
			if (len == 1) {
				sql += " and s.areaId in ('" + areaList.get(0)
						+ "')";
			}
			if (len > 1) {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						sql += " and s.areaId in ('" + areaList.get(i) + "', ";
					} else if (i == (len - 1)) {
						sql += " " + areaList.get(i)
								+ ")";
					} else {
						sql += " " + areaList.get(i) + ",";
					}
				}
			}
		}
		sql+=" order by s.time desc" + limit;
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<SmartControlEntity> lists = new ArrayList<>();
		try {
			rs = ps.executeQuery();
			int row = query.getStartRow();
			while (rs.next()) {
				SmartControlEntity smartEntity = new SmartControlEntity();
				smartEntity.setRow(++row);
				smartEntity.setEnterprise(rs.getString(1));
				smartEntity.setSmokeMac(rs.getString(2));
				smartEntity.setAddress(rs.getString(3));
				if (rs.getInt(4) == 0) {
					smartEntity.setDevState(Constant.NETSTATE0);
				} else {
					smartEntity.setDevState(Constant.NETSTATE1);
				}
				smartEntity.setRepeaterMac(rs.getString(5));
				smartEntity.setCompany(rs.getString(6));
				String placeTypeId = rs.getString(7) + "";
				if (StringUtils.isNumeric(placeTypeId)) {
					smartEntity.setPlaceTypeName(map.get(placeTypeId));
				}
				smartEntity.setStateTime(rs.getString(8));
				smartEntity.setCameraChannel(rs.getInt(9));
				smartEntity.setDevType(rs.getString(10));
				smartEntity.setRssivalue(rs.getString(11));
				smartEntity.setIfAlarm(rs.getInt(12));
				smartEntity.setDeviceTypeId(rs.getInt(13));
				lists.add(smartEntity);
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
	public List<ShopTypeEntity> getPlaceTypeNotType(String areaIdsStr) {
		List<ShopTypeEntity> list = new ArrayList<>();
		String basicSQL = "select DISTINCT s.placeTypeId,p.placeName from smoke s,placetypeidplacename p,areaidarea a WHERE 1=1" +
			" and s.areaId=a.areaId and s.placeTypeId = p.placeTypeId  and s.areaId in "+ areaIdsStr ;
		Connection conn = DBConnectionManager.getConnection();
		basicSQL = basicSQL + " order by p.placeName";
		PreparedStatement ppst = DBConnectionManager.prepare(conn, basicSQL);
		ResultSet rs = null;
		try {
			rs = ppst.executeQuery();
			while (rs.next()) {
				ShopTypeEntity entity = new ShopTypeEntity();
				entity.setPlaceTypeId(rs.getString(1));
				entity.setPlaceTypeName(rs.getString(2));
				list.add(entity);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return list;
	
	}


	@Override
	public List<AreaAndRepeater> getAreaAndRepeaterNoType(String areaIdsStr) {
		List<AreaAndRepeater> list = new ArrayList<>();
		StringBuffer sql=new StringBuffer();
		sql.append( "select DISTINCT s.areaId,a.area from smoke s,areaidarea a where 1=1 and s.areaId in " + areaIdsStr );
		sql.append(" and s.areaId=a.areaId order by  a.area");
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sql.toString());
		ResultSet rs = null;
		try {
			rs = ppst.executeQuery();
			while (rs.next()) {
				AreaAndRepeater areaAndRepeater = new AreaAndRepeater();
				areaAndRepeater.setAreaId(Integer.parseInt(rs.getString(1)));
				areaAndRepeater.setAreaName(rs.getString(2));
				list.add(areaAndRepeater);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return list;
	}

	


	@Override
	public List<DeviceType> getAllDeviceTypeAndName(String areaIdsStr) {

		List<DeviceType> list = new ArrayList<>();

		String baseSQL = "select distinct deviceType,deviceName from smoke s,devices d where 1=1  and s.areaid in "+areaIdsStr;
		StringBuffer strSql = new StringBuffer();
		strSql.append(" and s.deviceType = d.id order by deviceName");

		String allSQL = new String(baseSQL + strSql);
//		String allSQL = "select id,deviceName from devices order by deviceName";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, allSQL);
		ResultSet rs = null;
		try {
			rs = ppst.executeQuery();
			while (rs.next()) {
				DeviceType entity = new DeviceType();
				int deviceType = rs.getInt(1);
				String devName=rs.getString(2);
				entity.setDevName(devName);
				entity.setDeviceType(deviceType+"");
				list.add(entity);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return list;
	
	}

	
	@Override
	public List<DeviceNetState> getDeviceNetState(String areaIdsStr) {

		String baseSQL = "select DISTINCT netState from smoke where 1=1 and areaid in "+areaIdsStr;
		StringBuffer strSql = new StringBuffer();
		strSql.append(" ORDER BY smoke.netState desc");
		List<DeviceNetState> list = new ArrayList<>();
		String allSQL = new String(baseSQL + strSql);
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, allSQL);
		ResultSet rs = null;

		try {
			rs = ppst.executeQuery();
			while (rs.next()) {
				DeviceNetState entity = new DeviceNetState();
				int netState = rs.getInt(1);
				switch (netState) {
				case 1:
					entity.setNetState(String.valueOf(netState));
					entity.setNetStateName(Constant.NETSTATE1);
					break;
				case 0:
					entity.setNetState(String.valueOf(netState));
					entity.setNetStateName(Constant.NETSTATE0);
					break;
				}
				list.add(entity);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return list;
	}


	@Override
	public int getAllSmokeInfoCount(String areaIdsStr, SmokeQuery query) {

		mPlaceTypeDao = new PlaceTypeDaoImpl();
		int totalcount = 0;
		//Map<String, String> map = mPlaceTypeDao.getShopTypeById();
		String sql = "select  s.named,s.mac,s.address,s.netState,s.repeater,a.area,s.placeTypeId,s.time,s.deviceType,d.deviceName from smoke s,areaidarea a,devices d  "
				+ "where 1=1 and s.areaId = a.areaId and d.id = s.deviceType ";
//		sql += " and s.deviceType " + Constant.sql;
		if (query != null) {
			if (!StringUtils.isBlank(query.getDeviceType())) {
				sql += " and s.deviceType=" + query.getDeviceType();
			}
			if (!StringUtils.isBlank(query.getMac())) {
				sql += " and s.mac like '%" + query.getMac() + "%'";
			}
			if (!StringUtils.isBlank(query.getNetState())) {
				sql += " and s.netState ='" + query.getNetState() + "'";
			}
			if (!StringUtils.isBlank(query.getAreaName())) {
				sql += " and s.areaId = '"
						+ Integer.parseInt(query.getAreaName()) + "'";
			}
			if (!StringUtils.isBlank(query.getPlaceType())) {
				sql += " and s.placeTypeId = '"
						+ Integer.parseInt(query.getPlaceType()) + "'";
			}

		}
		String mysql = "select count(*) as totalcount from ( " + sql +"and s.areaId in "+ areaIdsStr+ " order by s.time desc ) aa";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, mysql);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				totalcount = rs.getInt("totalcount");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return totalcount;
	
	}


	@Override
	public List<SmartControlEntity> getAllSmokeInfo(String areaIdsStr,SmokeQuery query) {
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		mAllCameraDao = new AllCameraDaoImpl();
		String limit = " limit " + query.getStartRow() + ","+ query.getPageSize();
		Map<String, String> map = mPlaceTypeDao.getShopTypeById();
//		String sql = " select * from (select first.* ,a.alarmType,al.alarmName from (";
		String sql = "";
		sql += "select  s.named,s.mac,s.address,s.netState,s.repeater,a.area,s.placeTypeId,s.time,s.deviceType,s.cameraChannel,d.deviceName,s.rssivalue,s.ifAlarm " +
				"from smoke s,areaidarea a,devices d "
				+ "where 1=1 and s.areaId = a.areaId and d.id = s.deviceType ";
//		sql += " and s.deviceType " + Constant.sql;
		if (query != null) {
			if (!StringUtils.isBlank(query.getDeviceType())) {
				sql += " and s.deviceType=" + query.getDeviceType();
			}
			if (!StringUtils.isBlank(query.getMac())) {
				sql += " and s.mac like '%" + query.getMac() + "%'";
			}
			if (!StringUtils.isBlank(query.getNetState())) {
				sql += " and s.netState ='" + query.getNetState() + "'";
			}
			if (!StringUtils.isBlank(query.getAreaName())) {
				sql += " and s.areaId = '"
						+ Integer.parseInt(query.getAreaName()) + "'";
			}
			if (!StringUtils.isBlank(query.getPlaceType())) {
				sql += " and s.placeTypeId = '"
						+ Integer.parseInt(query.getPlaceType()) + "'";
			}
		}
		
		sql += " and s.areaId in "+ areaIdsStr+ " order by s.time desc " + limit ;
//				"  ,alarm a,alarmType al where  a.alarmType = al.alarmId  and first.mac = a.smokeMac order by a.alarmTime desc ) t group  by t.mac" ;
//				" left join (select * from " +
//				"(select smokeMac,a.alarmType,al.alarmName from alarm a,alarmType al " +
//				"where a.alarmType = al.alarmId order by alarmTime desc) ala group by smokeMac) second " +
//				" on first.mac = second.smokeMac";
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<SmartControlEntity> lists = new ArrayList<>();

		try {
			rs = ps.executeQuery();
			int row = query.getStartRow();
			while (rs.next()) {

				SmartControlEntity mSmartControlEntity = new SmartControlEntity();
				mSmartControlEntity.setRow(++row);
				mSmartControlEntity.setAddress(rs.getString(3));
				mSmartControlEntity.setCompany(rs.getString(6));
				mSmartControlEntity.setStateTime(rs.getString(8));

				CameraBean cameraBean = mAllCameraDao.getCameraBySmokeMac(rs
						.getString(2));
				mSmartControlEntity.setCamera(cameraBean);
				if (cameraBean == null) {
					mSmartControlEntity.setCameraChannel(0);
				} else {
					mSmartControlEntity.setCameraChannel(1);
				}
				// mSmartControlEntity.setCameraChannel(rs.getInt(10));

				String placeTypeId = rs.getString(7) + "";
				if (StringUtils.isNumeric(placeTypeId)) {
					mSmartControlEntity.setPlaceTypeName(map.get(placeTypeId));
				}
				if (rs.getInt(4) == 0) {
					mSmartControlEntity.setDevState(Constant.NETSTATE0);
				} else {
					mSmartControlEntity.setDevState(Constant.NETSTATE1);
				}
				mSmartControlEntity.setDevType(rs.getString("deviceName"));
				mSmartControlEntity.setEnterprise(rs.getString(1));
				mSmartControlEntity.setRepeaterMac(rs.getString(5));
				mSmartControlEntity.setSmokeMac(rs.getString(2));
				mSmartControlEntity.setRssivalue(rs.getString("rssivalue"));
				int ifAlarm = rs.getInt("ifAlarm");
				mSmartControlEntity.setIfAlarm(ifAlarm);
				if(ifAlarm == 0){ //报警状态
//					mSmartControlEntity.setAlarmType(rs.getInt("alarmType"));
//					mSmartControlEntity.setAlarmName(rs.getString("alarmName"));
					mSmartControlEntity.setAlarmName("报警");
				}
				lists.add(mSmartControlEntity);
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
	public int countUnknow(List<String> areaList, SmokeQuery query) {
		int count = 0;
		int len = areaList.size();
		String sql = "select count(DISTINCT s.mac) as totalcount";
		sql += " from smoke s,devices d,devsystem ds,areaidarea a where 1=1  and s.deviceType=d.id and d.devId = ds.id and s.areaId = a.areaId and ds.id = 26 ";
		if (query != null) {
			if (!StringUtils.isBlank(query.getDeviceType())) {
				sql += " and s.deviceType=" + query.getDeviceType();
			}
			if (!StringUtils.isBlank(query.getMac())) {
				sql += " and s.mac like '%" + query.getMac() + "%'";
			}
			if (!StringUtils.isBlank(query.getNetState())) {
				sql += " and s.netState ='" + query.getNetState() + "'";
			}
			if (!StringUtils.isBlank(query.getAreaName())) {
				sql += " and s.areaId = '"
						+ Integer.parseInt(query.getAreaName()) + "'";
			}
			if (!StringUtils.isBlank(query.getPlaceType())) {
				sql += " and s.placeTypeId = '"
						+ Integer.parseInt(query.getPlaceType()) + "'";
			}
			if (len == 0) {
				return (Integer) null;
			}
			if (len == 1) {
				sql += " and s.areaId in ('" + areaList.get(0) + "') ";
			}
			if (len > 1) {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						sql += " and s.areaId in ('" + areaList.get(i) + "', ";
					} else if (i == (len - 1)) {
						sql += " " + areaList.get(i) + ") ";
					} else {
						sql += " " + areaList.get(i) + ",";
					}
				}
			}
		}
		Connection conn = DBConnectionManager.getConnection();
		System.out.println("数量:"+sql);
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				count = rs.getInt("totalcount");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return count;
	}
	
	@Override
	public List<SmartControlEntity> getUnknowList(List<String> areaList, SmokeQuery query) {
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		mAllCameraDao = new AllCameraDaoImpl();
		int len = areaList.size();
		String limit = " limit " + query.getStartRow() + ","
				+ query.getPageSize();
		Map<String, String> map = mPlaceTypeDao.getShopTypeById();
		String sql = "";
		sql += "select  s.named,s.mac,s.address,s.netState,s.repeater,a.area,s.placeTypeId,s.time ,s.cameraChannel,d.deviceName,s.rssivalue,s.ifAlarm "
			  + "from smoke s,devices d,devsystem ds,areaidarea a "
			  + "where 1=1  and s.deviceType=d.id and d.devId = ds.id and s.areaId = a.areaId and ds.id = 26 ";
		if (query != null) {
			if (!StringUtils.isBlank(query.getDeviceType())) {
				sql += "and s.deviceType=" + query.getDeviceType();
			}
			if (!StringUtils.isBlank(query.getMac())) {
				sql += " and s.mac like '%" + query.getMac() + "%'";
			}
			if (!StringUtils.isBlank(query.getNetState())) {
				sql += " and s.netState ='" + query.getNetState() + "'";
			}
			if (!StringUtils.isBlank(query.getAreaName())) {
				sql += " and s.areaId = '"
						+ Integer.parseInt(query.getAreaName()) + "'";
			}
			if (!StringUtils.isBlank(query.getPlaceType())) {
				sql += " and s.placeTypeId = '"
						+ Integer.parseInt(query.getPlaceType()) + "'";
			}

			if (len == 0) {
				return null;
			}
			if (len == 1) {
				sql += " and s.areaId in ('" + areaList.get(0)
						+ "') order by s.time desc" + limit;
			}
			if (len > 1) {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						sql += " and s.areaId in ('" + areaList.get(i) + "', ";
					} else if (i == (len - 1)) {
						sql += " " + areaList.get(i)
								+ ") order by s.time desc " + limit;
					} else {
						sql += " " + areaList.get(i) + ",";
					}
				}
			}
		}
	
		Connection conn = DBConnectionManager.getConnection();
		System.out.println("数值:"+sql);
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<SmartControlEntity> lists = new ArrayList<>();

		try {
			rs = ps.executeQuery();
			int row = query.getStartRow();
			while (rs.next()) {
				SmartControlEntity mSmartControlEntity = new SmartControlEntity();
				mSmartControlEntity.setRow(++row);
				mSmartControlEntity.setAddress(rs.getString(3));
				mSmartControlEntity.setCompany(rs.getString(6));
				mSmartControlEntity.setStateTime(rs.getString(8));
				CameraBean cameraBean = mAllCameraDao.getCameraBySmokeMac(rs
						.getString(2));
				mSmartControlEntity.setCamera(cameraBean);
				if (cameraBean == null) {
					mSmartControlEntity.setCameraChannel(0);
				} else {
					mSmartControlEntity.setCameraChannel(1);
				}
				String placeTypeId = rs.getString(7) + "";
				if (StringUtils.isNumeric(placeTypeId)) {
					mSmartControlEntity.setPlaceTypeName(map.get(placeTypeId));
				}
				if (rs.getInt(4) == 0) {
					mSmartControlEntity.setDevState(Constant.NETSTATE0);
				} else {
					mSmartControlEntity.setDevState(Constant.NETSTATE1);
				}
				mSmartControlEntity.setDevType(rs.getString("deviceName"));
				mSmartControlEntity.setEnterprise(rs.getString(1));
				mSmartControlEntity.setRepeaterMac(rs.getString(5));
				mSmartControlEntity.setSmokeMac(rs.getString(2));
				mSmartControlEntity.setRssivalue(rs.getString("rssivalue"));
				int ifAlarm = rs.getInt("ifAlarm");
				mSmartControlEntity.setIfAlarm(ifAlarm);
				if(ifAlarm == 0){ //报警状态
//					mSmartControlEntity.setAlarmType(rs.getInt("alarmType"));
					mSmartControlEntity.setAlarmName("报警");
				}
				lists.add(mSmartControlEntity);
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
	
}
