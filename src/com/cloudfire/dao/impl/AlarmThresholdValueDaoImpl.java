package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.cloudfire.dao.AlarmThresholdValueDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.until.GetTime;

public class AlarmThresholdValueDaoImpl implements AlarmThresholdValueDao{

	public void addThresholdValue(String smokeMac, String value1,String value2,String value3,String value4,
			String repeaterMac, int alarmFamily) {
		// TODO Auto-generated method stub
		String sql = null;
		boolean result = ifExitValue(smokeMac,alarmFamily);
		Connection conn =null;
		PreparedStatement ps=null;
		String timeStr = GetTime.ConvertTimeByLong();
		try {
			if(result){
				sql = "update alarmthreshold set alarmthreshold1=?,alarmthreshold2=?," +
						"alarmthreshold3=?,alarmthreshold4=?,alarmTime=?,repeaterMac=? where smokeMac=? and alarmFamily=?";
			}else{
				sql = "insert alarmthreshold (alarmthreshold1,alarmthreshold2,alarmthreshold3," +
						"alarmthreshold4,alarmTime,repeaterMac,smokeMac,alarmFamily) values (?,?,?,?,?,?,?,?)";
			}
			conn = DBConnectionManager.getConnection();
			ps = DBConnectionManager.prepare(conn,sql);
			ps.setString(1, value1);
			ps.setString(2, value2);
			ps.setString(3, value3);
			ps.setString(4, value4);
			ps.setString(5, timeStr);
			ps.setString(6, repeaterMac);
			ps.setString(7, smokeMac);
			ps.setInt(8, alarmFamily);
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}

	public boolean ifExitValue(String smokeMac, int alarmFamily) {
		// TODO Auto-generated method stub
		String sql = "select count(*) from alarmthreshold where smokeMac= ? and alarmFamily= ? ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		int count=0;
		try {
			ps.setString(1, smokeMac);
			ps.setInt(2, alarmFamily);
			rs = ps.executeQuery();
			while(rs.next()){
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		if(count==0){
			return false;
		}else{
			return true;
		}
		
	}
	 

}
