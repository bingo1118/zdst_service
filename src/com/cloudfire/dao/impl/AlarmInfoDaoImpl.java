package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.cloudfire.dao.AlarmInfoDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.AlarmInfoEntity;
import com.cloudfire.entity.AlarmInfoEntity_PC;
import com.cloudfire.entity.PrinterEntity;
import com.cloudfire.entity.query.AlarmInfoQuery;
import com.cloudfire.until.Constant;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.StringUtil;
import com.cloudfire.until.Utils;

public class AlarmInfoDaoImpl implements AlarmInfoDao {

	public List<AlarmInfoEntity> getAlarmInfo(List<String> areaId) {
		StringBuffer strSql = new StringBuffer();
		if (areaId == null) {
			return null;
		} else {
			int len = areaId.size();
			if (len == 0) {
				return null;
			}
			if (len == 1) {
				strSql.append(" and s.areaId in (?) order by a.id desc");
			} else {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						strSql.append(" and s.areaId in (?, ");
					} else if (i == (len - 1)) {
						strSql.append(" ?) order by a.id desc");
					} else {
						strSql.append(" ?, ");
					}
				}
			}
			String sqlFire = "select a.alarmType,a.alarmTime,s.deviceType,s.address,s.mac,are.area,s.principal1,s.principal1Phone,s.principal2,s.principal2Phone,a.alarmFamily,a.alarmTruth,s.cameraChannel,s.named,tp.alarmName,d.deviceName,s.camera from alarm a,smoke s,areaidarea are,alarmtype tp,devices d  "
					+ "where a.smokeMac = s.mac and ifDealAlarm=0 and are.areaId=s.areaId and d.id = s.deviceType and tp.alarmId = a.alarmType ";
			String sql = new String(sqlFire + strSql);
			sql = sql + " limit 0,100";
			Connection conn = DBConnectionManager.getConnection();
			PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
			ResultSet rs = null;
			List<AlarmInfoEntity> lists = null;
			try {
				for (int i = 1; i <= len; i++) {
					ps.setString(i, areaId.get(i - 1));
				}
				rs = ps.executeQuery();
				while (rs.next()) {
					if (lists == null) {
						lists = new ArrayList<AlarmInfoEntity>();
					}
					AlarmInfoEntity mAlarmInfoEntity = new AlarmInfoEntity();
					String cameraStr = rs.getString("cameraChannel");
					if (cameraStr != "" && cameraStr != null
							&& !"0".equals(cameraStr) && cameraStr != "0") {
						mAlarmInfoEntity.setCameraChannel(cameraStr);
					} else {
						mAlarmInfoEntity.setCameraChannel("0");
					}

					mAlarmInfoEntity.setAlarmAddress(rs.getString(4));
					mAlarmInfoEntity.setAlarmTime(rs.getString(2));
					mAlarmInfoEntity.setDeviceType(rs.getInt("deviceType"));
					mAlarmInfoEntity.setAlarmType(rs.getString("alarmName"));
					mAlarmInfoEntity.setDevMac(rs.getString(5));
					mAlarmInfoEntity.setAreaName(rs.getString(6));
					mAlarmInfoEntity.setPrincipal1(rs.getString(7));
					mAlarmInfoEntity.setPrincipalPhone1(rs.getString(8));
					mAlarmInfoEntity.setPrincipal2(rs.getString(9));
					mAlarmInfoEntity.setPrincipalPhone2(rs.getString(10));
					mAlarmInfoEntity.setNamed(rs.getString("named"));
					int devType = rs.getInt(3);
					mAlarmInfoEntity.setDeviceType(devType);
					mAlarmInfoEntity.setDevName(rs.getString("deviceName"));
					mAlarmInfoEntity.setCameraChannel(rs.getString("camera"));
					lists.add(mAlarmInfoEntity);
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
	
	public List<AlarmInfoQuery> getAllAlarmInfoByMac(String mac, AlarmInfoEntity_PC query,List<String> areaIds) {
		StringBuffer stringBuffer = new StringBuffer();
		String limit = " limit "+query.getStartRow() +","+query.getPageSize();
		String sql = "select   a.smokeMac,s.named,at.alarmName,a.alarmTime,a.ifDealAlarm,a.dealTime,a.dealPeople,a.dealDetail from alarm a,smoke s,alarmType at where a.smokeMac=s.mac and a.alarmType=at.alarmId and mac=? ";
		int len = 0;
		if(areaIds!=null){
			len=areaIds.size();
		}
		
		if(Utils.isNullStr(query.getBegintime())){
			sql +=" AND a.alarmTime >= '" +query.getBegintime()+"'";
		}
		if(Utils.isNullStr(query.getEndtime())){
			sql +=" AND a.alarmTime <= '" +query.getEndtime()+"'";
		}
		if (len == 0) {
			stringBuffer.append("");
		}if (len ==1) {
			stringBuffer.append(" and s.areaId in (?) order by a.id desc "+limit);
		}else {
			for (int i=0 ;i<len ;i ++) {
				if (i==0) {
					stringBuffer.append(" and s.areaId in (?,");
				}else if (i == (len-1)) {
					stringBuffer.append(" ?) order by a.id desc"+limit);
				}else {
					stringBuffer.append(" ?, ");
				}
			}
		}
		
		String sql2 = new String(sql+stringBuffer);
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql2);
		ResultSet rs = null;
		List<AlarmInfoQuery> list = new ArrayList<>();
		try {
			ps.setString(1, mac);
			for (int i = 2; i <= areaIds.size()+1; i++) {
				ps.setString(i, areaIds.get(i-2));
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				AlarmInfoQuery mAlarmInfoEntity = new AlarmInfoQuery();
				mAlarmInfoEntity.setMac(rs.getString(1));
				mAlarmInfoEntity.setDevName(rs.getString(2));
				mAlarmInfoEntity.setAlarmType(rs.getString(3));
				mAlarmInfoEntity.setAlarmTime(rs.getString(4));
				String ifDealAlarm=rs.getString(5);
				mAlarmInfoEntity.setDealTime(rs.getString(6));
				mAlarmInfoEntity.setDealPeople(rs.getString(7));
				String dealDetail = rs.getString("dealDetail");
				mAlarmInfoEntity.setDealDetail(dealDetail);
				//ifDealAlarm.equals("1")?"已处理":"未处理"
				mAlarmInfoEntity.setIfDealAlarm(ifDealAlarm);
				/*String count =rs.getString(8);
				mAlarmInfoEntity.setDeviceType(count);*/
				list.add(mAlarmInfoEntity);
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

	public List<AlarmInfoEntity> getAlarmInfoByDev(String dev, String alarmTime) {

		String sqlFire = "select a.alarmType,a.alarmTime,s.deviceType,s.address,s.mac,are.area,s.principal1,s.principal1Phone,s.principal2,s.principal2Phone,s.named,s.longitude,s.latitude ,a.alarmFamily,a.alarmTruth,tp.alarmName,d.deviceName,s.camera from alarm a,smoke s,areaidarea are,alarmtype tp,devices d "
				+ "where a.smokeMac = s.mac and tp.alarmId=a.alarmType and d.id = s.deviceType and ifDealAlarm=0 and are.areaId=s.areaId and s.mac=? and a.alarmTime=?";
		String sql = new String(sqlFire);
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<AlarmInfoEntity> list = new ArrayList<>();
		try {
			ps.setString(1, dev);
			ps.setString(2, alarmTime);
			rs = ps.executeQuery();
			while (rs.next()) {

				AlarmInfoEntity mAlarmInfoEntity = new AlarmInfoEntity();
				mAlarmInfoEntity.setType(rs.getInt(1));
				mAlarmInfoEntity.setAlarmAddress(rs.getString(4));
				mAlarmInfoEntity.setAlarmTime(rs.getString(2));
				mAlarmInfoEntity.setDevMac(rs.getString(5));
				mAlarmInfoEntity.setAreaName(rs.getString(6));
				mAlarmInfoEntity.setPrincipal1(rs.getString(7));
				mAlarmInfoEntity.setPrincipalPhone1(rs.getString(8));
				mAlarmInfoEntity.setPrincipal2(rs.getString(9));
				mAlarmInfoEntity.setPrincipalPhone2(rs.getString(10));
				mAlarmInfoEntity.setNamed(rs.getString(11));
				int devType = rs.getInt(3);
				mAlarmInfoEntity.setDeviceType(devType);
				mAlarmInfoEntity.setAlarmType(rs.getString("alarmName"));
				mAlarmInfoEntity.setDevName(rs.getString("deviceName"));
				mAlarmInfoEntity.setLongitude(rs.getString("longitude"));
				mAlarmInfoEntity.setLatitude(rs.getString("latitude"));
				mAlarmInfoEntity.setCameraChannel(rs.getString("camera"));
				list.add(mAlarmInfoEntity);
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

	public List<AlarmInfoEntity> getAllAlarmInfo(List<String> areaId,
			AlarmInfoEntity query) {
		StringBuffer strSql = new StringBuffer();
		int len = areaId.size();
		String limit = " limit " + query.getStartRow() + ","
				+ query.getPageSize();
		if (len == 0) {
			return null;
		}
		if (len == 1) {
			strSql.append(" and s.areaId in (?) order by a.id desc "
					+ limit);
		} else {
			for (int i = 0; i < len; i++) {
				if (i == 0) {
					strSql.append(" and s.areaId in (?, ");
				} else if (i == (len - 1)) {
					strSql.append(" ?) order by a.id desc" + limit);
				} else {
					strSql.append(" ?, ");
				}
			}
		}
		String sqlFire = "select a.alarmType,a.alarmTime,s.deviceType,s.address,s.mac,are.area,s.principal1,s.principal1Phone,s.principal2,s.principal2Phone,a.alarmFamily,a.alarmTruth from alarm a,smoke s,areaidarea are "
				+ "where a.smokeMac = s.mac and ifDealAlarm=0 and are.areaId=s.areaId ";
//		sqlFire += " and s.deviceType " + Constant.sql;
		String sql = new String(sqlFire + strSql);
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<AlarmInfoEntity> lists = null;
		try {
			for (int i = 1; i <= len; i++) {
				ps.setString(i, areaId.get(i - 1));
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				if (lists == null) {
					lists = new ArrayList<AlarmInfoEntity>();
				}
				AlarmInfoEntity mAlarmInfoEntity = new AlarmInfoEntity();
				mAlarmInfoEntity.setAlarmAddress(rs.getString(4));
				mAlarmInfoEntity.setAlarmTime(rs.getString(2));
				int alarmType = rs.getInt(1);
				mAlarmInfoEntity.setDevMac(rs.getString(5));
				mAlarmInfoEntity.setAreaName(rs.getString(6));
				mAlarmInfoEntity.setPrincipal1(rs.getString(7));
				mAlarmInfoEntity.setPrincipalPhone1(rs.getString(8));
				mAlarmInfoEntity.setPrincipal2(rs.getString(9));
				mAlarmInfoEntity.setPrincipalPhone2(rs.getString(10));
				int devType = rs.getInt(3);
				int alarmFamily = rs.getInt(11);
				int alarmTrue = rs.getInt(12);
				switch (devType) {
				case 1:
					mAlarmInfoEntity.setDeviceType(1);
					switch (alarmType) {
					case 202:
						mAlarmInfoEntity.setAlarmType(Constant.ALARMTYPE202);
						break;

					case 193:
						mAlarmInfoEntity.setAlarmType(Constant.ALARMTYPE193);
						break;
					}
					mAlarmInfoEntity.setDevName(Constant.DEVICETYPE1);
					break;
				case 2:
					mAlarmInfoEntity.setDevName(Constant.DEVICETYPE2);
					mAlarmInfoEntity.setAlarmType(Constant.ALARMTYPE202);
					mAlarmInfoEntity.setDeviceType(2);
					break;

				case 5:
					mAlarmInfoEntity.setDevName(Constant.DEVICETYPE5);
					mAlarmInfoEntity.setDeviceType(5);
					/** begin */
					switch (alarmFamily) {
					case 36:
						mAlarmInfoEntity.setAlarmType(Constant.ALARMFAMILY36);
						break;
					case 136:
						mAlarmInfoEntity.setAlarmType(Constant.ALARMFAMILY136);
						break;
					case 43:
					case 143:
						mAlarmInfoEntity.setAlarmType(Constant.ALARMFAMILY43);
						break;
					case 44:
					case 144:
						mAlarmInfoEntity.setAlarmType(Constant.ALARMFAMILY44);
						break;
					case 45:
					case 145:
						mAlarmInfoEntity.setAlarmType(Constant.ALARMFAMILY45);
						break;
					case 46:
					case 146:
						mAlarmInfoEntity.setAlarmType(Constant.ALARMFAMILY46);
						break;
					case 47:
					case 147:
						mAlarmInfoEntity.setAlarmType(Constant.ALARMFAMILY47);
						break;
					case 48:
					case 148:
						mAlarmInfoEntity.setAlarmType(Constant.ALARMFAMILY48);
						break;
					default:
						mAlarmInfoEntity.setAlarmType(Constant.ALARMtYPE12);
						break;
					}
					/** end */
					break;
				case 7:
					mAlarmInfoEntity.setDevName(Constant.DEVICETYPE7);
					mAlarmInfoEntity.setAlarmType(Constant.ALARMTYPE7);
					mAlarmInfoEntity.setDeviceType(7);
					break;
				case 8:
					mAlarmInfoEntity.setDevName(Constant.DEVICETYPE8);
					mAlarmInfoEntity.setAlarmType(Constant.ALARMtYPE8);
					mAlarmInfoEntity.setDeviceType(8);
					break;
				case 9:
					mAlarmInfoEntity.setDevName(Constant.DEVICETYPE9);
					mAlarmInfoEntity.setDeviceType(9);
					break;
				case 11:
					mAlarmInfoEntity.setDevName(Constant.DEVICETYPE11);
					mAlarmInfoEntity.setAlarmType(Constant.ALARMtYPE11);
					mAlarmInfoEntity.setDeviceType(11);
					break;
				case 12:
					mAlarmInfoEntity.setDevName(Constant.DEVICETYPE12);
					mAlarmInfoEntity.setAlarmType(Constant.ALARMtYPE12);
					mAlarmInfoEntity.setDeviceType(12);
					break;
				case 13:
					mAlarmInfoEntity.setDevName(Constant.DEVICETYPE13);
					mAlarmInfoEntity.setAlarmType(Constant.ALARMtYPE13);
					mAlarmInfoEntity.setDeviceType(13);
					break;
				case 16:
					mAlarmInfoEntity.setDevName(Constant.DEVICETYPE16);
					mAlarmInfoEntity.setAlarmType(Constant.ALARMTYPE2);
					mAlarmInfoEntity.setDeviceType(13);
					break;
				case 18:
					mAlarmInfoEntity.setDevName(Constant.DEVICETYPE18);
					mAlarmInfoEntity.setDeviceType(18);
					switch (alarmType) {
					case 202:
						mAlarmInfoEntity.setAlarmType(Constant.ALARMtYPE13);
						break;
					case 201:
						mAlarmInfoEntity.setAlarmType(Constant.ALARMtYPE201);
						break;
					case 193:
						mAlarmInfoEntity.setAlarmType(Constant.ALARMTYPE193);
						break;
					}
					break;	
				case 10:
					mAlarmInfoEntity.setDevName(Constant.DEVICETYPE10);
					mAlarmInfoEntity.setDeviceType(10);
					switch (alarmType) {
					case 209:// d1
						mAlarmInfoEntity.setAlarmType(Constant.ALARMTYPE209);
						break;

					case 218:// da
						mAlarmInfoEntity.setAlarmType(Constant.ALARMTYPE218);
						break;
					case 193:
						mAlarmInfoEntity.setAlarmType("低电压");
						break;
					case 217:
						mAlarmInfoEntity.setAlarmType("高升");
						break;
					case 210:
						mAlarmInfoEntity.setAlarmType("降低");
						break;
					default:
						mAlarmInfoEntity.setAlarmType("其他");
						break;
					}
					break;

				default: // add by lzo at 2017-5-24
					mAlarmInfoEntity.setDevName("其他");
					mAlarmInfoEntity.setDeviceType(0);
					break;
				}
				lists.add(mAlarmInfoEntity);
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

	public int getAllAlarmInfoCount(List<String> areaId, AlarmInfoEntity query) {
		StringBuffer strSql = new StringBuffer();
		int resultcount = 0;
		int len = areaId.size();
		String limit = " limit " + query.getStartRow() + ","
				+ query.getPageSize();
		if (len == 0) {
			return (Integer) null;
		}
		if (len == 1) {
			strSql.append(" and s.areaId in (?) order by a.id desc "
					+ limit);
		} else {
			for (int i = 0; i < len; i++) {
				if (i == 0) {
					strSql.append(" and s.areaId in (?, ");
				} else if (i == (len - 1)) {
					strSql.append(" ?) order by a.id desc" + limit);
				} else {
					strSql.append(" ?, ");
				}
			}
		}
		String sqlFire = "select a.alarmType,a.alarmTime,s.deviceType,s.address,s.mac,are.area,s.principal1,s.principal1Phone,s.principal2,s.principal2Phone,a.alarmFamily,a.alarmTruth from alarm a,smoke s,areaidarea are "
				+ "where a.smokeMac = s.mac and ifDealAlarm=0 and are.areaId=s.areaId ";
//		sqlFire += " and s.deviceType " + Constant.sql;
		String sql = new String(sqlFire + strSql);
		String mysql = " select count(*) as count from ( " + sql + " ) aa ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, mysql);
		ResultSet rs = null;
		try {
			for (int i = 1; i <= len; i++) {
				ps.setString(i, areaId.get(i - 1));
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				resultcount = rs.getInt("count");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return resultcount;
	}
	
	public int getAlarmInfoCount(String mac, AlarmInfoEntity_PC query,List<String> areaIds) {
		/** 区域的查询*/
		StringBuffer stringBuffer = new StringBuffer();
		int len = 0;
		if(areaIds!=null){
			len=areaIds.size();
		}
		int resultcount = 0;
		/** 主查询*/
		String sql="select a.alarmTime,a.smokeMac,a.ifDealAlarm,a.dealTime,a.alarmType,a.dealDetail,s.mac "
				+ "from alarm a,smoke s where a.smokeMAC=s.mac and a.smokeMac=?";
		if(Utils.isNullStr(query.getBegintime())){
			sql +=" AND a.alarmTime >= '" +query.getBegintime()+"'";
		}
		if(Utils.isNullStr(query.getEndtime())){
			sql +=" AND a.alarmTime <= '" +query.getEndtime()+"'";
		}
		if (len == 0) {
			return (Integer) null;
		}if (len ==1) {
			stringBuffer.append(" and s.areaId in (?) ");
		}else {
			for (int i=0 ;i<len ;i ++) {
				if (i==0) {
					stringBuffer.append(" and s.areaId in (?,");
				}else if (i == (len-1)) {
					stringBuffer.append(" ?) " );
				}else {
					stringBuffer.append(" ?, ");
				}
			}
		}
		sql=new String (sql+stringBuffer);
		
		String sql2 = " select  count(*)  as resultcount from ("+sql+") aa";
		
		/*String sql="select count(*) from alarm where smokeMac=?";
		*/
		//String sql2=new String (sql+stringBuffer);
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql2);
		ResultSet rs = null;
		try {
			ps.setString(1, mac);
			for (int i = 2; i <=areaIds.size()+1; i++) {
				ps.setString(i, areaIds.get(i-2));
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				resultcount = rs.getInt("resultcount");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return resultcount;
	}

	public List<AlarmInfoEntity> getAlarmInfoByFault(List<String> areaId) {
		String sql = "SELECT repeaterMac,faultCode,faultDevDesc,faultType,faultInfo,faultTime FROM faultinfo ORDER BY faultTime DESC LIMIT 0,1";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<AlarmInfoEntity> lists = null;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				if (lists == null) {
					lists = new ArrayList<AlarmInfoEntity>();
				}
				AlarmInfoEntity mAlarmInfoEntity = new AlarmInfoEntity();
				mAlarmInfoEntity.setDevMac(rs.getString("faultCode"));
				mAlarmInfoEntity.setDevName(rs.getString("faultInfo"));
				mAlarmInfoEntity.setAlarmTime(rs.getString("faultTime"));
				mAlarmInfoEntity.setAlarmType(rs.getString("faultType"));
				mAlarmInfoEntity.setAreaName(rs.getString("repeaterMac"));
				mAlarmInfoEntity.setNamed(rs.getString("faultDevDesc"));

				lists.add(mAlarmInfoEntity);
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

	public PrinterEntity getFaltAlarmInfo(String repeaterMac) {
		String sql = "SELECT repeaterMac,faultCode,faultDevDesc,faultType,faultInfo,faultTime FROM faultinfo where repeaterMac = ? ORDER BY faultTime DESC LIMIT 0,1";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		PrinterEntity pe = new PrinterEntity();
		try {
			ps.setString(1, repeaterMac);
			rs = ps.executeQuery();
			while (rs.next()) {
				pe.setFaultCode(rs.getString("faultCode"));
				pe.setFaultInfo(rs.getString("faultInfo"));
				pe.setFaultTime(rs.getString("faultTime"));
				pe.setFaultType(rs.getString("faultType"));
				pe.setRepeater(rs.getString("repeaterMac"));
				pe.setFaultDevDesc(rs.getString("faultDevDesc"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return pe;
	}

	public boolean updateFaultInfo(String dealUser, String dealText,
			String repeaterMac, String faultCode, String faultTime) {
		String sql = "UPDATE faultInfo SET dealUser=?,dealTime=?,dealText=? WHERE repeaterMac=? AND faultCode = ? AND faultTime<=? AND (dealUser IS NULL OR dealUser='')";
		String dealTime = GetTime.ConvertTimeByLong();
		boolean result = false;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, dealUser);
			ps.setString(2, dealTime);
			ps.setString(3, dealText);
			ps.setString(4, repeaterMac);
			ps.setString(5, faultCode);
			ps.setString(6, faultTime);
			int num = ps.executeUpdate();
			if (num > 0) {
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
	
	//@@2018.08.06
	public List<AlarmInfoEntity> getNeedDealAlarmInfo(String repeaterMac, String faultCode) {
		String sql = "SELECT * FROM faultinfo ORDER BY faultTime desc";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<AlarmInfoEntity> lists = null;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				if (lists == null) {
					lists = new ArrayList<AlarmInfoEntity>();
				}
				AlarmInfoEntity mAlarmInfoEntity = new AlarmInfoEntity();
				mAlarmInfoEntity.setDevMac(rs.getString("faultCode"));
				mAlarmInfoEntity.setDevName(rs.getString("faultInfo"));
				mAlarmInfoEntity.setAlarmTime(rs.getString("faultTime"));
				mAlarmInfoEntity.setAlarmType(rs.getString("faultType"));
				mAlarmInfoEntity.setAreaName(rs.getString("repeaterMac"));
				mAlarmInfoEntity.setNamed(rs.getString("faultDevDesc"));

				lists.add(mAlarmInfoEntity);
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
	
	public boolean updateAlarmInfo(String repeaterMac, String dealText,
			String dealUser,String alarmTime,String alarmType) {
		String sql = "UPDATE alarm SET dealPeople=?,dealTime=?,dealDetail=?,ifDealAlarm='1' WHERE repeaterMac=?  AND alarmTime<=? AND (dealPeople IS NULL OR dealPeople='')";
		String dealTime = GetTime.ConvertTimeByLong();
		boolean result = false;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, dealUser);
			ps.setString(2, dealTime);
			ps.setString(3, dealText);
			ps.setString(4, repeaterMac);
			/*ps.setString(5, alarmType);*/
			ps.setString(5, alarmTime);
			int num = ps.executeUpdate();
			if (num > 0) {
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
	
	
	public AlarmInfoEntity getAlarmInfoByMac(String mac){
		AlarmInfoEntity ai = null;
		String sql = "select mac,named,address from smoke,alarm where mac=? and mac = smokeMac";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs=null;
		try {
			ps.setString(1, mac);
			rs=ps.executeQuery();
			if(rs.next()){
				ai = new AlarmInfoEntity();
				ai.setAlarmAddress(rs.getString("address"));
				ai.setDevMac(rs.getString("mac"));
				ai.setDevName(rs.getString("named"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return ai;
	}

	@Override
	public AlarmInfoEntity getRecentAlarmInfo(String mac) {
		AlarmInfoEntity ai = null;
		String sql = "select mac,named,address,ate.alarmName,a.alarmTime,principal1,principal1Phone,principal2,principal2Phone,ai.area from smoke s,alarmtype ate, areaidarea ai," +
				"(select smokeMac,alarmType,alarmTime from alarm where smokeMac = ? ORDER BY id desc limit 1) a " +
				"where s.mac = a.smokeMac and a.alarmType = ate.alarmId and s.areaid = ai.areaid";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs=null;
		try {
			ps.setString(1, mac);
			rs=ps.executeQuery();
			if(rs.next()){
				ai = new AlarmInfoEntity();
				ai.setAlarmAddress(rs.getString("address"));
				ai.setDevMac(rs.getString("mac"));
				ai.setDevName(rs.getString("named"));
				ai.setAlarmTime(rs.getString("alarmTime"));
				ai.setAlarmType(rs.getString("alarmName"));
				ai.setPrincipal1(rs.getString("principal1"));
				ai.setPrincipalPhone1(rs.getString("principal1Phone"));
				ai.setPrincipal2(rs.getString("principal2"));
				ai.setPrincipalPhone2(rs.getString("principal2Phone"));
				ai.setAreaName(rs.getString("area"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return ai;
	}

}
