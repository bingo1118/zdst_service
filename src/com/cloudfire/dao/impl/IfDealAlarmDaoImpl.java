package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cloudfire.dao.IfDealAlarmDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.LoginEntity;

public class IfDealAlarmDaoImpl implements IfDealAlarmDao{

	public List<String> getUserSmokeMac(List<String> listNum) {
		// TODO Auto-generated method stub
		StringBuffer strSql = new StringBuffer();
		int len = listNum.size();
		if(len==1){
			strSql.append(" where areaId in (?)");
		}else{
			for(int i=0;i<len;i++){
				if(i==0){
					strSql.append(" where areaId in (?, ");
				}else if(i==(len-1)){
					strSql.append(" ?)");
				}else{
					strSql.append(" ?, ");
				}
			}
		}
		String loginSql = new String("select mac from smoke ");
		String sqlStr = new String(loginSql+strSql.toString());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
		List<String> list = null;
		try {
			for(int i=1;i<=len;i++){
				ps.setInt(i, Integer.parseInt(listNum.get(i-1)));
			}
			rs = ps.executeQuery();
			while(rs.next()){
				if(list==null){
					list = new ArrayList<String>();
				}
				list.add(rs.getString("mac"));
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

//	public Map<String, Integer> getIfDealAlarm(List<String> macList) {
//		// TODO Auto-generated method stub
//		String loginSql = "select count(*) from alarm where smokeMac = ? and ifDealAlarm = 0";
//		Connection conn = DBConnectionManager.getConnection();
//		PreparedStatement ps = DBConnectionManager.prepare(conn,loginSql);
//		ResultSet rs = null;
//		Map<String, Integer> map = null;
//		try {
//			int len = macList.size();
//			for(int i=0;i<len;i++){
//				if(map==null){
//					map = new HashMap<String, Integer>();
//				}
//				String mac = macList.get(i);
//				ps.setString(1, mac);
//				rs = ps.executeQuery();
//				if(rs.next()){
//					int num = rs.getInt(1);
//					if(num>0){
//						map.put(mac, 0);
//					}else{
//						map.put(mac, 1);
//					}
//				}
//			}
//			
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}finally{
//			DBConnectionManager.close(ps);
//			DBConnectionManager.close(rs);
//			DBConnectionManager.close(conn);
//		}
//		
//		return map;
//	}

	public int getDealState(String smokeMac) {
		// TODO Auto-generated method stub
		String loginSql = "select count(*) from alarm where smokeMac = ? and ifDealAlarm = 0";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,loginSql);
		ResultSet rs = null;
		Map<String, Integer> map = null;
		int dealState = 1;
		try {
			ps.setString(1, smokeMac);
			rs = ps.executeQuery();
			if(rs.next()){
				int num = rs.getInt(1);
				if(num>0){
					dealState = 0;
				}else{
					dealState = 1;
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
		return dealState;
	}

	public List<String> getLostSmokeMac(List<String> listNum) {
		// TODO Auto-generated method stub
		StringBuffer strSql = new StringBuffer();
		int len = listNum.size();
		if(len==1){
			strSql.append(" areaId in (?)");
		}else{
			for(int i=0;i<len;i++){
				if(i==0){
					strSql.append(" areaId in (?, ");
				}else if(i==(len-1)){
					strSql.append(" ?)");
				}else{
					strSql.append(" ?, ");
				}
			}
		}
		String loginSql = new String("select mac from smoke where netState=0 and ");
		String sqlStr = new String(loginSql+strSql.toString());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
		List<String> list = null;
		try {
			for(int i=1;i<=len;i++){
				ps.setInt(i, Integer.parseInt(listNum.get(i-1)));
			}
			rs = ps.executeQuery();
			while(rs.next()){
				if(list==null){
					list = new ArrayList<String>();
				}
				list.add(rs.getString("mac"));
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

}
