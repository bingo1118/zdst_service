package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.cloudfire.dao.AddCameraDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.until.GetTime;

public class AddCameraDaoImpl implements AddCameraDao{

	public HttpRsult addCamera(String cameraId, String cameraName,
			String cameraPwd, String cameraAddress, String longitude,
			String latitude, String principal1, String principal1Phone,
			String principal2, String principal2Phone, String areaId,
			String placeTypeId,String videoPotion,String videoSize) {
		// TODO Auto-generated method stub
		boolean isExited = isExited(cameraId);
		String addTime = GetTime.ConvertTimeByLong();
		String sql =null;
		if(isExited){
			sql = "update camera set cameraName= ?," +
					"cameraAddress= ?,cameraPwd= ? ," +
					"longitude= ?,latitude= ? ," +
					"principal1= ? ," +
					"principal1Phone= ?,principal2= ? ," +
					"principal2Phone= ?,areaId= ?,placeTypeId= ?,time=? ,videoPosition=?,videoSize=? where cameraId= ?";
		}else{
			sql = "insert camera (cameraName,cameraAddress,cameraPwd,longitude,latitude," +
					"principal1,principal1Phone,principal2,principal2Phone," +
					"areaId,placeTypeId,time,videoPosition,videoSize,cameraId) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		}
		//INSERT table (auto_id, auto_name) values (1, ‘yourname') ON DUPLICATE KEY UPDATE auto_name='yourname'
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		HttpRsult hr = null;
		try {
			ps.setString(15, cameraId);
			ps.setString(1, cameraName);
			ps.setString(2, cameraAddress);
			ps.setString(3, cameraPwd);
			ps.setString(4, longitude);
			ps.setString(5, latitude);
			ps.setString(6, principal1);
			ps.setString(7, principal1Phone);
			ps.setString(8, principal2);
			ps.setString(9, principal2Phone);
			if(areaId!=null&&areaId.length()>0){
				ps.setInt(10, Integer.parseInt(areaId));
			}else{
				ps.setInt(10, 0);
			}
			if(placeTypeId!=null&&placeTypeId.length()>0){
				ps.setInt(11, Integer.parseInt(placeTypeId));
			}else{
				ps.setInt(11, 0);
			}
			ps.setString(12, addTime);
			ps.setInt(13, Integer.parseInt(videoPotion));
			ps.setInt(14, Integer.parseInt(videoSize));
			int rs = ps.executeUpdate();
			hr = new HttpRsult();
			if(rs<=0){
				hr.setError("添加摄像机失败");
				hr.setErrorCode(2);
			}else{
				hr.setError("添加摄像机成功");
				hr.setErrorCode(0);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return hr;
	}
	
	public boolean isExited(String cameraId){
		String loginSql = "select cameraId from camera where cameraId = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,loginSql);
		ResultSet rs = null;
		boolean result = false;
		try {
			ps.setString(1, cameraId);
			rs = ps.executeQuery();
			if(rs.next()){
				result = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	} 

}
