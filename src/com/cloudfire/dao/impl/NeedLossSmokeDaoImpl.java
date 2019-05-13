package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cloudfire.dao.AllCameraDao;
import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.IfDealAlarmDao;
import com.cloudfire.dao.NeedLossSmokeDao;
import com.cloudfire.dao.PlaceTypeDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.AllSmokeEntity;
import com.cloudfire.entity.CameraBean;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.until.Utils;

public class NeedLossSmokeDaoImpl implements NeedLossSmokeDao{
	private AreaDao mAreaDao;
	private AllCameraDao mAllCameraDao;
	private IfDealAlarmDao mIfDealAlarmDao;
	private PlaceTypeDao mPlaceTypeDao;

	public AllSmokeEntity getNeedLossSmoke(String userId, String privilege,String parentId,
			String areaId, String page, String placeTypeId ,String appId) {
		// TODO Auto-generated method stub
//		StringBuffer strSql = new StringBuffer();
//		List<String> listNum =null;
//		if(Utils.isNumeric(areaID)){
//			listNum = new ArrayList<String>();
//			listNum.add(areaID);
//			strSql.append(" s.areaId in (?) ");
//		}else{
//			mAreaDao = new AreaDaoImpl();
//			listNum = mAreaDao.getAreaStr(userId,privilege);
//			if(listNum==null){
//				return null;
//			}
//			int len = listNum.size();
//			if(len==1){
//				//s.areaId in (14,  15) order by a.alarmTime desc limit 0 , 20
//				strSql.append(" s.areaId in (?) ");
//			}else{
//				for(int i=0;i<len;i++){
//					if(i==0){
//						strSql.append(" s.areaId in (?, ");
//					}else if(i==(len-1)){
//						strSql.append(" ?) ");
//					}else{
//						strSql.append(" ?, ");
//					}
//				}
//			}
//		}
		mAreaDao = new AreaDaoImpl();
		List<String> listNum =mAreaDao.getAreaStr(userId,privilege);
		boolean isAllArea = false;
		StringBuffer strSql = new StringBuffer();
		if(parentId!=null&&parentId.length()>0){
			if(privilege.equals("4")){
				strSql.append(" s.areaId IN (SELECT areaid FROM areaidarea WHERE parentId = "+parentId);
				strSql.append(" )");
			}else{
				strSql.append(" s.areaId IN (select a.areaId from areaidarea a,useridareaid u WHERE a.areaId=u.areaId AND u.userId="+userId+" and a.parentId="+parentId);
				strSql.append(" )");
			}
		}else if(areaId!=null&&areaId.length()>0){
			isAllArea = false;
			int areaIdInt = Integer.parseInt(areaId);
			if(areaIdInt==44){
				strSql.append("  s.areaId IN (SELECT areaid FROM areaidarea WHERE parentId = 44");
				strSql.append(" )");
			}else{
				strSql.append("  s.areaId = "+areaIdInt);
			}
		}else{
			isAllArea = true;
			int len = listNum.size();
			if(len==1){
				strSql.append("  s.areaId in (?)");
			}else{
				for(int i=0;i<len;i++){
					if(i==0){
						strSql.append("  s.areaId in (?, ");
					}else if(i==(len-1)){
						strSql.append(" ?)");
					}else{
						strSql.append(" ?, ");
					}
				}
			}
		}
		
		if(appId!="2"&&!"2".equals(appId)){		//add by lzo at 2017-5-25
			strSql.append(" AND s.deviceType not in (11,12,13)");
		}
		
		mIfDealAlarmDao = new IfDealAlarmDaoImpl();
		List<String> listMac = mIfDealAlarmDao.getLostSmokeMac(listNum);
		if(listMac==null&&!privilege.equals("1")){
			return null;
		}
//		Map<String, Integer> macStateMap = mIfDealAlarmDao.getIfDealAlarm(listMac);
		mAllCameraDao = new AllCameraDaoImpl();
		Map<String,CameraBean> mapCamera = mAllCameraDao.getCameraByMac(listNum);
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		Map<String,String> map = mPlaceTypeDao.getShopTypeById();
		
		if(Utils.isNumeric(placeTypeId)){
			strSql.append(" and s.placeTypeId = "+placeTypeId);
		}
		
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*20;
				int endNum = 20;
				strSql.append(" order by time desc limit "+startNum+" , "+endNum);
			}
		}
		String sql = "select s.address,a.area,s.camera,s.deviceType,s.latitude,s.longitude,s.mac,s.named,s.netState,s.placeTypeId" +
				",s.placeAddress,s.principal1,s.principal1Phone,s.principal2,s.principal2Phone,s.repeater,s.ifAlarm from smoke s," +
				"areaidarea a where s.areaId = a.areaId " +
				" and s.netState=0 and s.mac<>s.repeater and ";	
		String sqlStr = new String(sql+strSql.toString());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
		AllSmokeEntity ae = null;
		List<SmokeBean> sbList = null;
		try {
			if(isAllArea){
				int len = listNum.size();
				for(int i=1;i<=len;i++){
					ps.setInt(i, Integer.parseInt(listNum.get(i-1)));
				}
			}
			rs = ps.executeQuery();
			while(rs.next()){
				if(sbList==null){
					sbList = new ArrayList<SmokeBean>();
				}
				String mac = rs.getString("mac");
//				int ifDeal = 1;
//				if(macStateMap!=null&&macStateMap.size()>0){
//					ifDeal = macStateMap.get(mac);
//				}
				String pId = rs.getInt(10)+"";
				SmokeBean sb = new SmokeBean();
				CameraBean cb =null;
				if(mapCamera!=null&&mapCamera.size()>0){
					cb = mapCamera.get(mac);
				}
				if(cb==null){
					cb = new CameraBean();
				}	
				sb.setCamera(cb);
				sb.setAddress(rs.getString(1));
				sb.setDeviceType(rs.getInt(4));
				sb.setLatitude(rs.getString(5));
				sb.setLongitude(rs.getString(6));
				sb.setMac(mac);
				sb.setName(rs.getString(8));
				sb.setNetState(0);
				sb.setPlaceeAddress(rs.getString(11));
				sb.setPrincipal1(rs.getString(12));
				if(Utils.isNumeric(pId)){
					sb.setPlaceType(map.get(pId));
				}
				sb.setAreaName(rs.getString(2));
				sb.setIfDealAlarm(rs.getInt(17));
				sb.setPrincipal1Phone(rs.getString(13));
				sb.setPrincipal2(rs.getString(14));
				sb.setPrincipal2Phone(rs.getString(15));
				sb.setRepeater(rs.getString(16));
				sbList.add(sb);
			}
			if(sbList!=null&&sbList.size()>0){
				ae = new AllSmokeEntity();
				ae.setSmoke(sbList);
				ae.setError("获取烟感成功");
				ae.setErrorCode(0);
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return ae;
	}

}
