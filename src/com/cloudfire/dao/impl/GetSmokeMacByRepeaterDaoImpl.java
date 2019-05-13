package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import com.cloudfire.dao.GetSmokeMacByRepeaterDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.ElectricHistoryEntity;
import com.cloudfire.entity.TimerMap;
import com.cloudfire.entity.UserMap;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.LzstoneTimeTask;
import com.cloudfire.until.Utils;

public class GetSmokeMacByRepeaterDaoImpl implements GetSmokeMacByRepeaterDao{

	public List<String> getSmokeMacByRepeater(String repeaterMac) {
		// TODO Auto-generated method stub
		String sql="select mac from smoke where repeater=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		List<String> list =new ArrayList<String>();
		try {
			ps.setString(1,repeaterMac);
			rs = ps.executeQuery();
			while(rs.next()){
				if(list==null){
					list = new ArrayList<String>();
				}
				String macSmoke = rs.getString(1);
				if(macSmoke.length()==8){
					list.add(rs.getString(1));
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
		return list;
	}
	
	public List<String> getSmokeOnLineMacByRepeater(String repeaterMac) {
		// TODO Auto-generated method stub
		String sql="select mac from smoke where repeater=? and netstate = 1";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		List<String> list =new ArrayList<String>();
		try {
			ps.setString(1,repeaterMac);
			rs = ps.executeQuery();
			while(rs.next()){
				if(list==null){
					list = new ArrayList<String>();
				}
				String macSmoke = rs.getString(1);
				if(macSmoke.length()==8){
					list.add(rs.getString(1));
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
		return list;
	}

	@Override
	public String getRepeaterMacBySmokeMac(String smokeMac) {
		String sql = "SELECT repeater FROM smoke WHERE mac = ?";
		String repeater = "";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, smokeMac);
			rs = ps.executeQuery();
			while(rs.next()){
				repeater = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return repeater;
	}

	@Override
	public void updateSmokeInfo(String smokeMac, int eleState) {
		String sql = "UPDATE smoke SET electrState = ? WHERE mac = ? AND (electrState='1' OR electrState='2')";
		eleState++;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setInt(1, eleState);
			ps.setString(2, smokeMac);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
	}
	
	@Override
	public void chanageElectric(String smokeMac, int eleState) {
		String sql = "UPDATE smoke SET electrState = ? WHERE mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setInt(1, eleState);
			ps.setString(2, smokeMac);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
	}

	@Override
	public void insert_Electric_change_history(ElectricHistoryEntity ehe) {
		String sql = "insert into electric_change_history(mac,userId,state,changetime)values(?,?,?,?)";
		String changeTime = GetTime.ConvertTimeByLong();
		int states = ehe.getState();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, ehe.getMac());
			if(ehe.getUserId()==null){
				ps.setString(2, "6631");
			}else{
				ps.setString(2, ehe.getUserId());
			}
			ps.setInt(3, states);
			ps.setString(4, changeTime);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}

	@Override
	public List<ElectricHistoryEntity> getElectricHistory(String mac,
			String page) {
		String sql = "SELECT e.mac,e.userId,u.named,e.state,e.changetime from electric_change_history e,user u where e.userId = u.userId and mac = ?";
		
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*20;
				int endNum = 20;
				sql = sql +" order by e.changetime desc limit "+startNum+" , "+endNum;
			}
		}
		List<ElectricHistoryEntity> eleList = new ArrayList<ElectricHistoryEntity>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, mac);
			rs = ps.executeQuery();
			while(rs.next()){
				ElectricHistoryEntity eleEntity = new ElectricHistoryEntity();
				eleEntity.setChangetime(rs.getString("changetime"));
				eleEntity.setMac(rs.getString("mac"));
				eleEntity.setState(rs.getInt("state"));
				eleEntity.setUserId(rs.getString("userId"));
				eleEntity.setUserName(rs.getString("named"));
				eleList.add(eleEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return eleList;
	}
	
	
	public static boolean saveElectricHistory(String smokemac,String repeater,int state){
		GetSmokeMacByRepeaterDao mbrDao19 = new GetSmokeMacByRepeaterDaoImpl();
		ElectricHistoryEntity ehe9 = new ElectricHistoryEntity();
		String userId9 = UserMap.newInstance().getUser(repeater);
		ehe9.setUserId(userId9);
		ehe9.setMac(smokemac);
		
		ehe9.setState(state);
		mbrDao19.insert_Electric_change_history(ehe9);
		return true;
	}
	
	public static boolean saveElectricHistoryByUserid(String smokemac,String userid,int state){
		GetSmokeMacByRepeaterDao mbrDao19 = new GetSmokeMacByRepeaterDaoImpl();
		ElectricHistoryEntity ehe9 = new ElectricHistoryEntity();
		ehe9.setUserId(userid);
		ehe9.setMac(smokemac);
		
		ehe9.setState(state);
		mbrDao19.insert_Electric_change_history(ehe9);
		return true;
	}
	
}
