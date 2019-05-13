package com.cloudfire.dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.cloudfire.dao.FreeDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.PayRecord;
import com.cloudfire.entity.PayRecords;
import com.cloudfire.entity.Recharge;
import com.cloudfire.entity.SmartControlEntity;
import com.cloudfire.entity.SmokePay;
import com.cloudfire.entity.UserEntity;
import com.cloudfire.entity.query.DevicesFee;
import com.cloudfire.entity.query.SmokeQuery;
import com.cloudfire.until.Utils;

public class FreeDaoImpl implements FreeDao {
	
	@Override
	public Recharge getRechargeByMac(String mac) {
		String sql="select d.money, s.free, s.freeTime, s.stopTime from smoke s, devices d where s.mac=? and s.deviceType = d.id";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		Recharge recharge = null;
		try {
			if (Utils.isNullStr(mac)) {
				ps.setString(1, mac);
			}
			rs = ps.executeQuery();
			
			while (rs.next()) {
				recharge = new Recharge();
				recharge.setMac(mac);
				recharge.setSetFee(rs.getBigDecimal(1));
				recharge.setBeforeFee(rs.getBigDecimal(2));
				recharge.setFeetime(rs.getString(3));
				recharge.setStopTime(rs.getString(4));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return recharge;
	}
	
	@Override
	public List<Recharge> listRecharge() {
		//String sql="select mac, free, freeTime, stopTime from smoke where free is not null and freeTime is not null and stopTime is not null";
		//String sql="select mac, free, freeTime, stopTime from smoke";//以后换成这个查全部的，未充值过的发消息提醒，已充值过的另做处理
		String sql = "select DISTINCT s.mac, s.free, s.freeTime, s.stopTime from smoke s,payrecord p where s.mac = p.mac";
		//现在为了查询速度快，暂时用下面的，仅查充值过的
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<Recharge> rechargeList = new ArrayList<Recharge>();
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				Recharge recharge = new Recharge();
				recharge.setMac(rs.getString(1));
				//recharge.setSetFee(rs.getBigDecimal(1));
				recharge.setBeforeFee(rs.getBigDecimal(2));
				recharge.setFeetime(rs.getString(3));
				recharge.setStopTime(rs.getString(4));
				rechargeList.add(recharge);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return rechargeList;
	}

	/*@Override
	public BigDecimal getMoneyByMacAndType(String mac) {
		String sql="select d.money from smoke s, devices d where s.mac=? and s.deviceType = d.id";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		BigDecimal money = null;
		try {
			if (Utils.isNullStr(mac)) {
				ps.setString(1, mac);
			}
			rs = ps.executeQuery();
			
			while (rs.next()) {
				money = rs.getBigDecimal(1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return money;
	}*/

	@Override
	public int updateFree(String mac, BigDecimal free, String nowTime, String stopTime) {
		String sql="update smoke set freeTime=?, stopTime=?, free=?  where mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		int a = 0;
		try {
			ps.setString(1, nowTime);//现在时间
			ps.setString(2, stopTime);//一年后
			ps.setBigDecimal(3, free);
			ps.setString(4, mac);
			a = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return a;
	}

	@Override
	public void addPayRecord(PayRecord payRecord) {
		String sql="insert into payrecord(mac, free, payTime, userId, stopTime) values(?, ?, ?, ?, ?);";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		try {
			ps.setString(1, payRecord.getMac());
			ps.setBigDecimal(2, payRecord.getFree());
			ps.setString(3, payRecord.getPayTime());
			ps.setString(4, payRecord.getUserId());
			ps.setString(5, payRecord.getStopTime());
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
	}

	/*@Override
	public BigDecimal getBeforeFeeByMac(String mac) {
		String sql="select free from smoke where mac=? ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		BigDecimal money = null;
		try {
			if (Utils.isNullStr(mac)) {
				ps.setString(1, mac);
			}
			rs = ps.executeQuery();
			
			while (rs.next()) {
				money = rs.getBigDecimal(1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return money;
	}*/

	/*@Override
	public String getStopTimeByMac(String mac) {
		String sql="select stopTime from smoke where mac=? ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		String stopTime = null;
		try {
			if (Utils.isNullStr(mac)) {
				ps.setString(1, mac);
			}
			rs = ps.executeQuery();
			
			while (rs.next()) {
				stopTime = rs.getString(1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return stopTime;
	}*/

	@Override
	public boolean recordExistMac(String mac) {
		String sql="select mac from payRecord where mac=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		String recordMac = null;
		boolean bool = false;
		try {
			if (Utils.isNullStr(mac)) {
				ps.setString(1, mac);
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				recordMac = rs.getString(1);
			}
			if (Utils.isNullStr(recordMac)) {
				bool = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return bool;
	}

	@Override
	public List<String> getUserIdsByMac(String mac) {
		String sql="select DISTINCT ui.userId  from useridareaid ui, smoke s where s.areaId = ui.areaId and s.mac = ? ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<String> userIds = new ArrayList<String>();
		try {
			if (Utils.isNullStr(mac)) {
				ps.setString(1, mac);
			}
			rs = ps.executeQuery();
			
			while (rs.next()) {
				String userId = rs.getString(1);
				userIds.add(userId);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return userIds;
	}
	
	@Override
	public List<UserEntity> getUsersByMac(String mac) {
		String sql="select DISTINCT ui.userId, u.isTxt from useridareaid ui, smoke s, user u  where s.areaId = ui.areaId and ui.userId = u.userId  and u.isTxt!=0  and  s.mac = ? ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		//List<String> userIds = new ArrayList<String>();
		List<UserEntity> users = new ArrayList<UserEntity>();
		try {
			if (Utils.isNullStr(mac)) {
				ps.setString(1, mac);
			}
			rs = ps.executeQuery();
			
			while (rs.next()) {
				UserEntity user = new UserEntity();
				user.setUserId(rs.getString(1));
				user.setIstxt(rs.getInt(2));
				users.add(user);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return users;
	}

	@Override
	public int reduceFee(String mac) {
		//String sql="update smoke set freeTime=?, stopTime=?, free=?  where mac = ?";
		String sql = "update smoke set free='0.00', freeTime='', stopTime='' where mac=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		int a = 0;
		try {
			if (Utils.isNullStr(mac)) {
				ps.setString(1, mac);
			}
			a = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return a;
	}

	@Override
	public List<SmokePay> getUserAllList() {
		String sql="SELECT DISTINCT s.mac, s.stopTime, ui.userId, u.isTxt FROM smoke s, useridareaid ui, user u  WHERE stopTime IN"
				+ "(SELECT stopTime FROM smoke GROUP BY stopTime HAVING COUNT(stopTime) > 1) and s.areaId = ui.areaId and u.userId = ui.userId";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<SmokePay> smokePays = new ArrayList<SmokePay>();
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				SmokePay smokePay = new SmokePay();
				smokePay.setMac(rs.getString(1));
				smokePay.setStopTime(rs.getString(2));
				smokePay.setUserId(rs.getString(3));
				smokePay.setIsTxt(rs.getInt(4));
				smokePays.add(smokePay);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return smokePays;
	}

	@Override
	public int countDeviceType(DevicesFee query) {
		int totalcount = 0;
		String sql = "select s.deviceName, s.money, ds.devName, s.id from devices s, devsystem ds where s.devId = ds.id";
		if (query != null) {
			if (!StringUtils.isBlank(query.getDeviceName())) {
				sql += " and s.deviceName='" + query.getDeviceName()+"'";
			}
			if (!StringUtils.isBlank(query.getDevSystemName())) {
				sql += " and ds.id=" + query.getDevSystemName();
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

	@Override
	public List<DevicesFee> deviceList(DevicesFee query) {
		String sql="select s.deviceName, s.money, ds.devName, s.id from devices s, devsystem ds where s.devId = ds.id";
		if (query != null) {
			if (!StringUtils.isBlank(query.getDeviceName())) {
				sql += " and s.deviceName='" + query.getDeviceName()+"'";
			}
			if (!StringUtils.isBlank(query.getDevSystemName())) {
				sql += " and ds.id=" + query.getDevSystemName();
			}
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		
		ResultSet rs = null;
		List<DevicesFee> devicesFeeList = new ArrayList<DevicesFee>();
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				DevicesFee devicesFee = new DevicesFee();
				devicesFee.setDeviceName(rs.getString(1));
				devicesFee.setMoney(rs.getBigDecimal(2));
				devicesFee.setDevSystemName(rs.getString(3));
				devicesFee.setId(rs.getInt(4));
				devicesFeeList.add(devicesFee);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return devicesFeeList;
	}

	@Override
	public int updateMoney(String id, String money) {
		String sql="update devices set money=? where id=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		int a = 0;
		try {
			ps.setString(1, money);
			ps.setString(2, id);
			a = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return a;
	}

	@Override
	public void addPayRecords(PayRecords payRecords) {
		String sql = "insert into payrecords(orderId,payName,mac,payTime,payStatus,payMoney) values(?,?,?,?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		int rs = 0;
		
		try {
			ps.setString(1, payRecords.getOrderId());
			ps.setString(2, payRecords.getPayName());
			ps.setString(3, payRecords.getMac());
			ps.setString(4, payRecords.getPayTime());
			ps.setString(5, payRecords.getPayStatus());
			ps.setDouble(6, payRecords.getPayMoney());
			rs =ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
	}

	@Override
	public PayRecords queryRecordsByOrderId(String orderId) {
		String sql = "select orderId,payName,mac,payTime,payStatus,payMoney from payrecords where orderId = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		PayRecords p = new PayRecords();
		try {
			ps.setString(1, orderId);
			rs =ps.executeQuery();
			while(rs.next()){
				p.setOrderId(rs.getString(1));
				p.setPayName(rs.getString(2));
				p.setMac(rs.getString(3));
				p.setPayTime(rs.getString(4));
				p.setPayStatus(rs.getString(5));
				p.setPayMoney(rs.getDouble(6));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return p;
	}

	@Override
	public boolean updateCostByMac(String mac, Double cost) {
		String sql = "update devicecost set cost = ? where SmokeCostMac = ? ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		int rs = 0;
		boolean bool =false;
		try {
			ps.setDouble(1, cost);
			ps.setString(2, mac);
			rs = ps.executeUpdate();
			if(rs>0){
				bool = true ;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return bool;
	}

	@Override
	public boolean updateRecords(PayRecords payRecords) {
		String sql = "update payrecords set payStatus = ? where mac = ? and orderId =?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		int rs = 0;
		boolean bool = false;
		try {
			ps.setString(1, payRecords.getPayStatus());
			ps.setString(2, payRecords.getMac());
			ps.setString(3, payRecords.getOrderId());
			rs = ps.executeUpdate();
			if(rs>0){
				bool = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bool;
	}

	@Override
	public Double queryCostByMac(String mac) {
		String sql = "select cost from devicecost where smokeCostMac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		Double cost = 0.0;
		try {
			ps.setString(1, mac);
			rs = ps.executeQuery();
			while(rs.next()){
			cost = rs.getDouble("cost");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cost;
	}

	@Override
	public int countDetail(List<String> areaList, SmokeQuery query) {
		int len = areaList.size();
		int totalcount = 0;
		String sql = "select DISTINCT aa.area, s.mac,s.named,s.address,s.repeater,d.deviceName,pp.placeName,p.userId,s.freeTime,s.stopTime "
					+ "from smoke s, payrecord p, devices d, areaidarea aa, placetypeidplacename pp where "
					+"s.stopTime is not null and s.stopTime!='' and s.mac = p.mac and s.deviceType = d.id " 
					+"and s.areaId = aa.areaId and s.placeTypeId = pp.placeTypeId ";
		if (query != null) {
			if (!StringUtils.isBlank(query.getDeviceType())) {
				sql += " and s.deviceType=" + query.getDeviceType();
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
		sql+=" order by s.stopTime";
		
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
	public List<SmartControlEntity> detailList(List<String> areaList, SmokeQuery query) {
		String limit = " limit " + query.getStartRow() + ","
				+ query.getPageSize();
		String sql = "select DISTINCT aa.area, s.mac,s.named,s.address,s.repeater,d.deviceName,pp.placeName,p.userId,s.freeTime,s.stopTime "
				+ "from smoke s, payrecord p, devices d, areaidarea aa, placetypeidplacename pp where "
				+"s.stopTime is not null and s.stopTime!='' and s.mac = p.mac and s.deviceType = d.id " 
				+"and s.areaId = aa.areaId and s.placeTypeId = pp.placeTypeId ";
		
		int len = areaList.size();
		if (query != null) {
			if (!StringUtils.isBlank(query.getDeviceType())) {
				sql += " and s.deviceType=" + query.getDeviceType();
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
		sql+=" order by s.stopTime" + limit;
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<SmartControlEntity> lists = new ArrayList<>();
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				SmartControlEntity smartEntity = new SmartControlEntity();
				smartEntity.setCompany(rs.getString(1));
				smartEntity.setSmokeMac(rs.getString(2));
				smartEntity.setEnterprise(rs.getString(3));
				smartEntity.setAddress(rs.getString(4));
				smartEntity.setRepeaterMac(rs.getString(5));
				smartEntity.setDevType(rs.getString(6));
				smartEntity.setPlaceTypeName(rs.getString(7));
				smartEntity.setUserId(rs.getString(8));
				smartEntity.setFreeTime(rs.getString(9));
				smartEntity.setStopTime(rs.getString(10));
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
	

}
