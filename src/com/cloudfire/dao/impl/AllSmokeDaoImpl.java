package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.cloudfire.dao.AllCameraDao;
import com.cloudfire.dao.AllSmokeDao;
import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.IfDealAlarmDao;
import com.cloudfire.dao.PlaceTypeDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.AllRepeaterEntity;
import com.cloudfire.entity.AllSmokeEntity;
import com.cloudfire.entity.CameraBean;
import com.cloudfire.entity.CountValue;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.LoginEntity;
import com.cloudfire.entity.RepeaterBean;

import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.meter.MeterInfoEntity;
import com.cloudfire.entity.meter.MeterInfoHttpEntity;
import com.cloudfire.entity.meter.MeterReadingEntity;
import com.cloudfire.entity.meter.MeterReadingHttpEntity;
import com.cloudfire.entity.meter.MeterSettingHttpEntity;
import com.cloudfire.until.Constant;
import com.cloudfire.until.Utils;

public class AllSmokeDaoImpl implements AllSmokeDao{
	private PlaceTypeDao mPlaceTypeDao;
	private AreaDao mAreaDao;
	private AllCameraDao mAllCameraDao;
	private IfDealAlarmDao mIfDealAlarmDao;

	public AllSmokeEntity getAdminAllSmoke(String userId, String privilege,String page) {
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
		String loginSql = new String("select address,latitude,longitude,mac,named,netState,deviceType,placeAddress" +
				",principal1,principal1Phone,principal2,principal2Phone,repeater,areaId,placeTypeId,ifAlarm" +
//				" from smoke where deviceType<>5);
//				" from smoke where deviceType<>5 and mac<>repeater and deviceType not in(11,12,13,14,15)");     //update by lzz at 2017-5-19
				" from smoke where 1=1 ");
		loginSql+=Constant.devTypeChooseSQLStatement_zdst("1");
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
	
	public AllSmokeEntity getAdminAllZDSTDev(String userId, String privilege,String page,String devType) {
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
		String loginSql = new String("select address,latitude,longitude,mac,named,netState,deviceType,placeAddress" +
				",principal1,principal1Phone,principal2,principal2Phone,repeater,areaId,placeTypeId,ifAlarm" +
//				" from smoke where deviceType<>5);
				" from smoke where 1=1 ");     //update by lzz at 2017-5-19
		loginSql+=Constant.devTypeChooseSQLStatement_zdst(devType);
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

	public AllSmokeEntity getSuperAllSmoke(String userId, String privilege,String page) {
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
		String loginSql = new String("select address,latitude,longitude,mac,named,netState,deviceType,placeAddress" +
				",principal1,principal1Phone,principal2,principal2Phone,repeater,areaId,placeTypeId,ifAlarm" +
//				" from smoke where deviceType<>5 ");
//				" from smoke where deviceType<>5 and mac<>repeater and deviceType not in(11,12,13,14,15)"); 
				" from smoke where 1=1 ");//update by lzz at 2017-5-19
		loginSql+=Constant.devTypeChooseSQLStatement_zdst("1");
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
				ps.setInt(i, Integer.parseInt(listNum.get(i-1)));
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
	
	
	public AllSmokeEntity getSuperAllZDSTDev(String userId, String privilege,String page,String devType) {
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
		String loginSql = new String("select address,latitude,longitude,mac,named,netState,deviceType,placeAddress" +
				",principal1,principal1Phone,principal2,principal2Phone,repeater,areaId,placeTypeId,ifAlarm" +
//				" from smoke where deviceType<>5 ");
				" from smoke where 1=1 ");     //update by lzz at 2017-5-19
		loginSql+=Constant.devTypeChooseSQLStatement_zdst(devType);
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
				ps.setInt(i, Integer.parseInt(listNum.get(i-1)));
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

	@Override
	public AllSmokeEntity getSuperAllSmokeBySearch(String userId,
			String privilege, String search) {
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
		
		String likeString= " and (named like '%"+search+"%' or mac like '%"+search+"%' or address like '%"+search+"%' ) ";
		
		StringBuffer strSql = new StringBuffer();
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
		String loginSql = new String("select address,latitude,longitude,mac,named,netState,deviceType,placeAddress" +
				",principal1,principal1Phone,principal2,principal2Phone,repeater,areaId,placeTypeId,ifAlarm" +
//				" from smoke where deviceType<>5 ");
				" from smoke where deviceType>0 ");//@change by liangbin in 2017.08.11
		String sqlStr = null;
		
		sqlStr= new String(loginSql+likeString+strSql.toString());
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
		AllSmokeEntity ae = null;
		List<SmokeBean> sbList = null;
		try {
			for(int i=1;i<=len;i++){
				ps.setInt(i, Integer.parseInt(listNum.get(i-1)));
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
	
	
	public AllSmokeEntity getAdminAllDevice(String userId, String privilege,String page) {
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
		String loginSql = new String("select address,latitude,longitude,mac,named,netState,deviceType,placeAddress" +
				",principal1,principal1Phone,principal2,principal2Phone,repeater,areaId,placeTypeId,ifAlarm" +
				" from smoke where deviceType not in(11,12,13,14,15)");     //update by lzz at 2017-5-19
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

	public AllSmokeEntity getSuperAllDevice(String userId, String privilege,String page) {
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
		String loginSql = new String("select address,latitude,longitude,mac,named,netState,deviceType,placeAddress" +
				",principal1,principal1Phone,principal2,principal2Phone,repeater,areaId,placeTypeId,ifAlarm" +
				" from smoke where deviceType not in(11,12,13)");     //update by lzz at 2017-5-19
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
				ps.setInt(i, Integer.parseInt(listNum.get(i-1)));
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
	
	public AllSmokeEntity getAdminAllFaultinfo(String userId, String privilege,String page,String areaIdstr) {
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
		String loginSql = new String("select address,latitude,longitude,mac,named,netState,deviceType,placeAddress" +
				",principal1,principal1Phone,principal2,principal2Phone,repeater,areaId,placeTypeId,ifAlarm" +
				" from smoke where mac = repeater AND repeater IN (SELECT repeaterMac FROM faultInfo GROUP BY repeaterMac)" +
				" and deviceType not in(11,12,13,14,15)"); 
		
		if(Utils.isNullStr(areaIdstr)){
			loginSql = loginSql+" and areaId = " + areaIdstr;
		}
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

	public AllSmokeEntity getSuperAllFaultinfo(String userId, String privilege,String page,String areaIdstr) {
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
		String loginSql = new String("select address,latitude,longitude,mac,named,netState,deviceType,placeAddress" +
				",principal1,principal1Phone,principal2,principal2Phone,repeater,areaId,placeTypeId,ifAlarm" +
				" from smoke where mac = repeater AND repeater IN (SELECT repeaterMac FROM faultInfo GROUP BY repeaterMac) and deviceType not in(11,12,13,14,15)");
		if(Utils.isNullStr(areaIdstr)){
			loginSql = loginSql+" and areaId = " + areaIdstr;
		}
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
				ps.setInt(i, Integer.parseInt(listNum.get(i-1)));
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
	public MeterInfoHttpEntity getMeterDeviceByUser(String user) {
		// TODO Auto-generated method stub
		String sql="select device from elecMeterUserDevice where userId=?";
		Connection conn=DBConnectionManager.getConnection();
		PreparedStatement ps=DBConnectionManager.prepare(conn, sql);
		ResultSet resultSet;
		MeterInfoHttpEntity mhe=null;
		
		List<MeterInfoEntity>list=null;
		try{
			ps.setString(1, user);
			resultSet=ps.executeQuery();
			while (resultSet.next()){
				if(mhe==null)	{
					mhe=new MeterInfoHttpEntity();
					list=new ArrayList<MeterInfoEntity>();
				}
				MeterInfoEntity mde=getMeterInfoByMac(resultSet.getString(1));
				list.add(mde);
			}
			if(mhe!=null){
				mhe.setDeviceList(list);
			}
			
		}catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);;
			DBConnectionManager.close(conn);
		}
		return mhe;

	}

	@Override
	public MeterInfoEntity getMeterInfoByMac(String mac) {
		// TODO Auto-generated method stub
		String sql="select name,address,ifSendVoltage ,ifSendElectricity ,ifSendPower  from elecMeterDevice where device=?";
		Connection conn=DBConnectionManager.getConnection();
		PreparedStatement ps=DBConnectionManager.prepare(conn, sql);
		ResultSet resultSet;
		MeterInfoEntity mde=new MeterInfoEntity();
		
		try{
			ps.setString(1, mac);
			resultSet=ps.executeQuery();
			while (resultSet.next()){
				mde.setMac(mac);
				mde.setName(resultSet.getString(1));
				mde.setAddress(resultSet.getString(2));
				mde.setIfSendVoltage(Integer.parseInt(resultSet.getString(3)));
				mde.setIfSendElectricity(Integer.parseInt(resultSet.getString(4)));
				mde.setIfSendPower(Integer.parseInt(resultSet.getString(5)));
			}
		
		}catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);;
			DBConnectionManager.close(conn);
		}
		return mde;
	}

	@Override
	public MeterReadingHttpEntity getMeterReadingByMac(String mac) {
		// TODO Auto-generated method stub
		String sql="select quantity,time,voltage,electricity,power from elecMeterDeviceInfo where device=? order by time desc  limit 0,15";
		Connection conn=DBConnectionManager.getConnection();
		PreparedStatement ps=DBConnectionManager.prepare(conn, sql);
		ResultSet resultSet;
		MeterReadingHttpEntity mrhe=null;
		List<MeterReadingEntity> list=null;
		
		try{
			ps.setString(1, mac);
			resultSet=ps.executeQuery();
			while(resultSet.next()){	
				if(mrhe==null){
					mrhe=new MeterReadingHttpEntity();	
					list=new ArrayList<MeterReadingEntity>();
				}								
				MeterReadingEntity mre=new MeterReadingEntity();
				mre.setMac(mac);
				mre.setQuantity(resultSet.getString(1));
				mre.setTime(resultSet.getString(2));
				mre.setVoltage(resultSet.getString(3));
				mre.setElectricity(resultSet.getString(4));
				mre.setPower(resultSet.getString(5));
				list.add(mre);
			}	
			if(list!=null&&list.size()>0){
				mrhe.setReadingList(list);
			}
		}catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);;
			DBConnectionManager.close(conn);
		}
		return mrhe;
	}

	@Override
	public HttpRsult elecMeterSetSettingByMac(String mac, String voltage,
			String electricity, String power) {
		// TODO Auto-generated method stub
		HttpRsult hr=new HttpRsult();
		String sql="update elecMeterDevice set ifSendVoltage =?,ifSendElectricity =?,ifSendPower =? where device =?";
		Connection conn=DBConnectionManager.getConnection();
		PreparedStatement ps=DBConnectionManager.prepare(conn, sql);
		
		try{
			ps.setString(1, voltage);;
			ps.setString(2, electricity);
			ps.setString(3, power);
			ps.setString(4, mac);
			if(ps.executeUpdate()>0)
			{
				hr.setError("修改配置成功");
				hr.setErrorCode(0);
			}
			else {
				hr.setError("修改配置失败");
				hr.setErrorCode(2);
			}
				
		}catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);;
			DBConnectionManager.close(conn);
		}
		return hr;
	}

	@Override
	public AllRepeaterEntity getAllRepeaterInfo(String userid,
			String privilege, String page,String search) {
		mAreaDao = new AreaDaoImpl();
		mIfDealAlarmDao = new IfDealAlarmDaoImpl();
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		
		String pageSql = null;
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*20;
				int endNum = 20;
				pageSql = new String(" limit "+startNum+" , "+endNum);
			}
		}
		StringBuffer strSql = new StringBuffer();
		String loginSql="";
		if(search!=null&&search.length()>0){
			loginSql = new String("select * from (SELECT s.repeater,repeaterinfo.repeaterState from (" +
					"SELECT repeater from smoke where areaId in(SELECT areaid from useridareaid where userid = ? and repeater!='') GROUP BY repeater desc" +
					" ) as s  LEFT JOIN repeaterinfo on s.repeater = repeaterinfo.repeaterMac ) as a where a.repeater  like '%"+search+"%' ");     //update by lzz at 2017-5-19
		}else{
			loginSql = new String("SELECT s.repeater,repeaterinfo.repeaterState from (" +
					"SELECT repeater from smoke where areaId in(SELECT areaid from useridareaid where userid = ? and repeater!='') GROUP BY repeater desc" +
					" ) as s  LEFT JOIN repeaterinfo on s.repeater = repeaterinfo.repeaterMac ");     //update by lzz at 2017-5-19
		}
		String sqlStr = null;
		if(pageSql!=null){
			sqlStr= new String(loginSql+strSql.toString()+pageSql.toString());
		}else{
			sqlStr= new String(loginSql+strSql.toString());
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
		AllRepeaterEntity ar = null;
		List<RepeaterBean> rList = new ArrayList<RepeaterBean>();
		try {
			ps.setString(1, userid);
			rs = ps.executeQuery();
			while(rs.next()){
				if(ar==null){
					ar = new AllRepeaterEntity();
				}
				RepeaterBean rb = new RepeaterBean();
				rb.setRepeaterMac(rs.getString(1));
				rb.setHoststate(rs.getInt(2));
				rList.add(rb);
			}
			if(rList!=null&&rList.size()>0){
				ar.setError("获取设备成功");
				ar.setErrorCode(0);
				ar.setRepeater(rList);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return ar;
	}
	
	@Override
	public AllRepeaterEntity getAllRepeaterInfo(String privilege, String page,String search) {
		mAreaDao = new AreaDaoImpl();
		mIfDealAlarmDao = new IfDealAlarmDaoImpl();
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		
		String pageSql = null;
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*20;
				int endNum = 20;
				pageSql = new String(" limit "+startNum+" , "+endNum);
			}
		}
		StringBuffer strSql = new StringBuffer();
		String loginSql = "";
		if(search!=null&&search.length()>0){
			loginSql = new String("select * from (SELECT s.repeater,repeaterinfo.repeaterState from (" +
					"SELECT repeater from smoke where areaId in(SELECT areaid from useridareaid where repeater!='') GROUP BY repeater desc" +
					" ) as s  LEFT JOIN repeaterinfo on s.repeater = repeaterinfo.repeaterMac) as a where a.repeater  like '%"+search+"%'");     //update by lzz at 2017-5-19
		}else{
			loginSql = new String("SELECT s.repeater,repeaterinfo.repeaterState from (" +
					"SELECT repeater from smoke where areaId in(SELECT areaid from useridareaid where repeater!='') GROUP BY repeater desc" +
					" ) as s  LEFT JOIN repeaterinfo on s.repeater = repeaterinfo.repeaterMac ");     //update by lzz at 2017-5-19
		}
		
		String sqlStr = null;
		if(pageSql!=null){
			sqlStr= new String(loginSql+strSql.toString()+pageSql.toString());
		}else{
			sqlStr= new String(loginSql+strSql.toString());
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
		AllRepeaterEntity ar = null;
		List<RepeaterBean> rList = new ArrayList<RepeaterBean>();
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				if(ar==null){
					ar = new AllRepeaterEntity();
				}
				RepeaterBean rb = new RepeaterBean();
				rb.setRepeaterMac(rs.getString(1));
				rb.setHoststate(rs.getInt(2));
				rList.add(rb);
			}
			if(rList!=null&&rList.size()>0){
				ar.setError("获取设备成功");
				ar.setErrorCode(0);
				ar.setRepeater(rList);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return ar;
	}

	@Override
	public List<SmokeBean> getSmokeList(String areaId, String state) {
		List<SmokeBean> lists = null;
		String sql = null;
		if(state.equals("2")){
			sql = "SELECT s.named,d.deviceName,s.netState,s.address,s.mac,s.longitude,s.latitude,s.principal1,s.principal1Phone,s.principal2," +
					"s.principal2Phone,s.ifAlarm,a.area,d.id,at.alarmId,at.alarmName FROM smoke as s,areaidarea as a,devices d,alarmType at, " +
					"(select smokeMac,alarmType from (select smokeMac,alarmType from alarm where smokeMac in (select mac from smoke s where s.areaId = " + areaId +
					" and s.ifAlarm = 0) order by id desc ) as temp group by smokeMac)as al "+
					"where s.areaId = a.areaId and s.deviceType = d.id and s.mac = al.smokeMac and al.alarmType = at.alarmId "; 
		} else {
			sql = "SELECT s.named,d.deviceName,s.netState,s.address,s.mac,s.longitude,s.latitude,s.principal1,s.principal1Phone,s.principal2," +
					"s.principal2Phone,s.ifAlarm,a.area,d.id";
			sql += " FROM smoke as s,areaidarea as a,devices d";
			sql += " WHERE s.areaId=a.areaId and s.deviceType = d.id" +" and s.areaId="+areaId +" and s.netState="+state;
		}
		
		
//		CountValue cv = new CountValue();
//		int macNum = 0; // 终端总数&&设备正常运行数量 2
//		int netStaterNum = 0; // 正常状态数量&&设备故障数量4
//		int ifDealNum = 0; // 报警状态个数&&报警总数5
//		int noNetStater = 0; // 故障个数&&设备掉线数量 3
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
//				macNum++;
				if (lists == null) {
					lists = new ArrayList<SmokeBean>();
				}
				SmokeBean mSmokeBean = new SmokeBean();
				
				mSmokeBean.setMac(rs.getString(5));
				mSmokeBean.setAddress(rs.getString(4));
				mSmokeBean.setName(rs.getString(1));
				mSmokeBean.setPlaceeAddress(rs.getString(2));
				int netState = rs.getInt(3);
				if (netState == 1) {
//					netStaterNum++;
					mSmokeBean.setPlaceType("在线");
				} else {
//					noNetStater++;
					mSmokeBean.setPlaceType("离线");
				}
				// mSmokeBean.setAreaName(areaName);
				mSmokeBean.setLatitude(rs.getString(7));
				mSmokeBean.setLongitude(rs.getString(6));
				mSmokeBean.setPrincipal1(rs.getString(8));
				mSmokeBean.setPrincipal1Phone(rs.getString(9));
				mSmokeBean.setPrincipal2(rs.getString(10));
				mSmokeBean.setPrincipal2Phone(rs.getString(11));
				mSmokeBean.setDeviceType(rs.getInt("id"));
				if(state.equals("2")){
					mSmokeBean.setAlarmName(rs.getString("alarmName"));
				}
				int ifalarmNum = rs.getInt("ifAlarm");
				mSmokeBean.setAreaName(rs.getString(13));
//				if (ifalarmNum == 0) {
//					ifDealNum++;
//				}
				mSmokeBean.setIfAlarm(ifalarmNum);
//				cv.setIfDealNum(ifDealNum);
//				cv.setNetStaterNum(netStaterNum);
//				cv.setNoNetStater(noNetStater);
//				cv.setMacNum(macNum);
//				mSmokeBean.setCv(cv);
				lists.add(mSmokeBean);
			}
//			if (macNum == 0) {
//				SmokeBean mSmokeBean = new SmokeBean();
//				mSmokeBean.setCv(cv);
//				if (lists == null) {
//					lists = new ArrayList<SmokeBean>();
//				}
//				lists.add(mSmokeBean);
//			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return lists;
	}

//	public static void main(String[] args) {
//		AllSmokeDaoImpl asdi = new AllSmokeDaoImpl();
//		List<String> areaIds = new ArrayList<String>();
//		areaIds.add("1");
//		areaIds.add("3");
//		List<SmokeBean> lst = asdi.getSmokeList(2, 1, areaIds);
//		System.out.println("It's over");
//	}
	
	/**
	 * 根据设备类型和flag标记（0表示为空，1表示在线，2表示异常，即离线+报警），和区域集合查询设备列表
	 */
	@Override
	public List<SmokeBean> getSmokeList(int deviceType, int flag,List<String> areaIds) {
		List<SmokeBean> lists = null;
		String sql = "select temp1.*,alt.alarmName from (";
		sql += "SELECT s.named,d.deviceName,s.netState,s.address,s.mac,s.longitude,s.latitude,s.principal1,s.principal1Phone,s.principal2," +
				"s.principal2Phone,s.ifAlarm,a.area,s.areaId,s.deviceType";
		sql += " FROM smoke as s,areaidarea as a,devices d";
		sql += " WHERE s.areaId=a.areaId and s.deviceType = d.id";
//		sql += " and s.areaId="+areaId;
		if (flag == 2) {
			sql += " and (s.ifAlarm = 0 or s.netState = 0) ";
		} else if (flag == 1){
			sql +=" and s.netState= 1";
		}
		if (deviceType != 0) {
			sql += " and s.deviceType = " +deviceType; 
		}
		if(areaIds != null) {
			int len = areaIds.size();
			if (len == 1)
				sql += " and s.areaId in (" + areaIds.get(0) +") "; 
			else {
				for (int i = 0; i < len; i++) {
					if (i == 0) {
						sql += " and s.areaId in (" + areaIds.get(0);
					} else if (i == len - 1) {
						sql += " ,"+areaIds.get(i) + ") ";
					} else {
						sql += " ,"+areaIds.get(i);
					}
				}
			}
		}
		sql += ") as temp1 	LEFT JOIN " +
				"(select smokeMac,alarmName from alarmType at,(select * from (select smokeMac,alarmType from alarm where smokeMac in (select mac from smoke s where s.ifAlarm = 0) order by id desc) temp group by smokeMac )as al "+ 
					" where al.alarmType = at.alarmId) as alt "+
					"on  mac = smokeMac";
		CountValue cv = new CountValue();
		int macNum = 0; // 终端总数&&设备正常运行数量 2
		int netStaterNum = 0; // 正常状态数量&&设备故障数量4
		int ifDealNum = 0; // 报警状态个数&&报警总数5
		int noNetStater = 0; // 故障个数&&设备掉线数量 3
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				macNum++;
				if (lists == null) {
					lists = new ArrayList<SmokeBean>();
				}
				SmokeBean mSmokeBean = new SmokeBean();
				mSmokeBean.setDeviceType(rs.getInt("deviceType"));
				mSmokeBean.setMac(rs.getString(5));
				mSmokeBean.setAddress(rs.getString(4));
				mSmokeBean.setName(rs.getString(1));
				mSmokeBean.setPlaceeAddress(rs.getString(2));
				int netState = rs.getInt(3);
				if (netState == 1) {
					netStaterNum++;
					mSmokeBean.setPlaceType("在线");
				} else {
					noNetStater++;
					mSmokeBean.setPlaceType("离线");
				}
				// mSmokeBean.setAreaName(areaName);
				mSmokeBean.setLatitude(rs.getString(7));
				mSmokeBean.setLongitude(rs.getString(6));
				mSmokeBean.setPrincipal1(rs.getString(8));
				mSmokeBean.setPrincipal1Phone(rs.getString(9));
				mSmokeBean.setPrincipal2(rs.getString(10));
				mSmokeBean.setPrincipal2Phone(rs.getString(11));
				mSmokeBean.setAreaId(rs.getInt("areaId"));
				int ifalarmNum = rs.getInt("ifAlarm");
				mSmokeBean.setAreaName(rs.getString(13));
				if (ifalarmNum == 0) {
					mSmokeBean.setAlarmName(rs.getString("alarmName"));
					ifDealNum++;
				}
				mSmokeBean.setIfAlarm(ifalarmNum);
				cv.setIfDealNum(ifDealNum);
				cv.setNetStaterNum(netStaterNum);
				cv.setNoNetStater(noNetStater);
				cv.setMacNum(macNum);
				mSmokeBean.setCv(cv);
				lists.add(mSmokeBean);
			}
			if (macNum == 0) {
				SmokeBean mSmokeBean = new SmokeBean();
				mSmokeBean.setCv(cv);
				if (lists == null) {
					lists = new ArrayList<SmokeBean>();
				}
				lists.add(mSmokeBean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return lists;
	}
	
	/**
	 * 根据areaId查询设备信息列表
	 */
	@Override
	public List<SmokeBean> getSmokeList(String areaId) {
		List<SmokeBean> lists = null;
//		String sql = "SELECT s.named,d.deviceName,s.netState,s.address,s.mac,s.longitude,s.latitude,s.principal1,s.principal1Phone,s.principal2," +
//				"s.principal2Phone,s.ifAlarm,a.area,s.areaId,s.deviceType";
//		sql += " FROM smoke as s,areaidarea as a,devices d";
//		sql += " WHERE s.areaId=a.areaId and s.deviceType = d.id and s.areaId = "+areaId+" ";
		String sql2 = "select s.longitude,s.latitude,s.netState,count(*) as number from smoke s where areaId = "+ areaId+" group by netState";
		CountValue cv = new CountValue();
		int macNum = 0; // 终端总数&&设备正常运行数量 2
		int netStaterNum = 0; // 正常状态数量&&设备故障数量4
		int ifDealNum = 0; // 报警状态个数&&报警总数5
		int noNetStater = 0; // 故障个数&&设备掉线数量 3
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql2);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			SmokeBean mSmokeBean = new SmokeBean();
			while (rs.next()) {
//				macNum++;
				if (lists == null) {
					lists = new ArrayList<SmokeBean>();
				}
				int number = rs.getInt("number");
				macNum += number;
				if(rs.getInt("netState") == 1)
					cv.setNetStaterNum(number);
				else 
					cv.setNoNetStater(number);
//				SmokeBean mSmokeBean = new SmokeBean();
				mSmokeBean.setLongitude(rs.getString("longitude"));
				mSmokeBean.setLatitude(rs.getString("latitude"));
//				mSmokeBean.setDeviceType(rs.getInt("deviceType"));
//				mSmokeBean.setMac(rs.getString(5));
//				mSmokeBean.setAddress(rs.getString(4));
//				mSmokeBean.setName(rs.getString(1));
//				mSmokeBean.setPlaceeAddress(rs.getString(2));
//				int netState = rs.getInt(3);
//				if (netState == 1) {
//					netStaterNum++;
//					mSmokeBean.setPlaceType("在线");
//				} else {
//					noNetStater++;
//					mSmokeBean.setPlaceType("离线");
//				}
				// mSmokeBean.setAreaName(areaName);
//				mSmokeBean.setLatitude(rs.getString(7));
//				mSmokeBean.setLongitude(rs.getString(6));
//				mSmokeBean.setPrincipal1(rs.getString(8));
//				mSmokeBean.setPrincipal1Phone(rs.getString(9));
//				mSmokeBean.setPrincipal2(rs.getString(10));
//				mSmokeBean.setPrincipal2Phone(rs.getString(11));
//				mSmokeBean.setAreaId(rs.getInt("areaId"));
//				int ifalarmNum = rs.getInt("ifAlarm");
//				mSmokeBean.setAreaName(rs.getString(13));
//				if (ifalarmNum == 0) {
//					ifDealNum++;
//				}
//				mSmokeBean.setIfAlarm(ifalarmNum);
//				if(rs.isLast()){
//					cv.setIfDealNum(ifDealNum);
//					cv.setNetStaterNum(netStaterNum);
//					cv.setNoNetStater(noNetStater);
//					cv.setMacNum(macNum);
//					mSmokeBean.setCv(cv);
//				}
			}
			cv.setMacNum(macNum);
			mSmokeBean.setCv(cv);
			lists.add(mSmokeBean);
//				lists.add(mSmokeBean);
//			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return lists;
	}

	public String getAlarmNameByMac(String mac){
		String sql = "select alarmName from alarmType  where alarmId  =( select alarmType from alarm where smokeMac = '"+mac+"' order by id desc limit 1)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		String alarmName="";
		try {
			rs = ps.executeQuery();
			if (rs.next()){
				alarmName = rs.getString(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	
		return alarmName;
	}
	
}
