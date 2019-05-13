package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.cloudfire.dao.THDeviceInfoDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.WaterAckEntity;
import com.cloudfire.until.GetTime;

public class THDeviceInfoImpl implements THDeviceInfoDao{

	@Override
	public void addTHDeviceInfo(String mac, String t, String h) {
		String sqlString ="insert into th_info (mac,temperature,humidity,time) value(?,?,?,?)";
		Connection connection = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(connection, sqlString);
		try {
			ppst.setString(1, mac);
			ppst.setString(2, t);
			ppst.setString(3, h);
			String time = GetTime.ConvertTimeByLong();
			ppst.setString(4, time);
			ppst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(connection);
		}
	}

	
	@Override
	public void addWaterAckEntity(WaterAckEntity wae) {
		if(wae.getWaterMac()==null){
			return;
		}
		String sql = "insert into alarmthreshold (smokeMac,alarmthreshold1,alarmTime,alarmFamily) values(?,?,?,?)";
		
	}
	
	public boolean exitFile(String mac,int alarmFamily){
		boolean result = false;
		String sql = "SELECT smokemac from alarmthreshold where smokeMac = ? and alarmFamily = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, mac);
			ps.setInt(2, alarmFamily);
			rs = ps.executeQuery();
			while(rs.next()){
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
	
	public boolean exitFile(String mac,String alarmFamily){
		boolean result = false;
		String sql = "SELECT smokemac from alarmthreshold where smokeMac = ? and alarmFamily = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, mac);
			ps.setString(2, alarmFamily);
			rs = ps.executeQuery();
			while(rs.next()){
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


	@Override
	public void updateWaterAckEntity(WaterAckEntity wae) {
		String sql = "UPDATE alarmthreshold set alarmthreshold1 = ? where smokeMac = ? and alarmFamily = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
	}


	@Override
	public void updateWaterAckEntity(String mac, int alarmFamily, int waeValue) {
		String sql = "";
		String dateTime = GetTime.ConvertTimeByLong();
		if(exitFile(mac, alarmFamily)){
			sql = "UPDATE alarmthreshold set alarmthreshold1 = ?,alarmTime = ? where smokeMac = ? and alarmFamily = ?";
		}else{
			sql = "insert into alarmthreshold (alarmthreshold1,alarmTime,smokeMac,alarmFamily) values(?,?,?,?)";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setInt(1, waeValue);
			ps.setString(2, dateTime);
			ps.setString(3, mac);
			ps.setInt(4, alarmFamily);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
	}
	
	@Override
	public void updateWaterAckEntity(String mac, String alarmFamily, String waeValue) {
		String sql = "";
		String dateTime = GetTime.ConvertTimeByLong();
		if(exitFile(mac, alarmFamily)){
			sql = "UPDATE alarmthreshold set alarmthreshold1 = ?,alarmTime = ? where smokeMac = ? and alarmFamily = ?";
		}else{
			sql = "insert into alarmthreshold (alarmthreshold1,alarmTime,smokeMac,alarmFamily) values(?,?,?,?)";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, waeValue);
			ps.setString(2, dateTime);
			ps.setString(3, mac);
			ps.setString(4, alarmFamily);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
	}

}
