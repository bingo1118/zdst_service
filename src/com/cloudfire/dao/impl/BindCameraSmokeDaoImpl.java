package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.cloudfire.dao.AddCameraDao;
import com.cloudfire.dao.AddSmokeDao;
import com.cloudfire.dao.BindCameraSmokeDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.HttpRsult;

public class BindCameraSmokeDaoImpl implements BindCameraSmokeDao{
	private AddCameraDao mAddCameraDao;
	private AddSmokeDao mAddSmokeDao;

	public HttpRsult bindCameraSmoke(String cameraId, String smoke,String cameraChannel) {
		// TODO Auto-generated method stub
		mAddCameraDao = new AddCameraDaoImpl();
		mAddSmokeDao = new AddSmokeDaoImpl();
		boolean ifCameraExit = mAddCameraDao.isExited(cameraId);
		boolean ifSmokeExit = mAddSmokeDao.isExited(smoke);
		HttpRsult hr = null;
		if(ifSmokeExit&&ifCameraExit){
			String sql ="update smoke set camera = ? ,cameraChannel=? where mac = ?";
			Connection conn = DBConnectionManager.getConnection();
			PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
			try {
				ps.setString(1, cameraId);
				ps.setInt(2, Integer.parseInt(cameraChannel));
				ps.setString(3, smoke);
				int rs = ps.executeUpdate();
				hr = new HttpRsult();
				if(rs<=0){
					hr.setError("绑定失败");
					hr.setErrorCode(2);
				}else{
					hr.setError("绑定成功");
					hr.setErrorCode(0);
				}
			}catch (SQLException e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally{
				DBConnectionManager.close(ps);
				//DBConnectionManager.close(rs);
				DBConnectionManager.close(conn);
			}
		}
		return hr;
	}

	@Override
	public boolean meterBindUserDevce(String user, String device) {
		// TODO Auto-generated method stub
		
			String sql="insert elecMeterUserDevice (userId,device) values(?,?)";
			Connection conn=DBConnectionManager.getConnection();
			PreparedStatement ps=DBConnectionManager.prepare(conn, sql);
			boolean result=false;
			try{
				ps.setString(1, user);;
				ps.setString(2, device);
				if(ps.executeUpdate()>0)
					result=true;
			}catch (SQLException e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally{
				DBConnectionManager.close(ps);;
				DBConnectionManager.close(conn);
			}
			return result;
			
		
	}

}
