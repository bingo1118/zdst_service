package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.cloudfire.dao.AllCameraDao;
import com.cloudfire.dao.PlaceTypeDao;
import com.cloudfire.dao.PushAlarmMsgDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.CameraBean;
import com.cloudfire.entity.PushAlarmMsgEntity;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.Utils;

public class PushAlarmMsgDaoImpl implements PushAlarmMsgDao{
	private PlaceTypeDao mPlaceTypeDao;
	private AllCameraDao mAllCameraDao;

	public PushAlarmMsgEntity getPushAlarmMsg(String mac,int alarmType) {
		// TODO Auto-generated method stub
		String addTime = GetTime.ConvertTimeByLong();
		String sql = "select s.address,a.area,s.camera,s.deviceType,s.latitude,s.longitude," +
				"s.mac,s.named,s.placeAddress,s.placeTypeId,s.principal1,s.principal1Phone," +
				"s.principal2,s.principal2Phone from smoke s,areaidarea a where s.areaId = a.areaId and s.mac =?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		PushAlarmMsgEntity apme = null;
		int placeId = 0;
		String cameraId = null;
		try {
			ps.setString(1, mac);
			rs = ps.executeQuery();
			while(rs.next()){
				if(apme==null){
					apme = new PushAlarmMsgEntity();
				}
				apme.setAddress(rs.getString(1));
				apme.setAlarmTime(addTime);
				apme.setAlarmType(alarmType+"");
				apme.setAreaId(rs.getString(2));
				apme.setDeviceType(rs.getInt(4));
				apme.setIfDealAlarm(0);
				apme.setLatitude(rs.getString(5));
				apme.setLongitude(rs.getString(6));
				apme.setMac(mac);
				apme.setName(rs.getString(8));
				apme.setPlaceAddress(rs.getString(9));
				placeId = rs.getInt(10);
				cameraId = rs.getString(3);
				apme.setPrincipal1(rs.getString(11));
				apme.setPrincipal1Phone(rs.getString(12));
				apme.setPrincipal2(rs.getString(13));
				apme.setPrincipal2Phone(rs.getString(14));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		//查询
		if(placeId>0){
			mPlaceTypeDao = new PlaceTypeDaoImpl();
			Map<String,String> map = mPlaceTypeDao.getShopTypeById();
			apme.setPlaceType(map.get(placeId+""));
		}else{
			if(apme==null){
				apme = new PushAlarmMsgEntity();
			}
			apme.setPlaceType("");
		}
		if(Utils.isNullStr(cameraId)){
			//查询摄像机信息
			mAllCameraDao = new AllCameraDaoImpl();
			PushAlarmMsgEntity.CameraBean cb = mAllCameraDao.getCameraByCameraId(cameraId);
			if(cb!=null){
				apme.setCamera(cb);
			}else{
				PushAlarmMsgEntity.CameraBean pc = new PushAlarmMsgEntity.CameraBean();
				apme.setCamera(pc);
			}
		}else{
			PushAlarmMsgEntity.CameraBean pc = new PushAlarmMsgEntity.CameraBean();
			apme.setCamera(null);
		}
		return apme;
	}

	
	@Override
	public PushAlarmMsgEntity getPushAlarmMsg(String userId) {
		String sql = "SELECT userid,named,workingTime from user where userid = ?";
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		PushAlarmMsgEntity apme = null;
		try {
			ps.setString(1, userId);
			rs = ps.executeQuery();
			while(rs.next()){
				if(apme==null){
					apme = new PushAlarmMsgEntity();
				}
				apme.setAddress(rs.getString(3));
				apme.setAlarmType("80");
				apme.setMac(userId);
				apme.setName(rs.getString(2));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		System.out.println(sql+"="+userId+".........................."+apme.toString());
		return apme;
	}
}
