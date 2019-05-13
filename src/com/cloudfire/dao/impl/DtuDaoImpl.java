package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.cloudfire.dao.DtuDao;
import com.cloudfire.db.DBConnectionManager;

public class DtuDaoImpl implements DtuDao {
	public static void main(String[] args) {
		DtuDaoImpl ddi = new DtuDaoImpl();
//		System.out.println(ddi.addDtuData("123",1,2.02,2,"45465"));
		System.out.println(ddi.updateDtu("6488764555700000", "2018-01-31 11:12:34",1));
//		System.out.println(ddi.addAlarmMsg("123", "890000", 12));
	}
	
	@Override
	public int addDtuData(String imei, int state,float value, int unit,String time) {
		String remark = "";
		switch (unit) {
		case 1: //水压统一为Kpa
		case 2: //水压
		case 3: //水压
			remark = "KPa";
			break;
		case 5: //水位
			remark = "M";
			break;
		case 6:
			remark = "m^3/h";
			break;
		}
		String sql = "insert into waterinfo(waterMac,status,value,remark,time) values ('"+imei+"',"+state+","+value+",'"+remark+"','"+time+"')";
		if (ifExitWaterMac(imei,value+"")) {  //若最近的一次状态相同，则不保存。
			return 0;
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		int rs = 0;
		try {
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
	public int updateDtu(String imei, String heartime,int netState) {
		String sql = "";
		if (existDtu(imei)) {
			sql = "update smoke set heartime = '" + heartime+"',netState = "+netState+
					" where mac = '"+imei+"'";  
		}
		else {
			sql = "insert into smoke(mac,heartime,netState) value ('"+imei+
					"','"+heartime+"',"+netState+")";	
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql); 
		int rs = 0;
		try {
			rs = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return rs;
	}
	
	private boolean existDtu(String imei){
		boolean exist = false;
		String sql = "select mac from smoke where mac = "+ imei;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			if (rs.next()) 
				exist = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return exist;
	}

	@Override
	public int addAlarmMsg(String imei, String time, int alarmType) {
		String sql = "insert into alarm(smokeMac,alarmTime,alarmType) values ('"+imei+"','"+time+"',"+alarmType+")";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		int rs = 0;
		try {
			rs = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return rs;
	}

	public boolean ifExitWaterMac(String waterMac,String waterLeve){
		boolean result = false;
		String sql = "SELECT waterMac,value from waterinfo where waterMac = ? ORDER BY id desc LIMIT 1";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, waterMac);
			rs = ps.executeQuery();
			while(rs.next()){
				if(Float.parseFloat(waterLeve)==Float.parseFloat(rs.getString(2))){
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
	
//	@Override
//	public double getValueByMac(String imei) {
////		String sql = "select value from "
//		return 0;
//	}

}
