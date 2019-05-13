package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.cloudfire.dao.StyleDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.Style;
import com.cloudfire.until.Utils;

public class StyleDaoImpl implements StyleDao {

	@Override
	public boolean isExistUser(String userId) {
		String sql = "select userId from style where userId=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		String userId1 = null;
		boolean bool = false;
		try {
			if (Utils.isNullStr(userId)) {
				ps.setString(1, userId);
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				userId1 = rs.getString(1);
			}
			if (Utils.isNullStr(userId1)) {
				bool = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return bool;
	}

	public Style getStyle(String userId) {
		String sql = "select logo1, logo2, logo3 from style where userId = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		Style style = null;
		try {
			ps.setString(1, userId);
			rs = ps.executeQuery();
			while (rs.next()) {
				style = new Style();
				style.setLogo1(rs.getString(1));
				style.setLogo2(rs.getString(2));
				style.setLogo3(rs.getString(3));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return style;
	}

}
