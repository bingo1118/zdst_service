package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.cloudfire.dao.DvrInfoDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.DvrInfo;
import com.cloudfire.entity.DvrInfoEntity;

public class DvrInfoDaoImpl implements DvrInfoDao{

	public DvrInfo getDvrInfo() {
		// TODO Auto-generated method stub
		String sql="select dvrId,dvrName,dvrPwd,dvrIp,dvrPort,dvrLocation,dvrLat,dvrLon,constructionId from dvrInfo";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		List<DvrInfoEntity> lists =null;
		DvrInfo mDvrInfo= null;
		try {
			rs=ps.executeQuery();
			while(rs.next()){
				if(lists==null){
					lists = new ArrayList<DvrInfoEntity>();
				}
				DvrInfoEntity mDvrInfoEntity = new DvrInfoEntity();
				mDvrInfoEntity.setConstructionId(rs.getInt(9));
				mDvrInfoEntity.setDvrId(rs.getString(1));
				mDvrInfoEntity.setDvrIP(rs.getString(4));
				mDvrInfoEntity.setDvrLat(rs.getString(7));
				mDvrInfoEntity.setDvrLocation(rs.getString(6));
				mDvrInfoEntity.setDvrLon(rs.getString(8));
				mDvrInfoEntity.setDvrName(rs.getString(2));
				mDvrInfoEntity.setDvrPort(rs.getString(5));
				mDvrInfoEntity.setDvrPwd(rs.getString(3));
				lists.add(mDvrInfoEntity);
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		if(lists!=null&&lists.size()>0){
			mDvrInfo= new DvrInfo();
			mDvrInfo.setDvrInfo(lists);
			mDvrInfo.setError("获取成功");
			mDvrInfo.setErrorCode(0);
		}
		return mDvrInfo;
	}

	public DvrInfo getDvrInfoByConstructionId(int constructionId) {
		// TODO Auto-generated method stub
		String sql="select dvrId,dvrName,dvrPwd,dvrIp,dvrPort,dvrLocation,dvrLat,dvrLon from dvrInfo " +
				"where constructionId=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		List<DvrInfoEntity> lists =null;
		DvrInfo mDvrInfo= null;
		try {
			ps.setInt(1, constructionId);
			rs=ps.executeQuery();
			while(rs.next()){
				if(lists==null){
					lists = new ArrayList<DvrInfoEntity>();
				}
				DvrInfoEntity mDvrInfoEntity = new DvrInfoEntity();
				mDvrInfoEntity.setConstructionId(constructionId);
				mDvrInfoEntity.setDvrId(rs.getString(1));
				mDvrInfoEntity.setDvrIP(rs.getString(4));
				mDvrInfoEntity.setDvrLat(rs.getString(7));
				mDvrInfoEntity.setDvrLocation(rs.getString(6));
				mDvrInfoEntity.setDvrLon(rs.getString(8));
				mDvrInfoEntity.setDvrName(rs.getString(2));
				mDvrInfoEntity.setDvrPort(rs.getString(5));
				mDvrInfoEntity.setDvrPwd(rs.getString(3));
				lists.add(mDvrInfoEntity);
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		if(lists!=null&&lists.size()>0){
			mDvrInfo= new DvrInfo();
			mDvrInfo.setDvrInfo(lists);
			mDvrInfo.setError("获取成功");
			mDvrInfo.setErrorCode(0);
		}
		return mDvrInfo;
	}

}
