package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cloudfire.dao.AllElectricInfoDao;
import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.IfDealAlarmDao;
import com.cloudfire.dao.PlaceTypeDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.AllElectricInfoEntity;
import com.cloudfire.entity.Electric;
import com.cloudfire.until.Constant;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.Utils;

public class AllElectricInfoDaoImpl implements AllElectricInfoDao{
	private AreaDao mAreaDao;
	private IfDealAlarmDao mIfDealAlarmDao;
	private PlaceTypeDao mPlaceTypeDao;

	public AllElectricInfoEntity getAllElectricInfo(String userId,
			String privilege, String page) {
		// TODO Auto-generated method stub
		mAreaDao = new AreaDaoImpl();
		List<String> listNum = mAreaDao.getAreaStr(userId,privilege);
		if(listNum==null){
			return null;
		}
		mIfDealAlarmDao = new IfDealAlarmDaoImpl();
		List<String> listMac = mIfDealAlarmDao.getUserSmokeMac(listNum);
		if(listMac==null&&!privilege.equals("1")){
			return null;
		}
//		Map<String, Integer> macStateMap = mIfDealAlarmDao.getIfDealAlarm(listMac);
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		Map<String,String> map = mPlaceTypeDao.getShopTypeById();
		
		StringBuffer strSql = new StringBuffer();
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
		
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*20;
				int endNum = 20;
				strSql.append(" order by time desc limit "+startNum+" , "+endNum);
			}
		}
		
		String sql = "select s.time,s.address,a.area,s.latitude,s.longitude,s.mac,s.named,s.netState" +
				",s.placeTypeId,s.placeAddress,s.principal1,s.principal1Phone,s.principal2,s.principal2Phone,s.repeater,s.ifAlarm,s.electrState,s.deviceType from smoke s,areaidarea a" +
				" where s.areaId = a.areaId and s.deviceType in (5,35,52,53,59,75,76,77) and ";
		String sqlStr = new String(sql+strSql.toString());
		sqlStr = "select * from ("+sqlStr+") as a LEFT JOIN (SELECT * from (SELECT smokeMac,alarmFamily,alarmTime from alarm where alarmFamily = 36 or alarmFamily = 136 ORDER BY id desc) as b GROUP BY smokeMac) m on a.mac = m.smokeMac";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
		AllElectricInfoEntity aeie = null;
		List<Electric> listElectric = null;
		try {
			for(int i=1;i<=len;i++){
				ps.setInt(i, Integer.parseInt(listNum.get(i-1)));
			}
			rs = ps.executeQuery();
			while(rs.next()){
				if(listElectric==null){
					listElectric = new ArrayList<Electric>();
				}
				/**
     * addSmokeTime : 2016-11-03 15:07:14
     * address : 中国广东省广州市天河区黄埔大道西554号
     * areaName : 测试区
     * deviceType : 5
     * ifDealAlarm : 1
     * latitude : 23.131788
     * longitude : 113.350338
     * mac : 32110533
     * name : 电流测试
     * netState : 0
     * placeType : 烧烤
     * placeeAddress :
     * principal1 :
     * principal1Phone :
     * principal2 :
     * principal2Phone :
     * repeater : 11091620
     */
				String mac = rs.getString(6);
				Electric mElectric = new Electric();
				mElectric.setAddress(rs.getString(2));
				mElectric.setAddSmokeTime(rs.getString(1));
				mElectric.setAreaName(rs.getString(3));
				mElectric.setDeviceType(rs.getInt("deviceType"));
				mElectric.setIfDealAlarm(rs.getInt(16));
				mElectric.setLatitude(rs.getString(4));
				mElectric.setLongitude(rs.getString(5));
				mElectric.setMac(mac);
				mElectric.setName(rs.getString(7));
				mElectric.setNetState(rs.getInt(8));
				mElectric.setPlaceeAddress(rs.getString(10));
				String placeType = rs.getInt(9)+"";
				if(Utils.isNumeric(placeType)){
					mElectric.setPlaceType(map.get(placeType));
				}
				mElectric.setPrincipal1(rs.getString(11));
				mElectric.setPrincipal1Phone(rs.getString(12));
				mElectric.setPrincipal2(rs.getString(13));
				mElectric.setPrincipal2Phone(rs.getString(14));
				mElectric.setRepeater(rs.getString(15));
				mElectric.setEleState(rs.getInt("electrState"));
				if(rs.getInt("alarmFamily")==36||rs.getInt("alarmFamily")==136){
					String startime = rs.getString("alarmTime");
					String endtime = GetTime.ConvertTimeByLong();
					long lstart = Utils.getTimeByStr(startime);
					long lend = Utils.getTimeByStr(endtime);
//					System.out.println("mac:"+mElectric.getMac()+"time=================="+startime+":"+lstart+"   ============="+endtime+":====="+lend+"时间:"+((lend-lstart)<60*5000));
					if((lend-lstart)<2*60*60*1000){
						mElectric.setIfFault("1");
					}else{
						mElectric.setIfFault("0");
					}
				}
				listElectric.add(mElectric);
			}
			if(listElectric!=null&&listElectric.size()>0){
				aeie = new AllElectricInfoEntity();
				aeie.setElectric(listElectric);
				aeie.setErrorCode(0);
				aeie.setError("获取电气设备成功");
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
		return aeie;
	}
	
	//自己写的
	public List<Electric> getAllElectricInfo(String userId,
			String privilege) {
		// TODO Auto-generated method stub
		String limit = " limit " ;
		mAreaDao = new AreaDaoImpl();
		List<String> listNum = mAreaDao.getAreaStr(userId,privilege);
		if(listNum==null){
			return null;
		}
		mIfDealAlarmDao = new IfDealAlarmDaoImpl();
		List<String> listMac = mIfDealAlarmDao.getUserSmokeMac(listNum);
		if(listMac==null&&!privilege.equals("1")){
			return null;
		}
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		Map<String,String> map = mPlaceTypeDao.getShopTypeById();
		
		StringBuffer strSql = new StringBuffer();
		int len = listNum.size();
		if(len==1){
			strSql.append(" s.areaId in (?) order by s.time desc  ");
		}else{
			for(int i=0;i<len;i++){
				if(i==0){
					strSql.append(" s.areaId in (?, ");
				}else if(i==(len-1)){
					strSql.append(" ?) order by s.time desc ");
				}else{
					strSql.append(" ?, ");
				}
			}
		}
		
		String sql = "select distinct s.time,s.address,a.area,s.latitude,s.longitude,s.mac,s.named,s.netState" +
				",s.placeTypeId,s.placeAddress,s.principal1,s.principal1Phone,s.principal2,s.principal2Phone,s.repeater,s.ifAlarm,s.electrState from smoke s,areaidarea a,electricinfo e" +
				" where s.areaId = a.areaId and s.deviceType=5 and ";
		String sqlStr = new String(sql+strSql.toString());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
		List<Electric> listElectric = null;
		try {
			for(int i=1;i<=len;i++){
				ps.setInt(i, Integer.parseInt(listNum.get(i-1)));
			}
			rs = ps.executeQuery();
			while(rs.next()){
				if(listElectric==null){
					listElectric = new ArrayList<Electric>();
				}
				String mac = rs.getString(6);
				Electric mElectric = new Electric();
				mElectric.setAddress(rs.getString(2));
				mElectric.setAddSmokeTime(rs.getString(1));
				mElectric.setAreaName(rs.getString(3));
				mElectric.setDeviceType(5);
				mElectric.setIfDealAlarm(rs.getInt(16));
				mElectric.setLatitude(rs.getString(4));
				mElectric.setLongitude(rs.getString(5));
				mElectric.setMac(mac);
				mElectric.setName(rs.getString(7));
				mElectric.setNetState(rs.getInt(8));
				mElectric.setPlaceeAddress(rs.getString(10));
				String placeType = rs.getInt(9)+"";
				if(Utils.isNumeric(placeType)){
					mElectric.setPlaceType(map.get(placeType));
				}
				mElectric.setPrincipal1(rs.getString(11));
				mElectric.setPrincipal1Phone(rs.getString(12));
				mElectric.setPrincipal2(rs.getString(13));
				mElectric.setPrincipal2Phone(rs.getString(14));
				mElectric.setRepeater(rs.getString(15));
				mElectric.setEleState(rs.getInt("electrState"));
				listElectric.add(mElectric);
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
		return listElectric;
	}

	public AllElectricInfoEntity getNeedElectricInfo(String userId,
			String privilege, String parentId,String page, String areaId, String placeTypeId) {
		// TODO Auto-generated method stub
		StringBuffer strSql = new StringBuffer();
		mAreaDao = new AreaDaoImpl();
		List<String> listNum =null;
		if(Utils.isNumeric(areaId)&&!areaId.equals("44")){
			listNum = new ArrayList<String>();
			listNum.add(areaId);
		}else{
			listNum = mAreaDao.getAreaStr(userId,privilege);
			if(listNum==null){
				return null;
			}
		}
		
		mIfDealAlarmDao = new IfDealAlarmDaoImpl();
		List<String> listMac = mIfDealAlarmDao.getUserSmokeMac(listNum);
		if(listMac==null&&!privilege.equals("1")){
			return null;
		}
//		Map<String, Integer> macStateMap = mIfDealAlarmDao.getIfDealAlarm(listMac);
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		Map<String,String> map = mPlaceTypeDao.getShopTypeById();
		
		if(Utils.isNumeric(placeTypeId)){
			strSql.append(" s.placeTypeId ="+placeTypeId+" and ");
		}
		
//		int len = listNum.size();
//		if(len==1){
//			//s.areaId in (14,  15) order by a.alarmTime desc limit 0 , 20
//			strSql.append(" s.areaId in (?) ");
//		}else{
//			for(int i=0;i<len;i++){
//				if(i==0){
//					strSql.append(" s.areaId in (?, ");
//				}else if(i==(len-1)){
//					strSql.append(" ?) ");
//				}else{
//					strSql.append(" ?, ");
//				}
//			}
//		}
		boolean isAllArea = false;
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
				strSql.append(" s.areaId IN (SELECT areaid FROM areaidarea WHERE parentId = 44");
				strSql.append(" )");
			}else{
				strSql.append(" s.areaId = "+areaIdInt);
			}
		}else{
			isAllArea = true;
			int len = listNum.size();
			if(len==1){
				strSql.append(" s.areaId in (?)");
			}else{
				for(int i=0;i<len;i++){
					if(i==0){
						strSql.append(" s.areaId in (?, ");
					}else if(i==(len-1)){
						strSql.append(" ?)");
					}else{
						strSql.append(" ?, ");
					}
				}
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
		
		String sql = "select s.time,s.address,a.area,s.latitude,s.longitude,s.mac,s.named,s.netState" +
				",s.placeTypeId,s.placeAddress,s.principal1,s.principal1Phone,s.principal2,s.principal2Phone,s.repeater,s.ifAlarm,s.electrState,s.deviceType from smoke s,areaidarea a" +
				" where s.areaId = a.areaId and s.deviceType in (5,35,52,53,59,75,76,77) and ";
		String sqlStr = new String(sql+strSql.toString());
		sqlStr = "select * from ("+sqlStr+") as a LEFT JOIN (SELECT * from (SELECT smokeMac,alarmFamily,alarmTime from alarm where alarmFamily = 36 or alarmFamily = 136 ORDER BY id desc) as b GROUP BY smokeMac) m on a.mac = m.smokeMac";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
		AllElectricInfoEntity aeie = null;
		List<Electric> listElectric = null;
		try {
//			for(int i=1;i<=len;i++){
//				ps.setInt(i, Integer.parseInt(listNum.get(i-1)));
//			}
			if(isAllArea){
				int len = listNum.size();
				for(int i=1;i<=len;i++){
					ps.setInt(i, Integer.parseInt(listNum.get(i-1)));
				}
			}
			rs = ps.executeQuery();
			while(rs.next()){
				if(listElectric==null){
					listElectric = new ArrayList<Electric>();
				}
				String mac = rs.getString(6);
				Electric mElectric = new Electric();
				mElectric.setAddress(rs.getString(2));
				mElectric.setAddSmokeTime(rs.getString(1));
				mElectric.setAreaName(rs.getString(3));
				mElectric.setDeviceType(rs.getInt("deviceType"));
				mElectric.setIfDealAlarm(rs.getInt(16));
				mElectric.setLatitude(rs.getString(4));
				mElectric.setLongitude(rs.getString(5));
				mElectric.setMac(mac);
				mElectric.setName(rs.getString(7));
				mElectric.setNetState(rs.getInt(8));
				mElectric.setPlaceeAddress(rs.getString(10));
				String placeType = rs.getInt(9)+"";
				if(Utils.isNumeric(placeType)){
					mElectric.setPlaceType(map.get(placeType));
				}
				mElectric.setPrincipal1(rs.getString(11));
				mElectric.setPrincipal1Phone(rs.getString(12));
				mElectric.setPrincipal2(rs.getString(13));
				mElectric.setPrincipal2Phone(rs.getString(14));
				mElectric.setRepeater(rs.getString(15));
				mElectric.setEleState(rs.getInt("electrState"));
				if(rs.getInt("alarmFamily")==36||rs.getInt("alarmFamily")==136){
					long lstart = Utils.getTimeByStr(rs.getString("alarmTime"));
					long lend = System.currentTimeMillis();
					if((lend-lstart)<60*2000){
						mElectric.setIfFault("1");
					}else{
						mElectric.setIfFault("0");
					}
				}
				listElectric.add(mElectric);
			}
			if(listElectric!=null&&listElectric.size()>0){
				aeie = new AllElectricInfoEntity();
				aeie.setElectric(listElectric);
				aeie.setErrorCode(0);
				aeie.setError("获取电气设备成功");
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
		return aeie;
	}

}
