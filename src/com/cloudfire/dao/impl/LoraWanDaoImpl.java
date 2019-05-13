package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cloudfire.dao.LoraWanDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.until.Utils;

public class LoraWanDaoImpl implements LoraWanDao {

	@Override
	public void ifLossUpLoraWan() {
		String sqlstr = "UPDATE smoke SET netState = 0 WHERE mac = ?";
		List<SmokeBean> gpsList = getLoraList();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		try {
			conn.setAutoCommit(false);
			for(SmokeBean devBean:gpsList){
				String dataTime =devBean.getHeartime();
				if((StringUtils.isBlank(dataTime))||(System.currentTimeMillis()-Utils.getTimeByStr(dataTime)>1000*60*60*4)){
					ps.setString(1, devBean.getMac());
					ps.addBatch();
				}
			}
			ps.executeBatch();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}

	@Override
	public List<SmokeBean> getLoraList() {
		List<SmokeBean> loraList = new ArrayList<SmokeBean>();
		String sql = "SELECT mac,longitude,latitude,netState,TIME,heartime,repeater,deviceType FROM smoke WHERE deviceType in(21) and netState=1 GROUP BY mac";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				SmokeBean gpsBean = new SmokeBean();
				gpsBean.setMac(rs.getString("mac"));
				gpsBean.setLongitude(rs.getString("longitude"));
				gpsBean.setLatitude(rs.getString("latitude"));
				gpsBean.setNetState(rs.getInt("netState"));
				gpsBean.setAddTime(rs.getString("time"));
				gpsBean.setHeartime(rs.getString("heartime"));
				gpsBean.setRepeater(rs.getString("repeater"));//@@12.4
				gpsBean.setDeviceType(rs.getInt("deviceType"));
				loraList.add(gpsBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return loraList;
	}
	
	

}
