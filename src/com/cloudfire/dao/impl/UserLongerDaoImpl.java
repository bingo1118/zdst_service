package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.cloudfire.dao.UserLongDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.HttpRsultUser;
import com.cloudfire.entity.UserEntity;

public class UserLongerDaoImpl implements UserLongDao{

	@Override
	public UserEntity getUserInfoByUserId(String userId) {
		String sqlstr = "SELECT userid,named,privilege,iftryuser,endtime,privid,istxt FROM USER WHERE userId=? ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		UserEntity ue = null;
		try {
			ps.setString(1, userId);
			rs = ps.executeQuery();
			while(rs.next()){
				ue = new UserEntity();
				ue.setUserId(rs.getString(1));
				ue.setNamed(rs.getString(2));
				ue.setPrivilege(rs.getInt(3));
				ue.setIfTryUser(rs.getInt(4));
				ue.setEndTime(rs.getString(5));
				int privId = rs.getInt(6);
				if(privId>2){
					privId = 3;
				}
				ue.setPrivId(privId);
				ue.setIstxt(rs.getInt(7));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return ue;
	}
	
	public boolean updateUserTxtState(String userid,int txtState){
		boolean result = false;
		String sql="update user set istxt=? where userid=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setInt(1, txtState);
			ps.setString(2, userid);
			int rs=ps.executeUpdate();
			if (rs > 0) {
				result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return result;
	}

	@Override
	public HttpRsultUser getHostInfo(String host) {
		String sqlstr = "SELECT host,host2,ifvalid,encryption,retime FROM customer where host = ? OR host2 = ?";
		String result = "";
		String times = "";
		int code = 0;
		HttpRsultUser hr = new HttpRsultUser();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		try {
			ps.setString(1, host);
			ps.setString(2, host);
			rs = ps.executeQuery();
			while(rs.next()){
				result = rs.getString("encryption");
				times = rs.getString("retime");
				code = rs.getInt("ifvalid");
				hr.setResult(true);
			}
			hr.setError(result);
			hr.setErrorCode(code);
			hr.setRetime(times);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return hr;
	}

}
