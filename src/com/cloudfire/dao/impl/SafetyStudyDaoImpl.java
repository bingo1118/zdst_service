package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cloudfire.dao.SafetyStudyDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.SafetyItem;
import com.cloudfire.entity.SafetyItemEntity;

public class SafetyStudyDaoImpl implements SafetyStudyDao{

	@Override
	public SafetyItemEntity getAllSafetyItem() {
		String loginSql = "select * from safety_first_item";
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,loginSql);
		ResultSet rs = null;
		List<SafetyItem> listsafetyitem = null;
		SafetyItemEntity sie = new SafetyItemEntity();
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				if(listsafetyitem==null){
					listsafetyitem = new ArrayList<SafetyItem>();
				}
				SafetyItem stp = new SafetyItem();
				stp.setStudyId(rs.getString("mac"));
				stp.setStudyName(rs.getString("name"));
				listsafetyitem.add(stp);
			}
			if(listsafetyitem!=null&&listsafetyitem.size()>0){
				sie.setErrorCode(0);
				sie.setError("获取培训内容成功");
				sie.setSafetyItems(listsafetyitem);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return sie;
	}

	@Override
	public SafetyItemEntity getAllSafetyRuleItem() {
		String loginSql = "select * from safety_rule_item";
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,loginSql);
		ResultSet rs = null;
		List<SafetyItem> listsafetyitem = null;
		SafetyItemEntity sie = new SafetyItemEntity();
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				if(listsafetyitem==null){
					listsafetyitem = new ArrayList<SafetyItem>();
				}
				SafetyItem stp = new SafetyItem();
				stp.setStudyId(rs.getString("mac"));
				stp.setStudyName(rs.getString("name"));
				listsafetyitem.add(stp);
			}
			if(listsafetyitem!=null&&listsafetyitem.size()>0){
				sie.setErrorCode(0);
				sie.setError("获取培训内容成功");
				sie.setSafetyItems(listsafetyitem);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return sie;
	}

}
