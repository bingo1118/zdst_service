package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.cloudfire.dao.DealAlarmDao;
import com.cloudfire.dao.FromRepeaterAlarmDao;
import com.cloudfire.dao.LoginDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.LoginEntity;
import com.cloudfire.until.GetTime;

public class DealAlarmDaoImpl implements DealAlarmDao{
	private LoginDao mLoginDao;
	private FromRepeaterAlarmDao mFromRepeaterAlarmDao;

	public HttpRsult dealAlarm(String userId, String dealPeople,String mac,String dealDetail) {
		// TODO Auto-generated method stub
		mFromRepeaterAlarmDao = new FromRepeaterAlarmDaoImpl();
		mFromRepeaterAlarmDao.updateSmokeAlarm(mac, 1);
		mLoginDao = new LoginDaoImpl();
		LoginEntity le = mLoginDao.login(userId);
		String dealName=null;
		if(le!=null){
			dealName = le.getName();
			if(dealName==null){
				dealName=userId;
			}
		}else{
			dealName=userId;
		}
		String dealTime = GetTime.ConvertTimeByLong();
		String loginSql = "update alarm set dealUserId = ?,dealUserName=?,ifDealAlarm= ?,dealTime= ?,dealDetail = ? ,dealPeople= ? where smokeMac = ? and ifDealAlarm=0";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,loginSql);
		HttpRsult hr = null;
		try {
			ps.setString(1, userId);
			ps.setString(2, dealName);
			ps.setInt(3, 1);
			ps.setString(4, dealTime);
			ps.setString(5, dealDetail);
			ps.setString(6, dealPeople);
			ps.setString(7, mac);
			int rs = ps.executeUpdate();
			hr = new HttpRsult();
			if(rs==0){
				hr.setError("报警消息已处理");
				hr.setErrorCode(0);
			}else{
				hr.setError("处理报警消息成功");
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
	
	public HttpRsult dealAlarm(String userId, String dealPeople,String mac,String dealDetail,String alarmTruth) {
		// TODO Auto-generated method stub
		mFromRepeaterAlarmDao = new FromRepeaterAlarmDaoImpl();
		mFromRepeaterAlarmDao.updateSmokeAlarm(mac, 1);
		mLoginDao = new LoginDaoImpl();
		LoginEntity le = mLoginDao.login(userId);
		String dealName=null;
		if(le!=null){
			dealName = le.getName();
			if(dealName==null){
				dealName=userId;
			}
		}else{
			dealName=userId;
		}
		String dealTime = GetTime.ConvertTimeByLong();
		String loginSql = "update alarm set dealUserId = ?,dealUserName=?,ifDealAlarm= ?,dealTime= ?,dealDetail = ? ,dealPeople= ?,alarmTruth=? where smokeMac = ? and ifDealAlarm=0";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,loginSql);
		HttpRsult hr = null;
		try {
			ps.setString(1, userId);
			ps.setString(2, dealName);
			ps.setInt(3, 1);
			ps.setString(4, dealTime);
			ps.setString(5, dealDetail);
			ps.setString(6, dealPeople);
			ps.setString(7, alarmTruth);
			ps.setString(8, mac);
			int rs = ps.executeUpdate();
			hr = new HttpRsult();
			if(rs==0){
				hr.setError("报警消息已处理");
				hr.setErrorCode(0);
			}else{
				hr.setError("处理报警消息成功");
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
	
	public HttpRsult dealAlarm(String userId, String mac) {
		// TODO Auto-generated method stub
		mFromRepeaterAlarmDao = new FromRepeaterAlarmDaoImpl();
		mFromRepeaterAlarmDao.updateSmokeAlarm(mac, 1);
		mLoginDao = new LoginDaoImpl();
		LoginEntity le = mLoginDao.login(userId);
		String dealName=null;
		if(le!=null){
			dealName = le.getName();
			if(dealName==null){
				dealName=userId;
			}
		}else{
			dealName=userId;
		}
		String dealTime = GetTime.ConvertTimeByLong();
		String loginSql = "update alarm set dealUserId = ?,dealUserName= ?,ifDealAlarm= ?,dealTime= ? where smokeMac = ? and ifDealAlarm=0";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,loginSql);
		HttpRsult hr = null;
		try {
			ps.setString(1, userId);
			ps.setString(2, dealName);
			ps.setInt(3, 1);
			ps.setString(4, dealTime);
			ps.setString(5, mac);
			int rs = ps.executeUpdate();
			hr = new HttpRsult();
			if(rs==0){
				hr.setError("报警消息已处理");
				hr.setErrorCode(0);
			}else{
				hr.setError("处理报警消息成功");
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

	public HttpRsult dealAlarmDetail(String userId, String mac,
			String dealPeople, String alarmTruth, String dealDetail,
			String image_path,String video_path) {
		// TODO Auto-generated method stub
		mFromRepeaterAlarmDao = new FromRepeaterAlarmDaoImpl();
		mFromRepeaterAlarmDao.updateSmokeAlarm(mac, 1);
		mLoginDao = new LoginDaoImpl();
		LoginEntity le = mLoginDao.login(userId);
		String dealName=null;
		if(le!=null){
			dealName = le.getName();
			if(dealName==null){
				dealName=userId;
			}
		}else{
			dealName=userId;
		}
		String dealTime = GetTime.ConvertTimeByLong();
		String loginSql = "update alarm set dealUserId = ?,dealUserName= ?,ifDealAlarm= ?," +
				"dealTime= ?,dealPeople=?,dealDetail=?,alarmTruth=?,image_path=?,video_path=? where smokeMac = ? and ifDealAlarm=0";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,loginSql);
		HttpRsult hr = null;
		try {
			ps.setString(1, userId);
			ps.setString(2, dealName);
			ps.setInt(3, 1);
			ps.setString(4, dealTime);
			ps.setString(5, dealPeople);
			ps.setString(6, dealDetail);
			ps.setString(7, alarmTruth);
			ps.setString(8, image_path);
			ps.setString(9, video_path);
			ps.setString(10, mac);
			int rs = ps.executeUpdate();
			hr = new HttpRsult();
			if(rs==0){
				hr.setError("报警消息已处理");
				hr.setErrorCode(0);
			}else{
				hr.setError("处理报警消息成功");
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
	
	public HttpRsult dealAlarm(String userId) {
		// TODO Auto-generated method stub
		mFromRepeaterAlarmDao = new FromRepeaterAlarmDaoImpl();
		mFromRepeaterAlarmDao.updateSmokeAlarm();
		mLoginDao = new LoginDaoImpl();
		LoginEntity le = mLoginDao.login(userId);
		String dealName=null;
		if(le!=null){
			dealName = le.getName();
			if(dealName==null){
				dealName=userId;
			}
		}else{
			dealName=userId;
		}
		String dealTime = GetTime.ConvertTimeByLong();
		String loginSql = "update alarm set dealUserId = ?,dealUserName= ?,ifDealAlarm= ?,dealTime= ? where ifDealAlarm = 0";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,loginSql);
		HttpRsult hr = null;
		try {
			ps.setString(1, userId);
			ps.setString(2, dealName);
			ps.setInt(3, 1);
			ps.setString(4, dealTime);
			int rs = ps.executeUpdate();
			hr = new HttpRsult();
			if(rs==0){
				hr.setError("报警消息已处理");
				hr.setErrorCode(0);
			}else{
				hr.setError("处理报警消息成功");
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

	public HttpRsult dealAlarmDetail(String userId,String dealPeople, String alarmTruth, String dealDetail) {
		// TODO Auto-generated method stub
		mFromRepeaterAlarmDao = new FromRepeaterAlarmDaoImpl();
		mFromRepeaterAlarmDao.updateSmokeAlarm();
		mLoginDao = new LoginDaoImpl();
		LoginEntity le = mLoginDao.login(userId);
		String dealName=null;
		if(le!=null){
			dealName = le.getName();
			if(dealName==null){
				dealName=userId;
			}
		}else{
			dealName=userId;
		}
		String dealTime = GetTime.ConvertTimeByLong();
		String loginSql = "update alarm set dealUserId = ?,dealUserName= ?,ifDealAlarm= ?," +
				"dealTime= ?,dealPeople=?,dealDetail=?,alarmTruth=? where ifDealAlarm= 0 ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,loginSql);
		HttpRsult hr = null;
		try {
			ps.setString(1, userId);
			ps.setString(2, dealName);
			ps.setInt(3, 1);
			ps.setString(4, dealTime);
			ps.setString(5, dealPeople);
			ps.setString(6, dealDetail);
			ps.setString(7, alarmTruth);
			int rs = ps.executeUpdate();
			hr = new HttpRsult();
			if(rs==0){
				hr.setError("报警消息已处理");
				hr.setErrorCode(0);
			}else{
				hr.setError("处理报警消息成功");
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


}
