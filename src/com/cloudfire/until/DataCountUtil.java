package com.cloudfire.until;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.cloudfire.db.DBConnectionManager;

/**
 * @author lzo
 * 统计某张表里的总数据。
 */
public class DataCountUtil {
	
	public static  int getTableCount(String tableName){
		StringBuffer sb = new StringBuffer();
		String sqlstr ="SELECT COUNT(*) FROM ";
		sb.append(sqlstr);
		sb.append(tableName);
		int result = 0;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sb.toString());
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				result = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
}
