package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.cloudfire.dao.AllCameraDao;
import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.IfDealAlarmDao;
import com.cloudfire.dao.NeedSmokeDao;
import com.cloudfire.dao.PlaceTypeDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.AllCheakItemEntity;
import com.cloudfire.entity.AllEnviDevEntity;
import com.cloudfire.entity.AllNFCInfoEntity;
import com.cloudfire.entity.AllSmokeEntity;
import com.cloudfire.entity.CameraBean;
import com.cloudfire.entity.CountValue;
import com.cloudfire.entity.ElevatorInfoBean;
import com.cloudfire.entity.ElevatorInfoBeanEntity;
import com.cloudfire.entity.EnviDevBean;
import com.cloudfire.entity.EnvironmentEntity;
import com.cloudfire.entity.GPSInfoBean;
import com.cloudfire.entity.NFCCheakItem;
import com.cloudfire.entity.NFCInfoEntity;
import com.cloudfire.entity.NFCTraceEntity;
import com.cloudfire.entity.NFC_infoEntity;
import com.cloudfire.entity.NeedNFCRecordEntity;
import com.cloudfire.entity.OneGPSInfoEntity;
import com.cloudfire.entity.OneGPSTraceEntity;
import com.cloudfire.entity.OneKindDevRecord;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.THDevice;
import com.cloudfire.entity.THInfoBeanEntity;
import com.cloudfire.until.Constant;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.Utils;

public class NeedSmokeDaoImpl implements NeedSmokeDao{
	private PlaceTypeDao mPlaceTypeDao;
	private AreaDao mAreaDao;
	private AllCameraDao mAllCameraDao;
	private IfDealAlarmDao mIfDealAlarmDao;

	public AllSmokeEntity getNeedSmoke(String userId, String privilege,
			String page, String areaId, String placeTypeId) {
		// TODO Auto-generated method stub
		mAreaDao = new AreaDaoImpl();
		List<String> listNum = mAreaDao.getAreaStr(userId,privilege);
		if(listNum==null&&!privilege.equals("1")){
			return null;
		}
		mIfDealAlarmDao = new IfDealAlarmDaoImpl();
		List<String> listMac = mIfDealAlarmDao.getUserSmokeMac(listNum);
		if(listMac==null&&!privilege.equals("1")){
			return null;
		}
//		Map<String, Integer> macStateMap = mIfDealAlarmDao.getIfDealAlarm(listMac);
		mAllCameraDao = new AllCameraDaoImpl();
		Map<String,CameraBean> mapCamera = mAllCameraDao.getCameraByMac(listNum);
		Map<Integer, String> mapArea = mAreaDao.getAreaById(listNum);
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		Map<String,String> map = mPlaceTypeDao.getShopTypeById();

		boolean isAllArea = false;
		StringBuffer strSql = new StringBuffer();
		if(areaId!=null&&areaId.length()>0){
			isAllArea = false;
			int areaIdInt = Integer.parseInt(areaId);
			if(areaIdInt == 44){
				strSql.append(" where areaId IN (SELECT areaid FROM areaidarea WHERE parentId = 44) ");
			}else if(areaIdInt>0){
				strSql.append(" where areaId = "+areaIdInt);
			}
		}else{
			isAllArea = true;
			int len = listNum.size();
			if(len==1){
				strSql.append(" where areaId in (?)");
			}else{
				for(int i=0;i<len;i++){
					if(i==0){
						strSql.append(" where areaId in (?, ");
					}else if(i==(len-1)){
						strSql.append(" ?)");
					}else{
						strSql.append(" ?, ");
					}
				}
			}
		}
		
//		strSql.append(" AND (deviceType<11 OR deviceType>15) and mac<>repeater AND deviceType <> 5 AND deviceType <> 17");	//add by lzo at 2017-5-24
		strSql.append(Constant.devTypeChooseSQLStatement_zdst("1"));
		
		if(placeTypeId!=null&&placeTypeId.length()>0){
			int placeTypeIdInt = Integer.parseInt(placeTypeId);
			if(placeTypeIdInt>0){
				strSql.append(" and placeTypeId = "+placeTypeIdInt);
			}
		}
		
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*20;
				int endNum = 20;
				strSql.append(" order by time desc limit "+startNum+" , "+endNum);
			}
		}
		
		String loginSql = new String("select address,latitude,longitude,mac,named,netState,deviceType,placeAddress" +
				",principal1,principal1Phone,principal2,principal2Phone,repeater,areaId,placeTypeId,ifAlarm" +
				" from smoke ");
//				" from smoke WHERE deviceType<11 AND deviceType <> 5");	//add by lzo at 2017-5-24
		String sqlStr= new String(loginSql+strSql.toString());
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
				if(ae==null){
					ae = new AllSmokeEntity();
				}
				String mac = rs.getString("mac");
				int areaID = rs.getInt("areaId");
				String placeTypeID = rs.getString("placeTypeId");
//				int ifDeal = 1;
//				if(macStateMap!=null&&macStateMap.size()>0){
//					ifDeal = macStateMap.get(mac);
//				}
				String placeName =null;
				if(map!=null&&map.size()>0){
					placeName = map.get(placeTypeID);
				}
				String areaName =null;
				if(mapArea!=null&&mapArea.size()>0){
					areaName = mapArea.get(areaID);
				}
				SmokeBean sb = new SmokeBean();
				if(sbList==null){
					sbList = new ArrayList<SmokeBean>();	
				}
				CameraBean cb =null;
				if(mapCamera!=null&&mapCamera.size()>0){
					cb = mapCamera.get(mac);
				}
				if(cb!=null){
					cb.setAreaName(areaName);
					cb.setPlaceType(placeName);
					sb.setCamera(cb);
				}else{
					cb = new CameraBean();
					sb.setCamera(cb);
				}
				sb.setAddress(rs.getString("address"));
				sb.setDeviceType(rs.getInt("deviceType"));
				sb.setLatitude(rs.getString("latitude"));
				sb.setLongitude(rs.getString("longitude"));
				sb.setMac(mac);
				sb.setName(rs.getString("named"));
				sb.setNetState(rs.getInt("netState"));
				sb.setPlaceeAddress(rs.getString("placeAddress"));
				sb.setPrincipal1(rs.getString("principal1"));
				sb.setPlaceType(placeName);
				sb.setAreaName(areaName);
				sb.setIfDealAlarm(rs.getInt("ifAlarm"));
				sb.setPrincipal1Phone(rs.getString("principal1Phone"));
				sb.setPrincipal2(rs.getString("principal2"));
				sb.setPrincipal2Phone(rs.getString("principal2Phone"));
				sb.setRepeater(rs.getString("repeater"));
				sbList.add(sb);
			}
			if(sbList!=null&&sbList.size()>0){
				ae.setError("获取烟感成功");
				ae.setErrorCode(0);
				ae.setSmoke(sbList);
			}
			
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
	
	@Override
	public AllSmokeEntity getNeedSecurity(String userId, String privilege,
			String page, String areaId, String placeTypeId) {
			// TODO Auto-generated method stub
			mAreaDao = new AreaDaoImpl();
			List<String> listNum = mAreaDao.getAreaStr(userId,privilege);
			if(listNum==null&&!privilege.equals("1")){
				return null;
			}
			mIfDealAlarmDao = new IfDealAlarmDaoImpl();
			List<String> listMac = mIfDealAlarmDao.getUserSmokeMac(listNum);
			if(listMac==null&&!privilege.equals("1")){
				return null;
			}
//			Map<String, Integer> macStateMap = mIfDealAlarmDao.getIfDealAlarm(listMac);
			mAllCameraDao = new AllCameraDaoImpl();
			Map<String,CameraBean> mapCamera = mAllCameraDao.getCameraByMac(listNum);
			Map<Integer, String> mapArea = mAreaDao.getAreaById(listNum);
			mPlaceTypeDao = new PlaceTypeDaoImpl();
			Map<String,String> map = mPlaceTypeDao.getShopTypeById();

			boolean isAllArea = false;
			StringBuffer strSql = new StringBuffer();
			if(areaId!=null&&areaId.length()>0){
				isAllArea = false;
				int areaIdInt = Integer.parseInt(areaId);
				if(areaIdInt>0){
					strSql.append(" where areaId = "+areaIdInt);
				}
			}else{
				isAllArea = true;
				int len = listNum.size();
				if(len==1){
					strSql.append(" where areaId in (?)");
				}else{
					for(int i=0;i<len;i++){
						if(i==0){
							strSql.append(" where areaId in (?, ");
						}else if(i==(len-1)){
							strSql.append(" ?)");
						}else{
							strSql.append(" ?, ");
						}
					}
				}
			}
			
			if(placeTypeId!=null&&placeTypeId.length()>0){
				int placeTypeIdInt = Integer.parseInt(placeTypeId);
				if(placeTypeIdInt>0){
					strSql.append(" and placeTypeId = "+placeTypeIdInt);
				}
			}
			
//			strSql.append(" and deviceType in (11,12,15)");
			strSql.append(Constant.devTypeChooseSQLStatement_zdst("3"));

			if(page!=null&&page.length()>0){
				int pageInt= Integer.parseInt(page);
				if(pageInt>0){
					int startNum = (pageInt-1)*20;
					int endNum = 20;
					strSql.append(" order by time desc limit "+startNum+" , "+endNum);
				}
			}
			
			String loginSql = new String("select address,latitude,longitude,mac,named,netState,deviceType,placeAddress" +
					",principal1,principal1Phone,principal2,principal2Phone,repeater,areaId,placeTypeId,ifAlarm" +
					" from smoke ");
			String sqlStr= new String(loginSql+strSql.toString());
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
					if(ae==null){
						ae = new AllSmokeEntity();
					}
					String mac = rs.getString("mac");
					int areaID = rs.getInt("areaId");
					String placeTypeID = rs.getString("placeTypeId");
					String placeName =null;
					if(map!=null&&map.size()>0){
						placeName = map.get(placeTypeID);
					}
					String areaName =null;
					if(mapArea!=null&&mapArea.size()>0){
						areaName = mapArea.get(areaID);
					}
					SmokeBean sb = new SmokeBean();
					if(sbList==null){
						sbList = new ArrayList<SmokeBean>();	
					}
					CameraBean cb =null;
					if(mapCamera!=null&&mapCamera.size()>0){
						cb = mapCamera.get(mac);
					}
					if(cb!=null){
						cb.setAreaName(areaName);
						cb.setPlaceType(placeName);
						sb.setCamera(cb);
					}else{
						cb = new CameraBean();
						sb.setCamera(cb);
					}
					sb.setAddress(rs.getString("address"));
					sb.setDeviceType(rs.getInt("deviceType"));
					sb.setLatitude(rs.getString("latitude"));
					sb.setLongitude(rs.getString("longitude"));
					sb.setMac(mac);
					sb.setName(rs.getString("named"));
					sb.setNetState(rs.getInt("netState"));
					sb.setPlaceeAddress(rs.getString("placeAddress"));
					sb.setPrincipal1(rs.getString("principal1"));
					sb.setPlaceType(placeName);
					sb.setAreaName(areaName);
					sb.setIfDealAlarm(rs.getInt("ifAlarm"));
					sb.setPrincipal1Phone(rs.getString("principal1Phone"));
					sb.setPrincipal2(rs.getString("principal2"));
					sb.setPrincipal2Phone(rs.getString("principal2Phone"));
					sb.setRepeater(rs.getString("repeater"));
					sbList.add(sb);
				}
				if(sbList!=null&&sbList.size()>0){
					ae.setError("获取设备成功");
					ae.setErrorCode(0);
					ae.setSmoke(sbList);
				}
				
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
	
	public AllSmokeEntity getNeedDevice(String userId, String privilege,
			String page, String areaId, String placeTypeId) {
		// TODO Auto-generated method stub
		mAreaDao = new AreaDaoImpl();
		List<String> listNum = mAreaDao.getAreaStr(userId,privilege);
		if(listNum==null&&!privilege.equals("1")){
			return null;
		}
		mIfDealAlarmDao = new IfDealAlarmDaoImpl();
		List<String> listMac = mIfDealAlarmDao.getUserSmokeMac(listNum);
		if(listMac==null&&!privilege.equals("1")){
			return null;
		}
//		Map<String, Integer> macStateMap = mIfDealAlarmDao.getIfDealAlarm(listMac);
		mAllCameraDao = new AllCameraDaoImpl();
		Map<String,CameraBean> mapCamera = mAllCameraDao.getCameraByMac(listNum);
		Map<Integer, String> mapArea = mAreaDao.getAreaById(listNum);
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		Map<String,String> map = mPlaceTypeDao.getShopTypeById();

		boolean isAllArea = false;
		StringBuffer strSql = new StringBuffer();
		if(areaId!=null&&areaId.length()>0){
			isAllArea = false;
			int areaIdInt = Integer.parseInt(areaId);
			if(areaIdInt == 44){
				strSql.append(" where areaId IN (SELECT areaid FROM areaidarea WHERE parentId = 44) ");
			}else if(areaIdInt>0){
				strSql.append(" where areaId = "+areaIdInt);
			}
		}else{
			isAllArea = true;
			int len = listNum.size();
			if(len==1){
				strSql.append(" where areaId in (?)");
			}else{
				for(int i=0;i<len;i++){
					if(i==0){
						strSql.append(" where areaId in (?, ");
					}else if(i==(len-1)){
						strSql.append(" ?)");
					}else{
						strSql.append(" ?, ");
					}
				}
			}
		}
		
//		strSql.append(" AND deviceType<11 ");	//add by lzo at 2017-5-24
		
		if(placeTypeId!=null&&placeTypeId.length()>0){
			int placeTypeIdInt = Integer.parseInt(placeTypeId);
			if(placeTypeIdInt>0){
				strSql.append(" and placeTypeId = "+placeTypeIdInt);
			}
		}
		
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*20;
				int endNum = 20;
				strSql.append(" order by time desc limit "+startNum+" , "+endNum);
			}
		}
		
		String loginSql = new String("select address,latitude,longitude,mac,named,netState,deviceType,placeAddress" +
				",principal1,principal1Phone,principal2,principal2Phone,repeater,areaId,placeTypeId,ifAlarm" +
				" from smoke ");
//				" from smoke WHERE deviceType<11 AND deviceType <> 5");	//add by lzo at 2017-5-24
		String sqlStr= new String(loginSql+strSql.toString());
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
				if(ae==null){
					ae = new AllSmokeEntity();
				}
				String mac = rs.getString("mac");
				int areaID = rs.getInt("areaId");
				String placeTypeID = rs.getString("placeTypeId");
//				int ifDeal = 1;
//				if(macStateMap!=null&&macStateMap.size()>0){
//					ifDeal = macStateMap.get(mac);
//				}
				String placeName =null;
				if(map!=null&&map.size()>0){
					placeName = map.get(placeTypeID);
				}
				String areaName =null;
				if(mapArea!=null&&mapArea.size()>0){
					areaName = mapArea.get(areaID);
				}
				SmokeBean sb = new SmokeBean();
				if(sbList==null){
					sbList = new ArrayList<SmokeBean>();	
				}
				CameraBean cb =null;
				if(mapCamera!=null&&mapCamera.size()>0){
					cb = mapCamera.get(mac);
				}
				if(cb!=null){
					cb.setAreaName(areaName);
					cb.setPlaceType(placeName);
					sb.setCamera(cb);
				}else{
					cb = new CameraBean();
					sb.setCamera(cb);
				}
				sb.setAddress(rs.getString("address"));
				sb.setDeviceType(rs.getInt("deviceType"));
				sb.setLatitude(rs.getString("latitude"));
				sb.setLongitude(rs.getString("longitude"));
				sb.setMac(mac);
				sb.setName(rs.getString("named"));
				sb.setNetState(rs.getInt("netState"));
				sb.setPlaceeAddress(rs.getString("placeAddress"));
				sb.setPrincipal1(rs.getString("principal1"));
				sb.setPlaceType(placeName);
				sb.setAreaName(areaName);
				sb.setIfDealAlarm(rs.getInt("ifAlarm"));
				sb.setPrincipal1Phone(rs.getString("principal1Phone"));
				sb.setPrincipal2(rs.getString("principal2"));
				sb.setPrincipal2Phone(rs.getString("principal2Phone"));
				sb.setRepeater(rs.getString("repeater"));
				sbList.add(sb);
			}
			if(sbList!=null&&sbList.size()>0){
				ae.setError("获取烟感成功");
				ae.setErrorCode(0);
				ae.setSmoke(sbList);
			}
			
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

	//@@ liangbin 2017.07.19
	@Override
	public AllSmokeEntity getNeedDev(String userId, String privilege,
			String page, String areaId, String placeTypeId, String devType,String parentId) {
		mAreaDao = new AreaDaoImpl();
		List<String> listNum = mAreaDao.getAreaStr(userId,privilege);
		if(listNum==null&&!privilege.equals("1")){
			return null;
		}
		mIfDealAlarmDao = new IfDealAlarmDaoImpl();
		List<String> listMac = mIfDealAlarmDao.getUserSmokeMac(listNum);
		if(listMac==null&&!privilege.equals("1")){
			return null;
		}
//		Map<String, Integer> macStateMap = mIfDealAlarmDao.getIfDealAlarm(listMac);
		mAllCameraDao = new AllCameraDaoImpl();
		Map<String,CameraBean> mapCamera = mAllCameraDao.getCameraByMac(listNum);
		Map<Integer, String> mapArea = mAreaDao.getAreaById(listNum);
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		Map<String,String> map = mPlaceTypeDao.getShopTypeById();

		boolean isAllArea = false;
		StringBuffer strSql = new StringBuffer();
		if(parentId!=null&&parentId.length()>0){
			if(privilege.equals("4")){
				strSql.append(" where areaId IN (SELECT areaid FROM areaidarea WHERE parentId = "+parentId);
				strSql.append(" )");
			}else{
				strSql.append(" where areaId IN (select a.areaId from areaidarea a,useridareaid u WHERE a.areaId=u.areaId AND u.userId="+userId+" and a.parentId="+parentId);
				strSql.append(" )");
			}
			
		}else if(areaId!=null&&areaId.length()>0){
			isAllArea = false;
			int areaIdInt = Integer.parseInt(areaId);
			if(areaIdInt==44){
				strSql.append(" where areaId IN (SELECT areaid FROM areaidarea WHERE parentId = 44");
				strSql.append(" )");
			}else{
				strSql.append(" where areaId = "+areaIdInt);
			}
		}else{
			isAllArea = true;
			int len = listNum.size();
			if(len==1){
				strSql.append(" where areaId in (?)");
			}else{
				for(int i=0;i<len;i++){
					if(i==0){
						strSql.append(" where areaId in (?, ");
					}else if(i==(len-1)){
						strSql.append(" ?)");
					}else{
						strSql.append(" ?, ");
					}
				}
			}
		}
		
//		strSql.append(" AND deviceType="+devType+" ");
		
		strSql.append(Constant.devTypeChooseSQLStatement(devType));
		
//		if(devType=="1"||"1".equals(devType)){		//by liangbin
//			strSql.append(" AND deviceType not in (5,10,11,12,13,14,15,18,19,25,52,53,119,124,125) AND mac<>repeater ");
//		}else if(devType=="3"||"3".equals(devType)){		
//			strSql.append(" AND deviceType in (5,52,53)");
//		}else if(devType=="4"||"4".equals(devType)){		
//			strSql.append(" AND deviceType in (10,11,12,13,15,18,19,25,42,124,125)");
//		}else if(devType=="2"||"2".equals(devType)){		
//			strSql.append(" AND (mac=repeater or deviceType=119) ");
//		}
		
		if(placeTypeId!=null&&placeTypeId.length()>0){
			int placeTypeIdInt = Integer.parseInt(placeTypeId);
			if(placeTypeIdInt>0){
				strSql.append(" and placeTypeId = "+placeTypeIdInt);
			}
		}
		
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*20;
				int endNum = 20;
				strSql.append(" order by time desc limit "+startNum+" , "+endNum);
			}
		}
		
		String loginSql = new String("select address,latitude,longitude,mac,named,netState,deviceType,placeAddress" +
				",principal1,principal1Phone,principal2,principal2Phone,repeater,areaId,placeTypeId,ifAlarm,electrState,rssivalue,lowVoltage " +
				" from smoke ");
//				" from smoke WHERE deviceType<11 AND deviceType <> 5");	//add by lzo at 2017-5-24
		String sqlStr= new String(loginSql+strSql.toString());
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
				if(ae==null){
					ae = new AllSmokeEntity();
				}
				String mac = rs.getString("mac");
				int areaID = rs.getInt("areaId");
				String placeTypeID = rs.getString("placeTypeId");
//				int ifDeal = 1;
//				if(macStateMap!=null&&macStateMap.size()>0){
//					ifDeal = macStateMap.get(mac);
//				}
				String placeName =null;
				if(map!=null&&map.size()>0){
					placeName = map.get(placeTypeID);
				}
				String areaName =null;
				if(mapArea!=null&&mapArea.size()>0){
					areaName = mapArea.get(areaID);
				}
				SmokeBean sb = new SmokeBean();
				if(sbList==null){
					sbList = new ArrayList<SmokeBean>();	
				}
				CameraBean cb =null;
				if(mapCamera!=null&&mapCamera.size()>0){
					cb = mapCamera.get(mac);
				}
				if(cb!=null){
					cb.setAreaName(areaName);
					cb.setPlaceType(placeName);
					sb.setCamera(cb);
				}else{
					cb = new CameraBean();
					sb.setCamera(cb);
				}
				sb.setAddress(rs.getString("address"));
				sb.setDeviceType(rs.getInt("deviceType"));
				sb.setLatitude(rs.getString("latitude"));
				sb.setLongitude(rs.getString("longitude"));
				sb.setMac(mac);
				sb.setName(rs.getString("named"));
				sb.setNetState(rs.getInt("netState"));
				sb.setPlaceeAddress(rs.getString("placeAddress"));
				sb.setPrincipal1(rs.getString("principal1"));
				sb.setPlaceType(placeName);
				sb.setAreaName(areaName);
				sb.setIfDealAlarm(rs.getInt("ifAlarm"));
				sb.setPrincipal1Phone(rs.getString("principal1Phone"));
				sb.setPrincipal2(rs.getString("principal2"));
				sb.setPrincipal2Phone(rs.getString("principal2Phone"));
				sb.setRepeater(rs.getString("repeater"));
				sb.setElectrState(rs.getInt("electrState"));
				sb.setRssivalue(rs.getString("rssivalue"));
				sb.setLowVoltage(rs.getString("lowVoltage"));
				sbList.add(sb);
			}
			if(sbList!=null&&sbList.size()>0){
				ae.setError("获取烟感成功");
				ae.setErrorCode(0);
				ae.setSmoke(sbList);
			}
			
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

	@Override
	public AllSmokeEntity getNeedLossDev(String userId, String privilege,
			String page, String areaId, String placeTypeId, String devType,String parentId) {
		if(privilege.equals("1")){
			return getAdminAllLossSmoke(userId, privilege, page, devType);
		}//@@9.29
		mAreaDao = new AreaDaoImpl();
		List<String> listNum = mAreaDao.getAreaStr(userId,privilege);
		if(listNum==null&&!privilege.equals("1")){
			return null;
		}
		mIfDealAlarmDao = new IfDealAlarmDaoImpl();
		List<String> listMac = mIfDealAlarmDao.getUserSmokeMac(listNum);
		if(listMac==null&&!privilege.equals("1")){
			return null;
		}
//		Map<String, Integer> macStateMap = mIfDealAlarmDao.getIfDealAlarm(listMac);
		mAllCameraDao = new AllCameraDaoImpl();
		Map<String,CameraBean> mapCamera = mAllCameraDao.getCameraByMac(listNum);
		Map<Integer, String> mapArea = mAreaDao.getAreaById(listNum);
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		Map<String,String> map = mPlaceTypeDao.getShopTypeById();

		boolean isAllArea = false;
		StringBuffer strSql = new StringBuffer();
		if(parentId!=null&&parentId.length()>0){
			if(privilege.equals("4")){
				strSql.append(" and areaId IN (SELECT areaid FROM areaidarea WHERE parentId = "+parentId);
				strSql.append(" )");
			}else{
				strSql.append(" and areaId IN (select a.areaId from areaidarea a,useridareaid u WHERE a.areaId=u.areaId AND u.userId="+userId+" and a.parentId="+parentId);
				strSql.append(" )");
			}
			
		}else if(areaId!=null&&areaId.length()>0){
			isAllArea = false;
			int areaIdInt = Integer.parseInt(areaId);
			if(areaIdInt == 44){
				strSql.append("and areaId IN (SELECT areaid FROM areaidarea WHERE parentId = 44) ");
			}else if(areaIdInt>0){
				strSql.append("and areaId = "+areaIdInt);
			}
		}else{
			isAllArea = true;
			int len = listNum.size();
			if(len==1){
				strSql.append("and areaId in (?)");
			}else{
				for(int i=0;i<len;i++){
					if(i==0){
						strSql.append("and areaId in (?, ");
					}else if(i==(len-1)){
						strSql.append(" ?)");
					}else{
						strSql.append(" ?, ");
					}
				}
			}
		}
		
		strSql.append(Constant.devTypeChooseSQLStatement(devType));
		
//		if(devType=="1"||"1".equals(devType)){		//by liangbin
//			strSql.append(" AND deviceType not in (5,10,11,12,13,14,15,18,19,25,119,124,125) AND mac<>repeater ");
//		}else if(devType=="3"||"3".equals(devType)){		
//			strSql.append(" AND deviceType in (5)");
//		}else if(devType=="4"||"4".equals(devType)){		
//			strSql.append(" AND deviceType in (10,11,12,13,15,18,19,25,42,124,125)");
//		}else if(devType=="2"||"2".equals(devType)){		
//			strSql.append(" AND (mac=repeater or deviceType=119) ");
//		}
		
		if(placeTypeId!=null&&placeTypeId.length()>0){
			int placeTypeIdInt = Integer.parseInt(placeTypeId);
			if(placeTypeIdInt>0){
				strSql.append(" and placeTypeId = "+placeTypeIdInt);
			}
		}
		
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*20;
				int endNum = 20;
				strSql.append(" order by time desc limit "+startNum+" , "+endNum);
			}
		}
		
		String loginSql = new String("select address,latitude,longitude,mac,named,netState,deviceType,placeAddress" +
				",principal1,principal1Phone,principal2,principal2Phone,repeater,areaId,placeTypeId,ifAlarm,electrState,rssivalue" +
				" from smoke where netState=0 ");
		String sqlStr= new String(loginSql+strSql.toString());
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
				if(ae==null){
					ae = new AllSmokeEntity();
				}
				String mac = rs.getString("mac");
				int areaID = rs.getInt("areaId");
				String placeTypeID = rs.getString("placeTypeId");
//				int ifDeal = 1;
//				if(macStateMap!=null&&macStateMap.size()>0){
//					ifDeal = macStateMap.get(mac);
//				}
				String placeName =null;
				if(map!=null&&map.size()>0){
					placeName = map.get(placeTypeID);
				}
				String areaName =null;
				if(mapArea!=null&&mapArea.size()>0){
					areaName = mapArea.get(areaID);
				}
				SmokeBean sb = new SmokeBean();
				if(sbList==null){
					sbList = new ArrayList<SmokeBean>();	
				}
				CameraBean cb =null;
				if(mapCamera!=null&&mapCamera.size()>0){
					cb = mapCamera.get(mac);
				}
				if(cb!=null){
					cb.setAreaName(areaName);
					cb.setPlaceType(placeName);
					sb.setCamera(cb);
				}else{
					cb = new CameraBean();
					sb.setCamera(cb);
				}
				sb.setAddress(rs.getString("address"));
				sb.setDeviceType(rs.getInt("deviceType"));
				sb.setLatitude(rs.getString("latitude"));
				sb.setLongitude(rs.getString("longitude"));
				sb.setMac(mac);
				sb.setName(rs.getString("named"));
				sb.setNetState(rs.getInt("netState"));
				sb.setPlaceeAddress(rs.getString("placeAddress"));
				sb.setPrincipal1(rs.getString("principal1"));
				sb.setPlaceType(placeName);
				sb.setAreaName(areaName);
				sb.setIfDealAlarm(rs.getInt("ifAlarm"));
				sb.setPrincipal1Phone(rs.getString("principal1Phone"));
				sb.setPrincipal2(rs.getString("principal2"));
				sb.setPrincipal2Phone(rs.getString("principal2Phone"));
				sb.setRepeater(rs.getString("repeater"));
				sb.setElectrState(rs.getInt("electrState"));
				sb.setRssivalue(rs.getString("rssivalue"));
				sbList.add(sb);
			}
			if(sbList!=null&&sbList.size()>0){
				ae.setError("获取烟感成功");
				ae.setErrorCode(0);
				ae.setSmoke(sbList);
			}
			
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

	@Override
	public AllEnviDevEntity getNeedEnviDev(String userId, String privilege,
			String page, String areaId, String placeTypeId) {
					mAreaDao = new AreaDaoImpl();
					List<String> listNum = mAreaDao.getAreaStr(userId,privilege);
					if(listNum==null&&!privilege.equals("1")){
						return null;
					}
					mIfDealAlarmDao = new IfDealAlarmDaoImpl();
					List<String> listMac = mIfDealAlarmDao.getUserSmokeMac(listNum);
					if(listMac==null&&!privilege.equals("1")){
						return null;
					}
					Map<Integer, String> mapArea = mAreaDao.getAreaById(listNum);
					mPlaceTypeDao = new PlaceTypeDaoImpl();
					Map<String,String> map = mPlaceTypeDao.getShopTypeById();

					boolean isAllArea = false;
					StringBuffer strSql = new StringBuffer();
					if(areaId!=null&&areaId.length()>0){
						isAllArea = false;
						int areaIdInt = Integer.parseInt(areaId);
						if(areaIdInt>0){
							strSql.append(" and areaId = "+areaIdInt);
						}
					}else{
						isAllArea = true;
						int len = listNum.size();
						if(len==1){
							strSql.append(" and areaId in (?)");
						}else{
							for(int i=0;i<len;i++){
								if(i==0){
									strSql.append(" and areaId in (?, ");
								}else if(i==(len-1)){
									strSql.append(" ?)");
								}else{
									strSql.append(" ?, ");
								}
							}
						}
					}
					
					if(placeTypeId!=null&&placeTypeId.length()>0){
						int placeTypeIdInt = Integer.parseInt(placeTypeId);
						if(placeTypeIdInt>0){
							strSql.append(" and placeTypeId = "+placeTypeIdInt);
						}
					}
					
//					strSql.append(" and deviceType=13 ");

					if(page!=null&&page.length()>0){
						int pageInt= Integer.parseInt(page);
						if(pageInt>0){
							int startNum = (pageInt-1)*20;
							int endNum = 20;
							strSql.append(" order by time desc limit "+startNum+" , "+endNum);
						}
					}
					
					String loginSql = new String("select a.address,a.latitude,a.longitude,a.mac,a.named,a.netState,a.deviceType,a.placeAddress" +
							",a.principal1,a.principal1Phone,a.principal2,a.principal2Phone,a.repeater,a.areaId,a.placeTypeId,a.ifAlarm" +
							",b.arimac,b.co2,b.temperature,b.humidity,b.pm25,b.methanal"+
							" from smoke a left join (select * from (select * from environment order by dateTime desc) as a group by arimac) b on a.mac=b.arimac where deviceType=13 ");
					String sqlStr= new String(loginSql+strSql.toString());
					Connection conn = DBConnectionManager.getConnection();
					PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
					ResultSet rs = null;
					AllEnviDevEntity ae = null;
					List<EnviDevBean> sbList = null;
					try {
						if(isAllArea){
							int len = listNum.size();
							for(int i=1;i<=len;i++){
								ps.setInt(i, Integer.parseInt(listNum.get(i-1)));
							}
						}
						rs = ps.executeQuery();
						
						while(rs.next()){
							if(ae==null){
								ae = new AllEnviDevEntity();
							}
							String mac = rs.getString("mac");
							int areaID = rs.getInt("areaId");
							String placeTypeID = rs.getString("placeTypeId");
							String placeName =null;
							if(map!=null&&map.size()>0){
								placeName = map.get(placeTypeID);
							}
							String areaName =null;
							if(mapArea!=null&&mapArea.size()>0){
								areaName = mapArea.get(areaID);
							}
							EnviDevBean sb = new EnviDevBean();
							if(sbList==null){
								sbList = new ArrayList<EnviDevBean>();	
							}
							EnvironmentEntity envi =new EnvironmentEntity();
							envi.setCo2(rs.getString("co2"));
							envi.setHumidity(rs.getString("humidity"));
							envi.setPm25(rs.getString("pm25"));
							envi.setTemperature(rs.getString("temperature"));
							envi.setMethanal(rs.getString("methanal"));
							envi.setArimac(rs.getString("arimac"));
							int pm25 = rs.getInt("pm25");
							if(pm25>=180){
								envi.setPriority2(4);
							}else if(pm25>=100&&pm25<180){
								envi.setPriority2(3);
							}else if(pm25>=70&&pm25<100){
								envi.setPriority2(2);
							}else if(pm25>=0&&pm25<70){
								envi.setPriority2(1);
							}
							envi.setPm25(pm25+"");
							double methanal = rs.getDouble("methanal");
							if(methanal>=0.080d){
								envi.setPriority1(4);
							}else if(methanal>=0.030d&&methanal<0.080d){
								envi.setPriority1(3);
							}else if(methanal>=0.020d&&methanal<0.030d){
								envi.setPriority1(2);
							}else if(methanal>=0.000d&&methanal<0.020d){
								envi.setPriority1(1);
							}
							int max = Math.max(envi.getPriority1(), envi.getPriority2());
							if(rs.getString("co2")==null&&rs.getString("temperature")==null){
								envi.setPriority(0);
							}else{
								envi.setPriority(max);
							}
							
							
							sb.setAddress(rs.getString("address"));
							sb.setDeviceType(rs.getInt("deviceType"));
							sb.setLatitude(rs.getString("latitude"));
							sb.setLongitude(rs.getString("longitude"));
							sb.setMac(mac);
							sb.setName(rs.getString("named"));
							sb.setNetState(rs.getInt("netState"));
							sb.setPlaceeAddress(rs.getString("placeAddress"));
							sb.setPrincipal1(rs.getString("principal1"));
							sb.setPlaceType(placeName);
							sb.setAreaName(areaName);
							sb.setIfDealAlarm(rs.getInt("ifAlarm"));
							sb.setPrincipal1Phone(rs.getString("principal1Phone"));
							sb.setPrincipal2(rs.getString("principal2"));
							sb.setPrincipal2Phone(rs.getString("principal2Phone"));
							sb.setRepeater(rs.getString("repeater"));
							sb.setEnviInfo(envi);
							sbList.add(sb);
						}
						if(sbList!=null&&sbList.size()>0){
							Comparator<EnviDevBean> cmp = Collections.reverseOrder(); 
						    Collections.sort(sbList, cmp); 
//							Collections.reverse(sbList);
							ae.setError("获取设备成功");
							ae.setErrorCode(0);
							ae.setSmoke(sbList);
						}
						
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

	@Override
	public AllSmokeEntity getNeedGPSDev(String userId, String privilege,
			String page, String areaId, String placeTypeId) {
		// TODO Auto-generated method stub
				mAreaDao = new AreaDaoImpl();
				List<String> listNum = mAreaDao.getAreaStr(userId,privilege);
				if(listNum==null&&!privilege.equals("1")){
					return null;
				}
				mIfDealAlarmDao = new IfDealAlarmDaoImpl();
				List<String> listMac = mIfDealAlarmDao.getUserSmokeMac(listNum);
				if(listMac==null&&!privilege.equals("1")){
					return null;
				}
//				Map<String, Integer> macStateMap = mIfDealAlarmDao.getIfDealAlarm(listMac);
				mAllCameraDao = new AllCameraDaoImpl();
				Map<String,CameraBean> mapCamera = mAllCameraDao.getCameraByMac(listNum);
				Map<Integer, String> mapArea = mAreaDao.getAreaById(listNum);
				mPlaceTypeDao = new PlaceTypeDaoImpl();
				Map<String,String> map = mPlaceTypeDao.getShopTypeById();

				boolean isAllArea = false;
				StringBuffer strSql = new StringBuffer();
				if(areaId!=null&&areaId.length()>0){
					isAllArea = false;
					int areaIdInt = Integer.parseInt(areaId);
					if(areaIdInt == 44){
						strSql.append(" where areaId IN (SELECT areaid FROM areaidarea WHERE parentId = 44) ");
					}else if(areaIdInt>0){
						strSql.append(" where areaId = "+areaIdInt);
					}
				}else{
					isAllArea = true;
					int len = listNum.size();
					if(len==1){
						strSql.append(" where areaId in (?)");
					}else{
						for(int i=0;i<len;i++){
							if(i==0){
								strSql.append(" where areaId in (?, ");
							}else if(i==(len-1)){
								strSql.append(" ?)");
							}else{
								strSql.append(" ?, ");
							}
						}
					}
				}
				
				strSql.append(" AND deviceType=14");	
				
				if(placeTypeId!=null&&placeTypeId.length()>0){
					int placeTypeIdInt = Integer.parseInt(placeTypeId);
					if(placeTypeIdInt>0){
						strSql.append(" and placeTypeId = "+placeTypeIdInt);
					}
				}
				
				if(page!=null&&page.length()>0){
					int pageInt= Integer.parseInt(page);
					if(pageInt>0){
						int startNum = (pageInt-1)*20;
						int endNum = 20;
						strSql.append(" order by time desc limit "+startNum+" , "+endNum);
					}
				}
				
				String loginSql = new String("select address,latitude,longitude,mac,named,netState,deviceType,placeAddress" +
						",principal1,principal1Phone,principal2,principal2Phone,repeater,areaId,placeTypeId,ifAlarm" +
						" from smoke ");
//						" from smoke WHERE deviceType<11 AND deviceType <> 5");	//add by lzo at 2017-5-24
				String sqlStr= new String(loginSql+strSql.toString());
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
						if(ae==null){
							ae = new AllSmokeEntity();
						}
						String mac = rs.getString("mac");
						int areaID = rs.getInt("areaId");
						String placeTypeID = rs.getString("placeTypeId");
//						int ifDeal = 1;
//						if(macStateMap!=null&&macStateMap.size()>0){
//							ifDeal = macStateMap.get(mac);
//						}
						String placeName =null;
						if(map!=null&&map.size()>0){
							placeName = map.get(placeTypeID);
						}
						String areaName =null;
						if(mapArea!=null&&mapArea.size()>0){
							areaName = mapArea.get(areaID);
						}
						SmokeBean sb = new SmokeBean();
						if(sbList==null){
							sbList = new ArrayList<SmokeBean>();	
						}
						CameraBean cb =null;
						if(mapCamera!=null&&mapCamera.size()>0){
							cb = mapCamera.get(mac);
						}
						if(cb!=null){
							cb.setAreaName(areaName);
							cb.setPlaceType(placeName);
							sb.setCamera(cb);
						}else{
							cb = new CameraBean();
							sb.setCamera(cb);
						}
						sb.setAddress(rs.getString("address"));
						sb.setDeviceType(rs.getInt("deviceType"));
						sb.setLatitude(rs.getString("latitude"));
						sb.setLongitude(rs.getString("longitude"));
						sb.setMac(mac);
						sb.setName(rs.getString("named"));
						sb.setNetState(rs.getInt("netState"));
						sb.setPlaceeAddress(rs.getString("placeAddress"));
						sb.setPrincipal1(rs.getString("principal1"));
						sb.setPlaceType(placeName);
						sb.setAreaName(areaName);
						sb.setIfDealAlarm(rs.getInt("ifAlarm"));
						sb.setPrincipal1Phone(rs.getString("principal1Phone"));
						sb.setPrincipal2(rs.getString("principal2"));
						sb.setPrincipal2Phone(rs.getString("principal2Phone"));
						sb.setRepeater(rs.getString("repeater"));
						sbList.add(sb);
					}
					if(sbList!=null&&sbList.size()>0){
						ae.setError("获取烟感成功");
						ae.setErrorCode(0);
						ae.setSmoke(sbList);
					}
					
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

	@Override
	public OneGPSInfoEntity getOneGPSInfo(String mac) {
		String sqlStr = new String("select devMac,longitude,latitude,speeds,dataTime" +
				" from gpsinfo where devMac=? ORDER BY dataTime DESC LIMIT 1");
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
		OneGPSInfoEntity ae = null;
		GPSInfoBean info=null;
		try {
			ps.setString(1, mac);
			rs = ps.executeQuery();
			
			while(rs.next()){
				if(ae==null){
					ae = new OneGPSInfoEntity();
				}
				if(info==null){
					info = new GPSInfoBean();
				}
				String devMac = rs.getString("devMac");
				String longitude = rs.getString("longitude");
				String latitude = rs.getString("latitude");
				String speeds = rs.getString("speeds");
				String dataTime = rs.getString("dataTime");
				info.setDevMac(devMac);
				info.setLat(latitude);
				info.setLon(longitude);
				info.setSpeed(speeds);
				info.setDataTime(dataTime);
			}
			if(info!=null){
				ae.setError("获取烟感成功");
				ae.setErrorCode(0);
				ae.setInfo(info);
			}
			
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

	@Override
	public OneGPSTraceEntity getOneGPSTrace(String mac, String begintime,
			String endtime) {
		String sqlStr = new String("SELECT * from gpsinfo WHERE devMac=? AND dataTime BETWEEN ?  AND ? order by dataTime");
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
		OneGPSTraceEntity ae = null;
		List<GPSInfoBean> trace=null;
		GPSInfoBean info=null;
		try {
			ps.setString(1, mac);
			ps.setString(2, begintime);
			ps.setString(3, endtime);
			rs = ps.executeQuery();
			
			while(rs.next()){
				if(ae==null){
					ae = new OneGPSTraceEntity();
				}
				if(trace==null){
					trace = new ArrayList<>();
				}
				info = new GPSInfoBean();
			
				String devMac = rs.getString("devMac");
				String longitude = rs.getString("longitude");
				String latitude = rs.getString("latitude");
				String speeds = rs.getString("speeds");
				String dataTime = rs.getString("dataTime");
				info.setDevMac(devMac);
				info.setLat(latitude);
				info.setLon(longitude);
				info.setSpeed(speeds);
				info.setDataTime(dataTime);
				trace.add(info);
			}
			if(trace!=null&&trace.size()>0){
				ae.setError("获取烟感成功");
				ae.setErrorCode(0);
				ae.setTrace(trace);
			}
			
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
	
	@Override
	public NFCTraceEntity getNFCTrace(String areaId, String begintime,
			String endtime) {
		String sqlStr = new String("SELECT * from nfcrecord n,nfcinfo i WHERE n.uuid=i.uid and i.areaId=? AND n.endTime BETWEEN ?  AND ? order by n.endTime");
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
		NFCTraceEntity ae = null;
		List<NFCInfoEntity> trace=null;
		NFCInfoEntity info=null;
		try {
			ps.setString(1, areaId);
			ps.setString(2, begintime);
			ps.setString(3, endtime);
			rs = ps.executeQuery();
			
			while(rs.next()){
				if(ae==null){
					ae = new NFCTraceEntity();
				}
				if(trace==null){
					trace = new ArrayList<>();
				}
				info = new NFCInfoEntity();
			
				String devMac = rs.getString("uuid");
				String longitude = rs.getString("longitude");
				String latitude = rs.getString("latitude");
				int devicestate = rs.getInt("devicestate");
				String memo = rs.getString("memo");
				String dataTime = rs.getString("endTime");
				String userId = rs.getString("userId");//@@10.30
				String photo1 = rs.getString("photo1");//@@10.30
				String deviceName = rs.getString("deviceName");//@@10.30
				info.setUid(devMac);
				info.setLatitude(latitude);
				info.setLongitude(longitude);
				info.setDevicestate(devicestate);
				info.setMemo(memo);
				info.setAddTime(dataTime);
				info.setUserId(userId);//@@10.30
				info.setPhoto1(photo1);//@@10.30
				info.setDeviceName(deviceName);//@@10.30
				trace.add(info);
			}
			if(trace!=null&&trace.size()>0){
				ae.setError("获取烟感成功");
				ae.setErrorCode(0);
				ae.setNfcTraceList(trace);
			}
			
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
	

	@Override
	public AllNFCInfoEntity getAllNFCInfo(List<String> areaIds,String page,String areaId,String period,String devicetype,String devicestate) {
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("SELECT * from (SELECT * FROM( SELECT s.uid,s.addTime,s.areaid,s.area,s.deviceType,s.deviceName,s.devTypeName,s.address,s.longitude,s.latitude,r.devicestate,r.memo,r.endTime,s.producer,s.makeTime,s.overTime,s.workerName,s.workerPhone ");
		sqlstr.append(" FROM(SELECT n.uid,n.addTime,n.devTypeName,n.areaid,a.area,n.deviceType,n.deviceName,n.address,n.longitude,n.latitude,n.producer,n.makeTime,n.overTime,n.workerName,n.workerPhone ");
		sqlstr.append(" from (SELECT * from nfcinfo n LEFT JOIN nfc_device_type t ON n.deviceType=t.devTypeId) n,areaidarea a where n.areaid in(");
		String areaSql = "";
		for (int i = 0; i < areaIds.size(); i++) {
			if(i==0){
				areaSql = areaSql + areaIds.get(i);
			}else if(i==areaIds.size() - 1){
				areaSql = areaSql + "," + areaIds.get(i);
			}else{
				areaSql = areaSql + "," + areaIds.get(i);
			}
		}
		sqlstr.append(areaSql+") ");
		if(areaId!=null&&areaId.length()>0){
			sqlstr.append(" and a.areaId = ?");
		}
		if(devicetype!=null&&devicetype.length()>0){
			sqlstr.append(" and n.deviceType="+devicetype+" ");//@@1107
		}
		sqlstr.append(" and a.areaId = n.areaid) as s ");
		sqlstr.append(" LEFT JOIN nfcrecord r on r.uuid = s.uid AND r.endTime <? AND r.endTime>? ");
		sqlstr.append(" ORDER BY endTime DESC)as cc GROUP BY uid )as z ");
		if(devicestate!=null&&devicestate.length()>0){
			if(devicestate.equals("0")){
				sqlstr.append(" where ISNULL(devicestate) ");//@@2018.03.05
			}else{
				sqlstr.append(" where devicestate="+devicestate);//@@2018.03.05
			}
		}
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*20;
				int endNum = 20;
				sqlstr.append(" limit "+startNum+" , "+endNum);
			}
		}
		String getTime = GetTime.ConvertTimeByLong();
		String endTime_1 = getTime.substring(0,8)+"01 00:00:01";
		String endTime_2 = getTime.substring(0,8)+"31 23:59:59";
		Calendar calendar=Calendar.getInstance(Locale.CHINA);//@@10.23
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		switch (period) {
		case "0"://every month
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			endTime_1=dateFormat.format(calendar.getTime())+" 00:00:01";
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			endTime_2=dateFormat.format(calendar.getTime())+" 23:59:59";
			break;
		case "1"://every week
			calendar.set(Calendar.DAY_OF_WEEK, 2);
			endTime_1=dateFormat.format(calendar.getTime())+" 00:00:01";
			calendar.add(Calendar.DATE, 6);
			endTime_2=dateFormat.format(calendar.getTime())+" 23:59:59";
			break;
		case "2"://everyday
			endTime_1=dateFormat.format(calendar.getTime())+" 00:00:01";
			endTime_2=dateFormat.format(calendar.getTime())+" 23:59:59";
			break;
		default:
			break;
		}//@@10.23
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr.toString());
		ResultSet rs = null;
		AllNFCInfoEntity nfcInfo = null;
		List<NFCInfoEntity> nfcList = new ArrayList<NFCInfoEntity>();
		try {
			if(areaId!=null&&areaId.length()>0){
				ps.setString(1, areaId);
				ps.setString(2, endTime_2);
				ps.setString(3, endTime_1);
			}else{
				ps.setString(1, endTime_2);
				ps.setString(2, endTime_1);
			}
			rs = ps.executeQuery();
			while(rs.next()){
				NFCInfoEntity nfc = new NFCInfoEntity();
				nfc.setDevicestate(rs.getInt("devicestate"));
				nfc.setUid(rs.getString("uid"));
				nfc.setAddress(rs.getString("address"));
				nfc.setAddTime(rs.getString("addTime"));
				nfc.setAreaid(rs.getString("areaid"));
				nfc.setAreaName(rs.getString("area"));
				nfc.setLatitude(rs.getString("latitude"));
				nfc.setLongitude(rs.getString("longitude"));
				nfc.setDeviceTypeName(rs.getString("devTypeName"));
				nfc.setDeviceName(rs.getString("deviceName"));
				nfc.setDeviceType(rs.getString("deviceType"));
				nfc.setMemo(rs.getString("memo"));
				nfc.setProducer(rs.getString("producer"));
				nfc.setMakeTime(rs.getString("makeTime"));
				nfc.setOverTime(rs.getString("overTime"));
				nfc.setWorkerName(rs.getString("workerName"));
				nfc.setWorkerPhone(rs.getString("workerPhone"));
				nfcList.add(nfc);
			}
			if(nfcList.size()>0){
				nfcInfo = new AllNFCInfoEntity();
				nfcInfo.setNfcList(nfcList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return nfcInfo;
	}
	
	@Override
	public List<NFC_infoEntity> getAllNFCInfo_web(List<String> areaIds,NFC_infoEntity query) {
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("SELECT * FROM( SELECT s.uid,s.areaid,s.area,s.deviceType,s.deviceName,s.address,s.longitude,s.latitude,r.devicestate,r.memo,r.endTime ");
		sqlstr.append(" FROM(SELECT n.uid,n.areaid,a.area,n.deviceType,n.deviceName,n.address,n.longitude,n.latitude ");
		sqlstr.append(" from nfcinfo n,areaidarea a where 1=1 ");
		if(query.getUid()!=null){
			sqlstr.append(" and n.uid like '%"+query.getUid()+"%'");
		}
		if(query.getDeviceName()!=null){
			sqlstr.append(" and n.deviceName like '%"+query.getDeviceName()+"%'");
		}
		if(areaIds!=null){
			for (int i = 0; i < areaIds.size(); i++) {
				if(i==0){
					sqlstr.append(" and n.areaid in("+areaIds.get(i));
				}else if(i==areaIds.size() - 1){
					sqlstr.append(","+areaIds.get(i));
				}else{
					sqlstr.append(","+areaIds.get(i));
				}
			}
			sqlstr.append(") ");
		}
		if(!query.getAreaId().equals("0")){
			sqlstr.append(" and a.areaId = ?");
		}
		sqlstr.append(" and a.areaId = n.areaid) as s ");
		sqlstr.append(" LEFT JOIN nfcrecord r on r.uuid = s.uid where r.endTime <? AND r.endTime>? ");
		if(query.getDevicestate()!=null&&query.getDevicestate().length()>0){
			sqlstr.append(" and r.devicestate= "+query.getDevicestate()+" ");
		}
		sqlstr.append(" ORDER BY endTime DESC)as cc GROUP BY uid");
		sqlstr.append(" limit "+query.getStartRow()+" , "+query.getPageSize());
		String getTime = GetTime.ConvertTimeByLong();
		String endTime_1 = getTime.substring(0,8)+"01 00:00:01";
		String endTime_2 = getTime.substring(0,8)+"31 23:59:59";
		if(query.getEndTime_1()!=null&&query.getEndTime_1()!=""){
			endTime_1 = query.getEndTime_1()+" 00:00:01";
			endTime_2 = query.getEndTime_2()+" 23:59:59";
		}
		CountValue cv = new CountValue();
		int macNum = 0;//总数
		int netStaterNum = 0;//合格
		int noNetStater = 0;//不合格
		int otherNum = 0;//待检
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr.toString());
		ResultSet rs = null;
		List<NFC_infoEntity> nfcList = new ArrayList<NFC_infoEntity>();
		try {
			if(!query.getAreaId().equals("0")){
				ps.setString(1, query.getAreaId());
				ps.setString(2, endTime_2);
				ps.setString(3, endTime_1);
			}else{
				ps.setString(1, endTime_2);
				ps.setString(2, endTime_1);
			}
			
			int row = query.getStartRow();
			rs = ps.executeQuery();
			while(rs.next()){
				NFC_infoEntity nfc = new NFC_infoEntity();
				nfc.setRow(++row);
				macNum = nfc.getRow();
				int states = rs.getInt("devicestate");
				nfc.setNowState(states+"");
				if(states == 0){
					nfc.setDevicestate("待检");
					otherNum++;
				}else if(states == 1){
					nfc.setDevicestate("合格");
					netStaterNum++;
				}else{
					nfc.setDevicestate("不合格");
					noNetStater++;
				}
				
				nfc.setUid(rs.getString("uid"));
				nfc.setAddress(rs.getString("address"));
				nfc.setAddTime(rs.getString("endTime"));
				nfc.setAreaId(rs.getString("areaid"));
				nfc.setAreaName(rs.getString("area"));
				nfc.setLatitude(rs.getString("latitude"));
				nfc.setLongitude(rs.getString("longitude"));
				nfc.setDeviceName(rs.getString("deviceName"));
				nfc.setDeviceType(rs.getInt("deviceType"));
				nfc.setMemo(rs.getString("memo"));
				cv.setMacNum(macNum);
				cv.setNetStaterNum(netStaterNum);
				cv.setNoNetStater(noNetStater);
				cv.setOtherNum(otherNum);
				nfc.setCv(cv);
				nfcList.add(nfc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return nfcList;
	}
	
	@Override
	public List<NFC_infoEntity> getAllNFCInfo_web(List<String> areaIds,NFC_infoEntity query,String time1,String time2) {
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("SELECT * FROM( SELECT s.uid,s.areaid,s.area,s.deviceType,s.deviceName,s.address,s.longitude,s.latitude,r.devicestate,r.memo,r.endTime ");
		sqlstr.append(" FROM(SELECT n.uid,n.areaid,a.area,n.deviceType,n.deviceName,n.address,n.longitude,n.latitude ");
		sqlstr.append(" from nfcinfo n,areaidarea a where 1=1 ");
		if(query.getUid()!=null){
			sqlstr.append(" and n.uid like '%"+query.getUid()+"%'");
		}
		if(query.getDeviceName()!=null){
			sqlstr.append(" and n.deviceName like '%"+query.getDeviceName()+"%'");
		}
		if(areaIds!=null){
			for (int i = 0; i < areaIds.size(); i++) {
				if(i==0){
					sqlstr.append(" and n.areaid in("+areaIds.get(i));
				}else if(i==areaIds.size() - 1){
					sqlstr.append(","+areaIds.get(i));
				}else{
					sqlstr.append(","+areaIds.get(i));
				}
			}
			sqlstr.append(") ");
		}
		if(!query.getAreaId().equals("0")){
			sqlstr.append(" and a.areaId = ?");
		}
		sqlstr.append(" and a.areaId = n.areaid) as s ");
		sqlstr.append(" LEFT JOIN nfcrecord r on r.uuid = s.uid where 1 = 1");
		if(query.getEndTime_1()!=null&&query.getEndTime_1()!=""){
			sqlstr.append("r.endTime <'"+query.getEndTime_1()+" 00:00:01"+"' AND r.endTime> '"+query.getEndTime_2()+" 23:59:59' ");
		}
		if(query.getDevicestate()!=null&&query.getDevicestate().length()>0){
			//sqlstr.append(" and r.devicestate= "+query.getDevicestate()+" ");
			if(query.getDevicestate().equals("0")) {
				sqlstr.append(" and (r.devicestate = "+ query.getDevicestate() +" or r.devicestate is null) ");
			}else{
				sqlstr.append(" and r.devicestate= "+query.getDevicestate());
			}
		}
		sqlstr.append(" ORDER BY r.endTime DESC) as cc GROUP BY uid");
		sqlstr.append(" limit "+query.getStartRow()+" , "+query.getPageSize());
		CountValue cv = new CountValue();
		int macNum = 0;//总数
		int netStaterNum = 0;//合格
		int noNetStater = 0;//不合格
		int otherNum = 0;//待检
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr.toString());
		ResultSet rs = null;
		List<NFC_infoEntity> nfcList = new ArrayList<NFC_infoEntity>();
		try {
			if(!query.getAreaId().equals("0")){
				ps.setString(1, query.getAreaId());
			}
			
			int row = query.getStartRow();
			rs = ps.executeQuery();
			while(rs.next()){
				NFC_infoEntity nfc = new NFC_infoEntity();
				nfc.setRow(++row);
				macNum = nfc.getRow();
				int states = rs.getInt("devicestate");
				nfc.setNowState(states+"");
				if(states == 0){
					nfc.setDevicestate("待检");
					otherNum++;
				}else if(states == 1){
					nfc.setDevicestate("合格");
					netStaterNum++;
				}else{
					nfc.setDevicestate("不合格");
					noNetStater++;
				}
				
				nfc.setUid(rs.getString("uid"));
				nfc.setAddress(rs.getString("address"));
				nfc.setAddTime(rs.getString("endTime"));
				nfc.setAreaId(rs.getString("areaid"));
				nfc.setAreaName(rs.getString("area"));
				nfc.setLatitude(rs.getString("latitude"));
				nfc.setLongitude(rs.getString("longitude"));
				nfc.setDeviceName(rs.getString("deviceName"));
				nfc.setDeviceType(rs.getInt("deviceType"));
				nfc.setMemo(rs.getString("memo"));
				cv.setMacNum(macNum);
				cv.setNetStaterNum(netStaterNum);
				cv.setNoNetStater(noNetStater);
				cv.setOtherNum(otherNum);
				nfc.setCv(cv);
				nfcList.add(nfc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return nfcList;
	}
	
	@Override
	public NFC_infoEntity getNFCInfo_map(List<String> areaIds,NFC_infoEntity query) {
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("SELECT * FROM( SELECT s.uid,s.areaid,s.area,s.deviceType,s.deviceName,s.address,s.longitude,s.latitude,r.devicestate,r.memo,r.endTime ");
		sqlstr.append(" FROM(SELECT n.uid,n.areaid,a.area,n.deviceType,n.deviceName,n.address,n.longitude,n.latitude ");
		sqlstr.append(" from nfcinfo n,areaidarea a where 1=1 ");
		if(query.getUid()!=null){
			sqlstr.append(" and n.uid = ?");
		}
		if(areaIds!=null){
			for (int i = 0; i < areaIds.size(); i++) {
				if(i==0){
					sqlstr.append(" and n.areaid in("+areaIds.get(i));
				}else if(i==areaIds.size() - 1){
					sqlstr.append(","+areaIds.get(i));
				}else{
					sqlstr.append(","+areaIds.get(i));
				}
			}
			sqlstr.append(") ");
		}
		if(query.getAreaId()!=null&&query.getAreaId().length()>0){
			sqlstr.append(" and a.areaId = ?");
		}
		sqlstr.append(" and a.areaId = n.areaid) as s ");
		sqlstr.append(" LEFT JOIN nfcrecord r on r.uuid = s.uid ");
		if(query.getEndTime_1() != null && query.getEndTime_2() !=null )
			sqlstr.append("AND r.endTime > '"+query.getEndTime_1()+" 00:00:01'"+ " AND r.endTime< '"+query.getEndTime_2()+" 23:59:59' ");
		sqlstr.append(" ORDER BY endTime DESC)as cc GROUP BY uid");
//		sqlstr.append(" limit "+query.getStartRow()+" , "+query.getPageSize());
//		String getTime = GetTime.ConvertTimeByLong();
//		String endTime_1 = getTime.substring(0,8)+"01 00:00:01";
//		String endTime_2 = getTime.substring(0,8)+"31 23:59:59";
//		if(query.getEndTime_1()!=null&&query.getEndTime_1()!=""){
//			endTime_1 = query.getEndTime_1()+" 00:00:01";
//			endTime_2 = query.getEndTime_2()+" 23:59:59";
//		}
		CountValue cv = new CountValue();
		int macNum = 0;//总数
		int netStaterNum = 0;//合格
		int noNetStater = 0;//不合格
		int otherNum = 0;//待检
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr.toString());
		ResultSet rs = null;
		NFC_infoEntity nfcEntity = new NFC_infoEntity();
		try {
			if(query.getUid()!=null){
				ps.setString(1, query.getUid());
				if(query.getAreaId()!=null&&query.getAreaId().length()>0){
					ps.setString(2, query.getAreaId());
//					ps.setString(3, endTime_2);
//					ps.setString(4, endTime_1);
				}
//				else{
//					ps.setString(2, endTime_2);
//					ps.setString(3, endTime_1);
//				}
			}else{
				if(query.getAreaId()!=null&&query.getAreaId().length()>0){
					ps.setString(1, query.getAreaId());
//					ps.setString(2, endTime_2);
//					ps.setString(3, endTime_1);
				}
//				else{
//					ps.setString(1, endTime_2);
//					ps.setString(2, endTime_1);
//				}
			}
			
			int row = query.getStartRow();
			rs = ps.executeQuery();
			while(rs.next()){
				nfcEntity.setRow(++row);
				macNum = nfcEntity.getRow();
				int states = rs.getInt("devicestate");
				if(states == 0){
					otherNum++;
				}else if(states == 1){
					netStaterNum++;
				}else{
					noNetStater++;
				}
				cv.setMacNum(macNum);
				cv.setNetStaterNum(netStaterNum);
				cv.setNoNetStater(noNetStater);
				cv.setOtherNum(otherNum);
				nfcEntity.setCv(cv);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return nfcEntity;
	}
	
	@Override
	public List<NFC_infoEntity> getAllNFCInfo_map(List<String> areaIds,NFC_infoEntity query) {
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("SELECT * FROM( SELECT s.uid,s.areaid,s.area,s.deviceType,s.deviceName,s.address,s.longitude,s.latitude,r.devicestate,r.memo,r.endTime ");
		sqlstr.append(" FROM(SELECT n.uid,n.areaid,a.area,n.deviceType,n.deviceName,n.address,n.longitude,n.latitude ");
		sqlstr.append(" from nfcinfo n,areaidarea a where 1=1 ");
		
		if(query.getUid()!=null){
			sqlstr.append(" and n.uid = '");
			sqlstr.append(query.getUid());
			sqlstr.append("' ");
		}
		
		if(areaIds!=null){
			for (int i = 0; i < areaIds.size(); i++) {
				if(i==0){
					sqlstr.append(" and n.areaid in("+areaIds.get(i));
				}else if(i==areaIds.size() - 1){
					sqlstr.append(","+areaIds.get(i));
				}else{
					sqlstr.append(","+areaIds.get(i));
				}
			}
			sqlstr.append(") ");
		}
		
		if(query.getAreaId()!=null&&query.getAreaId().length()>0){
			sqlstr.append(" and a.areaId = '");
			sqlstr.append(query.getAreaId());
			sqlstr.append("' ");
		}
		
		sqlstr.append(" and a.areaId = n.areaid) as s ");
		
		sqlstr.append(" LEFT JOIN nfcrecord r on r.uuid = s.uid ");
		
//		String getTime = GetTime.ConvertTimeByLong();
//		String endTime_1 = getTime.substring(0,8)+"01 00:00:01";
//		String endTime_2 = getTime.substring(0,8)+"31 23:59:59";
		if(query.getEndTime_1()!=null&&query.getEndTime_1()!=""){
			String endTime_1 = query.getEndTime_1()+" 00:00:01";
			String endTime_2 = query.getEndTime_2()+" 23:59:59";
			
			sqlstr.append("AND r.endTime < '");
			sqlstr.append(endTime_2);
			sqlstr.append("' AND r.endTime>'");
			sqlstr.append(endTime_1);
			sqlstr.append("' ");
		}
		
		sqlstr.append(" ORDER BY endTime DESC)as cc GROUP BY uid");
//		sqlstr.append(" limit "+query.getStartRow()+" , "+query.getPageSize());
//<<<<<<< .mine
//		
//=======
//		String getTime = GetTime.ConvertTimeByLong();
//		String endTime_1 = getTime.substring(0,10)+" 00:00:01";
//		String endTime_2 = getTime.substring(0,10)+" 23:59:59";
//		if(query.getEndTime_1()!=null&&query.getEndTime_1()!=""){
//			endTime_1 = query.getEndTime_1()+" 00:00:01";
//			endTime_2 = query.getEndTime_2()+" 23:59:59";
//		}
//>>>>>>> .r1907
		CountValue cv = new CountValue();
		int macNum = 0;//总数
		int netStaterNum = 0;//合格
		int noNetStater = 0;//不合格
		int otherNum = 0;//待检
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr.toString());
		ResultSet rs = null;
		List<NFC_infoEntity> nfcList = new ArrayList<NFC_infoEntity>();
		try {
//			if(query.getUid()!=null){
//				ps.setString(1, query.getUid());
//				if(query.getAreaId()!=null&&query.getAreaId().length()>0){
//					ps.setString(2, query.getAreaId());
//					ps.setString(3, endTime_2);
//					ps.setString(4, endTime_1);
//				}else{
//					ps.setString(2, endTime_2);
//					ps.setString(3, endTime_1);
//				}
//			}else{
//				if(query.getAreaId()!=null&&query.getAreaId().length()>0){
//					ps.setString(1, query.getAreaId());
//					ps.setString(2, endTime_2);
//					ps.setString(3, endTime_1);
//				}else{
//					ps.setString(1, endTime_2);
//					ps.setString(2, endTime_1);
//				}
//			}
			
			int row = query.getStartRow();
			rs = ps.executeQuery();
			while(rs.next()){
				NFC_infoEntity nfc = new NFC_infoEntity();
				nfc.setRow(++row);
				macNum = nfc.getRow();
				int states = rs.getInt("devicestate");
				nfc.setNowState(states+"");
				if(states == 0){
					nfc.setDevicestate("待检");
					otherNum++;
				}else if(states == 1){
					nfc.setDevicestate("合格");
					netStaterNum++;
				}else{
					nfc.setDevicestate("不合格");
					noNetStater++;
				}
				
				nfc.setUid(rs.getString("uid"));
				nfc.setAddress(rs.getString("address"));
				nfc.setAddTime(rs.getString("endTime"));
				nfc.setAreaId(rs.getString("areaid"));
				nfc.setAreaName(rs.getString("area"));
				nfc.setLatitude(rs.getString("latitude"));
				nfc.setLongitude(rs.getString("longitude"));
				nfc.setDeviceName(rs.getString("deviceName"));
				nfc.setDeviceType(rs.getInt("deviceType"));
				nfc.setMemo(rs.getString("memo"));
				cv.setMacNum(macNum);
				cv.setNetStaterNum(netStaterNum);
				cv.setNoNetStater(noNetStater);
				cv.setOtherNum(otherNum);
				nfc.setCv(cv);
				nfcList.add(nfc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return nfcList;
	}
	
	@Override
	public int getAllNFCInfo_web_count(List<String> areaIds,NFC_infoEntity query) {
		StringBuffer sqlstr = new StringBuffer();
		int result = 0;
		sqlstr.append("SELECT * FROM( SELECT s.uid,s.areaid,s.area,s.deviceType,s.deviceName,s.address,s.longitude,s.latitude,r.devicestate,r.memo,r.endTime ");
		sqlstr.append(" FROM(SELECT n.uid,n.areaid,a.area,n.deviceType,n.deviceName,n.address,n.longitude,n.latitude ");
		sqlstr.append(" from nfcinfo n,areaidarea a where 1=1 ");
		if(query.getUid()!=null){
			sqlstr.append(" and n.uid = ?");
		}
		if(query.getDeviceName()!=null){
			sqlstr.append(" and n.deviceName like '%"+query.getDeviceName()+"%'");
		}
		if(areaIds!=null){
			for (int i = 0; i < areaIds.size(); i++) {
				if(i==0){
					sqlstr.append(" and n.areaid in("+areaIds.get(i));
				}else if(i==areaIds.size() - 1){
					sqlstr.append(","+areaIds.get(i));
				}else{
					sqlstr.append(","+areaIds.get(i));
				}
			}
			sqlstr.append(") ");
		}
		if(!query.getAreaId().equals("0")){
			sqlstr.append(" and a.areaId = ?");
		}
		sqlstr.append(" and a.areaId = n.areaid) as s ");
		sqlstr.append(" LEFT JOIN nfcrecord r on r.uuid = s.uid where  r.endTime <? AND r.endTime>? ");
		if(query.getDevicestate()!=null&&query.getDevicestate().length()>0){
			sqlstr.append(" AND r.devicestate = " +query .getDevicestate()+" ");
		}
		sqlstr.append(" ORDER BY endTime DESC)as cc GROUP BY uid");
//		sqlstr.append(" limit "+query.getStartRow()+" , "+query.getPageSize());
		String getTime = GetTime.ConvertTimeByLong();
		String endTime_1 = getTime.substring(0,8)+"01 00:00:01";
		String endTime_2 = getTime.substring(0,8)+"31 23:59:59";
		if(query.getEndTime_1()!=null&&query.getEndTime_1()!=""){
			endTime_1 = query.getEndTime_1()+" 00:00:01";
			endTime_2 = query.getEndTime_2()+" 23:59:59";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr.toString());
		ResultSet rs = null;
		List<NFC_infoEntity> nfcList = new ArrayList<NFC_infoEntity>();
		try {
			if(query.getUid()!=null){
				ps.setString(1, query.getUid());
				if(!query.getAreaId().equals("0")){
					ps.setString(2, query.getAreaId());
					ps.setString(3, endTime_2);
					ps.setString(4, endTime_1);
				}else{
					ps.setString(2, endTime_2);
					ps.setString(3, endTime_1);
				}
			}else{
				if(!query.getAreaId().equals("0")){
					ps.setString(1, query.getAreaId());
					ps.setString(2, endTime_2);
					ps.setString(3, endTime_1);
				}else{
					ps.setString(1, endTime_2);
					ps.setString(2, endTime_1);
				}
			}
			
			rs = ps.executeQuery();
			while(rs.next()){
				result++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	@Override
	public int getAllNFCInfo_web_count(List<String> areaIds,NFC_infoEntity query,String time1,String time2) {
		StringBuffer sqlstr = new StringBuffer();
		int result = 0;
		sqlstr.append("SELECT * FROM( SELECT s.uid,s.areaid,s.area,s.deviceType,s.deviceName,s.address,s.longitude,s.latitude,r.devicestate,r.memo,r.endTime ");
		sqlstr.append(" FROM(SELECT n.uid,n.areaid,a.area,n.deviceType,n.deviceName,n.address,n.longitude,n.latitude ");
		sqlstr.append(" from nfcinfo n,areaidarea a where 1=1 ");
		if(query.getUid()!=null){
			sqlstr.append(" and n.uid = ?");
		}
		if(query.getDeviceName()!=null){
			sqlstr.append(" and n.deviceName like '%"+query.getDeviceName()+"%'");
		}
		if(areaIds!=null){
			for (int i = 0; i < areaIds.size(); i++) {
				if(i==0){
					sqlstr.append(" and n.areaid in("+areaIds.get(i));
				}else if(i==areaIds.size() - 1){
					sqlstr.append(","+areaIds.get(i));
				}else{
					sqlstr.append(","+areaIds.get(i));
				}
			}
			sqlstr.append(") ");
		}
		if(!query.getAreaId().equals("0")){
			sqlstr.append(" and a.areaId = ?");
		}
		sqlstr.append(" and a.areaId = n.areaid) as s ");
		sqlstr.append(" LEFT JOIN nfcrecord r on r.uuid = s.uid where 1 = 1 ");
		if(query.getEndTime_1()!=null&&query.getEndTime_1()!=""){
			sqlstr.append("r.endTime <'"+query.getEndTime_1()+" 00:00:01"+"' AND r.endTime> '"+query.getEndTime_2()+" 23:59:59'");
		}
		if(query.getDevicestate()!=null&&query.getDevicestate().length()>0){
			//sqlstr.append(" AND r.devicestate = " +query .getDevicestate()+" ");
			if(query.getDevicestate().equals("0")) {
				sqlstr.append(" and (r.devicestate = "+ query.getDevicestate() +" or r.devicestate is null) ");
			}else{
				sqlstr.append(" and r.devicestate= "+query.getDevicestate());
			}
		}
		sqlstr.append(" ORDER BY r.endTime DESC)as cc GROUP BY uid");
//		sqlstr.append(" limit "+query.getStartRow()+" , "+query.getPageSize());
		String getTime = GetTime.ConvertTimeByLong();
		String endTime_1 = getTime.substring(0,8)+"01 00:00:01";
		String endTime_2 = getTime.substring(0,8)+"31 23:59:59";
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr.toString());
		ResultSet rs = null;
		List<NFC_infoEntity> nfcList = new ArrayList<NFC_infoEntity>();
		try {
			if(query.getUid()!=null){
				ps.setString(1, query.getUid());
				if(!query.getAreaId().equals("0")){
					ps.setString(2, query.getAreaId());
//					ps.setString(3, endTime_2);
//					ps.setString(4, endTime_1);
				}else{
//					ps.setString(2, endTime_2);
//					ps.setString(3, endTime_1);
				}
			}else{
				if(!query.getAreaId().equals("0")){
					ps.setString(1, query.getAreaId());
//					ps.setString(2, endTime_2);
//					ps.setString(3, endTime_1);
				}else{
//					ps.setString(1, endTime_2);
//					ps.setString(2, endTime_1);
				}
			}
			
			rs = ps.executeQuery();
			while(rs.next()){
				result++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	@Override
	public List<NFC_infoEntity> getAllNFCInfo_web_record(String uuid,NFC_infoEntity query) {
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("SELECT r.uuid,r.longitude,r.latitude,r.userId,r.endTime,r.devicestate,r.memo,n.areaid,a.area,n.deviceType,n.deviceName,n.address FROM nfcrecord r,nfcinfo n,areaidarea a WHERE a.areaid = n.areaid AND n.uid = r.uuid ");
		if(uuid!=null&&uuid!=""){
			sqlstr.append(" AND r.uuid = ? ");
		}
		if (query.getDeviceName()!=null && query.getDeviceName().length() > 0) {
			sqlstr.append(" AND n.deviceName like'%"+ query.getDeviceName()+"%'");
		}
		if (query.getDevicestate()!=null && query.getDevicestate().length() > 0) {
			//sqlstr.append(" AND r.devicestate="+ query.getDevicestate());
			if(query.getDevicestate().equals("0")) {
				sqlstr.append(" and (r.devicestate = "+ query.getDevicestate() +" or r.devicestate is null) ");
			}else{
				sqlstr.append(" and r.devicestate= "+query.getDevicestate());
			}
		}
		if(!"0".equals(query.getAreaId())){
			sqlstr.append(" and a.areaid = "+query.getAreaId());
		}else if (query.getAreaIds() != null){
			sqlstr.append(" and a.areaid in(");
			for(String areaid:query.getAreaIds()){
				sqlstr.append(areaid+",");
			}
			sqlstr.append("0)");
		}else {
			sqlstr.append(" and a.areaid = 0 ");
		}
		if(StringUtils.isNotBlank(query.getDeviceName())){
			sqlstr.append(" AND n.deviceName like '%"+query.getDeviceName()+"%' ");
		}
		if (query.getEndTime_1()!=null && query.getEndTime_1().length() > 0) {
			sqlstr.append(" AND r.endtime >'"+ query.getEndTime_1()+" 00:00:01'");
		}
		if (query.getEndTime_2()!=null && query.getEndTime_2().length() > 0) {
			sqlstr.append(" AND r.endtime <'"+ query.getEndTime_2()+" 23:59:59'");
		}
		sqlstr.append("  ORDER BY endTime DESC");
		sqlstr.append(" limit "+query.getStartRow()+" , "+query.getPageSize());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr.toString());
		ResultSet rs = null;
		List<NFC_infoEntity> nfcList = new ArrayList<NFC_infoEntity>();
		try {
			int row = query.getStartRow();
			if(uuid!=null&&uuid!=""){
				ps.setString(1, uuid);
			}
			rs = ps.executeQuery();
			while(rs.next()){
				NFC_infoEntity nfc = new NFC_infoEntity();
				if(query.getNowState() == "0"||"0".equals(query.getNowState())){
					nfc.setNowState("待检");
				}else if(query.getNowState() == "1"||"1".equals(query.getNowState())){
					nfc.setNowState("合格");
				}else{
					nfc.setNowState("不合格");
				}
				
				nfc.setRow(++row);
				int states = rs.getInt("devicestate");
				if(states == 0){
					nfc.setDevicestate("待检");
				}else if(states == 1){
					nfc.setDevicestate("合格");
				}else{
					nfc.setDevicestate("不合格");
				}
				nfc.setLatitude(rs.getString("latitude"));
				nfc.setLongitude(rs.getString("longitude"));
				nfc.setUid(rs.getString("uuid"));
				nfc.setUserId(rs.getString("userId"));
				nfc.setAddress(rs.getString("address"));
				nfc.setAddTime(rs.getString("endTime"));
				nfc.setAreaId(rs.getString("areaid"));
				nfc.setAreaName(rs.getString("area"));
				nfc.setDeviceName(rs.getString("deviceName"));
				nfc.setDeviceType(rs.getInt("deviceType"));
				nfc.setMemo(rs.getString("memo"));
				nfcList.add(nfc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return nfcList;
	}
	
	@Override
	public int getAllNFCInfo_web_record_count1(String uuid,NFC_infoEntity query) {
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("SELECT count(*) FROM nfcrecord r,nfcinfo n,areaidarea a WHERE a.areaid = n.areaid AND n.uid = r.uuid ");
		/*if(!"0".equals(query.getAreaId())){
			sqlstr.append(" and a.areaid = "+query.getAreaId());
		}else{
			sqlstr.append(" and a.areaid in(");
			for(String areaid:query.getAreaIds()){
				sqlstr.append(areaid+",");
			}
			sqlstr.append("0)");
		}*/
		if (query.getUid()!=null && query.getUid().length() > 0) {
			sqlstr.append(" AND n.uid='"+ query.getUid()+"'");
		}
		if (query.getDeviceName()!=null && query.getDeviceName().length() > 0) {
			sqlstr.append(" AND n.deviceName like '%"+ query.getDeviceName()+"%'");
		}
		if (query.getDevicestate()!=null && query.getDevicestate().length() > 0) {
			//sqlstr.append(" AND r.devicestate="+ query.getDevicestate());
			if(query.getDevicestate().equals("0")) {
				sqlstr.append(" and (r.devicestate = "+ query.getDevicestate() +" or r.devicestate is null) ");
			}else{
				sqlstr.append(" and r.devicestate= "+query.getDevicestate());
			}
		}
		if (query.getEndTime_1()!=null && query.getEndTime_1().length() > 0) {
			sqlstr.append(" AND r.endtime >'"+ query.getEndTime_1()+" 00:00:01'");
		}
		if (query.getEndTime_2()!=null && query.getEndTime_2().length() > 0) {
			sqlstr.append(" AND r.endtime <'"+ query.getEndTime_2()+" 23:59:59'");
		}
		sqlstr.append(" ORDER BY endTime DESC");
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr.toString());
		ResultSet rs = null;
		int result = 0;
		try {
//			ps.setString(1, uuid);
			rs = ps.executeQuery();
			while(rs.next()){
				result = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	@Override
	public List<NFC_infoEntity> getAllNFCInfo_web_record1(String uuid,NFC_infoEntity query) {
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("SELECT r.uuid,r.longitude,r.latitude,r.userId,r.endTime,r.devicestate,r.memo,n.areaid,a.area,n.deviceType,n.deviceName,n.address,r.items FROM nfcrecord r,nfcinfo n,areaidarea a WHERE a.areaid = n.areaid AND n.uid = r.uuid ");
		if(uuid!=null&&uuid!=""){
			sqlstr.append(" AND r.uuid = ? ");
		}
		if (query.getDeviceName()!=null && query.getDeviceName().length() > 0) {
			sqlstr.append(" AND n.deviceName like'%"+ query.getDeviceName()+"%'");
		}
		if (query.getDevicestate()!=null && query.getDevicestate().length() > 0) {
			//sqlstr.append(" AND r.devicestate="+ query.getDevicestate());
			if(query.getDevicestate().equals("0")) {
				sqlstr.append(" and (r.devicestate = "+ query.getDevicestate() +" or r.devicestate is null) ");
			}else{
				sqlstr.append(" and r.devicestate= "+query.getDevicestate());
			}
		}
		/*if(!"0".equals(query.getAreaId())){
			sqlstr.append(" and a.areaid = "+query.getAreaId());
		}else if (query.getAreaIds() != null){
			sqlstr.append(" and a.areaid in(");
			for(String areaid:query.getAreaIds()){
				sqlstr.append(areaid+",");
			}
			sqlstr.append("0)");
		}else {
			sqlstr.append(" and a.areaid = 0 ");
		}*/
		if(StringUtils.isNotBlank(query.getDeviceName())){
			sqlstr.append(" AND n.deviceName like '%"+query.getDeviceName()+"%' ");
		}
		if (query.getEndTime_1()!=null && query.getEndTime_1().length() > 0) {
			sqlstr.append(" AND r.endtime >'"+ query.getEndTime_1()+" 00:00:01'");
		}
		if (query.getEndTime_2()!=null && query.getEndTime_2().length() > 0) {
			sqlstr.append(" AND r.endtime <'"+ query.getEndTime_2()+" 23:59:59'");
		}
		sqlstr.append("  ORDER BY endTime DESC");
		sqlstr.append(" limit "+query.getStartRow()+" , "+query.getPageSize());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr.toString());
		ResultSet rs = null;
		List<NFC_infoEntity> nfcList = new ArrayList<NFC_infoEntity>();
		try {
			int row = query.getStartRow();
			if(uuid!=null&&uuid!=""){
				ps.setString(1, uuid);
			}
			rs = ps.executeQuery();
			while(rs.next()){
				NFC_infoEntity nfc = new NFC_infoEntity();
				if(query.getNowState() == "0"||"0".equals(query.getNowState())){
					nfc.setNowState("待检");
				}else if(query.getNowState() == "1"||"1".equals(query.getNowState())){
					nfc.setNowState("合格");
				}else{
					nfc.setNowState("不合格");
				}
				
				nfc.setRow(++row);
				int states = rs.getInt("devicestate");
				if(states == 0){
					nfc.setDevicestate("待检");
				}else if(states == 1){
					nfc.setDevicestate("合格");
				}else{
					nfc.setDevicestate("不合格");
				}
				nfc.setLatitude(rs.getString("latitude"));
				nfc.setLongitude(rs.getString("longitude"));
				nfc.setUid(rs.getString("uuid"));
				nfc.setUserId(rs.getString("userId"));
				nfc.setAddress(rs.getString("address"));
				nfc.setAddTime(rs.getString("endTime"));
				nfc.setAreaId(rs.getString("areaid"));
				nfc.setAreaName(rs.getString("area"));
				nfc.setDeviceName(rs.getString("deviceName"));
				nfc.setDeviceType(rs.getInt("deviceType"));
				nfc.setMemo(rs.getString("memo"));
				nfc.setItems(rs.getString("items"));
				nfcList.add(nfc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return nfcList;
	}
	
	@Override
	public int getAllNFCInfo_web_record_count(String uuid,NFC_infoEntity query) {
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("SELECT count(*) FROM nfcrecord r,nfcinfo n,areaidarea a WHERE a.areaid = n.areaid AND n.uid = r.uuid ");
		if(!"0".equals(query.getAreaId())){
			sqlstr.append(" and a.areaid = "+query.getAreaId());
		}else{
			sqlstr.append(" and a.areaid in(");
			for(String areaid:query.getAreaIds()){
				sqlstr.append(areaid+",");
			}
			sqlstr.append("0)");
		}
		if (query.getUid()!=null && query.getUid().length() > 0) {
			sqlstr.append(" AND n.uid='"+ query.getUid()+"'");
		}
		if (query.getDeviceName()!=null && query.getDeviceName().length() > 0) {
			sqlstr.append(" AND n.deviceName like '%"+ query.getDeviceName()+"%'");
		}
		if (query.getDevicestate()!=null && query.getDevicestate().length() > 0) {
			//sqlstr.append(" AND r.devicestate="+ query.getDevicestate());
			if(query.getDevicestate().equals("0")) {
				sqlstr.append(" and (r.devicestate = "+ query.getDevicestate() +" or r.devicestate is null) ");
			}else{
				sqlstr.append(" and r.devicestate= "+query.getDevicestate());
			}
		}
		if (query.getEndTime_1()!=null && query.getEndTime_1().length() > 0) {
			sqlstr.append(" AND r.endtime >'"+ query.getEndTime_1()+" 00:00:01'");
		}
		if (query.getEndTime_2()!=null && query.getEndTime_2().length() > 0) {
			sqlstr.append(" AND r.endtime <'"+ query.getEndTime_2()+" 23:59:59'");
		}
		sqlstr.append(" ORDER BY endTime DESC");
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr.toString());
		ResultSet rs = null;
		int result = 0;
		try {
//			ps.setString(1, uuid);
			rs = ps.executeQuery();
			while(rs.next()){
				result = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	@Override
	public AllNFCInfoEntity getAllNFCRecord(String uid, String page,String userId,String privilege) {
		String sql ="";
		if(uid!=null&&uid.length()>0){
			sql = "SELECT uuid,n.longitude,n.latitude,n.userId,endTime,devicestate,n.memo,photo1,deviceName,a.areaid,a.area,n.items FROM nfcrecord n,nfcinfo i,areaidarea a WHERE uuid = ? AND uuid=uid and i.areaid=a.areaId ORDER BY endTime DESC ";
		}else{
			AreaDao mAreaDao = new AreaDaoImpl();
			List<String> areaIds = new ArrayList<String>();
			areaIds = mAreaDao.getAreaStr(userId, privilege+"");
			sql = "SELECT uuid,n.longitude,n.latitude,n.userId,endTime,devicestate,n.memo,photo1,deviceName,a.areaid,a.area,n.items FROM nfcrecord n,nfcinfo i,areaidarea a WHERE uuid "+ 
					"in (SELECT uid from nfcinfo where  areaid in( ";
			for (int i = 0; i < areaIds.size(); i++) {
				if(i==0){
					sql = sql + areaIds.get(i);
				}else if(i==areaIds.size() - 1){
					sql = sql + "," + areaIds.get(i);
				}else{
					sql = sql + "," + areaIds.get(i);
				}
			}
			sql=sql+")) AND uuid=uid and i.areaid=a.areaId ORDER BY endTime DESC ";
		}
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*20;
				int endNum = 20;
				sql = sql +" limit "+startNum+" , "+endNum;
			}
		}
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		AllNFCInfoEntity info = null;
		List<NFCInfoEntity> nfcList = new ArrayList<NFCInfoEntity>();
		try {
			if(uid!=null&&uid.length()>0){
				ps.setString(1, uid);
			}
			rs = ps.executeQuery();
			while(rs.next()){
				NFCInfoEntity nfc = new NFCInfoEntity();
				nfc.setUid(rs.getString("uuid"));
				nfc.setLongitude(rs.getString("longitude"));
				nfc.setLatitude(rs.getString("latitude"));
				nfc.setUserId(rs.getString("userId"));
				nfc.setAddTime(rs.getString("endTime"));
				nfc.setDevicestate(rs.getInt("devicestate"));
				nfc.setMemo(rs.getString("memo"));
				nfc.setPhoto1(rs.getString("photo1"));
//				if(uid==null||uid.length()==0){
					nfc.setAreaName(rs.getString("area"));
					nfc.setDeviceName(rs.getString("deviceName"));	
//				}
					nfc.setItems(rs.getString("items"));
				nfcList.add(nfc);
			}
			info = new AllNFCInfoEntity();
			info.setNfcList(nfcList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return info;
	}
	
	@Override
	public NeedNFCRecordEntity getAllNFCRecordByCondition( String page,String userId,String privilege,String areaid,String starttime,String endtime) {
		String sql ="";
		sql = "SELECT uuid,n.longitude,n.latitude,n.userId,endTime,devicestate,n.memo,photo1,deviceName,a.areaid,a.area,t.devTypeName,i.deviceType,n.items "
		+"FROM nfcrecord n,nfcinfo i,areaidarea a,nfc_device_type t WHERE i.areaid = ? AND uuid=uid and i.areaid=a.areaId and i.deviceType=t.devTypeId and endtime between ? and ? ORDER BY endTime DESC ";
		
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*20;
				int endNum = 20;
				sql = sql +" limit "+startNum+" , "+endNum;
			}
		}
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		NeedNFCRecordEntity info = null;
		List<OneKindDevRecord> nfcList = new ArrayList<OneKindDevRecord>();
		try {

			ps.setString(1, areaid);
			ps.setString(2, starttime+ " 00:00:01");
			ps.setString(3, endtime+" 23:59:59");
			
			rs = ps.executeQuery();
			Map<String,OneKindDevRecord> devTypeMap=new HashMap<String, OneKindDevRecord>();
			while(rs.next()){
				NFCInfoEntity nfc = new NFCInfoEntity();
				String type=rs.getString("devTypeName");
				String typeId=rs.getString("deviceType");
				if(!devTypeMap.containsKey(type)){
					devTypeMap.put(type, new OneKindDevRecord(type,typeId,getNFCCheakItems(typeId).getItems()));
				}
				
				nfc.setUid(rs.getString("uuid"));
				nfc.setLongitude(rs.getString("longitude"));
				nfc.setLatitude(rs.getString("latitude"));
				nfc.setUserId(rs.getString("userId"));
				nfc.setAddTime(rs.getString("endTime"));
				nfc.setDevicestate(rs.getInt("devicestate"));
				nfc.setMemo(rs.getString("memo"));
				nfc.setPhoto1(rs.getString("photo1"));
				nfc.setAreaName(rs.getString("area"));
				nfc.setDeviceName(rs.getString("deviceName"));	
				nfc.setItems(rs.getString("items"));
				devTypeMap.get(type).getList().add(nfc);
			}
			info = new NeedNFCRecordEntity();
			for(String s:devTypeMap.keySet()) {
				nfcList.add(devTypeMap.get(s));
			}
			info.setNfcList(nfcList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return info;
	}

	@Override
	public AllCheakItemEntity getNFCCheakItems(String devType) {
		String sql ="";
		sql = "SELECT c.id,c.item from  nfc_cheak_item c,nfc_dev_item d where d.devtype=? and c.id=d.item";

		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		AllCheakItemEntity info = null;
		List<NFCCheakItem> nfcList = new ArrayList<NFCCheakItem>();
		try {
			ps.setString(1, devType);
			rs = ps.executeQuery();
			while(rs.next()){
				NFCCheakItem nfc = new NFCCheakItem();
				nfc.setId(rs.getString("id"));
				nfc.setItemName(rs.getString("item"));
				nfcList.add(nfc);
			}
			info = new AllCheakItemEntity();
			info.setItems(nfcList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return info;
	}
	
	
	@Override
	public AllSmokeEntity getSmokeAllInfo(String userId, String privilege,
			String page, String areaId, String placeTypeId, String prentId) {

		// TODO Auto-generated method stub
		mAreaDao = new AreaDaoImpl();
		List<String> listNum = mAreaDao.getAreaStr(userId,privilege);
		if(listNum==null&&!privilege.equals("1")){
			return null;
		}
		mIfDealAlarmDao = new IfDealAlarmDaoImpl();
		List<String> listMac = mIfDealAlarmDao.getUserSmokeMac(listNum);
		if(listMac==null&&!privilege.equals("1")){
			return null;
		}
//		Map<String, Integer> macStateMap = mIfDealAlarmDao.getIfDealAlarm(listMac);
		mAllCameraDao = new AllCameraDaoImpl();
		Map<String,CameraBean> mapCamera = mAllCameraDao.getCameraByMac(listNum);
		Map<Integer, String> mapArea = mAreaDao.getAreaById(listNum);
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		Map<String,String> map = mPlaceTypeDao.getShopTypeById();

		boolean isAllArea = false;
		StringBuffer strSql = new StringBuffer();
		if((areaId!=null&&areaId.length()>0)||(prentId!=null&&prentId.length()>0)){
			isAllArea = false;
			if(prentId!=null&&prentId.length()>0){
				strSql.append(" where areaId IN(SELECT areaId FROM areaidarea WHERE parentId = "+prentId+") ");
			}else{
				strSql.append(" where areaId = "+areaId);
			}
		}else{
			isAllArea = true;
			int len = listNum.size();
			if(len==1){
				strSql.append(" where areaId in (?)");
			}else{
				for(int i=0;i<len;i++){
					if(i==0){
						strSql.append(" where areaId in (?, ");
					}else if(i==(len-1)){
						strSql.append(" ?)");
					}else{
						strSql.append(" ?, ");
					}
				}
			}
		}
		
		strSql.append(" AND deviceType<11 and mac<>repeater AND deviceType <> 5");	//add by lzo at 2017-5-24
		
		if(placeTypeId!=null&&placeTypeId.length()>0){
			int placeTypeIdInt = Integer.parseInt(placeTypeId);
			if(placeTypeIdInt>0){
				strSql.append(" and placeTypeId = "+placeTypeIdInt);
			}
		}
		
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*20;
				int endNum = 20;
				strSql.append(" order by time desc limit "+startNum+" , "+endNum);
			}
		}
		
		String loginSql = new String("select address,latitude,longitude,mac,named,netState,deviceType,placeAddress" +
				",principal1,principal1Phone,principal2,principal2Phone,repeater,areaId,placeTypeId,ifAlarm" +
				" from smoke ");
//				" from smoke WHERE deviceType<11 AND deviceType <> 5");	//add by lzo at 2017-5-24
		String sqlStr= new String(loginSql+strSql.toString());
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
				if(ae==null){
					ae = new AllSmokeEntity();
				}
				String mac = rs.getString("mac");
				int areaID = rs.getInt("areaId");
				String placeTypeID = rs.getString("placeTypeId");
				String placeName =null;
				if(map!=null&&map.size()>0){
					placeName = map.get(placeTypeID);
				}
				String areaName =null;
				if(mapArea!=null&&mapArea.size()>0){
					areaName = mapArea.get(areaID);
				}
				SmokeBean sb = new SmokeBean();
				if(sbList==null){
					sbList = new ArrayList<SmokeBean>();	
				}
				CameraBean cb =null;
				if(mapCamera!=null&&mapCamera.size()>0){
					cb = mapCamera.get(mac);
				}
				if(cb!=null){
					cb.setAreaName(areaName);
					cb.setPlaceType(placeName);
					sb.setCamera(cb);
				}else{
					cb = new CameraBean();
					sb.setCamera(cb);
				}
				sb.setAddress(rs.getString("address"));
				sb.setDeviceType(rs.getInt("deviceType"));
				sb.setLatitude(rs.getString("latitude"));
				sb.setLongitude(rs.getString("longitude"));
				sb.setMac(mac);
				sb.setName(rs.getString("named"));
				sb.setNetState(rs.getInt("netState"));
				sb.setPlaceeAddress(rs.getString("placeAddress"));
				sb.setPrincipal1(rs.getString("principal1"));
				sb.setPlaceType(placeName);
				sb.setAreaName(areaName);
				sb.setIfDealAlarm(rs.getInt("ifAlarm"));
				sb.setPrincipal1Phone(rs.getString("principal1Phone"));
				sb.setPrincipal2(rs.getString("principal2"));
				sb.setPrincipal2Phone(rs.getString("principal2Phone"));
				sb.setRepeater(rs.getString("repeater"));
				sbList.add(sb);
			}
			if(sbList!=null&&sbList.size()>0){
				ae.setError("获取烟感成功");
				ae.setErrorCode(0);
				ae.setSmoke(sbList);
			}
			
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
	
	
	//@@9.29  1级权限查询所有设备
	public AllSmokeEntity getAdminAllSmoke(String userId, String privilege,String page, String devType) {
		// TODO Auto-generated method stub
		mAreaDao = new AreaDaoImpl();
		mIfDealAlarmDao = new IfDealAlarmDaoImpl();
		List<String> listMac = getNormalAllSmoke(userId,privilege,page);
		if(listMac==null){
			return null;
		}
//		Map<String, Integer> macStateMap = mIfDealAlarmDao.getIfDealAlarm(listMac);
		List<String> listNum = mAreaDao.getAreaStrByMac(listMac);
		mAllCameraDao = new AllCameraDaoImpl();
		Map<String,CameraBean> mapCamera = mAllCameraDao.getCameraByMac(listNum);
		Map<Integer, String> mapArea = mAreaDao.getAreaById(listNum);
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		Map<String,String> map = mPlaceTypeDao.getShopTypeById();
		
		String pageSql = null;
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*20;
				int endNum = 20;
				pageSql = new String(" order by time desc limit "+startNum+" , "+endNum);
			}
		}
		
		StringBuffer strSql = new StringBuffer();
		int len = listMac.size();
		if(len==1){
			strSql.append(" and mac in (?)");
		}else{
			for(int i=0;i<len;i++){
				if(i==0){
					strSql.append(" and mac in (?, ");
				}else if(i==(len-1)){
					strSql.append(" ?)");
				}else{
					strSql.append(" ?, ");
				}
			}
		}
		
		strSql.append(Constant.devTypeChooseSQLStatement(devType));
//		strSql.append(" AND deviceType="+devType+" ");
		
//		if(devType=="1"||"1".equals(devType)){		//by liangbin
//			strSql.append(" AND deviceType not in (5,10,11,12,13,14,15,18,19,25,119,124,125) AND mac<>repeater ");
//		}else if(devType=="3"||"3".equals(devType)){		
//			strSql.append(" AND deviceType in (5)");
//		}else if(devType=="4"||"4".equals(devType)){		
//			strSql.append(" AND deviceType in (10,11,12,13,15,18,19,25,42,124,125)");
//		}else if(devType=="2"||"2".equals(devType)){		
//			strSql.append(" AND (mac=repeater or deviceType=119) ");
//		}
		String loginSql = new String("select address,latitude,longitude,mac,named,netState,deviceType,placeAddress" +
				",principal1,principal1Phone,principal2,principal2Phone,repeater,areaId,placeTypeId,ifAlarm,rssivalue,lowVoltage" +
//				" from smoke where deviceType<>5);
//				" from smoke where deviceType<>5 and mac<>repeater and deviceType not in(11,12,13,14,15)");     //update by lzz at 2017-5-19
				" from smoke where 1=1 ");
		String sqlStr = null;
		if(pageSql!=null){
			sqlStr= new String(loginSql+strSql.toString()+pageSql.toString());
		}else{
			sqlStr= new String(loginSql+strSql.toString());
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
		AllSmokeEntity ae = null;
		List<SmokeBean> sbList = null;
		try {
			for(int i=1;i<=len;i++){
				ps.setString(i, listMac.get(i-1));
			}
			rs = ps.executeQuery();
			while(rs.next()){
				if(ae==null){
					ae = new AllSmokeEntity();
				}
				String mac = rs.getString("mac");
				int areaId = rs.getInt("areaId");
				String placeTypeId = rs.getString("placeTypeId");
//				int ifDeal = 1;
//				if(macStateMap!=null&&macStateMap.size()>0){
//					ifDeal = macStateMap.get(mac);
//				}
				String placeName =null;
				if(map!=null&&map.size()>0){
					placeName = map.get(placeTypeId);
				}
				String areaName =null;
				if(mapArea!=null&&mapArea.size()>0){
					areaName = mapArea.get(areaId);
				}
				SmokeBean sb = new SmokeBean();
				if(sbList==null){
					sbList = new ArrayList<SmokeBean>();	
				}
				CameraBean cb =null;
				if(mapCamera!=null&&mapCamera.size()>0){
					cb = mapCamera.get(mac);
				}
				if(cb!=null){
					cb.setAreaName(areaName);
					cb.setPlaceType(placeName);
					sb.setCamera(cb);
				}else{
					cb = new CameraBean();
					sb.setCamera(cb);
				}
				sb.setAddress(rs.getString("address"));
				sb.setDeviceType(rs.getInt("deviceType"));
				sb.setLatitude(rs.getString("latitude"));
				sb.setLongitude(rs.getString("longitude"));
				sb.setMac(mac);
				sb.setName(rs.getString("named"));
				sb.setNetState(rs.getInt("netState"));
				sb.setPlaceeAddress(rs.getString("placeAddress"));
				sb.setPrincipal1(rs.getString("principal1"));
				sb.setPlaceType(placeName);
				sb.setAreaName(areaName);
				sb.setIfDealAlarm(rs.getInt("ifAlarm"));
				sb.setPrincipal1Phone(rs.getString("principal1Phone"));
				sb.setPrincipal2(rs.getString("principal2"));
				sb.setPrincipal2Phone(rs.getString("principal2Phone"));
				sb.setRepeater(rs.getString("repeater"));
				sb.setRssivalue(rs.getString("rssivalue"));
				sb.setLowVoltage(rs.getString("lowVoltage"));
				sbList.add(sb);
			}
			if(sbList!=null&&sbList.size()>0){
				ae.setError("获取烟感成功");
				ae.setErrorCode(0);
				ae.setSmoke(sbList);
			}
			
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
	
	public List<String> getNormalAllSmoke(String userId, String privilege,String page) {
		// TODO Auto-generated method stub
		String loginSql = "select smokeMac from useridsmoke where userId = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,loginSql);
		ResultSet rs = null;
		List<String> listMac = null;
		try {
			ps.setString(1, userId);
			rs = ps.executeQuery();
			while(rs.next()){
				if(listMac==null){
					listMac = new ArrayList<String>();
				}
				listMac.add(rs.getString("smokeMac"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return listMac;
	}
	
	//@@9.29  1级权限查询所有离线设备
			public AllSmokeEntity getAdminAllLossSmoke(String userId, String privilege,String page, String devType) {
				// TODO Auto-generated method stub
				mAreaDao = new AreaDaoImpl();
				mIfDealAlarmDao = new IfDealAlarmDaoImpl();
				List<String> listMac = getNormalAllSmoke(userId,privilege,page);
				if(listMac==null){
					return null;
				}
//				Map<String, Integer> macStateMap = mIfDealAlarmDao.getIfDealAlarm(listMac);
				List<String> listNum = mAreaDao.getAreaStrByMac(listMac);
				mAllCameraDao = new AllCameraDaoImpl();
				Map<String,CameraBean> mapCamera = mAllCameraDao.getCameraByMac(listNum);
				Map<Integer, String> mapArea = mAreaDao.getAreaById(listNum);
				mPlaceTypeDao = new PlaceTypeDaoImpl();
				Map<String,String> map = mPlaceTypeDao.getShopTypeById();
				
				String pageSql = null;
				if(page!=null&&page.length()>0){
					int pageInt= Integer.parseInt(page);
					if(pageInt>0){
						int startNum = (pageInt-1)*20;
						int endNum = 20;
						pageSql = new String(" order by time desc limit "+startNum+" , "+endNum);
					}
				}
				
				StringBuffer strSql = new StringBuffer();
				int len = listMac.size();
				if(len==1){
					strSql.append(" and mac in (?)");
				}else{
					for(int i=0;i<len;i++){
						if(i==0){
							strSql.append(" and mac in (?, ");
						}else if(i==(len-1)){
							strSql.append(" ?)");
						}else{
							strSql.append(" ?, ");
						}
					}
				}
				
				strSql.append(Constant.devTypeChooseSQLStatement(devType));
				
//				if(devType=="1"||"1".equals(devType)){		//by liangbin
//					strSql.append(" AND deviceType not in (5,10,11,12,13,14,15,18,19,25,119,124,125) AND mac<>repeater ");
//				}else if(devType=="3"||"3".equals(devType)){		
//					strSql.append(" AND deviceType in (5)");
//				}else if(devType=="4"||"4".equals(devType)){		
//					strSql.append(" AND deviceType in (10,11,12,13,15,18,19,25,42,124,125)");
//				}else if(devType=="2"||"2".equals(devType)){		
//					strSql.append(" AND (mac=repeater or deviceType=119) ");
//				}
				String loginSql = new String("select address,latitude,longitude,mac,named,netState,deviceType,placeAddress" +
						",principal1,principal1Phone,principal2,principal2Phone,repeater,areaId,placeTypeId,ifAlarm" +
//						" from smoke where deviceType<>5);
//						" from smoke where deviceType<>5 and mac<>repeater and deviceType not in(11,12,13,14,15)");     //update by lzz at 2017-5-19
						" from smoke where  netState=0 ");
				String sqlStr = null;
				if(pageSql!=null){
					sqlStr= new String(loginSql+strSql.toString()+pageSql.toString());
				}else{
					sqlStr= new String(loginSql+strSql.toString());
				}
				Connection conn = DBConnectionManager.getConnection();
				PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
				ResultSet rs = null;
				AllSmokeEntity ae = null;
				List<SmokeBean> sbList = null;
				try {
					for(int i=1;i<=len;i++){
						ps.setString(i, listMac.get(i-1));
					}
					rs = ps.executeQuery();
					while(rs.next()){
						if(ae==null){
							ae = new AllSmokeEntity();
						}
						String mac = rs.getString("mac");
						int areaId = rs.getInt("areaId");
						String placeTypeId = rs.getString("placeTypeId");
//						int ifDeal = 1;
//						if(macStateMap!=null&&macStateMap.size()>0){
//							ifDeal = macStateMap.get(mac);
//						}
						String placeName =null;
						if(map!=null&&map.size()>0){
							placeName = map.get(placeTypeId);
						}
						String areaName =null;
						if(mapArea!=null&&mapArea.size()>0){
							areaName = mapArea.get(areaId);
						}
						SmokeBean sb = new SmokeBean();
						if(sbList==null){
							sbList = new ArrayList<SmokeBean>();	
						}
						CameraBean cb =null;
						if(mapCamera!=null&&mapCamera.size()>0){
							cb = mapCamera.get(mac);
						}
						if(cb!=null){
							cb.setAreaName(areaName);
							cb.setPlaceType(placeName);
							sb.setCamera(cb);
						}else{
							cb = new CameraBean();
							sb.setCamera(cb);
						}
						sb.setAddress(rs.getString("address"));
						sb.setDeviceType(rs.getInt("deviceType"));
						sb.setLatitude(rs.getString("latitude"));
						sb.setLongitude(rs.getString("longitude"));
						sb.setMac(mac);
						sb.setName(rs.getString("named"));
						sb.setNetState(rs.getInt("netState"));
						sb.setPlaceeAddress(rs.getString("placeAddress"));
						sb.setPrincipal1(rs.getString("principal1"));
						sb.setPlaceType(placeName);
						sb.setAreaName(areaName);
						sb.setIfDealAlarm(rs.getInt("ifAlarm"));
						sb.setPrincipal1Phone(rs.getString("principal1Phone"));
						sb.setPrincipal2(rs.getString("principal2"));
						sb.setPrincipal2Phone(rs.getString("principal2Phone"));
						sb.setRepeater(rs.getString("repeater"));
						sbList.add(sb);
					}
					if(sbList!=null&&sbList.size()>0){
						ae.setError("获取烟感成功");
						ae.setErrorCode(0);
						ae.setSmoke(sbList);
					}
					
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
			
			public List<String> getNormalLossAllSmoke(String userId, String privilege,String page) {
				// TODO Auto-generated method stub
				String loginSql = "select smokeMac from useridsmoke where userId = ?";
				Connection conn = DBConnectionManager.getConnection();
				PreparedStatement ps = DBConnectionManager.prepare(conn,loginSql);
				ResultSet rs = null;
				List<String> listMac = null;
				try {
					ps.setString(1, userId);
					rs = ps.executeQuery();
					while(rs.next()){
						if(listMac==null){
							listMac = new ArrayList<String>();
						}
						listMac.add(rs.getString("smokeMac"));
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					DBConnectionManager.close(rs);
					DBConnectionManager.close(ps);
					DBConnectionManager.close(conn);
				}
				
				return listMac;
			}
			
			@Override
			public AllSmokeEntity getNeedElevatorDev(String userId,
					String privilege, String page, String areaId,
					String placeTypeId) {
				// TODO Auto-generated method stub
				mAreaDao = new AreaDaoImpl();
				List<String> listNum = mAreaDao.getAreaStr(userId,privilege);
				if(listNum==null&&!privilege.equals("1")){
					return null;
				}
				mIfDealAlarmDao = new IfDealAlarmDaoImpl();
				List<String> listMac = mIfDealAlarmDao.getUserSmokeMac(listNum);
				if(listMac==null&&!privilege.equals("1")){
					return null;
				}
//				Map<String, Integer> macStateMap = mIfDealAlarmDao.getIfDealAlarm(listMac);
				mAllCameraDao = new AllCameraDaoImpl();
				Map<String,CameraBean> mapCamera = mAllCameraDao.getCameraByMac(listNum);
				Map<Integer, String> mapArea = mAreaDao.getAreaById(listNum);
				mPlaceTypeDao = new PlaceTypeDaoImpl();
				Map<String,String> map = mPlaceTypeDao.getShopTypeById();

				boolean isAllArea = false;
				StringBuffer strSql = new StringBuffer();
				if(areaId!=null&&areaId.length()>0){
					isAllArea = false;
					int areaIdInt = Integer.parseInt(areaId);
					if(areaIdInt == 44){
						strSql.append(" where areaId IN (SELECT areaid FROM areaidarea WHERE parentId = 44) ");
					}else if(areaIdInt>0){
						strSql.append(" where areaId = "+areaIdInt);
					}
				}else{
					isAllArea = true;
					int len = listNum.size();
					if(len==1){
						strSql.append(" where areaId in (?)");
					}else{
						for(int i=0;i<len;i++){
							if(i==0){
								strSql.append(" where areaId in (?, ");
							}else if(i==(len-1)){
								strSql.append(" ?)");
							}else{
								strSql.append(" ?, ");
							}
						}
					}
				}
				
				strSql.append(" AND deviceType=17");	
				
				if(placeTypeId!=null&&placeTypeId.length()>0){
					int placeTypeIdInt = Integer.parseInt(placeTypeId);
					if(placeTypeIdInt>0){
						strSql.append(" and placeTypeId = "+placeTypeIdInt);
					}
				}
				
				if(page!=null&&page.length()>0){
					int pageInt= Integer.parseInt(page);
					if(pageInt>0){
						int startNum = (pageInt-1)*20;
						int endNum = 20;
						strSql.append(" order by time desc limit "+startNum+" , "+endNum);
					}
				}
				
				String loginSql = new String("select address,latitude,longitude,mac,named,netState,deviceType,placeAddress" +
						",principal1,principal1Phone,principal2,principal2Phone,repeater,areaId,placeTypeId,ifAlarm" +
						" from smoke ");
//						" from smoke WHERE deviceType<11 AND deviceType <> 5");	//add by lzo at 2017-5-24
				String sqlStr= new String(loginSql+strSql.toString());
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
						if(ae==null){
							ae = new AllSmokeEntity();
						}
						String mac = rs.getString("mac");
						int areaID = rs.getInt("areaId");
						String placeTypeID = rs.getString("placeTypeId");
//						int ifDeal = 1;
//						if(macStateMap!=null&&macStateMap.size()>0){
//							ifDeal = macStateMap.get(mac);
//						}
						String placeName =null;
						if(map!=null&&map.size()>0){
							placeName = map.get(placeTypeID);
						}
						String areaName =null;
						if(mapArea!=null&&mapArea.size()>0){
							areaName = mapArea.get(areaID);
						}
						SmokeBean sb = new SmokeBean();
						if(sbList==null){
							sbList = new ArrayList<SmokeBean>();	
						}
						CameraBean cb =null;
						if(mapCamera!=null&&mapCamera.size()>0){
							cb = mapCamera.get(mac);
						}
						if(cb!=null){
							cb.setAreaName(areaName);
							cb.setPlaceType(placeName);
							sb.setCamera(cb);
						}else{
							cb = new CameraBean();
							sb.setCamera(cb);
						}
						sb.setAddress(rs.getString("address"));
						sb.setDeviceType(rs.getInt("deviceType"));
						sb.setLatitude(rs.getString("latitude"));
						sb.setLongitude(rs.getString("longitude"));
						sb.setMac(mac);
						sb.setName(rs.getString("named"));
						sb.setNetState(rs.getInt("netState"));
						sb.setPlaceeAddress(rs.getString("placeAddress"));
						sb.setPrincipal1(rs.getString("principal1"));
						sb.setPlaceType(placeName);
						sb.setAreaName(areaName);
						sb.setIfDealAlarm(rs.getInt("ifAlarm"));
						sb.setPrincipal1Phone(rs.getString("principal1Phone"));
						sb.setPrincipal2(rs.getString("principal2"));
						sb.setPrincipal2Phone(rs.getString("principal2Phone"));
						sb.setRepeater(rs.getString("repeater"));
						sbList.add(sb);
					}
					if(sbList!=null&&sbList.size()>0){
						ae.setError("获取烟感成功");
						ae.setErrorCode(0);
						ae.setSmoke(sbList);
					}
					
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

			@Override
			public ElevatorInfoBeanEntity getOneElevatorDev(String id) {
				// TODO Auto-generated method stub
				String sqlStr =" select mac,overGround,storeyNumber,status,door,people,alarm,storeyStuckAlarm,besiegeAlarm,"+
						"upperLimitAlarm,lowerLimitAlarm,runOpenDoorAlarm,overSpeedAlarm,netState from elevator where mac=?";
				Connection conn = DBConnectionManager.getConnection();
				PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
				ResultSet rs = null;
				ElevatorInfoBeanEntity eibe = null;
				ElevatorInfoBean eib=null;
				try {
					ps.setString(1, id);
					rs = ps.executeQuery();
					
					while(rs.next()){
						if(eibe==null){
							eibe = new ElevatorInfoBeanEntity();
						}
						if(eib==null){
							eib = new ElevatorInfoBean();
						}
						
						byte overGround=rs.getByte(2);
						short storeyNumber =rs.getShort(3);
						byte status=rs.getByte(4);
						byte door=rs.getByte(5);
						byte people=rs.getByte(6);
						byte alarm=rs.getByte(7);
						byte storeyStuckAlarm=rs.getByte(8);
						byte besiegeAlarm=rs.getByte(9);
						byte upperLimit=rs.getByte(10);
						byte lowerLimit=rs.getByte(11);
						byte runOpenDoor=rs.getByte(12);
						byte overSpeed=rs.getByte(13);
						byte netState=rs.getByte(14);
						
						eib.setId(id);
						eib.setOverGround(overGround);
						eib.setStoreyNumber(storeyNumber);
						eib.setStatus(status);
						eib.setDoor(door);
						eib.setPeople(people);
						eib.setAlarm(alarm);
						eib.setStoreyStuckAlarm(storeyStuckAlarm);
						eib.setBesiegeAlarm(besiegeAlarm);
						eib.setUpperLimit(upperLimit);
						eib.setLowerLimit(lowerLimit);
						eib.setRunOpen(runOpenDoor);
						eib.setOverSpeed(overSpeed);
						eib.setNetState(netState);
					}
					if(eib!=null){
						eibe.setElevator(eib);
					}
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					DBConnectionManager.close(rs);
					DBConnectionManager.close(ps);
					DBConnectionManager.close(conn);
				}
				
				return eibe;
			}
			
			public int getElectrState(String deviceMac) {
				// TODO Auto-generated method stub
				String sql = "select electrState from smoke where mac = ?";
				Connection conn = DBConnectionManager.getConnection();
				PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
				ResultSet rs = null;
				int type = -1;
				try {
					ps.setString(1, deviceMac);
					rs= ps.executeQuery();
					while(rs.next()){
						type = rs.getInt(1);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					DBConnectionManager.close(rs);
					DBConnectionManager.close(ps);
					DBConnectionManager.close(conn);
				}
				return type;
			}

			@Override
			public NFCInfoEntity getNFC_Info_Entity(String uuid) {
				StringBuilder sb = new StringBuilder();
				sb.append("select t.*,nfd.endTime from ");
				sb.append("(SELECT nfo.uid,nfo.areaid,nfo.deviceName,nfo.address,nfo.producer,nfo.addTime,nfo.makeTime,");
				sb.append("nfo.overTime,nfo.workerName,nfo.workerPhone,a.area,deviceType ");
				sb.append("from nfcinfo nfo,areaidarea a ");
				sb.append("where 1 = 1 and nfo.uid = ? and nfo.areaid = a.areaId) t ");
				sb.append("left join ");
				sb.append("(select uuid,endtime from nfcrecord WHERE uuid = ? ORDER BY endtime desc limit 1) nfd ");
				sb.append("on t.uid=nfd.uuid");
				Connection conn = DBConnectionManager.getConnection();
				PreparedStatement ps = DBConnectionManager.prepare(conn, sb.toString());
				ResultSet rs = null;
				NFCInfoEntity nfc = new NFCInfoEntity();
				try {
					ps.setString(1, uuid);
					rs = ps.executeQuery();
					while(rs.next()){
						nfc.setDeviceType(rs.getInt("deviceType")+"");
						nfc.setUid(rs.getString(1));
						nfc.setAreaid(rs.getString(2));
						nfc.setDeviceName(rs.getString(3));
						nfc.setAddress(rs.getString(4));
						nfc.setProducer(rs.getString(5));
						nfc.setAddTime(rs.getString(6));
						nfc.setMakeTime(rs.getString(7));
						nfc.setOverTime(rs.getString(8));
						nfc.setWorkerName(rs.getString(9));
						nfc.setWorkerPhone(rs.getString(10));
						nfc.setEndTime(rs.getString(13));
						nfc.setAreaName(rs.getString(11));
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					DBConnectionManager.close(rs);
					DBConnectionManager.close(ps);
					DBConnectionManager.close(conn);
				}
				return nfc;
			}
			
			@Override
			public THInfoBeanEntity getTHDevInfo(String mac) {
				// TODO Auto-generated method stub
				String sqlStr =" select mac,temperature,humidity,time from th_info where mac=? order by time desc limit 1";
				Connection conn = DBConnectionManager.getConnection();
				PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
				ResultSet rs = null;
				THInfoBeanEntity eibe = null;
				THDevice eib=null;
				try {
					ps.setString(1, mac);
					rs = ps.executeQuery();
					
					while(rs.next()){
						if(eibe==null){
							eibe = new THInfoBeanEntity();
						}
						if(eib==null){
							eib = new THDevice();
						}
						
						String t=rs.getString(1);
						String h=rs.getString(2);
						
						eib.setTemperature(t);
						eib.setHumidity(h);
						
					}
					if(eib!=null){
						eibe.setThdevice(eib);
					}
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					DBConnectionManager.close(rs);
					DBConnectionManager.close(ps);
					DBConnectionManager.close(conn);
				}
				
				return eibe;
			}

}
