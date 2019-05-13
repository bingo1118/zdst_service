package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.cloudfire.dao.NFCDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.NFCInfoEntity;

public class NFCDaoImpl implements NFCDao {

	@Override
	public int  isExited(String uid) {
		String sql="select * from nfcinfo where uid=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		int result=1;
		try {
			ps.setString(1, uid);
			rs=ps.executeQuery();
			if(rs.next()){
				result=0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	@Override
	public NFCInfoEntity getNFCInfo(String mac) {
		NFCInfoEntity nie = null;
		String sql = "select ni.* ,nr.endTime from (select deviceName,uid,deviceType,address,producer,addTime,makeTime,overTime,workerName from nfcInfo where uid = ?)ni  left join (select uuid,endTime from nfcRecord where uuid = ? order by endTime desc  limit 1 )nr on ni.uid = nr.uuid ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		try {
			ps.setString(1, mac);
			ps.setString(2, mac);
			rs = ps.executeQuery();
			while (rs.next()){
				nie = new NFCInfoEntity();
				nie.setDeviceName(rs.getString("deviceName"));
				nie.setUid(rs.getString("uid"));
				nie.setDeviceType(rs.getString("deviceType"));
				nie.setAddress(rs.getString("address"));
				nie.setProducer(rs.getString("producer"));
				nie.setAddTime(rs.getString("addTime"));
				nie.setMakeTime(rs.getString("makeTime"));
				nie.setOverTime(rs.getString("overTime"));
				nie.setWorkerName(rs.getString("workerName"));
				nie.setEndTime(rs.getString("endTime"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return nie;
	}

}
