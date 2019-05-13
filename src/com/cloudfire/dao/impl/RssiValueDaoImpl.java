package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.cloudfire.dao.RssiValueDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.RssiEntityQuery;
import com.cloudfire.until.GetTime;

public class RssiValueDaoImpl implements RssiValueDao {

	@Override
	public void saveRssiValue(RssiEntityQuery query) {
		String sqlstr = "INSERT into rssiinfo(devMac,rssi,rssitime)VALUES(?,?,?)";
		String datatime = GetTime.ConvertTimeByLong();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		System.out.println("rssivaluedao : = "+datatime);
		try {
			ps.setString(1, query.getDeviceMac());
			ps.setString(2, query.getRssivalue());
			ps.setString(3, datatime);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}

}
