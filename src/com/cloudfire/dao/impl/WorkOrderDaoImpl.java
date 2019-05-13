package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.cloudfire.dao.WorkOrderDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.WorkOrder;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.Utils;

public class WorkOrderDaoImpl implements WorkOrderDao{

	@Override
	public boolean insertOrder(String mac ,String alarmType,String deviceType,String alarmAddress) {
		String sql = "insert into workOrder(orderId,mac,deviceType,alarmType,alarmTime,alarmAdress,dealVideo,OrderStatus,dealUserId) values(?,?,?,?,?,?,?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		boolean bool = false;
		int rs = 0;
		try {
			ps.setString(1, Utils.getOutTradeNo());
			ps.setString(2, mac);
			ps.setString(3, deviceType);
			ps.setString(4, alarmType);
			ps.setString(5, GetTime.ConvertTimeByLong());
			ps.setString(6, alarmAddress);
			ps.setString(7, null);
			ps.setInt(8, 0);
			ps.setString(9, null);
			rs = ps.executeUpdate();
			if(rs>0){
				bool = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			DBConnectionManager.close(conn);
			DBConnectionManager.close(ps);
		}
		return bool;
	}

}
