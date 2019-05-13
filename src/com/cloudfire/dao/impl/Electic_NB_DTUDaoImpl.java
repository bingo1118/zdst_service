package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.cloudfire.dao.Electic_NB_DTUDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.query.ElectricDTUQuery;

public class Electic_NB_DTUDaoImpl implements Electic_NB_DTUDao {

	@Override
	public List<ElectricDTUQuery> getNeedNBDTU(String mac, String type) {
		// TODO Auto-generated method stub
		String sql = "SELECT id,smokeMac,electricValue1,electricValue2,electricValue3,electricValue4,electricTime,repeaterMac,electricType,electricDevType FROM electricinfo WHERE smokeMac = ? AND electrictype = ? ORDER BY electricTime DESC LIMIT 20 ";
		List<ElectricDTUQuery> list = new ArrayList<ElectricDTUQuery>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, mac);
			ps.setString(2, type);
			rs = ps.executeQuery();
			while(rs.next()){
				ElectricDTUQuery dtu = new ElectricDTUQuery();
				dtu.setElectricValue1(rs.getString("electricValue1"));
				dtu.setElectricValue2(rs.getString("electricValue2"));
				dtu.setElectricValue3(rs.getString("electricValue3"));
				dtu.setElectricValue4(rs.getString("electricValue4"));
				dtu.setSmokeMac(mac);
				dtu.setElectricTime(rs.getString("electricTime"));
				dtu.setElectricType(type);
				list.add(dtu);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return list;
	}

	@Override
	public ElectricDTUQuery getNeedNBDTUEntity(String mac, String type) {
		// TODO Auto-generated method stub
		return null;
	}

}
