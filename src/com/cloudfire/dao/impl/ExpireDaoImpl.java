package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cloudfire.dao.ExpireDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.Expire;
import com.cloudfire.until.GetTime;

public class ExpireDaoImpl implements ExpireDao{

	@Override
	public Expire getExpireByRegisterCode(String registerCode) {
		String sql = "select innerIp,outerIp,registerCode,expireTime,cur_innerIp,cur_outerIp,validTime,state from expire where registerCode = '"+registerCode+"'";
		Expire exp = null;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			rs = ppst.executeQuery();
			while (rs.next()){
				exp = new Expire();
				exp.setInnerIp(rs.getString(1));
				exp.setOuterIp(rs.getString(2));
				exp.setRegisterCode(rs.getString(3));
				exp.setExpireTime(rs.getString(4));
				exp.setCur_innerIp(rs.getString(5));
				exp.setCur_outerIp(rs.getString(6));
				String validTime = rs.getString(7);
				exp.setValidTime(validTime);
				if(validTime == null || System.currentTimeMillis() - GetTime.getTimeByString(validTime) > 24 * 60 * 60 * 1000) {
					exp.setServers(0);
				} else {
					exp.setServers(1);
				}
				exp.setState(rs.getInt(8));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		
		return exp;
	}

	@Override
	public int updateExpire(String cur_innerIp, String cur_outerIp, String validTime,
			int state, String registerCode) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int updateValidTime(String validTime, String registerCode) {
		String sql = "update expire set validTime = ? where registerCode = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sql);
		try {
			ppst.setString(1, validTime);
			ppst.setString(2, registerCode);
			ppst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return 0;
	}

	@Override
	public List<Expire> getAllExpire() {
		String sql = "select innerIp,outerIp,registerCode,expireTime,state from expire ";
		List<Expire> lstExp = null;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			rs  = ppst.executeQuery();
			while(rs.next()){
				if(lstExp == null) {
					lstExp = new ArrayList<Expire>();
				}
				Expire exp = new Expire();
				exp.setInnerIp(rs.getString(1));
				exp.setOuterIp(rs.getString(2));
				exp.setRegisterCode(rs.getString(3));
				exp.setExpireTime(rs.getString(4));
				exp.setState(rs.getInt(5));
				lstExp.add(exp);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return lstExp;
	}

	@Override
	public int updateExpire(String registerCode, int state) {
		int success = 0;
		String sql = "update expire set state = "+ state +" where registerCode = '"+registerCode+"'";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sql);
		try {
			success = ppst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  finally {
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return success;
	}

	@Override
	public int updateExpire(String innerIp, String outerIp,String validTime,String registerCode) {
		int success = 0;
		String sql = "update expire set cur_innerIp = '"+ innerIp+"',cur_outerIp = '"+outerIp+"',validTime = '"+validTime +"' where registerCode = '"+registerCode+"'";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sql);
		try {
			success = ppst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  finally {
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return success;
	}
	
	

}
