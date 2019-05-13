package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cloudfire.dao.AllAlarmDao;
import com.cloudfire.dao.AllSmokeDao;
import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.PlaceTypeDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.AlarmMessageEntity;
import com.cloudfire.entity.AllAlarmEntity;
import com.cloudfire.entity.ElectrAlarmThresholdEntity;
import com.cloudfire.entity.OneAlarmEntity;
import com.cloudfire.entity.THAlarmThresholdEntity;
import com.cloudfire.entity.WaterAckEntity;
import com.cloudfire.entity.WaterAlarmThresholdEntity;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.Utils;

public class AllAlarmDaoImpl implements AllAlarmDao{
	private PlaceTypeDao mPlaceTypeDao;
	private AreaDao mAreaDao;
	private AllSmokeDao mAllSmokeDao;

	public AllAlarmEntity getAdminAllAlarmMsg(String userId, String privilege,
			String page) {
		mAreaDao = new AreaDaoImpl();
		List<String> listNum = mAreaDao.getAreaStr(userId,privilege);
		if(listNum==null){
			return null;
		}

		mPlaceTypeDao = new PlaceTypeDaoImpl();
		Map<String,String> map = mPlaceTypeDao.getShopTypeById();
		Map<Integer, String> mapArea = mAreaDao.getAreaById(listNum);
		StringBuffer strSql = new StringBuffer();
		int len = listNum.size();
		if(len==1){
			//s.areaId in (14,  15) order by a.alarmTime desc limit 0 , 20
			strSql.append(" s.areaId in (?) order by a.id desc");
		}else{
			for(int i=0;i<len;i++){
				if(i==0){
					strSql.append(" s.areaId in (?, ");
				}else if(i==(len-1)){
					strSql.append(" ?) order by a.id desc");
				}else{
					strSql.append(" ?, ");
				}
			}
		}
		
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*20;
				int endNum = 20;
				strSql.append(" limit "+startNum+" , "+endNum);
			}
		}
		
		String loginSql = new String("select s.address,a.alarmFamily,a.alarmTime,a.alarmTruth,a.alarmType," +
				"s.areaId,a.dealDetail,a.dealPeople,a.dealTime,s.deviceType,a.ifDealAlarm,s.latitude," +
				"s.longitude,s.mac,s.named,s.placeAddress,s.principal1,s.principal1Phone,s.principal2," +
				"s.principal2Phone,s.placeTypeId" +
				" from smoke s,alarm a where s.mac = a.smokeMac and ");
		String sqlStr = new String(loginSql+strSql.toString());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
		AllAlarmEntity aae= null;
		List<AlarmMessageEntity> list = null;
		try {
			for(int i=1;i<=len;i++){
				ps.setInt(i, Integer.parseInt(listNum.get(i-1)));
			}
			rs = ps.executeQuery();
			
			while(rs.next()){
				if(aae==null){
					aae = new AllAlarmEntity();
					list = new ArrayList<AlarmMessageEntity>();
				}
				String mac = rs.getString(14);
				int areaId = rs.getInt(6);
				int placeType = rs.getInt(21);
				String shopType = map.get(placeType+"");
				if(shopType==null){
					shopType="";
				}
				
				AlarmMessageEntity ame = new AlarmMessageEntity();
				ame.setAddress(rs.getString(1));
				ame.setAlarmFamily(rs.getInt(2));
				ame.setAlarmFamilys(rs.getString(2));
				ame.setAlarmTime(rs.getString(3));
				ame.setAlarmType(rs.getInt(5));
				ame.setAreaName(mapArea.get(areaId));
				ame.setDeviceType(rs.getString(10));
				ame.setIfDealAlarm(rs.getInt(11));
				ame.setLatitude(rs.getString(12));
				ame.setLongitude(rs.getString(13));
				ame.setMac(mac);
				ame.setName(rs.getString(15));
				ame.setPlaceeAddress(rs.getString(16));
				ame.setPlaceType(shopType);
				ame.setPrincipal1(rs.getString(17));
				ame.setPrincipal1Phone(rs.getString(18));
				ame.setPrincipal2(rs.getString(19));
				ame.setPrincipal2Phone(rs.getString(20));
				ame.setAlarmTruth(rs.getInt(4));
				ame.setDealDetail(rs.getString(7));
				ame.setDealPeople(rs.getString(8));
				ame.setDealTime(rs.getString(9));
				list.add(ame);
			}
			if(aae!=null){
				aae.setAlarm(list);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return aae;
	}

	public AllAlarmEntity getNormalAllAlarmMsg(String userId, String privilege,
			String page) {
		mAreaDao = new AreaDaoImpl();
		mAllSmokeDao = new AllSmokeDaoImpl();
		List<String> listMac = mAllSmokeDao.getNormalAllSmoke(userId,privilege,page);
		List<String> listNum=null;
		Map<Integer, String> mapArea=null;
		if(listMac!=null){//@@
			listNum = mAreaDao.getAreaStrByMac(listMac);
			mapArea = mAreaDao.getAreaById(listNum);
		}
		
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		Map<String,String> map = mPlaceTypeDao.getShopTypeById();
		
		if(listMac==null){
			return null;
		}
		
		StringBuffer strSql = new StringBuffer();
		int len = listMac.size();
		if(len==1){
			//s.areaId in (14,  15) order by a.alarmTime desc limit 0 , 20
			strSql.append(" s.mac in (?) order by a.id desc");
		}else{
			for(int i=0;i<len;i++){
				if(i==0){
					strSql.append(" s.mac in (?, ");
				}else if(i==(len-1)){
					strSql.append(" ?) order by a.id desc");
				}else{
					strSql.append(" ?, ");
				}
			}
		}
		
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*20;
				int endNum = 20;
				strSql.append(" limit "+startNum+" , "+endNum);
			}
		}
		
		String loginSql = new String("select s.address,a.alarmFamily,a.alarmTime,a.alarmTruth,a.alarmType," +
				"s.areaId,a.dealDetail,a.dealPeople,a.dealTime,s.deviceType,a.ifDealAlarm,s.latitude," +
				"s.longitude,s.mac,s.named,s.placeAddress,s.principal1,s.principal1Phone,s.principal2," +
				"s.principal2Phone,s.placeTypeId" +
				" from smoke s,alarm a where s.mac = a.smokeMac and ");
		String sqlStr = new String(loginSql+strSql.toString());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
		AllAlarmEntity aae= null;
		List<AlarmMessageEntity> list = null;
		try {
			for(int i=1;i<=len;i++){
				ps.setString(i, listMac.get(i-1));
			}
			rs = ps.executeQuery();
			
			while(rs.next()){
				if(aae==null){
					aae = new AllAlarmEntity();
					list = new ArrayList<AlarmMessageEntity>();
				}
				AlarmMessageEntity ame = new AlarmMessageEntity();
				String mac = rs.getString(14);
				int areaId = rs.getInt(6);
				int placeType = rs.getInt(21);
				String shopType = map.get(placeType+"");
				if(shopType==null){
					shopType="";
				}
				
				ame.setAddress(rs.getString(1));
				ame.setAlarmFamily(rs.getInt(2));
				ame.setAlarmTime(rs.getString(3));
				ame.setAlarmType(rs.getInt(5));
				ame.setAreaName(mapArea.get(areaId));
				ame.setDeviceType(rs.getString(10));
				ame.setIfDealAlarm(rs.getInt(11));
				ame.setLatitude(rs.getString(12));
				ame.setLongitude(rs.getString(13));
				ame.setMac(mac);
				ame.setName(rs.getString(15));
				ame.setPlaceeAddress(rs.getString(16));
				ame.setPlaceType(shopType);
				ame.setPrincipal1(rs.getString(17));
				ame.setPrincipal1Phone(rs.getString(18));
				ame.setPrincipal2(rs.getString(19));
				ame.setPrincipal2Phone(rs.getString(20));
				ame.setAlarmTruth(rs.getInt(4));
				ame.setDealDetail(rs.getString(7));
				ame.setDealPeople(rs.getString(8));
				ame.setDealTime(rs.getString(9));
				list.add(ame);
			}
			if(aae!=null){
				aae.setAlarm(list);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return aae;
	}

	public AllAlarmEntity getAdminNeedAlarm(String userId, String privilege,
			String page, String startTime, String endTime, String areaID,
			String placeTypeId,String parentId) {
		List<String> listNum = null;

		mPlaceTypeDao = new PlaceTypeDaoImpl();
		Map<String,String> map = mPlaceTypeDao.getShopTypeById();
		Map<Integer, String> mapArea = null;
		StringBuffer strSql = new StringBuffer();
		
		if(Utils.isNumeric(placeTypeId)){
			strSql.append(" s.placeTypeId = "+placeTypeId +" and ");
		}

		mAreaDao = new AreaDaoImpl();
		if(Utils.isNumeric(areaID)||Utils.isNumeric(parentId)){
			listNum = new ArrayList<String>();
			if(parentId!=null&&parentId.length()>0){
				listNum.add(parentId);
				mapArea = mAreaDao.getAreaById2(listNum,1);
				if(privilege.equals("4")){
					strSql.append(" s.areaId IN (SELECT areaid FROM areaidarea WHERE parentId = ?");
					strSql.append(" )");
				}else{
					strSql.append(" s.areaId IN (select a.areaId from areaidarea a,useridareaid u WHERE a.areaId=u.areaId AND u.userId="+userId+" and a.parentId=?");
					strSql.append(" )");
				}
				
			}else if(areaID!=null&&areaID.length()>0){
				listNum.add(areaID);
				mapArea = mAreaDao.getAreaById2(listNum,2);
				int areaIdInt = Integer.parseInt(areaID);
				if(areaIdInt==44){
					strSql.append(" s.areaId IN (SELECT areaid FROM areaidarea WHERE parentId = ?");
					strSql.append(" )");
				}else{
					strSql.append(" s.areaId = ?");
				}
			}
			
//			listNum.add(areaID);
//			mapArea = mAreaDao.getAreaById(listNum);
//			strSql.append(" s.areaId in (?) ");
		}else{
			listNum = mAreaDao.getAreaStr(userId,privilege);
			if(listNum==null){
				return null;
			}
			mapArea = mAreaDao.getAreaById(listNum);
			int len = listNum.size();
			if(len==1){
				//s.areaId in (14,  15) order by a.alarmTime desc limit 0 , 20
				strSql.append(" s.areaId in (?) ");
			}else{
				for(int i=0;i<len;i++){
					if(i==0){
						strSql.append(" s.areaId in (?, ");
					}else if(i==(len-1)){
						strSql.append(" ?) ");
					}else{
						strSql.append(" ?, ");
					}
				}
			}
		}
		
		if(Utils.isNullStr(startTime)&&Utils.isNullStr(endTime)){
			strSql.append(" and a.alarmTime between ? and ? ");
		}
		strSql.append(" order by a.id desc ");
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*20;
				int endNum = 20;
				strSql.append(" limit "+startNum+" , "+endNum);
			}
		}
		
		String loginSql = new String("select s.address,a.alarmFamily,a.alarmTime,a.alarmTruth,a.alarmType," +
				"s.areaId,a.dealDetail,a.dealPeople,a.dealTime,s.deviceType,a.ifDealAlarm,s.latitude," +
				"s.longitude,s.mac,s.named,s.placeAddress,s.principal1,s.principal1Phone,s.principal2," +
				"s.principal2Phone,s.placeTypeId" +
				" from smoke s,alarm a where s.mac = a.smokeMac and ");
		String sqlStr = new String(loginSql+strSql.toString());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
		AllAlarmEntity aae= null;
		List<AlarmMessageEntity> list = null;
		try {
			int len = listNum.size();
			for(int i=1;i<=len;i++){
				ps.setInt(i, Integer.parseInt(listNum.get(i-1)));
			}
			if(Utils.isNullStr(startTime)&&Utils.isNullStr(endTime)){
				ps.setString(len+1,startTime);
				ps.setString(len+2,endTime);
			}
			rs = ps.executeQuery();
			while(rs.next()){
				if(aae==null){
					aae = new AllAlarmEntity();
					list = new ArrayList<AlarmMessageEntity>();
				}
				String mac = rs.getString(14);
				int areaId = rs.getInt(6);
				int placeType = rs.getInt(21);
				String shopType = map.get(placeType+"");
				if(shopType==null){
					shopType="";
				}
				
				AlarmMessageEntity ame = new AlarmMessageEntity();
				ame.setAddress(rs.getString(1));
				ame.setAlarmFamily(rs.getInt(2));
				ame.setAlarmTime(rs.getString(3));
				ame.setAlarmType(rs.getInt(5));
				ame.setAreaName(mapArea.get(areaId));
				ame.setDeviceType(rs.getString(10));
				ame.setIfDealAlarm(rs.getInt(11));
				ame.setLatitude(rs.getString(12));
				ame.setLongitude(rs.getString(13));
				ame.setMac(mac);
				ame.setName(rs.getString(15));
				ame.setPlaceeAddress(rs.getString(16));
				ame.setPlaceType(shopType);
				ame.setPrincipal1(rs.getString(17));
				ame.setPrincipal1Phone(rs.getString(18));
				ame.setPrincipal2(rs.getString(19));
				ame.setPrincipal2Phone(rs.getString(20));
				ame.setAlarmTruth(rs.getInt(4));
				ame.setDealDetail(rs.getString(7));
				ame.setDealPeople(rs.getString(8));
				ame.setDealTime(rs.getString(9));
				list.add(ame);
			}
			if(aae!=null){
				aae.setAlarm(list);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return aae;
	}

	public AllAlarmEntity getNormalNeedAlarm(String userId, String privilege,
			String page, String startTime, String endTime) {
		mAreaDao = new AreaDaoImpl();
		mAllSmokeDao = new AllSmokeDaoImpl();
		List<String> listMac = mAllSmokeDao.getNormalAllSmoke(userId,privilege,page);
		List<String> listNum = mAreaDao.getAreaStrByMac(listMac);
		Map<Integer, String> mapArea = mAreaDao.getAreaById(listNum);
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		Map<String,String> map = mPlaceTypeDao.getShopTypeById();
		
		if(listMac==null){
			return null;
		}
		
		StringBuffer strSql = new StringBuffer();
		int len = listMac.size();
		if(len==1){
			//s.areaId in (14,  15) order by a.alarmTime desc limit 0 , 20
			strSql.append(" s.mac in (?) ");
		}else{
			for(int i=0;i<len;i++){
				if(i==0){
					strSql.append(" s.mac in (?, ");
				}else if(i==(len-1)){
					strSql.append(" ?) ");
				}else{
					strSql.append(" ?, ");
				}
			}
		}
		
		if(Utils.isNullStr(startTime)&&Utils.isNullStr(endTime)){
			strSql.append(" and a.alarmTime between ? and ? ");
		}
		strSql.append(" order by a.id desc ");
		
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*20;
				int endNum = 20;
				strSql.append(" limit "+startNum+" , "+endNum);
			}
		}
		
		String loginSql = new String("select s.address,a.alarmFamily,a.alarmTime,a.alarmTruth,a.alarmType," +
				"s.areaId,a.dealDetail,a.dealPeople,a.dealTime,s.deviceType,a.ifDealAlarm,s.latitude," +
				"s.longitude,s.mac,s.named,s.placeAddress,s.principal1,s.principal1Phone,s.principal2," +
				"s.principal2Phone,s.placeTypeId" +
				" from smoke s,alarm a where s.mac = a.smokeMac and ");
		String sqlStr = new String(loginSql+strSql.toString());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
		AllAlarmEntity aae= null;
		List<AlarmMessageEntity> list = null;
		try {
			for(int i=1;i<=len;i++){
				ps.setString(i, listMac.get(i-1));
			}
			if(Utils.isNullStr(startTime)&&Utils.isNullStr(endTime)){
				ps.setString(len+1,startTime);
				ps.setString(len+2,endTime);
			}
			rs = ps.executeQuery();
			while(rs.next()){
				if(aae==null){
					aae = new AllAlarmEntity();
					list = new ArrayList<AlarmMessageEntity>();
				}
				AlarmMessageEntity ame = new AlarmMessageEntity();
				String mac = rs.getString(14);
				int areaId = rs.getInt(6);
				int placeType = rs.getInt(21);
				String shopType = map.get(placeType+"");
				if(shopType==null){
					shopType="";
				}
				
				ame.setAddress(rs.getString(1));
				ame.setAlarmFamily(rs.getInt(2));
				ame.setAlarmTime(rs.getString(3));
				ame.setAlarmType(rs.getInt(5));
				ame.setAreaName(mapArea.get(areaId));
				ame.setDeviceType(rs.getString(10));
				ame.setIfDealAlarm(rs.getInt(11));
				ame.setLatitude(rs.getString(12));
				ame.setLongitude(rs.getString(13));
				ame.setMac(mac);
				ame.setName(rs.getString(15));
				ame.setPlaceeAddress(rs.getString(16));
				ame.setPlaceType(shopType);
				ame.setPrincipal1(rs.getString(17));
				ame.setPrincipal1Phone(rs.getString(18));
				ame.setPrincipal2(rs.getString(19));
				ame.setPrincipal2Phone(rs.getString(20));
				ame.setAlarmTruth(rs.getInt(4));
				ame.setDealDetail(rs.getString(7));
				ame.setDealPeople(rs.getString(8));
				ame.setDealTime(rs.getString(9));
				list.add(ame);
			}
			if(aae!=null){
				aae.setAlarm(list);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return aae;
	}

	@Override
	public int getAdminAllAlarmPageNumber(String userId, String privilege,String startTime, String endTime, String areaID,
			String placeTypeId) {
	
		int alarmNumber=0;
		List<String> listNum = null;



		StringBuffer strSql = new StringBuffer();
		
		if(Utils.isNumeric(placeTypeId)){
			strSql.append(" s.placeTypeId = "+placeTypeId +" and ");
		}

		mAreaDao = new AreaDaoImpl();
		if(Utils.isNumeric(areaID)){
			listNum = new ArrayList<String>();
			listNum.add(areaID);

			strSql.append(" s.areaId in (?) ");
		}else{
			listNum = mAreaDao.getAreaStr(userId,privilege);
			if(listNum==null){
				return 0;
			}

			int len = listNum.size();
			if(len==1){
				//s.areaId in (14,  15) order by a.alarmTime desc limit 0 , 20
				strSql.append(" s.areaId in (?) ");
			}else{
				for(int i=0;i<len;i++){
					if(i==0){
						strSql.append(" s.areaId in (?, ");
					}else if(i==(len-1)){
						strSql.append(" ?) ");
					}else{
						strSql.append(" ?, ");
					}
				}
			}
		}
		
		if(Utils.isNullStr(startTime)&&Utils.isNullStr(endTime)){
			strSql.append(" and a.alarmTime between ? and ? ");
		}
		strSql.append(" order by a.id desc ");
		
		
		String loginSql = new String("select count(*) from smoke s,alarm a where s.mac = a.smokeMac and ");
		String sqlStr = new String(loginSql+strSql.toString());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
	
		
		try {
			int len = listNum.size();
			for(int i=1;i<=len;i++){
				ps.setInt(i, Integer.parseInt(listNum.get(i-1)));
			}
			if(Utils.isNullStr(startTime)&&Utils.isNullStr(endTime)){
				ps.setString(len+1,startTime);
				ps.setString(len+2,endTime);
			}
			rs = ps.executeQuery();
			if(rs.first()){
				alarmNumber=rs.getInt(1);	
				
			}
			
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return alarmNumber;
		
	}

	@Override
	public OneAlarmEntity getNormalLastestAlarm(String userId, String privilege) {
				mAreaDao = new AreaDaoImpl();
				mAllSmokeDao = new AllSmokeDaoImpl();
				List<String> listMac = mAllSmokeDao.getNormalAllSmoke(userId,privilege,"");
				if(listMac==null){
					return null;
				}
				List<String> listNum = mAreaDao.getAreaStrByMac(listMac);
				Map<Integer, String> mapArea = mAreaDao.getAreaById(listNum);
				mPlaceTypeDao = new PlaceTypeDaoImpl();
				Map<String,String> map = mPlaceTypeDao.getShopTypeById();
				
				
				
				StringBuffer strSql = new StringBuffer();
				int len = listMac.size();
				if(len==1){
					//s.areaId in (14,  15) order by a.alarmTime desc limit 0 , 20
					strSql.append(" s.mac in (?) order by a.id desc");
				}else{
					for(int i=0;i<len;i++){
						if(i==0){
							strSql.append(" s.mac in (?, ");
						}else if(i==(len-1)){
							strSql.append(" ?) order by a.id desc");
						}else{
							strSql.append(" ?, ");
						}
					}
				}
				
				
				String loginSql = new String("select  s.address,a.alarmFamily,a.alarmTime,a.alarmTruth,a.alarmType," +
						"s.areaId,a.dealDetail,a.dealPeople,a.dealTime,s.deviceType,a.ifDealAlarm,s.latitude," +
						"s.longitude,s.mac,s.named,s.placeAddress,s.principal1,s.principal1Phone,s.principal2," +
						"s.principal2Phone,s.placeTypeId" +
						" from smoke s,alarm a where s.mac = a.smokeMac and ");
				String sqlStr = new String(loginSql+strSql.toString()+" limit 1");
				Connection conn = DBConnectionManager.getConnection();
				PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
				ResultSet rs = null;
				OneAlarmEntity aae= null;
				try {
					for(int i=1;i<=len;i++){
						ps.setString(i, listMac.get(i-1));
					}
					rs = ps.executeQuery();
					
					if(rs.next()){
						if(aae==null){
							aae = new OneAlarmEntity();
						}
						AlarmMessageEntity ame = new AlarmMessageEntity();
						String mac = rs.getString(14);
						int areaId = rs.getInt(6);
						int placeType = rs.getInt(21);
						String shopType = map.get(placeType+"");
						if(shopType==null){
							shopType="";
						}
						
						ame.setAddress(rs.getString(1));
						ame.setAlarmFamily(rs.getInt(2));
						ame.setAlarmTime(rs.getString(3));
						ame.setAlarmType(rs.getInt(5));
						ame.setAreaName(mapArea.get(areaId));
						ame.setDeviceType(rs.getString(10));
						ame.setIfDealAlarm(rs.getInt(11));
						ame.setLatitude(rs.getString(12));
						ame.setLongitude(rs.getString(13));
						ame.setMac(mac);
						ame.setName(rs.getString(15));
						ame.setPlaceeAddress(rs.getString(16));
						ame.setPlaceType(shopType);
						ame.setPrincipal1(rs.getString(17));
						ame.setPrincipal1Phone(rs.getString(18));
						ame.setPrincipal2(rs.getString(19));
						ame.setPrincipal2Phone(rs.getString(20));
						ame.setAlarmTruth(rs.getInt(4));
						ame.setDealDetail(rs.getString(7));
						ame.setDealPeople(rs.getString(8));
						ame.setDealTime(rs.getString(9));
						aae.setLasteatAlarm(ame);
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					DBConnectionManager.close(rs);
					DBConnectionManager.close(ps);
					DBConnectionManager.close(conn);
				}
				return aae;
	}

	@Override
	public OneAlarmEntity getAdminLastestAlarm(String userId, String privilege) {
				mAreaDao = new AreaDaoImpl();
				List<String> listNum = mAreaDao.getAreaStr(userId,privilege);
				if(listNum==null){
					return null;
				}

				mPlaceTypeDao = new PlaceTypeDaoImpl();
				Map<String,String> map = mPlaceTypeDao.getShopTypeById();
				Map<Integer, String> mapArea = mAreaDao.getAreaById(listNum);
				StringBuffer strSql = new StringBuffer();
				int len = listNum.size();
				if(len==1){
					//s.areaId in (14,  15) order by a.alarmTime desc limit 0 , 20
					strSql.append(" s.areaId in (?) order by a.id desc");
				}else{
					for(int i=0;i<len;i++){
						if(i==0){
							strSql.append(" s.areaId in (?, ");
						}else if(i==(len-1)){
							strSql.append(" ?) order by a.id desc");
						}else{
							strSql.append(" ?, ");
						}
					}
				}
				
				
				
				String loginSql = new String("select s.address,a.alarmFamily,a.alarmTime,a.alarmTruth,a.alarmType," +
						"s.areaId,a.dealDetail,a.dealPeople,a.dealTime,s.deviceType,a.ifDealAlarm,s.latitude," +
						"s.longitude,s.mac,s.named,s.placeAddress,s.principal1,s.principal1Phone,s.principal2," +
						"s.principal2Phone,s.placeTypeId" +
						" from smoke s,alarm a where s.mac = a.smokeMac and ");
				String sqlStr = new String(loginSql+strSql.toString()+" limit 1");
				Connection conn = DBConnectionManager.getConnection();
				PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
				ResultSet rs = null;
				OneAlarmEntity aae= null;
				try {
					for(int i=1;i<=len;i++){
						ps.setInt(i, Integer.parseInt(listNum.get(i-1)));
					}
					rs = ps.executeQuery();
					
					if(rs.next()){
						if(aae==null){
							aae = new OneAlarmEntity();
						}
						String mac = rs.getString(14);
						int areaId = rs.getInt(6);
						int placeType = rs.getInt(21);
						String shopType = map.get(placeType+"");
						if(shopType==null){
							shopType="";
						}
						
						AlarmMessageEntity ame = new AlarmMessageEntity();
						ame.setAddress(rs.getString(1));
						ame.setAlarmFamily(rs.getInt(2));
						ame.setAlarmTime(rs.getString(3));
						ame.setAlarmType(rs.getInt(5));
						ame.setAreaName(mapArea.get(areaId));
						ame.setDeviceType(rs.getString(10));
						ame.setIfDealAlarm(rs.getInt(11));
						ame.setLatitude(rs.getString(12));
						ame.setLongitude(rs.getString(13));
						ame.setMac(mac);
						ame.setName(rs.getString(15));
						ame.setPlaceeAddress(rs.getString(16));
						ame.setPlaceType(shopType);
						ame.setPrincipal1(rs.getString(17));
						ame.setPrincipal1Phone(rs.getString(18));
						ame.setPrincipal2(rs.getString(19));
						ame.setPrincipal2Phone(rs.getString(20));
						ame.setAlarmTruth(rs.getInt(4));
						ame.setDealDetail(rs.getString(7));
						ame.setDealPeople(rs.getString(8));
						ame.setDealTime(rs.getString(9));
						aae.setLasteatAlarm(ame);
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					DBConnectionManager.close(rs);
					DBConnectionManager.close(ps);
					DBConnectionManager.close(conn);
				}
				return aae;
	}

	@Override
	public boolean updateAlarmThreshold(String mac, String threshold207,
			String threshold208) {
		boolean result = false;
		result = saveAlarmThreashold(mac,threshold207,207);
		if(result){
			result = saveAlarmThreashold(mac,threshold208,208);
		}
		return result;
	}
	
	@Override
	public boolean updateAlarmThreshold(String mac, String threshold207,
			String threshold208,String devtype) {
		boolean result = false;
		if(devtype.equals("1")){//温度
			result = saveAlarmThreashold(mac,threshold207,307);
			if(result){
				result = saveAlarmThreashold(mac,threshold208,308);
			}
		}else if(devtype.equals("2")){//湿度
			result = saveAlarmThreashold(mac,threshold207,407);
			if(result){
				result = saveAlarmThreashold(mac,threshold208,408);
			}
		}else{
			result = saveAlarmThreashold(mac,threshold207,207);
			if(result){
				result = saveAlarmThreashold(mac,threshold208,208);
			}
		}
		
		return result;
	}
	
	public boolean saveAlarmThreashold(String mac,String threshold,int alarmFamily){
		boolean result = false;
		String sql = "";
		String timeStr = GetTime.ConvertTimeByLong();
		if(ifExitAlarmThreashold(mac,alarmFamily)){
			sql = "update alarmthreshold set alarmthreshold1 = ?,alarmTime=? where alarmFamily = ? and smokeMac = ?";
		}else{
			sql = "insert into alarmthreshold(alarmthreshold1,alarmTime,alarmFamily,smokeMac) VALUES(?,?,?,?);";
		}
		Connection conn =  DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, threshold);
			ps.setString(2, timeStr);
			ps.setInt(3, alarmFamily);
			ps.setString(4, mac);
			if(ps.executeUpdate()>0){
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	public boolean ifExitAlarmThreashold(String mac,int alarmFamily){
		boolean result = false;
		String sql = "SELECT smokeMac,alarmthreshold1,alarmFamily from alarmthreshold where smokeMac = ? and alarmFamily = ?";
		Connection  conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, mac);
			ps.setInt(2, alarmFamily);
			rs = ps.executeQuery();
			while(rs.next()){
				result = true;
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
	public WaterAlarmThresholdEntity getWaterAlarmThreshold(String mac) {
		String sql = "SELECT * from alarmthreshold WHERE smokeMac=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		WaterAlarmThresholdEntity aae= null;
		try {
			ps.setString(1, mac);
			rs = ps.executeQuery();
			String low = "";
			String high = "";
			String waveValue="";
			String ackTimes="";
			while(rs.next()){
				if(aae==null){
					aae = new WaterAlarmThresholdEntity();
				}
				switch (rs.getString("alarmFamily")) {
				case "209":
				case "207":
					low=rs.getString("alarmthreshold1");
					break;
				case "218":
				case "208":
					high=rs.getString("alarmthreshold1");
					break;
				case "405":
					waveValue=rs.getInt("alarmthreshold1")+"";
					break;
				case "406":
					ackTimes=rs.getInt("alarmthreshold1")+"";
					break;
				default:
					break;
				}
				aae.setValue207(low);
				aae.setValue208(high);
				aae.setWaveValue(waveValue);
				aae.setAckTimes(ackTimes);
			}
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return aae;
	}
	
	@Override
	public ElectrAlarmThresholdEntity getElectrAlarmThreshold(String mac) {
		String sql = "SELECT * from alarmthreshold WHERE smokeMac=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		ElectrAlarmThresholdEntity aae= null;
		try {
			ps.setString(1, mac);
			rs = ps.executeQuery();
			String value43 = "";
			String value44 = "";
			String value45 = "";
			String value46 = "";
			String value47 = "";
			while(rs.next()){
				if(aae==null){
					aae = new ElectrAlarmThresholdEntity();
				}
				switch (rs.getString("alarmFamily")) {
				case "43":
					value43=rs.getInt("alarmthreshold1")+"";
					break;
				case "44":
					value44=rs.getInt("alarmthreshold1")+"";
					break;
				case "45":
					DecimalFormat decimalFormat=new DecimalFormat(".0");
					value45=decimalFormat.format(rs.getFloat("alarmthreshold1"))+"";
					break;
				case "46":
					value46=rs.getInt("alarmthreshold1")+"";
					break;
				case "47":
					value47=rs.getInt("alarmthreshold1")+"";
					break;
				default:
					break;
				}
				aae.setValue43(value43);
				aae.setValue44(value44);
				aae.setValue45(value45);
				aae.setValue46(value46);
				aae.setValue47(value47);
			}
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return aae;
	}
	
	@Override
	public THAlarmThresholdEntity getTHAlarmThreshold(String mac) {
		String sql = "SELECT * from alarmthreshold WHERE smokeMac=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		THAlarmThresholdEntity aae= null;
		try {
			ps.setString(1, mac);
			rs = ps.executeQuery();
			String t_low = "-";
			String t_high = "-";
			String h_low = "-";
			String h_high = "-";
			while(rs.next()){
				if(aae==null){
					aae = new THAlarmThresholdEntity();
				}
				switch (rs.getString("alarmFamily")) {
				case "307":
					t_low=rs.getString("alarmthreshold1");
					break;
				case "308":
					t_high=rs.getString("alarmthreshold1");
					break;
				case "407":
					h_low=rs.getString("alarmthreshold1");
					break;
				case "408":
					h_high=rs.getString("alarmthreshold1");
					break;
				default:
					break;
				}
			}
			if(aae!=null){
				aae.setValue307(t_low);
				aae.setValue308(t_high);
				aae.setValue407(h_low);
				aae.setValue408(h_high);
			}
			
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return aae;
	}

	@Override
	public AllAlarmEntity getNormalAllAlarmMsg(String userId, String privilege,
			String page, String smokeMac) {
		mAreaDao = new AreaDaoImpl();
		mAllSmokeDao = new AllSmokeDaoImpl();
		List<String> listMac = mAllSmokeDao.getNormalAllSmoke(userId,privilege,page);
		List<String> listNum=null;
		Map<Integer, String> mapArea=null;
		if(listMac!=null){//@@
			listNum = mAreaDao.getAreaStrByMac(listMac);
			mapArea = mAreaDao.getAreaById(listNum);
		}
		
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		Map<String,String> map = mPlaceTypeDao.getShopTypeById();
		
		if(listMac==null){
			return null;
		}
		
		StringBuffer strSql = new StringBuffer();
		int len = listMac.size();
		if(len==1){
			//s.areaId in (14,  15) order by a.alarmTime desc limit 0 , 20
			strSql.append(" s.mac in (?) order by a.id desc");
		}else{
			for(int i=0;i<len;i++){
				if(i==0){
					strSql.append(" s.mac in (?, ");
				}else if(i==(len-1)){
					strSql.append(" ?) order by a.id desc");
				}else{
					strSql.append(" ?, ");
				}
			}
		}
		
		if(smokeMac!=null&&smokeMac.length()>0){
			strSql.append(" s.mac="+smokeMac+" ");
		}
		
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*20;
				int endNum = 20;
				strSql.append(" limit "+startNum+" , "+endNum);
			}
		}
		
		String loginSql = new String("select s.address,a.alarmFamily,a.alarmTime,a.alarmTruth,a.alarmType," +
				"s.areaId,a.dealDetail,a.dealPeople,a.dealTime,s.deviceType,a.ifDealAlarm,s.latitude," +
				"s.longitude,s.mac,s.named,s.placeAddress,s.principal1,s.principal1Phone,s.principal2," +
				"s.principal2Phone,s.placeTypeId" +
				" from smoke s,alarm a where s.mac = a.smokeMac and ");
		String sqlStr = new String(loginSql+strSql.toString());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
		AllAlarmEntity aae= null;
		List<AlarmMessageEntity> list = null;
		try {
			for(int i=1;i<=len;i++){
				ps.setString(i, listMac.get(i-1));
			}
			rs = ps.executeQuery();
			
			while(rs.next()){
				if(aae==null){
					aae = new AllAlarmEntity();
					list = new ArrayList<AlarmMessageEntity>();
				}
				AlarmMessageEntity ame = new AlarmMessageEntity();
				String mac = rs.getString(14);
				int areaId = rs.getInt(6);
				int placeType = rs.getInt(21);
				String shopType = map.get(placeType+"");
				if(shopType==null){
					shopType="";
				}
				
				ame.setAddress(rs.getString(1));
				ame.setAlarmFamily(rs.getInt(2));
				ame.setAlarmTime(rs.getString(3));
				ame.setAlarmType(rs.getInt(5));
				ame.setAreaName(mapArea.get(areaId));
				ame.setDeviceType(rs.getString(10));
				ame.setIfDealAlarm(rs.getInt(11));
				ame.setLatitude(rs.getString(12));
				ame.setLongitude(rs.getString(13));
				ame.setMac(mac);
				ame.setName(rs.getString(15));
				ame.setPlaceeAddress(rs.getString(16));
				ame.setPlaceType(shopType);
				ame.setPrincipal1(rs.getString(17));
				ame.setPrincipal1Phone(rs.getString(18));
				ame.setPrincipal2(rs.getString(19));
				ame.setPrincipal2Phone(rs.getString(20));
				ame.setAlarmTruth(rs.getInt(4));
				ame.setDealDetail(rs.getString(7));
				ame.setDealPeople(rs.getString(8));
				ame.setDealTime(rs.getString(9));
				list.add(ame);
			}
			if(aae!=null){
				aae.setAlarm(list);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return aae;
	}

	@Override
	public AllAlarmEntity getAdminAllAlarmMsg(String userId, String privilege,
			String page, String smokeMac) {
		mAreaDao = new AreaDaoImpl();
		List<String> listNum = mAreaDao.getAreaStr(userId,privilege);
		if(listNum==null){
			return null;
		}

		mPlaceTypeDao = new PlaceTypeDaoImpl();
		Map<String,String> map = mPlaceTypeDao.getShopTypeById();
		Map<Integer, String> mapArea = mAreaDao.getAreaById(listNum);
		StringBuffer strSql = new StringBuffer();
		int len = listNum.size();
		if(len==1){
			//s.areaId in (14,  15) order by a.alarmTime desc limit 0 , 20
			strSql.append(" s.areaId in (?) order by a.id desc");
		}else{
			for(int i=0;i<len;i++){
				if(i==0){
					strSql.append(" s.areaId in (?, ");
				}else if(i==(len-1)){
					strSql.append(" ?) order by a.id desc");
				}else{
					strSql.append(" ?, ");
				}
			}
		}
		
		
		
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*20;
				int endNum = 20;
				strSql.append(" limit "+startNum+" , "+endNum);
			}
		}
		
		String loginSql = new String("select s.address,a.alarmFamily,a.alarmTime,a.alarmTruth,a.alarmType," +
				"s.areaId,a.dealDetail,a.dealPeople,a.dealTime,s.deviceType,a.ifDealAlarm,s.latitude," +
				"s.longitude,s.mac,s.named,s.placeAddress,s.principal1,s.principal1Phone,s.principal2," +
				"s.principal2Phone,s.placeTypeId" +
				" from smoke s,alarm a where s.mac = a.smokeMac and ");
		if(smokeMac!=null&&smokeMac.length()>0){
			loginSql=loginSql+" s.mac='"+smokeMac+"' and ";
		}
		String sqlStr = new String(loginSql+strSql.toString());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
		AllAlarmEntity aae= null;
		List<AlarmMessageEntity> list = null;
		try {
			for(int i=1;i<=len;i++){
				ps.setInt(i, Integer.parseInt(listNum.get(i-1)));
			}
			rs = ps.executeQuery();
			
			while(rs.next()){
				if(aae==null){
					aae = new AllAlarmEntity();
					list = new ArrayList<AlarmMessageEntity>();
				}
				String mac = rs.getString(14);
				int areaId = rs.getInt(6);
				int placeType = rs.getInt(21);
				String shopType = map.get(placeType+"");
				if(shopType==null){
					shopType="";
				}
				
				AlarmMessageEntity ame = new AlarmMessageEntity();
				ame.setAddress(rs.getString(1));
				ame.setAlarmFamily(rs.getInt(2));
				ame.setAlarmFamilys(rs.getString(2));
				ame.setAlarmTime(rs.getString(3));
				ame.setAlarmType(rs.getInt(5));
				ame.setAreaName(mapArea.get(areaId));
				ame.setDeviceType(rs.getString(10));
				ame.setIfDealAlarm(rs.getInt(11));
				ame.setLatitude(rs.getString(12));
				ame.setLongitude(rs.getString(13));
				ame.setMac(mac);
				ame.setName(rs.getString(15));
				ame.setPlaceeAddress(rs.getString(16));
				ame.setPlaceType(shopType);
				ame.setPrincipal1(rs.getString(17));
				ame.setPrincipal1Phone(rs.getString(18));
				ame.setPrincipal2(rs.getString(19));
				ame.setPrincipal2Phone(rs.getString(20));
				ame.setAlarmTruth(rs.getInt(4));
				ame.setDealDetail(rs.getString(7));
				ame.setDealPeople(rs.getString(8));
				ame.setDealTime(rs.getString(9));
				list.add(ame);
			}
			if(aae!=null){
				aae.setAlarm(list);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return aae;
	}
	
	
	public AllAlarmEntity getAdminNeedAlarmMsg(String userId, String privilege,
			String page, String startTime, String endTime, String areaID,
			String placeTypeId,String parentId,String grade,String distance,String progress,String id) {
		List<String> listNum = null;

		mPlaceTypeDao = new PlaceTypeDaoImpl();
		Map<String,String> map = mPlaceTypeDao.getShopTypeById();
		Map<Integer, String> mapArea = null;
		StringBuffer strSql = new StringBuffer();
		
		if(Utils.isNumeric(placeTypeId)){
			strSql.append(" s.placeTypeId = "+placeTypeId +" and ");
		}
		

		mAreaDao = new AreaDaoImpl();
		if(Utils.isNumeric(areaID)||Utils.isNumeric(parentId)){
			listNum = new ArrayList<String>();
			if(parentId!=null&&parentId.length()>0){
				listNum.add(parentId);
				mapArea = mAreaDao.getAreaById2(listNum,1);
				if(privilege.equals("4")){
					strSql.append(" s.areaId IN (SELECT areaid FROM areaidarea WHERE parentId = ?");
					strSql.append(" )");
				}else{
					strSql.append(" s.areaId IN (select a.areaId from areaidarea a,useridareaid u WHERE a.areaId=u.areaId AND u.userId="+userId+" and a.parentId=?");
					strSql.append(" )");
				}
				
			}else if(areaID!=null&&areaID.length()>0){
				listNum.add(areaID);
				mapArea = mAreaDao.getAreaById2(listNum,2);
				int areaIdInt = Integer.parseInt(areaID);
				if(areaIdInt==44){
					strSql.append(" s.areaId IN (SELECT areaid FROM areaidarea WHERE parentId = ?");
					strSql.append(" )");
				}else{
					strSql.append(" s.areaId = ?");
				}
			}
			
		}else{
			listNum = mAreaDao.getAreaStr(userId,privilege);
			if(listNum==null){
				return null;
			}
			mapArea = mAreaDao.getAreaById(listNum);
			int len = listNum.size();
			if(len==1){
				//s.areaId in (14,  15) order by a.alarmTime desc limit 0 , 20
				strSql.append(" s.areaId in (?) ");
			}else{
				for(int i=0;i<len;i++){
					if(i==0){
						strSql.append(" s.areaId in (?, ");
					}else if(i==(len-1)){
						strSql.append(" ?) ");
					}else{
						strSql.append(" ?, ");
					}
				}
			}
		}
		if(grade!=null){
			switch (grade) {
			case "1"://紧急
				strSql.append(" and a.alarmType not in (193,102,70,15)  " );
				break;
			case "2"://一般
				strSql.append(" and a.alarmType in (193,102,70,15)  " );
				break;
			default:
				break;
			}
		}
		
		if(progress!=null){
			switch(progress){
			case "1"://未处理
				strSql.append(" and a.ifDealAlarm =0 and a.alarmTime >'"+getBeforeDate(1)+"' " );
				break;
			case "2"://超时未处理
				strSql.append(" and a.ifDealAlarm =0 and a.alarmTime<'"+getBeforeDate(1)+"' " );
				break;
			case "3"://已处理
				strSql.append(" and a.ifDealAlarm =1 " );
				break;
			}
			
		}
		
		if(id!=null){
			strSql.append(" and a.id ="+id+" " );
		}
		
		if(Utils.isNullStr(startTime)&&Utils.isNullStr(endTime)){
			strSql.append(" and a.alarmTime between ? and ? ");
		}
		strSql.append(" order by a.id desc ");
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*20;
				int endNum = 20;
				strSql.append(" limit "+startNum+" , "+endNum);
			}else{
				strSql.append(" limit 500 ");//若不输入分页，则只查询前五百条数据
			}
		}
		
		String loginSql = new String("select s.address,a.alarmFamily,a.alarmTime,a.alarmTruth,a.alarmType," +
				"s.areaId,a.dealDetail,a.dealPeople,a.dealTime,s.deviceType,a.ifDealAlarm,s.latitude," +
				"s.longitude,s.mac,s.named,s.placeAddress,s.principal1,s.principal1Phone,s.principal2," +
				"s.principal2Phone,s.placeTypeId,a.image_path,a.video_path,a.id " +
				" from smoke s,alarm a where s.mac = a.smokeMac and ");
		String sqlStr = new String(loginSql+strSql.toString());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
		AllAlarmEntity aae= null;
		List<AlarmMessageEntity> list = null;
		try {
			int len = listNum.size();
			for(int i=1;i<=len;i++){
				ps.setInt(i, Integer.parseInt(listNum.get(i-1)));
			}
			if(Utils.isNullStr(startTime)&&Utils.isNullStr(endTime)){
				ps.setString(len+1,startTime);
				ps.setString(len+2,endTime);
			}
			rs = ps.executeQuery();
			while(rs.next()){
				if(aae==null){
					aae = new AllAlarmEntity();
					list = new ArrayList<AlarmMessageEntity>();
				}
				String mac = rs.getString(14);
				int areaId = rs.getInt(6);
				int placeType = rs.getInt(21);
				String shopType = map.get(placeType+"");
				if(shopType==null){
					shopType="";
				}
				
				AlarmMessageEntity ame = new AlarmMessageEntity();
				ame.setAddress(rs.getString(1));
				ame.setAlarmFamily(rs.getInt(2));
				ame.setAlarmTime(rs.getString(3));
				ame.setAlarmType(rs.getInt(5));
				ame.setAreaName(mapArea.get(areaId));
				ame.setDeviceType(rs.getString(10));
				
				if(rs.getInt(11)==0&&compare_date(rs.getString(3), getBeforeDate(1))<0){
					ame.setIfDealAlarm(3);//超时未处理
				}else{
					ame.setIfDealAlarm(rs.getInt(11));
				}
				ame.setLatitude(rs.getString(12));
				ame.setLongitude(rs.getString(13));
				ame.setMac(mac);
				ame.setName(rs.getString(15));
				ame.setPlaceeAddress(rs.getString(16));
				ame.setPlaceType(shopType);
				ame.setPrincipal1(rs.getString(17));
				ame.setPrincipal1Phone(rs.getString(18));
				ame.setPrincipal2(rs.getString(19));
				ame.setPrincipal2Phone(rs.getString(20));
				ame.setAlarmTruth(rs.getInt(4));
				ame.setDealDetail(rs.getString(7));
				ame.setDealPeople(rs.getString(8));
				ame.setDealTime(rs.getString(9));
				ame.setImage_path(rs.getString(22));
				ame.setVideo_path(rs.getString(23));
				ame.setId(rs.getString(24));
				list.add(ame);
			}
			if(aae!=null){
				aae.setAlarm(list);
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
		return aae;
	}
	
	public static int compare_date(String DATE1, String DATE2) {
        
        
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }
	
	public  String getBeforeDate(int days){
    	Calendar calendar = Calendar.getInstance();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		calendar.add(Calendar.DATE, -days);
		
		return sdf.format(calendar.getTime());
    }

//	207-低水位  208-高水位  209-低水压   218-高水压
//	307-低温		308-高温		407-低湿度		408-高湿度	406-采集时间	405-上报时间
	@Override
	public WaterAckEntity getWaterAckEntityThreshold(String mac) {
		String sql = "SELECT smokeMac,alarmthreshold1,alarmtime,alarmfamily from alarmthreshold WHERE smokeMac=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		WaterAckEntity wae= null;
		try {
			ps.setString(1, mac);
			rs = ps.executeQuery();
			while(rs.next()){
				if(wae==null){
					wae = new WaterAckEntity();
				}
				switch (rs.getString("alarmFamily")) {
				case "308":
				case "218":
					wae.setThreshold1(rs.getInt("alarmthreshold1"));
					break;
				case "209":
				case "307":
					wae.setThreshold2(rs.getInt("alarmthreshold1"));
					break;
				case "408":
					wae.setThreshold3(rs.getInt("alarmthreshold1"));
					break;
				case "407":
					wae.setThreshold4(rs.getInt("alarmthreshold1"));
					break;
				case "405":
					wae.setWaveValue(rs.getInt("alarmthreshold1"));
					break;
				case "406":
					wae.setAckTimes(rs.getInt("alarmthreshold1"));
					break;
				default:
					break;
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return wae;
	}

}
