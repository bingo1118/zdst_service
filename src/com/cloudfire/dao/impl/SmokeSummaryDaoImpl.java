package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.SmokeSummaryDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.BQMacEntity;
import com.cloudfire.entity.BQMacInfo;
import com.cloudfire.entity.CountValue;
import com.cloudfire.entity.NFC_infoEntity;
import com.cloudfire.entity.SmokeSummaryEntity;
import com.cloudfire.until.Constant;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.Utils;

public class SmokeSummaryDaoImpl implements SmokeSummaryDao {
	private AreaDao mAreaDao;

	public int getSmokeSummary(String userId, String privilege, String areaID,String appId,String placeTypeId,String devType) {
		// TODO Auto-generated method stub

		StringBuffer strSql = new StringBuffer();
		List<String> listNum = null;
		if (Utils.isNumeric(areaID)) {
			listNum = new ArrayList<String>();
			listNum.add(areaID);
			int aid = Integer.parseInt(areaID);
			//update 31-36 at lzo by 2017-6-9
			if(aid == 44){
				strSql.append(" areaId in (SELECT areaid FROM areaidarea WHERE parentId = ?) ");
			}else{
				strSql.append(" areaId in (?) ");
			}
		} else {
			mAreaDao = new AreaDaoImpl();
			listNum = mAreaDao.getAreaStr(userId, privilege);
			if (listNum == null) {
				return 0;
			}
			int len = listNum.size();
			if (len == 1) {
				// s.areaId in (14, 15) order by a.alarmTime desc limit 0 , 20
				strSql.append(" areaId in (?) ");
			} else {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						strSql.append(" areaId in (?, ");
					} else if (i == (len - 1)) {
						strSql.append(" ?) ");
					} else {
						strSql.append(" ?, ");
					}
				}
			}
		}
		
//		if(appId!="2"&&!"2".equals(appId)){		//add by lzo at 2017-5-25
//			strSql.append(" AND deviceType not in (11,12,13,15,18)");
//		}
				
		strSql.append(Constant.devTypeChooseSQLStatement_zdst(devType));
		
		
		if(Utils.isNumeric(placeTypeId)){
			strSql.append(" AND placeTypeId="+placeTypeId);
		}//@@liangbin 2017.08.11
		
		String sql = "select count(*) as total from smoke where ";
		
		String sqlStr = new String(sql + strSql.toString());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlStr);
		ResultSet rs = null;
		int totalNum = 0;
		try {
			int len = listNum.size();
			for (int i = 1; i <= len; i++) {
				ps.setInt(i, Integer.parseInt(listNum.get(i - 1)));
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				totalNum = rs.getInt("total");
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return totalNum;
	}

	public int getLostSmokeSummary(String userId, String privilege,
			String areaID,String appId,String placeTypeId,String devType) {
		// TODO Auto-generated method stub

		StringBuffer strSql = new StringBuffer();
		List<String> listNum = null;
		if (Utils.isNumeric(areaID)) {
			listNum = new ArrayList<String>();
			listNum.add(areaID);
			int aid = Integer.parseInt(areaID);
			if(aid == 44){
				strSql.append(" areaId in (SELECT areaid FROM areaidarea WHERE parentId = ?) ");
			}else{
				strSql.append(" areaId in (?) ");
			}
		} else {
			mAreaDao = new AreaDaoImpl();
			listNum = mAreaDao.getAreaStr(userId, privilege);
			if (listNum == null) {
				return 0;
			}
			int len = listNum.size();
			if (len == 1) {
				// s.areaId in (14, 15) order by a.alarmTime desc limit 0 , 20
				strSql.append(" areaId in (?) ");
			} else {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						strSql.append(" areaId in (?, ");
					} else if (i == (len - 1)) {
						strSql.append(" ?) ");
					} else {
						strSql.append(" ?, ");
					}
				}
			}
		}
//		if(appId!="2"&&!"2".equals(appId)){		//add by lzo at 2017-5-25
//			strSql.append(" AND deviceType not in (11,12,13)");
//		}
		
		strSql.append(Constant.devTypeChooseSQLStatement_zdst(devType));
		
		if(Utils.isNumeric(placeTypeId)){
			strSql.append(" AND placeTypeId="+placeTypeId);
		}//@@liangbin 2017.08.11
		
		String sql = "select count(*) as lostNum from smoke where netState=0 and ";
		String sqlStr = new String(sql + strSql.toString());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlStr);
		ResultSet rs = null;
		int lostNum = 0;
		try {
			int len = listNum.size();
			for (int i = 1; i <= len; i++) {
				ps.setInt(i, Integer.parseInt(listNum.get(i - 1)));
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				lostNum = rs.getInt("lostNum");
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return lostNum;
	}
	
	
	//获取低电量设备数量
	public int getLowVoltageSummary(String userId, String privilege,
			String areaID,String appId,String placeTypeId,String devType) {
		// TODO Auto-generated method stub

		StringBuffer strSql = new StringBuffer();
		List<String> listNum = null;
		if (Utils.isNumeric(areaID)) {
			listNum = new ArrayList<String>();
			listNum.add(areaID);
			int aid = Integer.parseInt(areaID);
			if(aid == 44){
				strSql.append(" areaId in (SELECT areaid FROM areaidarea WHERE parentId = ?) ");
			}else{
				strSql.append(" areaId in (?) ");
			}
		} else {
			mAreaDao = new AreaDaoImpl();
			listNum = mAreaDao.getAreaStr(userId, privilege);
			if (listNum == null) {
				return 0;
			}
			int len = listNum.size();
			if (len == 1) {
				// s.areaId in (14, 15) order by a.alarmTime desc limit 0 , 20
				strSql.append(" areaId in (?) ");
			} else {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						strSql.append(" areaId in (?, ");
					} else if (i == (len - 1)) {
						strSql.append(" ?) ");
					} else {
						strSql.append(" ?, ");
					}
				}
			}
		}
//		if(appId!="2"&&!"2".equals(appId)){		//add by lzo at 2017-5-25
//			strSql.append(" AND deviceType not in (11,12,13)");
//		}
		
		strSql.append(Constant.devTypeChooseSQLStatement_zdst(devType));
		
		if(Utils.isNumeric(placeTypeId)){
			strSql.append(" AND placeTypeId="+placeTypeId);
		}//@@liangbin 2017.08.11
		
		String sql = "select count(*) as lostNum from smoke where voltage=1 and ";
		String sqlStr = new String(sql + strSql.toString());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlStr);
		ResultSet rs = null;
		int low_valtage_Num = 0;
		try {
			int len = listNum.size();
			for (int i = 1; i <= len; i++) {
				ps.setInt(i, Integer.parseInt(listNum.get(i - 1)));
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				low_valtage_Num = rs.getInt("lostNum");
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return low_valtage_Num;
	}

	public SmokeSummaryEntity getTotalSmokeSummary(String userId,
			String privilege, String areaId,String appId,String placeTypeId,String devType) {
		// TODO Auto-generated method stub
		int allNum = getSmokeSummary(userId, privilege, areaId, appId,placeTypeId,devType);
		SmokeSummaryEntity sse = null;
		if (allNum > 0) {
			int lostNum = getLostSmokeSummary(userId, privilege, areaId, appId,placeTypeId,devType);
			int lowVoltageNum=getLowVoltageSummary(userId, privilege, areaId, appId,placeTypeId,devType);
			sse = new SmokeSummaryEntity();
			sse.setError("成功");
			sse.setErrorCode(0);
			sse.setLossSmokeNumber(lostNum);
			sse.setAllSmokeNumber(allNum);
			sse.setOnlineSmokeNumber(allNum - lostNum);
			sse.setLowVoltageNumber(lowVoltageNum);
		}else{
			sse = new SmokeSummaryEntity();
			sse.setError("成功");
			sse.setErrorCode(0);
			sse.setLossSmokeNumber(0);
			sse.setAllSmokeNumber(0);
			sse.setOnlineSmokeNumber(0);
		}
		return sse;
	}

	public void deleteBysmokeMac(String smokeMac) {
		String sql = "delete from smoke where smoke.mac=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement prepare = DBConnectionManager.prepare(conn, sql);
		try {
			prepare.setString(1, smokeMac);
			ResultSet rs = null;
			int i = prepare.executeUpdate();
			if (i > 0) {
//				System.out.println("删除一个烟感成功");
			} else {
//				System.out.println("删除失败");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			
			//DBConnectionManager.close(rs);
			DBConnectionManager.close(prepare);
			DBConnectionManager.close(conn);
		}
	}
	
	public void deleteAlarmBysmokeMac(String smokeMac) {
		String sql = "delete from alarm where smokeMac=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement prepare = DBConnectionManager.prepare(conn, sql);
		try {
			prepare.setString(1, smokeMac);
			int i = prepare.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(prepare);
			DBConnectionManager.close(conn);
		}
	}
	
	public void deleteElectricByMacs(String[] smokeMacs){
		String sqlstr = "DELETE from electricinfo where smokemac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		try {
			conn.setAutoCommit(false);
			for (int i = 0; i < smokeMacs.length; i++) {
				ps.setString(1, smokeMacs[i].trim());
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}
	
	/** 通过mac数据批量删除设备 */
	public void deleteBysmokeMacBate(String[] smokeMacs){
		deleteSmokeAlarmByMacs(smokeMacs);
		deleteElectricByMacs(smokeMacs);
		deleteAckDevice(smokeMacs);
		deleteWaterInfo(smokeMacs);
		deleteThInfo(smokeMacs);
		deleteG_proofgas(smokeMacs);
		delThree_electricinfo(smokeMacs);
		String sql = "delete from smoke where mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sql);
		try {
			conn.setAutoCommit(false);
			for(int i=0;i<smokeMacs.length;i++){				
					ppst.setString(1, smokeMacs[i].trim());
					ppst.addBatch();
			} 
			ppst.executeBatch();
			conn.commit();
		}catch (SQLException e) {			
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}finally{
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
	}
	
	public void delThree_electricinfo(String[] macs){
		String sql = "DELETE from threeelectricinfo where smokeImei = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sql);
		try {
			conn.setAutoCommit(false);
			for(int i=0;i<macs.length;i++){				
					ppst.setString(1, macs[i].trim());
					ppst.addBatch();
			} 
			ppst.executeBatch();
			conn.commit();
		}catch (SQLException e) {			
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}finally{
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
	}
	
	public void delElecRec(String[] macs){
		String sql = "DELETE from electric_change_history where mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sql);
		try {
			conn.setAutoCommit(false);
			for(int i=0;i<macs.length;i++){				
					ppst.setString(1, macs[i].trim());
					ppst.addBatch();
			} 
			ppst.executeBatch();
			conn.commit();
		}catch (SQLException e) {			
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}finally{
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
	}

	public void addDelDevMac(String[] delMac,String userid,HttpServletRequest request){
		String delTime = GetTime.ConvertTimeByLong();
		String ipaddress = Utils.getIpAddress(request);
		String sql = " insert into deleterecord(userid,delMac,dtime,address)VALUES(?,?,?,?)";
		int[] rs;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			conn.setAutoCommit(false);
			ps.setString(1, userid);
			ps.setString(3, delTime);
			ps.setString(4, ipaddress);
			for(int i=0;i<delMac.length;i++){	
				ps.setString(2, delMac[i].trim());
				ps.addBatch();
			} 
			rs = ps.executeBatch();
			conn.commit();
		}catch (SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}
	
	public int deleteWaterInfo(String[] waterMacs){
		String sqlstr = "DELETE FROM waterinfo WHERE waterMac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		int[] result = null;
		try {
			conn.setAutoCommit(false);
			for(int i = 0;i<waterMacs.length;i++){
				ps.setString(1, waterMacs[i]);
				ps.addBatch();
			}
			result = ps.executeBatch();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result.length;
	}
	
	public int deleteThInfo(String[] waterMacs){
		String sqlstr = "DELETE FROM th_info WHERE mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		int[] result = null;
		try {
			conn.setAutoCommit(false);
			for(int i = 0;i<waterMacs.length;i++){
				ps.setString(1, waterMacs[i]);
				ps.addBatch();
			}
			result = ps.executeBatch();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result.length;
	}
	
	public int deleteG_proofgas(String[] waterMacs){
		String sqlstr = "DELETE FROM g_proofgas WHERE proofGasMac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		int[] result = null;
		try {
			conn.setAutoCommit(false);
			for(int i = 0;i<waterMacs.length;i++){
				ps.setString(1, waterMacs[i]);
				ps.addBatch();
			}
			result = ps.executeBatch();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result.length;
	}
	
	public int deleteAckDevice(String[] deviceMac){
		String sqlstr = "DELETE FROM ackdevice WHERE deviceMac = ? or soundMac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		int[] result = null;
		try {
			conn.setAutoCommit(false);
			for(int i = 0;i<deviceMac.length;i++){
				ps.setString(1, deviceMac[i]);
				ps.setString(2, deviceMac[i]);
				ps.addBatch();
			}
			result = ps.executeBatch();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result.length;
	}
	
	public void deleteSmokeAlarmByMacs(String[] smokeMacs){
		for (int i = 0; i < smokeMacs.length; i++) {
			deleteAlarmBysmokeMac(smokeMacs[i]);
			/*Connection conn = DBConnectionManager.getConnection();
			PreparedStatement ppst = null;
			ResultSet rs = null;
			try {
				String sql = " select count(*) as count1 from alarm where smokeMac =? ";
				ppst = DBConnectionManager.prepare(conn, sql);
				ppst.setString(1, smokeMacs[i]);
				rs = ppst.executeQuery();
				while (rs.next()) {
					int count = rs.getInt("count1");
					if (count > 0) {
						deleteAlarmBysmokeMac(smokeMacs[i]);
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				DBConnectionManager.close(rs);
				DBConnectionManager.close(ppst);
				DBConnectionManager.close(conn);
			}*/
		}
		
	}

	/**
	 * 通过烟感MacID判断烟感是否存在
	 * */
	@Override
	public boolean selectSmokeMacById(String smokeMac) {
		// TODO Auto-generated method stub
		String sql = "select * from bqMac where bqMac.deviceId=?";
		boolean flag = false;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement prepare = DBConnectionManager.prepare(conn, sql);
		try {
			prepare.setString(1, smokeMac);
			ResultSet rs = null;
			rs = prepare.executeQuery();
			if (rs.next()) {
				flag = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			//DBConnectionManager.close(conn);
			DBConnectionManager.close(prepare);
			DBConnectionManager.close(conn);

		}
		return flag;
	}

	@Override
	public void insertSmokeMac(BQMacEntity bqMac) {
		// TODO Auto-generated method stub
		/*
		 * String
		 * sql="insert into smoke (named,mac,address,longitude,latitude,"+
		 * " placeAddress,placeTypeId,principal1,principal1Phone,principal2,principal2Phone,"
		 * +
		 * "areaId,netState,time,repeater,camera,deviceType,addUserId,ifAlarm,characterId)"
		 * + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		 */
		String sql = "insert into bqMac(id,equipName,equipType,deviceId,projectName,location,status,createTime)"
				+ " values(DEFAULT,?,?,?,?,?,?,?) ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, bqMac.getNamed());
			ps.setString(2, bqMac.getDevicetype());
			ps.setString(3, bqMac.getDeviceId());
			ps.setString(4, bqMac.getProjectName());
			ps.setString(5, bqMac.getAddress());
			ps.setInt(6, bqMac.getStatus());
			ps.setString(7, bqMac.getCreateTime());
			/*
			 * ps.setString(1, smoke.getName()); ps.setString(2,
			 * smoke.getMac()); ps.setString(3, smoke.getAddress());
			 * ps.setString(4, smoke.getLongitude()); ps.setString(5,
			 * smoke.getLatitude()); ps.setString(6, smoke.getPlaceeAddress());
			 * ps.setInt(7, Integer.parseInt(smoke.getPlaceTypeId()));
			 * ps.setString(8, smoke.getPrincipal1()); ps.setString(9,
			 * smoke.getPrincipal1Phone()); ps.setString(10,
			 * smoke.getPrincipal2()); ps.setString(11,
			 * smoke.getPrincipal2Phone()); ps.setInt(12, smoke.getAreaId());
			 * ps.setInt(13, smoke.getNetState()); ps.setString(14,
			 * smoke.getAddTime()); ps.setString(15, smoke.getRepeater());
			 * String cameId=""; if(smoke.getCamera()!=null){
			 * cameId=smoke.getCamera().getCameraId(); } ps.setString(16,
			 * cameId);//camera ps.setInt(17, smoke.getDeviceType());
			 * ps.setString(18, smoke.getAddUserId()); ps.setInt(19,
			 * smoke.getIfDealAlarm()); ps.setInt(20, smoke.getCharacterId());
			 */
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(conn);
			DBConnectionManager.close(ps);

		}
	}

	@Override
	public boolean selectEleInfoByTime(String deviceId,String createTime) {
		// TODO Auto-generated method stub
		String sql = "select * from bqEletricinfo info where info.createtime=? and info.bqEleMac=?";
		boolean flag = false;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement prepare = DBConnectionManager.prepare(conn, sql);
		try {
			prepare.setString(1, createTime);
			prepare.setString(2, deviceId);
			ResultSet rs = null;
			rs = prepare.executeQuery();
			if (rs.next()) {
				flag = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(conn);
			DBConnectionManager.close(prepare);

		}
		return flag;
	}

	@Override
	public boolean selectEleInfoByTimeValue(String createTime,
			String valueType, String value) {
		// TODO Auto-generated method stub
		String sql = "select * from bqEletricinfo info where info.createtime=? and info."
				+ valueType + "=" + value;
		boolean flag = false;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement prepare = DBConnectionManager.prepare(conn, sql);
		try {
			prepare.setString(1, createTime);
			ResultSet rs = null;
			rs = prepare.executeQuery();
			if (rs.next()) {
				flag = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(conn);
			DBConnectionManager.close(prepare);

		}
		return flag;
	}

	@Override
	public void insertByType(BQMacEntity bqMac, String valueType, String value,
			String createTime) {
		// TODO Auto-generated method stub
		String sql = "insert into bqEletricinfo(bqEleMac," + valueType
				+ ",createTime) values(?,?,?)";
		boolean flag = false;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement prepare = DBConnectionManager.prepare(conn, sql);
		try {
			prepare.setString(1, bqMac.getDeviceId());
			prepare.setString(2, value);
			prepare.setString(3, createTime);
			// ResultSet rs = null;
			int num = prepare.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(conn);
			DBConnectionManager.close(prepare);

		}
	}

	@Override
	public void updateByType(BQMacEntity bqMac, String valueType, String value,
			String createTime) {
		// TODO Auto-generated method stub
		String sql = "update bqeletricinfo info set info." + valueType + "='"
				+ value + "' where info.createTime='" + createTime + "'"
				+ " and info.bqEleMac ='" + bqMac.getDeviceId() + "'";
		boolean flag = false;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement prepare = DBConnectionManager.prepare(conn, sql);
		try {
			// ResultSet rs = null;
			int num = prepare.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(conn);
			DBConnectionManager.close(prepare);

		}
	}

	@Override
	public void insertNowData(BQMacInfo info) {
		// TODO Auto-generated method stub
		String mysql ="insert into bqeletricinfo values(DEFAULT,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement prepare = DBConnectionManager.prepare(conn, mysql);
		try {
			prepare.setString(1, info.getBqEleMac());
			prepare.setString(2, info.getTemperature1());
			prepare.setString(3, info.getTemperature2());
			prepare.setString(4, info.getTemperature3());
			prepare.setString(5, info.getTemperature4());
			prepare.setString(6, info.getEleCurrent1());
			prepare.setString(7, info.getEleCurrent2());
			prepare.setString(8, info.getEleCurrent3());
			prepare.setString(9, info.getVoltage1());
			prepare.setString(10, info.getVoltage2());
			prepare.setString(11, info.getVoltage3());
			prepare.setString(12, info.getLeakEleCurrent1());
			prepare.setString(13, info.getCreateTimeString());
			ResultSet rs = null;
			int num = prepare.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(conn);
			DBConnectionManager.close(prepare);

		}
	}

	@Override
	public List<BQMacInfo> getDataById(String deviceId) {
		// TODO Auto-generated method stub
		String mysql ="select * from bqeletricinfo where bqEleMac=? order by createTime desc";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement prepare = DBConnectionManager.prepare(conn, mysql);
		List<BQMacInfo> list=new ArrayList<BQMacInfo>();
		try {
			prepare.setString(1, deviceId);
			ResultSet rs = null;
			rs = prepare.executeQuery();
			BQMacInfo info=null;
			while (rs.next()) {
				info=new BQMacInfo();
				info.setBqEleMac(rs.getString(2));
				info.setTemperature1(rs.getString(3));
				info.setTemperature2(rs.getString(4));
				info.setTemperature3(rs.getString(5));
				info.setTemperature4(rs.getString(6));
				info.setEleCurrent1(rs.getString(7));
				info.setEleCurrent2(rs.getString(8));
				info.setEleCurrent3(rs.getString(9));
				info.setVoltage1(rs.getString(10));
				info.setVoltage2(rs.getString(11));
				info.setVoltage3(rs.getString(12));
				info.setLeakEleCurrent1(rs.getString(13));
				info.setCreateTimeString(rs.getString(14));
				list.add(info);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(conn);
			DBConnectionManager.close(prepare);

		}
		return list;
	}

	@Override
	public List<BQMacEntity> getAllBQMacForName(List<String> areaIds) {
		// TODO Auto-generated method stub
		String strSql="select * from bqmac b   ";
		int len = areaIds.size();
		//group by b.projectName order by b.id
		//区域的查询
				if(len==0){
					return  null;
				}
				if(len==1){
					strSql += " where b.areaId in ('"+areaIds.get(0)+"') group by b.projectName order by b.id " ;
				}if(len>1){
					for(int i=0;i<len;i++){
						if(i==0){
							strSql +=" where b.areaId in ('"+areaIds.get(i)+"', ";
						}else if(i==(len-1)){
							strSql +=" "+areaIds.get(i)+") group by b.projectName order by b.id " ;
						}else{
							strSql +=" "+areaIds.get(i)+",";
						}
					}
				}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement prepare = DBConnectionManager.prepare(conn, strSql);
		ResultSet rs = null;
		BQMacEntity bqMacEntity=null;
		List<BQMacEntity> list=new ArrayList<BQMacEntity>();
		try {
			rs=prepare.executeQuery();
			while(rs.next()){
				bqMacEntity=new BQMacEntity();
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
			DBConnectionManager.close(conn);
			DBConnectionManager.close(prepare);

		}
		return list;
	}
	
	
	
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>by liangbin 2017.07.19
	@Override
	public SmokeSummaryEntity getTotalDevSummary(String userId,
			String privilege, String parentId,String areaId, String placeTypeId,String devType) {//devType->1：三小场所  2：重点单位  3：电气防火  4：消防物联
		int allNum = getDevSummary(userId, privilege,parentId, areaId, placeTypeId,devType);
		SmokeSummaryEntity sse = null;
		if (allNum > 0) {
			int lostNum = getLostDevSummary(userId, privilege, parentId,areaId, placeTypeId,devType);
			int lowVoltageNum = getLowVoltageSummary(userId, privilege, parentId,areaId, placeTypeId,devType);
			sse = new SmokeSummaryEntity();
			sse.setError("成功");
			sse.setErrorCode(0);
			sse.setLossSmokeNumber(lostNum);
			sse.setAllSmokeNumber(allNum);
			sse.setOnlineSmokeNumber(allNum - lostNum);
			sse.setLowVoltageNumber(lowVoltageNum);
		}else{
			sse = new SmokeSummaryEntity();
			sse.setError("成功");
			sse.setErrorCode(0);
			sse.setLossSmokeNumber(0);
			sse.setAllSmokeNumber(0);
			sse.setOnlineSmokeNumber(0);
		}

		return sse;
	}

	@Override
	public int getDevSummary(String userId, String privilege,String parentId, String areaId,String placeTypeId,
			String devType) {
		StringBuffer strSql = new StringBuffer();
		mAreaDao=new AreaDaoImpl();
		List<String> listNum = mAreaDao.getAreaStr(userId,privilege);
		boolean isAllArea = false;
		if(parentId!=null&&parentId.length()>0){
			if(privilege.equals("4")){
				strSql.append(" areaId IN (SELECT areaid FROM areaidarea WHERE parentId = "+parentId);
				strSql.append(" )");
			}else{
				strSql.append(" areaId IN (select a.areaId from areaidarea a,useridareaid u WHERE a.areaId=u.areaId AND u.userId="+userId+" and a.parentId="+parentId);
				strSql.append(" )");
			}
		}else if(areaId!=null&&areaId.length()>0){
			isAllArea = false;
			int areaIdInt = Integer.parseInt(areaId);
			if(areaIdInt==44){
				strSql.append(" areaId IN (SELECT areaid FROM areaidarea WHERE parentId = 44");
				strSql.append(" )");
			}else{
				strSql.append(" areaId = "+areaIdInt);
			}
		}else{
			isAllArea = true;
			int len = listNum.size();
			if(len==1){
				strSql.append(" areaId in (?)");
			}else{
				for(int i=0;i<len;i++){
					if(i==0){
						strSql.append(" areaId in (?, ");
					}else if(i==(len-1)){
						strSql.append(" ?)");
					}else{
						strSql.append(" ?, ");
					}
				}
			}
		}
//		if (Utils.isNumeric(areaId)) {
//			listNum = new ArrayList<String>();
//			listNum.add(areaId);
//			int aid = Integer.parseInt(areaId);
//			//update 31-36 at lzo by 2017-6-9
//			if(aid == 44){
//				strSql.append(" areaId in (SELECT areaid FROM areaidarea WHERE parentId = ?) ");
//			}else{
//				strSql.append(" areaId in (?) ");
//			}
//		} else {
//			mAreaDao = new AreaDaoImpl();
//			listNum = mAreaDao.getAreaStr(userId, privilege);
//			if (listNum == null) {
//				return 0;
//			}
//			int len = listNum.size();
//			if (len == 1) {
//				// s.areaId in (14, 15) order by a.alarmTime desc limit 0 , 20
//				strSql.append(" areaId in (?) ");
//			} else {
//				for (int i = 0; i < len; i++) {
//					if (i == 0) {
//						strSql.append(" areaId in (?, ");
//					} else if (i == (len - 1)) {
//						strSql.append(" ?) ");
//					} else {
//						strSql.append(" ?, ");
//					}
//				}
//			}
//		}
		
		strSql.append(Constant.devTypeChooseSQLStatement(devType));
		
//		if(devType=="1"||"1".equals(devType)){		//by liangbin
//			strSql.append(" AND deviceType not in (5,10,11,12,13,14,15,18,19,25,119,124,125) AND mac<>repeater ");
//		}else if(devType=="3"||"3".equals(devType)){		
//			strSql.append(" AND deviceType in (5)");
//		}else if(devType=="4"||"4".equals(devType)){		
//			strSql.append(" AND deviceType in (10,11,12,13,15,18,19,25,42,124,125)");
//		}else if(devType=="2"||"2".equals(devType)){		
//			strSql.append(" AND (mac=repeater or deviceType=119) ");
//		}
		
		if(Utils.isNumeric(placeTypeId)){
			strSql.append(" AND placeTypeId="+placeTypeId);
		}//@@liangbin 2017.07.20
		
		String sql = "select count(*) as total from smoke where ";
		
		String sqlStr = new String(sql + strSql.toString());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlStr);
		ResultSet rs = null;
		int totalNum = 0;
		try {
//			int len = listNum.size();
//			for (int i = 1; i <= len; i++) {
//				ps.setInt(i, Integer.parseInt(listNum.get(i - 1)));
//			}
			if(isAllArea){
				int len = listNum.size();
				for(int i=1;i<=len;i++){
					ps.setInt(i, Integer.parseInt(listNum.get(i-1)));
				}
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				totalNum = rs.getInt("total");
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return totalNum;
	}

	@Override
	public int getLostDevSummary(String userId, String privilege,String parentId,
			String areaId, String placeTypeId,String devType) {
		StringBuffer strSql = new StringBuffer();
//		List<String> listNum = null;
//		if (Utils.isNumeric(areaId)) {
//			listNum = new ArrayList<String>();
//			listNum.add(areaId);
//			int aid = Integer.parseInt(areaId);
//			if(aid == 44){
//				strSql.append(" areaId in (SELECT areaid FROM areaidarea WHERE parentId = ?) ");
//			}else{
//				strSql.append(" areaId in (?) ");
//			}
//		} else {
//			mAreaDao = new AreaDaoImpl();
//			listNum = mAreaDao.getAreaStr(userId, privilege);
//			if (listNum == null) {
//				return 0;
//			}
//			int len = listNum.size();
//			if (len == 1) {
//				// s.areaId in (14, 15) order by a.alarmTime desc limit 0 , 20
//				strSql.append(" areaId in (?) ");
//			} else {
//				for (int i = 0; i < len; i++) {
//					if (i == 0) {
//						strSql.append(" areaId in (?, ");
//					} else if (i == (len - 1)) {
//						strSql.append(" ?) ");
//					} else {
//						strSql.append(" ?, ");
//					}
//				}
//			}
//		}
		
		List<String> listNum = mAreaDao.getAreaStr(userId,privilege);
		boolean isAllArea = false;
		if(parentId!=null&&parentId.length()>0){
			if(privilege.equals("4")){
				strSql.append(" areaId IN (SELECT areaid FROM areaidarea WHERE parentId = "+parentId);
				strSql.append(" )");
			}else{
				strSql.append(" areaId IN (select a.areaId from areaidarea a,useridareaid u WHERE a.areaId=u.areaId AND u.userId="+userId+" and a.parentId="+parentId);
				strSql.append(" )");
			}
		}else if(areaId!=null&&areaId.length()>0){
			isAllArea = false;
			int areaIdInt = Integer.parseInt(areaId);
			if(areaIdInt==44){
				strSql.append(" areaId IN (SELECT areaid FROM areaidarea WHERE parentId = 44");
				strSql.append(" )");
			}else{
				strSql.append(" areaId = "+areaIdInt);
			}
		}else{
			isAllArea = true;
			int len = listNum.size();
			if(len==1){
				strSql.append(" areaId in (?)");
			}else{
				for(int i=0;i<len;i++){
					if(i==0){
						strSql.append(" areaId in (?, ");
					}else if(i==(len-1)){
						strSql.append(" ?)");
					}else{
						strSql.append(" ?, ");
					}
				}
			}
		}
		
		strSql.append(Constant.devTypeChooseSQLStatement(devType));
		
//		if(devType=="1"||"1".equals(devType)){		//by liangbin
//			strSql.append(" AND deviceType not in (5,10,11,12,13,14,15,18,19,25,119,124,125) AND mac<>repeater ");
//		}else if(devType=="3"||"3".equals(devType)){		
//			strSql.append(" AND deviceType in (5)");
//		}else if(devType=="4"||"4".equals(devType)){		
//			strSql.append(" AND deviceType in (10,11,12,13,15,18,19,25,42,124,125)");
//		}else if(devType=="2"||"2".equals(devType)){		
//			strSql.append(" AND (mac=repeater or deviceType=119) ");
//		}
		
		if(Utils.isNumeric(placeTypeId)){
			strSql.append(" AND placeTypeId="+placeTypeId);
		}//@@liangbin 2017.07.20
		
		String sql = "select count(*) as lostNum from smoke where netState=0 and ";
		String sqlStr = new String(sql + strSql.toString());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlStr);
		ResultSet rs = null;
		int lostNum = 0;
		try {
//			int len = listNum.size();
//			for (int i = 1; i <= len; i++) {
//				ps.setInt(i, Integer.parseInt(listNum.get(i - 1)));
//			}
			if(isAllArea){
				int len = listNum.size();
				for(int i=1;i<=len;i++){
					ps.setInt(i, Integer.parseInt(listNum.get(i-1)));
				}
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				lostNum = rs.getInt("lostNum");
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return lostNum;
	}

	@Override
	public SmokeSummaryEntity getTotalNFCSummary(String userId,String privilege,String areaId,String period,String devicetype) {
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("SELECT * FROM( SELECT s.uid,s.areaid,s.area,s.deviceType,s.deviceName,s.address,s.longitude,s.latitude,r.devicestate,r.memo,r.endTime ");
		sqlstr.append(" FROM(SELECT n.uid,n.areaid,a.area,n.deviceType,n.deviceName,n.address,n.longitude,n.latitude ");
		sqlstr.append(" from nfcinfo n,areaidarea a where 1 = 1");
		CountValue cv = new CountValue();
		int macNum = 0;//设备总数
		int netStaterNum = 0; //合格状态1数量
		int noNetStater = 0; //不合格状态2数量
		int otherNum = 0;//待检查状态0数量
		if(areaId!=null&&areaId.length()>0){
			sqlstr.append(" and a.areaId = ?");
		}else{
			if(!privilege.equals("4")){
				sqlstr.append(" and a.areaId IN (select areaId from useridareaid WHERE userId="+userId);
				sqlstr.append(" )");
			}//@@9.29
		}
		if(devicetype!=null&&devicetype.length()>0){
			sqlstr.append(" and n.deviceType="+devicetype+" ");//@@1107
		}
		sqlstr.append(" and a.areaId = n.areaid) as s ");
		sqlstr.append(" LEFT JOIN nfcrecord r on r.uuid = s.uid AND r.endTime <? AND r.endTime>? ");
		sqlstr.append(" ORDER BY endTime DESC)as cc GROUP BY uid");
		String getTime = GetTime.ConvertTimeByLong();
		String endTime_1 = getTime.substring(0,8)+"01 00:00:01";
		String endTime_2 = getTime.substring(0,8)+"31 23:59:59";
		Calendar calendar=Calendar.getInstance(Locale.CHINA);//@@10.23
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		switch (period) {
		case "0"://every month
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			endTime_1=dateFormat.format(calendar.getTime())+" 00:00:01";
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			endTime_2=dateFormat.format(calendar.getTime())+" 23:59:59";
			break;
		case "1"://every week
			calendar.set(Calendar.DAY_OF_WEEK, 2);
			endTime_1=dateFormat.format(calendar.getTime())+" 00:00:01";
			calendar.add(Calendar.DATE, 6);
			endTime_2=dateFormat.format(calendar.getTime())+" 23:59:59";
			break;
		case "2"://everyday
			endTime_1=dateFormat.format(calendar.getTime())+" 00:00:01";
			endTime_2=dateFormat.format(calendar.getTime())+" 23:59:59";
			break;
		default:
			break;
		}//@@10.23
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr.toString());
		ResultSet rs = null;
		try {
			if(areaId!=null&&areaId.length()>0){
				ps.setString(1, areaId);
				ps.setString(2, endTime_2);
				ps.setString(3, endTime_1);
			}else{
				ps.setString(1, endTime_2);
				ps.setString(2, endTime_1);
			}
			int row =0;
			rs = ps.executeQuery();
			while(rs.next()){
				NFC_infoEntity nfc = new NFC_infoEntity();
				nfc.setRow(++row);
				int states = rs.getInt("devicestate");
				if(states == 0){
					nfc.setDevicestate("待检");
					otherNum++;
				}else if(states == 1){
					nfc.setDevicestate("合格");
					netStaterNum++;
				}else{
					nfc.setDevicestate("不合格");
					noNetStater++;
				}
				macNum = nfc.getRow();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		
		SmokeSummaryEntity sse = null;
		if (macNum > 0) {
			sse = new SmokeSummaryEntity();
			sse.setError("成功");
			sse.setErrorCode(0);
			sse.setLossSmokeNumber(otherNum);
			sse.setAllSmokeNumber(macNum);
			sse.setOnlineSmokeNumber(netStaterNum);
		}else{
			sse = new SmokeSummaryEntity();
			sse.setError("成功");
			sse.setErrorCode(0);
			sse.setLossSmokeNumber(0);
			sse.setAllSmokeNumber(0);
			sse.setOnlineSmokeNumber(0);
		}

		return sse;
	}

	@Override
	public SmokeSummaryEntity getSmokeSummaryTwo(String areaId, String devType) {
		// TODO Auto-generated method stub
		SmokeSummaryEntity sse=new SmokeSummaryEntity();
		int allSmokeNum=getAllSmokeNum(areaId,devType);
		int lossSmokeNum=getLossSmokeNum(areaId,devType);
		int onlineNum=allSmokeNum-lossSmokeNum;
		sse.setAllSmokeNumber(allSmokeNum);
		sse.setLossSmokeNumber(lossSmokeNum);
		sse.setOnlineSmokeNumber(onlineNum);
		sse.setErrorCode(0);
		sse.setError("成功");
		return sse;
	}
	
	private  int getAllSmokeNum(String areaId,String devType){
		int smokeNumber = 0;
		/** 区域的代码 */		
		
		String allSQL = "select count(*) as number from smoke where deviceType= ?  and  areaId=?";
		
		
		int deviceType = Integer.parseInt(devType);
		 Connection conn = DBConnectionManager.getConnection();
		 PreparedStatement ppst = DBConnectionManager.prepare(conn, allSQL);
		 ResultSet rs = null;
		 try {
			ppst.setInt(1, deviceType);
			ppst.setInt(2, Integer.parseInt(areaId));
			rs = ppst.executeQuery();
			while(rs.next()){
				smokeNumber = rs.getInt("number");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return smokeNumber;
	}
	private  int getLossSmokeNum(String areaId,String devType){
		int smokeNumber = 0;
		/** 区域的代码 */		
		
		String allSQL = "select count(*) as number from smoke where netState=0 and deviceType= ?  and  areaId=?";
		
		
		int deviceType = Integer.parseInt(devType);
		 Connection conn = DBConnectionManager.getConnection();
		 PreparedStatement ppst = DBConnectionManager.prepare(conn, allSQL);
		 ResultSet rs = null;
		 try {
			ppst.setInt(1, deviceType);
			ppst.setInt(2, Integer.parseInt(areaId));
			rs = ppst.executeQuery();
			while(rs.next()){
				smokeNumber = rs.getInt("number");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return smokeNumber;
	}

	@Override
	public String getRepeaterMacBySmokeMac(String smokeMac) {
		String sqlstr = "select repeater from smoke where mac = ?";
		String repeaterMac = "";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		try {
			ps.setString(1, smokeMac);
			rs = ps.executeQuery();
			while(rs.next()){
				repeaterMac = rs.getString(1);
			}
		} catch (Exception e) {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return repeaterMac;
	}


}
