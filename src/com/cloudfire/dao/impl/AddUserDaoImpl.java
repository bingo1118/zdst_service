package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.cloudfire.dao.AddUserDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.until.GetTime;

public class AddUserDaoImpl implements AddUserDao{

	public boolean ifExitUser(String userId) {
		// TODO Auto-generated method stub
		String sql = "select * from user where userId = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		boolean result = false;
		try {
			ps.setString(1, userId);
			rs = ps.executeQuery();
			if(rs.next()){
				result = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	public boolean ifExitUserArea(String areaId, String userId) {
		// TODO Auto-generated method stub
		String sql = "select * from useridareaid where userId = ? and areaId=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		boolean result = false;
		try {
			ps.setString(1, userId);
			rs = ps.executeQuery();
			if(rs.next()){
				result = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	public boolean addUser(String userId,int privilege,String name) {
		// TODO Auto-generated method stub
		String getTime = GetTime.ConvertTimeByLong();
		String sql = "insert user (userId,named,privilege,endTime) values (?,?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		int rs = -1;
		boolean result = false;
		try {
			ps.setString(1, userId);
			ps.setString(2, name);
			ps.setInt(3, privilege);
			ps.setString(4, getTime);
			rs = ps.executeUpdate();
			if(rs>0){
				result = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	public boolean addUser(String userId,int privilege,String name,String pwd) {
		// TODO Auto-generated method stub
		String getTime = GetTime.ConvertTimeByLong();
		String sql = "insert user (userId,named,privilege,endTime,pwd) values (?,?,?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		int rs = -1;
		boolean result = false;
		try {
			ps.setString(1, userId);
			ps.setString(2, name);
			ps.setInt(3, privilege);
			ps.setString(4, getTime);
			ps.setString(5, pwd);
			rs = ps.executeUpdate();
			if(rs>0){
				result = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	public boolean updateUser(String userId,int privilege,String name) {
		// TODO Auto-generated method stub
		String getTime = GetTime.ConvertTimeByLong();
		String sql = "update user set named=?,privilege=?,endTime=? where userId=?";
		if(privilege==1) privilege = 3;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		int rs = -1;
		boolean result = false;
		try {
			ps.setString(4, userId);
			ps.setString(1, name);
			ps.setInt(2, privilege);
			ps.setString(3, getTime);
			rs = ps.executeUpdate();
			if(rs>0){
				result = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	public boolean updateUser(String userId,int privilege,String name,String isCanCutEletr) {
		// TODO Auto-generated method stub
		String getTime = GetTime.ConvertTimeByLong();
		String sql ="";
		if(isCanCutEletr!=null&&isCanCutEletr.equals("0")){
			sql = "update user set named=?,privilege=?,endTime=?,isCanCutEletr=0 where userId=?";
		}else if(isCanCutEletr!=null&&isCanCutEletr.equals("1")){
			sql = "update user set named=?,privilege=?,endTime=?,isCanCutEletr=1 where userId=?";
		}else{
			sql = "update user set named=?,privilege=?,endTime=? where userId=?";
		}
		if(privilege==1) privilege = 3;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		int rs = -1;
		boolean result = false;
		try {
			ps.setString(4, userId);
			ps.setString(1, name);
			ps.setInt(2, privilege);
			ps.setString(3, getTime);
			rs = ps.executeUpdate();
			if(rs>0){
				result = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	public boolean addUserArea(String userId, Integer[] areaId) {
		// TODO Auto-generated method stub
		String sql = "insert useridareaid (userId,areaId) values (?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		int[] rs;
		boolean result = false;
		try {
			conn.setAutoCommit(false);
			ps.setString(1, userId);
			for(int id : areaId){
				ps.setInt(2, id);
				ps.addBatch();
			}
			rs = ps.executeBatch();
			conn.commit();
			if(rs.length>0){
				result = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	public boolean addUserArea(String userId, String areaId) {
		// TODO Auto-generated method stub
		String sql = "insert useridareaid (userId,areaId) values (?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		boolean result = false;
		try {
			ps.setString(1, userId);
			ps.setString(2, areaId);
			ps.executeBatch();
			int line = ps.executeUpdate();
			if(line>0){
				result = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	public boolean deleteUserArea(String userId) {
		// TODO Auto-generated method stub
		String sql = "delete from useridareaid where userId = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		int rs = -1;
		boolean result = false;
		try {
			ps.setString(1, userId);
			rs = ps.executeUpdate();
			if(rs>0){
				result = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

}
