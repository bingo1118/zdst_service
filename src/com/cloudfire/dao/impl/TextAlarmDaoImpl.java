package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.cloudfire.dao.TextAlarmDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.DisposeAlarmEntity;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.OneKeyAlarmEntity;
import com.cloudfire.until.GetTime;

public class TextAlarmDaoImpl implements TextAlarmDao{

	public HttpRsult textAlarm(String userId, String privilege,
			String smokeMac, String info) {
		// TODO Auto-generated method stub
//		insert into  keyalarm (callerId,smoke,info,areaId) select '13622215085',s.mac,'oooooo',s.areaId from smoke s where s.mac='29461730'
		String alarmTime = GetTime.ConvertTimeByLong();
		String alarmSerialNumber = alarmTime+"--"+userId;
		String sql = "insert into  keyalarm (callerId,smoke,address,longitude,latitude,info,areaId,alarmTime,alarmSerialNumber) " +
				"select "+"?"+","+"?"+",s.address,s.longitude,s.latitude,"+"?"+",s.areaId,"
				+"?"+","+"?"+" from smoke s where s.mac= ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		HttpRsult hr = null;
		try {
			ps.setString(1, userId);
			ps.setString(2, smokeMac);
			ps.setString(3, info);
			ps.setString(4, alarmTime);
			ps.setString(5, alarmSerialNumber);
			ps.setString(6, smokeMac);
			int result = ps.executeUpdate();
			hr = new HttpRsult();
			if(result<=0){
				hr.setError("发送失败");
				hr.setErrorCode(2);
			}else{
				hr.setError("发送成功");
				hr.setErrorCode(0);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return hr;
	}

	public HttpRsult textAlarmAck(String userId, String alarmSerialNumber) {
		// TODO Auto-generated method stub
		String alarmTime = GetTime.ConvertTimeByLong();
		String sql = "insert into keyalarmack (alarmSerialNumber,ackId,ackTime) values (?,?,?) ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		HttpRsult hr = null;
		try {
			ps.setString(1, alarmSerialNumber);
			ps.setString(2, userId);
			ps.setString(3, alarmTime);
			int result = ps.executeUpdate();
			hr = new HttpRsult();
			if(result<=0){
				hr.setError("发送失败");
				hr.setErrorCode(2);
			}else{
				hr.setError("发送成功");
				hr.setErrorCode(0);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return hr;
	}

	public OneKeyAlarmEntity oneKeyAlarm(String smokeMac, String info,String userId) {
		// TODO Auto-generated method stub
		String sql = "select k.address,a.area,u.named,k.latitude,k.longitude,k.alarmSerialNumber,k.alarmTime " +
				" from areaidarea a,user u,keyalarm k " +
				" where k.smoke=? and k.areaId = a.areaId and u.userId=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		OneKeyAlarmEntity okae = null;
		try {
			ps.setString(1, smokeMac);
			ps.setString(2, userId);
			rs = ps.executeQuery();
			while(rs.next()){
				if(okae==null){
					okae = new OneKeyAlarmEntity();
				}
				okae.setAddress(rs.getString(1));
				okae.setAlarmFamily(2);
				okae.setAlarmSerialNumber(rs.getString(6));
				okae.setAlarmTime(rs.getString(7));
				okae.setAlarmType(3);
				okae.setAreaName(rs.getString(2));
				okae.setCallerId(userId);
				okae.setCallerName(rs.getString(3));
				okae.setDeviceType(6);
				okae.setInfo(info);
				okae.setLatitude(rs.getString(4));
				okae.setLongitude(rs.getString(5));
				okae.setSmoke(smokeMac);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return okae;
	}

	public DisposeAlarmEntity oneKeyAlarmACK(String userId) {
		// TODO Auto-generated method stub
		String sql = "select named,userId from user where userId=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		DisposeAlarmEntity dae = null;
		try {
			ps.setString(1, userId);
			rs = ps.executeQuery();
			while(rs.next()){
				if(dae==null){
					dae = new DisposeAlarmEntity();
				}
				dae.setPoliceName(rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return dae;
	}
	
	
	
}
