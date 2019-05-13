package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.cloudfire.dao.AllCameraDao;
import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.IfDealAlarmDao;
import com.cloudfire.dao.OneSmokeDao;
import com.cloudfire.dao.PlaceTypeDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.CameraBean;
import com.cloudfire.entity.OneSmoke;
import com.cloudfire.entity.OneSmokeEntity;

public class OneSmokeDaoImpl implements OneSmokeDao{
	private PlaceTypeDao mPlaceTypeDao;
	private AreaDao mAreaDao;
	private AllCameraDao mAllCameraDao;
	private IfDealAlarmDao mIfDealAlarmDao;

	public OneSmokeEntity getOneSmoke(String userId, String smokeMac,
			String privilege) {
		// TODO Auto-generated method stub
		boolean isSafe = true;
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		Map<String,String> map = mPlaceTypeDao.getShopTypeById();
		String loginSql = new String("select address,latitude,longitude,mac,named,netState,deviceType,placeAddress" +
				",principal1,principal1Phone,principal2,principal2Phone,repeater,areaId,placeTypeId" +
				" from smoke where mac = ?");
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,loginSql);
		ResultSet rs = null;
		OneSmokeEntity ose = null;
		OneSmoke sb=null;
		String placeName =null;
		try {
			ps.setString(1, smokeMac);
			rs = ps.executeQuery();
			
			while(rs.next()){
				if(ose==null){
					ose = new OneSmokeEntity();
					sb = new OneSmoke();
				}
				String mac = rs.getString("mac");
				int areaId = rs.getInt("areaId");
				String placeTypeId = rs.getString("placeTypeId");
				if(map!=null&&map.size()>0){
					placeName = map.get(placeTypeId);
				}
				sb.setAddress(rs.getString("address"));
				sb.setDeviceType(rs.getInt("deviceType"));
				sb.setLatitude(rs.getString("latitude"));
				sb.setLongitude(rs.getString("longitude"));
				sb.setName(rs.getString("named"));
				sb.setNetState(rs.getInt("netState"));
				sb.setPrincipal1(rs.getString("principal1"));
				sb.setPlaceType(placeName);
				sb.setAreaId(areaId);
				sb.setPlaceAddress(rs.getString("placeAddress"));
				sb.setPrincipal1Phone(rs.getString("principal1Phone"));
				sb.setPrincipal2(rs.getString("principal2"));
				sb.setPrincipal2Phone(rs.getString("principal2Phone"));
				sb.setRepeater(rs.getString("repeater"));
				sb.setPlaceTypeId(placeTypeId);
			}	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isSafe = false;
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
			if(!isSafe){
				return null;
			}
		}
		if(sb!=null){
			mAreaDao = new AreaDaoImpl();
			int areaId = sb.getAreaId();
			String areaName = mAreaDao.getOneAreaName(areaId);
			mAllCameraDao = new AllCameraDaoImpl();
			CameraBean cb = mAllCameraDao.getCameraBySmokeMac(smokeMac);
			if(cb==null){
				cb = new CameraBean();
			}else{
				cb.setAreaName(areaName);
				cb.setPlaceType(placeName);
			}
			mIfDealAlarmDao = new IfDealAlarmDaoImpl();
			int ifDeal = mIfDealAlarmDao.getDealState(smokeMac);
			sb.setAreaName(areaName);
			sb.setCamera(cb);
			sb.setIfDealAlarm(ifDeal);
			ose.setSmoke(sb);
		}
		return ose;
	}

}
