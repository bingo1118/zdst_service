package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cloudfire.dao.GetPushUserIdDao;
import com.cloudfire.db.DBConnectionManager;

public class GetPushUserIdDaoImpl implements GetPushUserIdDao{
	
	
	//@@获取确认报警后需要推送的用户（权限为6或7）
	public List<String> getMakeSurePushAreaUserIdByMac(String mac) {
		// TODO Auto-generated method stub
		String sql="select DISTINCT(u.userId) from smoke s,useridareaid u,user uu where s.mac =? and u.areaId IN (s.areaId) and uu.userId = u.userId and uu.privilege!=4 and uu.privilege IN (6,7)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		List<String> list =null;
		try {
			ps.setString(1,mac);
			rs = ps.executeQuery();
			while(rs.next()){
				if(list==null){
					list = new ArrayList<String>();
				}
				list.add(rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return list;
	}

	public List<String> getPushAreaUserIdByMac(String mac) {
		// TODO Auto-generated method stub
		String sql="select DISTINCT(u.userId) from smoke s,useridareaid u,user uu where s.mac =? and u.areaId IN (s.areaId) and uu.userId = u.userId and uu.privilege!=4 and uu.privilege!=6";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		List<String> list =null;
		try {
			ps.setString(1,mac);
			rs = ps.executeQuery();
			while(rs.next()){
				if(list==null){
					list = new ArrayList<String>();
				}
				list.add(rs.getString(1));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return list;
	}

	public List<String> getPushUserIdByMac(String mac) {
		// TODO Auto-generated method stub
		String sql="SELECT u.userId from smoke s,useridsmoke u where s.mac =? and s.mac = u.smokeMac";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		List<String> list = new ArrayList<String>();;
		try {
			ps.setString(1,mac);
			rs = ps.executeQuery();
			while(rs.next()){
				list.add(rs.getString(1));
			}
			list.add("17484023");
			list.add("hanrun");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return list;
	}

	public List<String> getAllUser(String mac) {
		// TODO Auto-generated method stub
		List<String> userOne = getPushAreaUserIdByMac(mac);
		List<String> userTwo = getPushUserIdByMac(mac);
		if(userOne==null&&userTwo==null){
			return null;
		}
		List<String> allUser = new ArrayList<String>();
		if(userOne!=null&&userOne.size()>0){
			allUser.addAll(userOne);
		}
		if(userTwo!=null&&userTwo.size()>0){
			allUser.addAll(userTwo);
		}
		return allUser;
	}
	
	//@@获取主机绑定用户列表
	public List<String> getAllRepeatUser(String mac) {
		// TODO Auto-generated method stub
		String sql="select * from useridrepeaterid where repeaterMac=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		List<String> list =null;
		try {
			ps.setString(1,mac);
			rs = ps.executeQuery();
			while(rs.next()){
				if(list==null){
					list = new ArrayList<String>();
				}
				list.add(rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return list;
	}

	public Map<String, String> getIosUser(List<String> userIdList) {
		// TODO Auto-generated method stub
		int len = userIdList.size();
		if(len<=0){
			return null;
		}
		StringBuffer strSql = new StringBuffer();
		if(len==1){
			//s.areaId in (14,  15) order by a.alarmTime desc limit 0 , 20
			strSql.append(" userId in (?) ");
		}else{
			for(int i=0;i<len;i++){
				if(i==0){
					strSql.append(" userId in (?, ");
				}else if(i==(len-1)){
					strSql.append(" ?) ");
				}else{
					strSql.append(" ?, ");
				}
			}
		}
		String sql = "select userId,ios from useridios where ";
		String sqlStr = new String(sql+strSql.toString());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
		Map<String, String> map = null;
		try {
			for(int i=1;i<=len;i++){
				ps.setString(i, userIdList.get(i-1));
			}
			rs = ps.executeQuery();
			
			while(rs.next()){
				if(map==null){
					map = new HashMap<String,String>();
				}
				map.put(rs.getString(1), rs.getString(2));
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return map;
	}

	public List<String> getOneKeyUser(String mac) {
		// TODO Auto-generated method stub
		String sql="select u.userId from smoke s,useridareaid u,user uu where s.mac =? and u.areaId IN (s.areaId) and uu.privilege=5 and uu.userId=u.userId";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		List<String> list = null;
		try {
			ps.setString(1, mac);
			rs = ps.executeQuery();
			while(rs.next()){
				if(list==null){
					list = new ArrayList<String>();
				}
				list.add(rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return list;
	}

	public List<String> getUserByRepeaterMac(String repeaterMac) {
		// TODO Auto-generated method stub
		String sql = "SELECT u.userId from smoke s,useridareaid u where s.mac=? and s.areaId = u.areaId GROUP BY u.userId";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		List<String> list = new ArrayList<String>();;
		try {
			ps.setString(1, repeaterMac);
			rs = ps.executeQuery();
			while(rs.next()){
				list.add(rs.getString(1));
			}
			list.add("17484023");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return list;
	}

}
