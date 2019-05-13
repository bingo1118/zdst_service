package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cloudfire.dao.CameraPwdDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.CameraBean;
import com.cloudfire.entity.HttpRsult;

public class CameraPwdDaoImpl implements CameraPwdDao{

	public HttpRsult changeCameraPwd(String cameraId, String cameraPwd) {
		// TODO Auto-generated method stub
		String sql = "update camera set cameraPwd=? where cameraId = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		HttpRsult hr = null;
		try {
			ps.setString(1, cameraPwd);
			ps.setString(2, cameraId);
			int rs = ps.executeUpdate();
			hr = new HttpRsult();
			if(rs<=0){
				hr.setError("ÐÞ¸ÄÉãÏñ»úÃÜÂëÊ§°Ü");
				hr.setErrorCode(2);
			}else{
				hr.setError("ÐÞ¸ÄÉãÏñ»úÃÜÂë³É¹¦");
				hr.setErrorCode(0);
			}
		}catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return hr;
	}
	
	public List<CameraBean> getCameraByAreaId(List<String> areaList){
		List<CameraBean> clist = new ArrayList<CameraBean>();
		String sql = "SELECT cameraId,cameraName,cameraPwd,areaId,placeTypeId,videoPosition,videoSize,cameraAddress from camera where 1 = 1 and placeTypeId = 99 ";
		if(areaList !=null){
			sql = sql + " and areaId IN(";
			for(String area:areaList){
				sql = sql+area+",";
			}
			sql = sql + "0)";
		}
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				CameraBean cb = new CameraBean();
				cb.setCameraId(rs.getString("cameraId"));
				cb.setCameraName(rs.getString("cameraName"));
				cb.setCameraPwd(rs.getString("cameraPwd"));
				cb.setAreaName(rs.getString("areaId"));
				cb.setPlaceType(rs.getString("placeTypeId"));
				cb.setVideoPosition(rs.getString("videoPosition"));
				cb.setVideoSize(rs.getString("videoSize"));
				cb.setCameraAddress(rs.getString("cameraAddress"));
				clist.add(cb);
			}
		}catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return clist;
	}

}
