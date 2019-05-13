package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.cloudfire.dao.BindUserIdIosDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.until.Utils;

public class BindUserIdIosDaoImpl implements BindUserIdIosDao{

	public HttpRsult bindUserIdIos(String userId, String ios) {
		// TODO Auto-generated method stub
		String sql=null;
		int isExit = findUserId(userId,ios);
		HttpRsult hr = null;
		switch (isExit) {
		case 0:
			sql="insert useridios (ios,userId) values (?,?)";
			break;
		case 1:
			sql="update useridios set ios=? where userId=?";
			break;
		case 2:
			sql="update useridios set userId=? where ios=?";
			break;
		case -1:
			hr = new HttpRsult();
			hr.setError("添加成功");
			hr.setErrorCode(0);
			return hr;
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		try {
			if(isExit!=2){
				ps.setString(1, ios);
				ps.setString(2, userId);
			}
			else{
				ps.setString(2, ios);
				ps.setString(1, userId);
			}
			
			int rs = ps.executeUpdate();
			if(rs<=0){
				hr = new HttpRsult();
				hr.setError("添加失败");
				hr.setErrorCode(2);
			}else{
				hr = new HttpRsult();
				hr.setError("添加成功");
				hr.setErrorCode(0);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			//DBConnectionManager.close(rs);
			DBConnectionManager.close(conn);
		}
		return hr;
	}

	public int finduserIDIos(String userId,String ios){

		String sql="select userId,ios from useridios where userId=? and ios=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		int result = 0;
		try {
			ps.setString(1, userId);
			ps.setString(2, ios);
			rs = ps.executeQuery();
			while(rs.next()){
				ios = rs.getString(2);
				if(ios !=null){
					result = -1;
				}
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
	
	
	public int findUserId(String userId,String ios) {
		
		// TODO Auto-generated method stub result:1表示userId，2表示ios，0表示都没有
		removeUserIdIos(userId,ios);
		String sql="select userId,ios from useridios where userId=? or ios=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		int result = -1;
		Map<String,String> map = new HashMap<String,String>();
		try {
			ps.setString(1, userId);
			ps.setString(2, ios);
			rs = ps.executeQuery();
			String userid=null;
			String iosId=null;
			while(rs.next()){
				userid = rs.getString(1);
				iosId = rs.getString(2);
				map.put(userid, iosId);
			}
			
			if(!Utils.isNullStr(userid)&&!Utils.isNullStr(iosId)){
				result=0;
			}else if(Utils.isNullStr(userid)&&!Utils.isNullStr(iosId)){
				result=1;
			}else if(!Utils.isNullStr(userid)&&Utils.isNullStr(iosId)){
				result=2;
			}else if(Utils.isNullStr(userid)&&Utils.isNullStr(iosId)){
				if(userid.equals(userId)&&!iosId.equals(ios)){
					result=1;
				}else if(!userid.equals(userId)&&iosId.equals(ios)){
					result=2;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		int rsss = finduserIDIos(userId,ios);
		
		if(rsss == -1){
			result = -1;	
		}
		return result;
	}
	
	public void removeIos(String ios) {
			
			// TODO Auto-generated method stub result:1表示userId，2表示ios，0表示都没有
			String sql="DELETE FROM useridios  where  ios=?";
			Connection conn = DBConnectionManager.getConnection();
			PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
			int rs = 0;
			try {
				ps.setString(1, ios);
				rs = ps.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				DBConnectionManager.close(ps);
				DBConnectionManager.close(conn);
			}
		}
	
	public void removeUserId(String userid) {
		
		// TODO Auto-generated method stub result:1表示userId，2表示ios，0表示都没有
		String sql="DELETE FROM useridios  where  userId=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		int rs = 0;
		try {
			ps.setString(1, userid);
			rs = ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}
	
	public void removeUserIdIos(String userId,String ios) {
		
		// TODO Auto-generated method stub result:1表示userId，2表示ios，0表示都没有
		String sql="DELETE FROM useridios  where  userId=? or ios = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		try {
			ps.setString(1, userId);
			ps.setString(2, ios);
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}

}
